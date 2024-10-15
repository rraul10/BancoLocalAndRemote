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

/**
 * Implementacion del servicio de clientes.
 * Esta clase proporciona la logica de negocio para la gestion de clientes,
 * incluyendo la creacion, actualizacion, eliminacion y consulta de clientes.
 * @author Alvaro Herrero, Javier Ruiz, Javier Hernandez, Raul Fernandez, Yahya El Hadri, Samuel Cortes.
 * @since 1.0
 */


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


    /**
     * Inicializa la instancia con los repositorios y servicios necesarios.
     * @author Alvaro Herrero, Javier Ruiz, Javier Hernandez, Raul Fernandez, Yahya El Hadri, Samuel Cortes.
     * @since 1.0
     * @param usersRepository Repositorio de usuarios.
     * @param creditCardLocalRepository Repositorio de tarjetas de crédito local.
     * @param creditCardRepository Repositorio de tarjetas de crédito remoto.
     * @param cacheUsuario Cache de usuarios.
     * @param cacheTarjeta Cache de tarjetas de crédito.
     * @param usersRepository Validador de usuarios.
     * @param userRemoteRepository Repositorio de usuarios remoto.
     * @param tarjetaValidator Validador de tarjetas de crédito.
     */

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

    /**
     * Obtiene todos los clientes.
     * @author Alvaro Herrero, Javier Ruiz, Javier Hernandez, Raul Fernandez, Yahya El Hadri, Samuel Cortes.
     * @since 1.0
     * @param fromRemote Indica si se deben obtener los clientes desde el repositorio remoto.
     * @return Una lista de clientes.
     */


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

            Map<Long, List<TarjetaCredito>> tarjetasPorUsuario = tarjetas.stream()
                    .filter(tarjeta -> tarjeta.getClientID() != null)
                    .collect(Collectors.groupingBy(TarjetaCredito::getClientID));

            List<Cliente> clientes = usuarios.stream()
                    .map(usuario -> new Cliente(
                            usuario,
                            tarjetasPorUsuario.getOrDefault(usuario.getId(), new ArrayList<>())
                    ))
                    .collect(Collectors.toList());
            return Either.right(clientes);
        }catch(Exception e){
            logger.error("Error al obtener los clientes", e);
            return Either.left(new ServiceError.ClienteLoadErrors("Error al obtener los clientes"));
        }
    }

    /**
     * Obtiene todos los usuarios.
     * @author Alvaro Herrero, Javier Ruiz, Javier Hernandez, Raul Fernandez, Yahya El Hadri, Samuel Cortes.
     * @since 1.0
     * @param fromRemote Indica si se deben obtener los usuarios desde el repositorio remoto.
     * @return Una lista de usuarios.
     */

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

    /**
     * Obtiene todas las tarjetas de credito.
     * @author Alvaro Herrero, Javier Ruiz, Javier Hernandez, Raul Fernandez, Yahya El Hadri, Samuel Cortes.
     * @since 1.0
     * @param fromRemote Indica si se deben obtener las tarjetas de credito desde el repositorio remoto.
     * @return Una lista de tarjetas de credito.
     */

    @Override
    public Either<ServiceError, List<TarjetaCredito>> getAllTarjetas(Boolean fromRemote) {
        try {
            if(fromRemote){
                return Either.right(creditCardRepository.getAll());
            }
            return Either.right(creditCardLocalRepository.findAllCreditCards());
        }catch (Exception e) {
            logger.error("Error al obtener los clientes", e);
            return Either.left(new ServiceError.TarjetasLoadError("Error al obtener los usuarios"));
        }
    }

    /**
     * Obtiene un cliente por su ID.
     * @author Alvaro Herrero, Javier Ruiz, Javier Hernandez, Raul Fernandez, Yahya El Hadri, Samuel Cortes.
     * @since 1.0
     * @param id ID del cliente.
     * @return El cliente correspondiente al ID.
     */

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
                return Either.left(new ServiceError.ClienteNotFound("Cliente no encontrado con id: " + id));
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
            return Either.left(new ServiceError.ClienteNotFound("Error al obtener el cliente con id: " + id));
        }
    }

    /**
     * Obtiene un usuario por su ID.
     * @author Alvaro Herrero, Javier Ruiz, Javier Hernandez, Raul Fernandez, Yahya El Hadri, Samuel Cortes.
     * @since 1.0
     * @param id ID del usuario.
     * @return El usuario correspondiente al ID.
     */

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

    /**
     * Obtiene una tarjeta de credito por su ID.
     * @author Alvaro Herrero, Javier Ruiz, Javier Hernandez, Raul Fernandez, Yahya El Hadri, Samuel Cortes.
     * @since 1.0
     * @param id ID de la tarjeta de credito.
     * @return La tarjeta de credito correspondiente al ID.
     */

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
                return Either.left(new ServiceError.TarjetasLoadError("Tarjeta no encontrada con id: " + id));
            }
            return Either.right(tarjeta.get());
        }catch (Exception e){
            logger.error("Error al obtener la tarjeta con id: {}", id, e);
            return Either.left(new ServiceError.TarjetasLoadError("Error al obtener la tarjeta con id: " + id));
        }
    }

    /**
     * Obtiene un cliente por su nombre.
     * @author Alvaro Herrero, Javier Ruiz, Javier Hernandez, Raul Fernandez, Yahya El Hadri, Samuel Cortes.
     * @since 1.0
     * @param nombre Nombre del cliente.
     * @return El cliente correspondiente al nombre.
     */

    @Override
    public Either<ServiceError, List<Cliente>> getClienteByName(String nombre) {
        try {
            Optional<List<Usuario>> usuarios = Optional.empty();
            usuarios = Optional.ofNullable(usersRepository.findUsersByName(nombre));
            if(usuarios.isEmpty()) {
                usuarios = Optional.ofNullable(userRemoteRepository.getByName(nombre));
            }
            if(usuarios.isEmpty()){
                return Either.left(new ServiceError.TarjetasLoadError("Cliente no encontrado con nombre: " + nombre));
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
            return Either.left(new ServiceError.TarjetasLoadError("Error al obtener el cliente con id: " + nombre));
        }
    }

    /**
     * Obtiene un usuario por su nombre.
     * @author Alvaro Herrero, Javier Ruiz, Javier Hernandez, Raul Fernandez, Yahya El Hadri, Samuel Cortes.
     * @since 1.0
     * @param nombre Nombre del usuario.
     * @return El usuario correspondiente al nombre.
     */

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

    /**
     * Crea un nuevo cliente.
     * @author Alvaro Herrero, Javier Ruiz, Javier Hernandez, Raul Fernandez, Yahya El Hadri, Samuel Cortes.
     * @since 1.0
     * @param cliente El cliente a crear.
     * @return Either que contiene el cliente creado o un error si no se pudo crear.
     */

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
            return Either.left(new ServiceError.TarjetasLoadError("Error al crear el cliente" + cliente));
        }
    }

    /**
     * Crea un nuevo usuario.
     * @author Alvaro Herrero, Javier Ruiz, Javier Hernandez, Raul Fernandez, Yahya El Hadri, Samuel Cortes.
     * @since 1.0
     * @param usuario El usuario a crear.
     * @return Either que contiene el usuario creado o un error si no se pudo crear.
     */

    @Override
    public Either<ServiceError, Usuario> createUser(Usuario usuario) {
        try {
            userValidator.ValidateUser(usuario).bimap(
                    error -> {
                        logger.error("Error al validar el usuario", error);
                        return new ServiceError.UservalidatorError("Error al validar el usuario");
                    },
                    user -> {
                        Usuario usuarioCreado = userRemoteRepository.createUser(usuario);
                        cacheUsuario.put(usuarioCreado.getId(), usuarioCreado);
                        return Either.right(usuarioCreado);
                    }
            );
            return Either.right(usuario);
        }catch (Exception e){
            logger.error("Error al crear el usuario", e);
            return Either.left(new ServiceError.UserNotCreated("Error al crear el usuario" + usuario));
        }
    }

    /**
     * Crea una nueva tarjeta de credito.
     * @author Alvaro Herrero, Javier Ruiz, Javier Hernandez, Raul Fernandez, Yahya El Hadri, Samuel Cortes.
     * @since 1.0
     * @param tarjetaCredito La tarjeta de credito a crear.
     * @return Either que contiene la tarjeta de credito creada o un error si no se pudo crear.
     */

    @Override
    public Either<ServiceError, TarjetaCredito> createTarjeta(TarjetaCredito tarjetaCredito) {
        try {
            tarjetaValidator.validarTarjetaCredito(tarjetaCredito).bimap(
                    error -> {
                        logger.error("Error al validar la tarjeta", error);
                        return new ServiceError.tarjetaCreditValidatorError("Error al validar la tarjeta");
                    },
                    tarjeta -> {
                        TarjetaCredito tarjetaCreada = creditCardRepository.create(tarjeta);
                        cacheTarjeta.put(tarjetaCreada.getId(), tarjetaCreada);
                        return Either.right(tarjetaCreada);
                    }
            );
            return Either.right(tarjetaCredito);
        }catch (Exception e){
            logger.error("Error al crear la tarjeta", e);
            return Either.left(new ServiceError.TarjeteNotDeleted("Error al crear la tarjeta" + tarjetaCredito));
        }
    }

    /**
     * Actualiza un cliente existente.
     * @author Alvaro Herrero, Javier Ruiz, Javier Hernandez, Raul Fernandez, Yahya El Hadri, Samuel Cortes.
     * @since 1.0
     * @param id El ID del cliente a actualizar.
     * @param cliente El cliente actualizado.
     * @return Either que contiene el cliente actualizado o un error si no se pudo actualizar.
     */



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


    /**
     * Actualiza un usuario existente.
     * @author Alvaro Herrero, Javier Ruiz, Javier Hernandez, Raul Fernandez, Yahya El Hadri, Samuel Cortes.
     * @since 1.0
     * @param id El ID del usuario a actualizar.
     * @param usuario El usuario actualizado.
     * @return Either que contiene el usuario actualizado o un error si no se pudo actualizar.
     */

    @Override
    public Either<ServiceError, Usuario> updateUser(Long id, Usuario usuario) {
        try {
            getUserById(id).bimap(
                    error -> {
                        logger.error("Error al obtener el usuario con id: {}", id, error);
                        return new ServiceError.UserNotFound("Error al obtener el usuario con id: " + id);
                    },
                    user -> {
                        userValidator.ValidateUser(usuario).bimap(
                                error -> {
                                    logger.error("Error al validar el usuario", error);
                                    return new ServiceError.UservalidatorError("Error al validar el usuario");
                                },
                                usuarioValidado -> {
                                    cacheUsuario.remove(id);
                                    Usuario usuarioActualizado = userRemoteRepository.updateUser(id, usuario);
                                    cacheUsuario.put(usuarioActualizado.getId(), usuarioActualizado);
                                    return Either.right(usuarioActualizado);
                                }
                        );
                        return Either.right(user);
                    }
            );
            return Either.right(usuario);
        }catch (Exception e){
            logger.error("Error al actualizar el usuario con id: {}", id, e);
            return Either.left(new ServiceError.UserNotUpdated("Error al actualizar el usuario con id: " + id));
        }
    }

    /**
     * Actualiza una tarjeta de credito existente.
     * @author Alvaro Herrero, Javier Ruiz, Javier Hernandez, Raul Fernandez, Yahya El Hadri, Samuel Cortes.
     * @since 1.0
     * @param id El ID de la tarjeta de credito a actualizar.
     * @param tarjetaCredito La tarjeta de credito actualizada.
     * @return Either que contiene la tarjeta de credito actualizada o un error si no se pudo actualizar.
     */

    @Override
    public Either<ServiceError, TarjetaCredito> updateTarjeta(UUID id, TarjetaCredito tarjetaCredito) {
        try {
            getTarjetaById(id).bimap(
                    error -> {
                        logger.error("Error al obtener la tarjeta con id: {}", id, error);
                        return new ServiceError.TarjetasLoadError("Error al obtener la tarjeta con id: " + id);
                    },
                    tarjeta -> {
                        tarjetaValidator.validarTarjetaCredito(tarjetaCredito).bimap(
                                error -> {
                                    logger.error("Error al validar la tarjeta", error);
                                    return new ServiceError.tarjetaCreditValidatorError("Error al validar la tarjeta");
                                },
                                tarjetaValidada -> {
                                    cacheTarjeta.remove(id);
                                    TarjetaCredito tarjetaActualizada = creditCardRepository.update(id, tarjetaCredito);
                                    cacheTarjeta.put(tarjetaActualizada.getId(), tarjetaActualizada);
                                    return Either.right(tarjetaActualizada);
                                }
                        );
                        return Either.right(tarjeta);
                    }
            );
            return Either.right(tarjetaCredito);
        }catch (Exception e){
            logger.error("Error al actualizar la tarjeta con id: {}", id, e);
            return Either.left(new ServiceError.TarjeteNotDeleted("Error al actualizar la tarjeta con id: " + id));
        }
    }


    /**
     * Elimina un cliente existente.
     * @author Alvaro Herrero, Javier Ruiz, Javier Hernandez, Raul Fernandez, Yahya El Hadri, Samuel Cortes.
     * @since 1.0
     * @param id El ID del cliente a eliminar.
     * @return Either que contiene el cliente eliminado o un error si no se pudo eliminar.
     */

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

    /**
     * Elimina un usuario existente.
     * @param id El ID del usuario a eliminar.
     * @return Either que contiene el usuario eliminado o un error si no se pudo eliminar.
     */

    @Override
    public Either<ServiceError, Usuario> deleteUser(Long id) {
        try {
            getUserById(id).bimap(
                    error -> {
                        logger.error("Error al obtener el usuario con id: {}", id, error);
                        return new ServiceError.UserNotFound("Error al obtener el usuario con id: " + id);
                    },
                    user -> {
                        userRemoteRepository.deleteById(id);
                        cacheUsuario.remove(id);
                        return Either.right(user);
                    }
            );
            return Either.left(new ServiceError.UserNotDeleted("Error al eliminar el usuario con id: " + id));
        }catch (Exception e){
            logger.error("Error al eliminar el usuario con id: {}", id, e);
            return Either.left(new ServiceError.UserNotDeleted("Error al eliminar el usuario con id: " + id));
        }
    }

    /**
     * Elimina una tarjeta de credito existente.
     * @author Alvaro Herrero, Javier Ruiz, Javier Hernandez, Raul Fernandez, Yahya El Hadri, Samuel Cortes.
     * @since 1.0
     * @param id El ID de la tarjeta de credito a eliminar.
     * @return Either que contiene la tarjeta de crédito eliminada o un error si no se pudo eliminar.
     */

    @Override
    public Either<ServiceError, TarjetaCredito> deleteTarjeta(UUID id) {
        try {
            getTarjetaById(id).bimap(
                    error -> {
                        logger.error("Error al obtener la tarjeta con id: {}", id, error);
                        return new ServiceError.TarjetasLoadError("Error al obtener la tarjeta con id: " + id);
                    },
                    tarjeta -> {
                        creditCardRepository.delete(id);
                        cacheTarjeta.remove(id);
                        return Either.right(tarjeta);
                    }
            );
            return Either.left(new ServiceError.TarjeteNotDeleted("Error al eliminar la tarjeta con id: " + id));
        }catch (Exception e){
            logger.error("Error al eliminar la tarjeta con id: {}", id, e);
            return Either.left(new ServiceError.TarjeteNotDeleted("Error al eliminar la tarjeta con id: " + id));
        }
    }


    /**
     * @author Alvaro Herrero, Javier Ruiz, Javier Hernandez, Raul Fernandez, Yahya El Hadri, Samuel Cortes.
     * @since 1.0
     * Carga los datos remotos en la cache local.
     */

    @Override
    public void loadData() {
        logger.debug("Cargando datos remotos");
        usersRepository.deleteAllUsers();
        creditCardLocalRepository.deleteAllCreditCards();
        userRemoteRepository.getAll().forEach(usersRepository::saveUser);
        creditCardRepository.getAll().forEach(creditCardRepository::create);
    }

}
