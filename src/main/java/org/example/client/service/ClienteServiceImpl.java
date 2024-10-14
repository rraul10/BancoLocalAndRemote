package org.example.client.service;

import io.vavr.control.Either;
import org.example.client.repository.creditcard.CreditCardLocalRepository;
import org.example.client.repository.user.UsersRepository;
import org.example.client.service.errors.ServiceError;
import org.example.creditcard.cache.CacheTarjetaImpl;
import org.example.creditcard.repositories.CreditCardRepository;
import org.example.creditcard.validator.TarjetaValidator;
import org.example.models.Cliente;
import org.example.models.TarjetaCredito;
import org.example.models.Usuario;
import org.example.users.cache.CacheUsuario;
import org.example.users.repository.UserRemoteRepository;
import org.example.users.validator.UserValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

public class ClienteServiceImpl implements ClienteService {
    private final Logger logger = LoggerFactory.getLogger(ClienteServiceImpl.class);
    private final UsersRepository usersRepository;
    private final CreditCardLocalRepository creditCardLocalRepository;
    private final CreditCardRepository creditCardRepository;
    private final CacheUsuario cacheUsuario;
    private final CacheTarjetaImpl cacheTarjeta;
    private final UserValidator userValidator;
    private final UserRemoteRepository userRemoteRepository;
    private final TarjetaValidator tarjetaValidator;

    public ClienteServiceImpl(UsersRepository usersRepository, CreditCardLocalRepository creditCardLocalRepository, CreditCardRepository creditCardRepository, CacheUsuario cacheUsuario, CacheTarjetaImpl cacheTarjeta, UserValidator userValidator, UserRemoteRepository userRemoteRepository, TarjetaValidator tarjetaValidator) {
        this.usersRepository = usersRepository;
        this.creditCardLocalRepository = creditCardLocalRepository;
        this.creditCardRepository = creditCardRepository;
        this.cacheUsuario = cacheUsuario;
        this.cacheTarjeta = cacheTarjeta;
        this.userValidator = userValidator;
        this.userRemoteRepository = userRemoteRepository;
        this.tarjetaValidator = tarjetaValidator;
        loadData();
    }


    @Override
    public Either<ServiceError, List<Cliente>> getAllClientes(Boolean fromRemote) {
        try {
            List<Usuario> usuarios;
            List<TarjetaCredito> tarjetas;
            if(fromRemote){
                usuarios = userRemoteRepository.getAll();
                tarjetas = creditCardRepository.getAll();
            }else {
                usuarios = usersRepository.findAllUsers();
                tarjetas = creditCardLocalRepository.findAllCreditCards();
            }
            // Mapear las tarjetas por el idCliente
            Map<Long, List<TarjetaCredito>> tarjetasPorUsuario = tarjetas.stream()
                    .filter(tarjeta -> tarjeta.getClientID() != null)  // Filtrar tarjetas con idCliente no nulo
                    .collect(Collectors.groupingBy(TarjetaCredito::getClientID));

            // Crear la lista de clientes con usuarios y sus tarjetas
            List<Cliente> clientes = usuarios.stream()
                    .map(usuario -> new Cliente(
                            usuario,
                            tarjetasPorUsuario.getOrDefault(usuario.getId(), new ArrayList<>())  // Obtener las tarjetas o una lista vacía
                    ))
                    .collect(Collectors.toList());
            return Either.right(clientes);
        }catch(Exception e){
            logger.error("Error al obtener los clientes", e);
            return Either.left(new ServiceError.UsersLoadError("Error al obtener los clientes"));
        }
    }

    @Override
    public Either<ServiceError, List<Usuario> > getAllUsers(Boolean fromRemote) {
        try {
            if(fromRemote){
                return Either.right(userRemoteRepository.getAll());
            }
            return Either.right(usersRepository.findAllUsers());
        }catch (Exception e) {
            logger.error("Error al obtener los clientes", e);
            return Either.left(new ServiceError.UsersLoadError("Error al obtener los usuarios"));
        }
    }

    @Override
    public Either<ServiceError, List<TarjetaCredito>> getAllTarjetas(Boolean fromRemote) {
        try {
            if(fromRemote){
                return Either.right(creditCardRepository.getAll());
            }
            return Either.right(creditCardLocalRepository.findAllCreditCards());
        }catch (Exception e) {
            logger.error("Error al obtener los clientes", e);
            return Either.left(new ServiceError.UsersLoadError("Error al obtener los usuarios"));
        }
    }


    @Override
    public Either<ServiceError, Cliente> getClienteById(Long id) {
        try {
            Optional<Usuario> usuario = Optional.empty();
            if(cacheUsuario.containsKey(id)){
                usuario = Optional.ofNullable(cacheUsuario.get(id));
            }else {
                usuario = usersRepository.findUserById(id);
                if(usuario.isEmpty()) {
                    usuario = Optional.ofNullable(userRemoteRepository.getById(id));
                }
                usuario.ifPresent(u -> cacheUsuario.put(u.getId(), u));

            }
            if(usuario.isEmpty()){
                return Either.left(new ServiceError.UserNotFound("Cliente no encontrado con id: " + id));
            }
            Optional<List<TarjetaCredito>> tarjetas = Optional.ofNullable(cacheTarjeta.buscarPorIdUsuario(id));
            if(tarjetas.isEmpty()){
                tarjetas = Optional.ofNullable(creditCardLocalRepository.findAllCreditCardsByUserId(id));
                if(tarjetas.isEmpty()){
                    tarjetas = Optional.ofNullable(creditCardRepository.findAllCreditCardsByUserId(id));
                }
            }
            Cliente clienteDef = new Cliente(usuario.get(), tarjetas.orElse(new ArrayList<>()));
            return Either.right(clienteDef);
        }catch (Exception e){
            logger.error("Error al obtener el cliente con id: {}", id, e);
            return Either.left(new ServiceError.UserNotFound("Error al obtener el cliente con id: " + id));
        }
    }

    @Override
    public Either<ServiceError, Usuario> getUserById(Long id){
        try {
            Optional<Usuario> usuario = Optional.empty();
            if(cacheUsuario.containsKey(id)){
                usuario = Optional.ofNullable(cacheUsuario.get(id));
            } else {
                usuario = usersRepository.findUserById(id);
                if(usuario.isEmpty()) {
                    usuario = Optional.ofNullable(userRemoteRepository.getById(id));
                }
                usuario.ifPresent(u -> cacheUsuario.put(u.getId(), u));
            }
            if(usuario.isEmpty()){
                return Either.left(new ServiceError.UserNotFound("Usuario no encontrado con id: " + id));
            }
            return Either.right(usuario.get());
        }catch (Exception e){
            logger.error("Error al obtener el usuario con id: {}", id, e);
            return Either.left(new ServiceError.UserNotFound("Error al obtener el usuario con id: " + id));
        }
    }
    @Override
    public Either<ServiceError, TarjetaCredito> getTarjetaById(UUID id){
        try {
            Optional<TarjetaCredito> tarjeta = Optional.empty();
            if(cacheTarjeta.containsKey(id)){
                tarjeta = Optional.ofNullable(cacheTarjeta.get(id));
            } else {
                tarjeta = Optional.ofNullable(creditCardLocalRepository.findCreditCardById(id));
                if(tarjeta.isEmpty()) {
                    tarjeta = creditCardRepository.getById(id);
                }
                tarjeta.ifPresent(t -> cacheTarjeta.put(t.getId(), t));
            }
            if(tarjeta.isEmpty()){
                return Either.left(new ServiceError.UserNotFound("Tarjeta no encontrada con id: " + id));
            }
            return Either.right(tarjeta.get());
        }catch (Exception e){
            logger.error("Error al obtener la tarjeta con id: {}", id, e);
            return Either.left(new ServiceError.UserNotFound("Error al obtener la tarjeta con id: " + id));
        }
    }

    @Override
    public Either<ServiceError, List<Cliente>> getClienteByName(String nombre) {
        try {
            Optional<List<Usuario>> usuarios = Optional.empty();
            usuarios = Optional.ofNullable(usersRepository.findUsersByName(nombre));
            if(usuarios.isEmpty()) {
                usuarios = Optional.ofNullable(userRemoteRepository.getByName(nombre));
            }
            if(usuarios.isEmpty()){
                return Either.left(new ServiceError.UserNotFound("Cliente no encontrado con nombre: " + nombre));
            }
            List<Cliente> clientes = new ArrayList<>();
           for(int i = 0; i < usuarios.get().size(); i++){
              Usuario user  = usuarios.get().get(i);
               Optional<List<TarjetaCredito>> tarjetas = Optional.ofNullable(cacheTarjeta.buscarPorIdUsuario(user.getId()));
               if(tarjetas.isEmpty()){
                   tarjetas = Optional.ofNullable(creditCardLocalRepository.findAllCreditCardsByUserId(user.getId()));
                   if(tarjetas.isEmpty()){
                       tarjetas = Optional.ofNullable(creditCardRepository.findAllCreditCardsByUserId(user.getId()));
                   }
               }
              clientes.add(new Cliente(user, tarjetas.orElse(new ArrayList<>())));
           }

            return Either.right(clientes);
        }catch (Exception e){
            logger.error("Error al obtener el cliente con nombre: {}", nombre, e);
            return Either.left(new ServiceError.UserNotFound("Error al obtener el cliente con id: " + nombre));
        }
    }

    @Override
    public Either<ServiceError, List<Usuario>> getUserByName(String nombre) {
        try {
            Optional<List<Usuario>> usuarios = Optional.empty();
            usuarios = Optional.ofNullable(usersRepository.findUsersByName(nombre));
            if(usuarios.isEmpty()) {
                usuarios = Optional.ofNullable(userRemoteRepository.getByName(nombre));
            }
            if(usuarios.isEmpty()){
                return Either.left(new ServiceError.UserNotFound("Usuario no encontrado con nombre: " + nombre));
            }
            return Either.right(usuarios.get());
        }catch (Exception e){
            logger.error("Error al obtener el usuario con nombre: {}", nombre, e);
            return Either.left(new ServiceError.UserNotFound("Error al obtener el usuario con id: " + nombre));
        }
    }


    @Override
    public Either<ServiceError, Cliente> createCliente(Cliente cliente) {
        try{
            userValidator.ValidateUser(cliente.getUsuario()).bimap(
                    error -> {
                        logger.error("Error al validar el usuario", error);
                        return new ServiceError.tarjetaCreditValidatorError("Error al validar el usuario");
                    },
                    usuario -> {
                        Usuario usuarioCreado = userRemoteRepository.createUser(cliente.getUsuario());
                        cacheUsuario.put(usuarioCreado.getId(), usuarioCreado);
                        cliente.getTarjetas().forEach(tarjeta -> {
                            tarjeta.setClientID(usuarioCreado.getId());
                            tarjetaValidator.validarTarjetaCredito(tarjeta).bimap(
                                    error -> {
                                        logger.error("Error al validar la tarjeta", error);
                                        return new ServiceError.tarjetaCreditValidatorError("Error al validar la tarjeta");
                                    },
                                    tarjetaCredito -> {
                                        creditCardRepository.create(tarjetaCredito);
                                        cacheTarjeta.put(tarjetaCredito.getId(), tarjetaCredito);
                                        return Either.right(new Cliente(cliente.getUsuario(), cliente.getTarjetas()));
                                    }
                            );
                        });
                        return Either.right(new Cliente(cliente.getUsuario(), cliente.getTarjetas()));
                    }
            );
            return Either.right(new Cliente(cliente.getUsuario(), cliente.getTarjetas()));
        }catch (Exception e){
            logger.error("Error al crear el cliente", e);
            return Either.left(new ServiceError.UserNotDeleted("Error al crear el cliente" + cliente));
        }
    }


    @Override
    public Either<ServiceError, Cliente> updateCliente(Long id, Cliente cliente) {
        try {
            getClienteById(id).bimap(
                    error -> {
                        logger.error("Error al obtener el cliente con id: {}", id, error);
                        return new ServiceError.UserNotFound("Error al obtener el cliente con id: " + id);
                    },
                    user -> {
                        userValidator.ValidateUser(cliente.getUsuario()).bimap(
                                error -> {
                                    logger.error("Error al validar el usuario", error);
                                    return new ServiceError.UservalidatorError("Error al validar el usuario");
                                },
                                usuario -> {
                                    cacheUsuario.remove(id);
                                    Usuario usuarioActualizado = userRemoteRepository.updateUser(id, cliente.getUsuario());
                                    cacheUsuario.put(usuarioActualizado.getId(), usuarioActualizado);
                                    cliente.tarjetas.forEach(tarjetaCredito -> cacheTarjeta.remove(tarjetaCredito.getId()));
                                    cliente.getTarjetas().forEach(tarjeta -> {
                                        tarjeta.setClientID(usuarioActualizado.getId());
                                        tarjetaValidator.validarTarjetaCredito(tarjeta).bimap(
                                                error -> {
                                                    logger.error("Error al validar la tarjeta", error);
                                                    return new ServiceError.tarjetaCreditValidatorError("Error al validar la tarjeta");
                                                },
                                                tarjetaCredito -> {
                                                    creditCardRepository.update(tarjetaCredito.getId(), tarjetaCredito);
                                                    cacheTarjeta.put(tarjetaCredito.getId(), tarjetaCredito);
                                                    return Either.right(new Cliente(cliente.getUsuario(), cliente.getTarjetas()));
                                                }
                                        );
                                    });
                                    return Either.right(new Cliente(cliente.getUsuario(), cliente.getTarjetas()));
                                }
                        );
                        return Either.right(new Cliente(cliente.getUsuario(), cliente.getTarjetas()));
                    }
            );
            return Either.right(new Cliente(cliente.getUsuario(), cliente.getTarjetas()));
        }catch (Exception e){
            logger.error("Error al actualizar el cliente con id: {}", id, e);
            return Either.left(new ServiceError.UserNotUpdated("Error al actualizar el cliente con id: " + id));
        }
    }

    @Override
    public Either<ServiceError, Cliente> deleteCliente(Long id) {
        try {
            getClienteById(id).bimap(
                    error -> {
                        logger.error("Error al obtener el cliente con id: {}", id, error);
                        return new ServiceError.UserNotFound("Error al obtener el cliente con id: " + id);
                    },
                    user -> {
                        userRemoteRepository.deleteById(id);
                        cacheUsuario.remove(id);
                        user.getTarjetas().forEach(tarjeta -> {
                            creditCardRepository.delete(tarjeta.getId());
                        });
                        cacheUsuario.remove(id);
                        user.getTarjetas().forEach(tarjeta -> {
                            cacheTarjeta.remove(tarjeta.getId());
                        });
                        return Either.right(user);
                    }
            );
            return Either.left(new ServiceError.UserNotDeleted("Error al eliminar el cliente con id: " + id));
        }catch (Exception e){
            logger.error("Error al eliminar el cliente con id: {}", id, e);
            return Either.left(new ServiceError.UserNotDeleted("Error al eliminar el cliente con id: " + id));
        }
    }

    @Override
    public void loadData() {
        logger.debug("Cargando datos remotos");
        usersRepository.deleteAllUsers();
        creditCardLocalRepository.deleteAllCreditCards();
        userRemoteRepository.getAll().forEach(usersRepository::saveUser);
        creditCardRepository.getAll().forEach(creditCardRepository::create);
    }

}
