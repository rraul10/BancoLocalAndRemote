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
            Optional<Usuario> usuario = null;
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
            Optional<List<TarjetaCredito>> tarjetas = Optional.empty();

        }
    }

    @Override
    public Either<ServiceError, List<Cliente>> getClienteByName(String nombre) {
        return null;
    }

    @Override
    public Either<ServiceError, Cliente> createCliente(Cliente cliente) {
        return null;
    }

    @Override
    public Either<ServiceError, Cliente> updateCliente(Long id, Cliente cliente) {
        return null;
    }

    @Override
    public Either<ServiceError, Cliente> deleteCliente(Long id) {
        return null;
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
