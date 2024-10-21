package org.example.service;

import io.vavr.control.Either;
import org.example.client.repository.creditcard.CreditCardLocalRepository;
import org.example.client.repository.user.UsersRepository;
import org.example.client.storage.StorageJsonClient;
import org.example.creditcard.errors.TarjetaErrors;
import org.example.creditcard.storage.StorageCsvCredCard;
import org.example.service.errors.ServiceError;
import org.example.creditcard.cache.CacheTarjetaImpl;
import org.example.creditcard.dto.TarjetaCreditoDto;
import org.example.creditcard.repositories.CreditCardRepository;
import org.example.creditcard.validator.TarjetaValidator;
import org.example.models.Cliente;
import org.example.models.TarjetaCredito;
import org.example.models.Usuario;
import org.example.notification.Notification;
import org.example.notification.TarjetaNotificacion;
import org.example.notification.UserNotifications;
import org.example.users.cache.CacheUsuario;
import org.example.users.dto.UsuarioDto;
import org.example.users.errors.UserErrors;
import org.example.users.repository.UserRemoteRepository;
import org.example.users.storage.StorageCsvUser;
import org.example.users.validator.UserValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.scheduler.Schedulers;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

import static io.vavr.control.Either.right;

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
    private final UserNotifications userNotifications;
    private final TarjetaNotificacion tarjetaNotificacion;
    private final StorageJsonClient storageJsonClient;
    private final StorageCsvCredCard storageCsvCredCard;
    private final StorageCsvUser storageCsvUser;
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

    public ClienteServiceImpl(UsersRepository usersRepository, CreditCardLocalRepository creditCardLocalRepository, CreditCardRepository creditCardRepository, CacheUsuario cacheUsuario, CacheTarjetaImpl cacheTarjeta, UserValidator userValidator, UserRemoteRepository userRemoteRepository, TarjetaValidator tarjetaValidator, UserNotifications userNotifications, TarjetaNotificacion tarjetaNotificacion, StorageJsonClient storageJsonClient, StorageCsvCredCard storageCsvCredCard, StorageCsvUser storageCsvUser) {
        this.usersRepository = usersRepository;
        this.creditCardLocalRepository = creditCardLocalRepository;
        this.creditCardRepository = creditCardRepository;
        this.cacheUsuario = cacheUsuario;
        this.cacheTarjeta = cacheTarjeta;
        this.userValidator = userValidator;
        this.userRemoteRepository = userRemoteRepository;
        this.tarjetaValidator = tarjetaValidator;
        this.userNotifications = userNotifications;
        this.tarjetaNotificacion = tarjetaNotificacion;
        this.storageJsonClient = storageJsonClient;
        this.storageCsvCredCard = storageCsvCredCard;
        this.storageCsvUser = storageCsvUser;
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

            return right(clientes);
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
                return right(userRemoteRepository.getAll());
            }
            return right(usersRepository.findAllUsers());
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
                return right(creditCardRepository.getAll());
            }
            return right(creditCardLocalRepository.findAllCreditCards());
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
            return right(clienteDef);
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
            return right(usuario.get());
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
            return right(tarjeta.get());
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

            return right(clientes);
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
            return right(usuarios.get());
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
        try {
            Either<UserErrors, Usuario> validationResult = userValidator.ValidateUser(cliente.getUsuario());
            if (validationResult.isLeft()) {
                UserErrors error = validationResult.getLeft();
                logger.error("Error al validar el usuario", error);
                Notification<UsuarioDto> notificacionErrorUsuario = new Notification<>(
                        Notification.Type.CREATE,
                        new UsuarioDto(cliente.getUsuario()),
                        "Error al validar el usuario: " + cliente.getUsuario().getId() + error.getMessage()
                );
                userNotifications.send(notificacionErrorUsuario);
                return Either.left(new ServiceError.UservalidatorError("Error al validar el usuario"));
            }

            Usuario usuarioCreado = userRemoteRepository.createUser(cliente.getUsuario());
            cacheUsuario.put(usuarioCreado.getId(), usuarioCreado);
            Notification<UsuarioDto> notificacionUsuarioCreado = new Notification<>(
                    Notification.Type.CREATE,
                    new UsuarioDto(usuarioCreado),
                    "Usuario creado con éxito " + cliente.getUsuario().getId()
            );
            userNotifications.send(notificacionUsuarioCreado);

            for (TarjetaCredito tarjeta : cliente.getTarjetas()) {
                tarjeta.setClientID(usuarioCreado.getId());
                Either<TarjetaErrors, TarjetaCredito> tarjetaValidationResult = tarjetaValidator.validarTarjetaCredito(tarjeta);
                if (tarjetaValidationResult.isLeft()) {
                    TarjetaErrors tarjetaError = tarjetaValidationResult.getLeft();
                    logger.error("Error al validar la tarjeta", tarjetaError);
                    Notification<TarjetaCreditoDto> notificacionErrorTarjeta = new Notification<>(
                            Notification.Type.CREATE,
                            new TarjetaCreditoDto(tarjeta),
                            "Error al validar la tarjeta de crédito: " + tarjeta.getNumero() + tarjetaError.getMessage()
                    );
                    tarjetaNotificacion.send(notificacionErrorTarjeta);
                    return Either.left(new ServiceError.tarjetaCreditValidatorError("Error al validar la tarjeta"));
                }

                TarjetaCredito tarjetaCredito = tarjetaValidationResult.get();
                creditCardRepository.create(tarjetaCredito);
                cacheTarjeta.put(tarjetaCredito.getId(), tarjetaCredito);
                Notification<TarjetaCreditoDto> notificacionTarjetaCreada = new Notification<>(
                        Notification.Type.CREATE,
                        new TarjetaCreditoDto(tarjetaCredito),
                        "Tarjeta de crédito creada con éxito " + tarjeta.getNumero()
                );
                tarjetaNotificacion.send(notificacionTarjetaCreada);
            }

            return Either.right(new Cliente(usuarioCreado, cliente.getTarjetas()));
        } catch (Exception e) {
            logger.error("Error al crear el cliente", e);
            Notification<UsuarioDto> notificacionErrorGeneral = new Notification<>(
                    Notification.Type.CREATE,
                    new UsuarioDto(cliente.getUsuario()),
                    "Error al crear el cliente: " + cliente.getUsuario().getId() + e.getMessage()
            );
            userNotifications.send(notificacionErrorGeneral);
            return Either.left(new ServiceError.TarjetasLoadError("Error al crear el cliente " + cliente));
        }
    }


    /**
     * Crea un nuevo usuario.
     *
     * @param usuario El usuario a crear.
     * @return Either que contiene el usuario creado o un error si no se pudo crear.
     * @author Alvaro Herrero, Javier Ruiz, Javier Hernandez, Raul Fernandez, Yahya El Hadri, Samuel Cortes.
     * @since 1.0
     */

    @Override
    public Either<ServiceError, Usuario> createUser(Usuario usuario) {
        try {
            Either<UserErrors, Usuario> validationResult = userValidator.ValidateUser(usuario);
            if (validationResult.isLeft()) {
                UserErrors error = validationResult.getLeft();
                logger.error("Error al validar el usuario", error);
                Notification<UsuarioDto> notificacionError = new Notification<>(
                        Notification.Type.CREATE,
                        new UsuarioDto(usuario),
                        "Error al validar el usuario: " + usuario.getId() + error.getMessage()
                );
                userNotifications.send(notificacionError);
                return Either.left(new ServiceError.UservalidatorError("Error al validar el usuario"));
            }

            Usuario usuarioCreado = userRemoteRepository.createUser(usuario);
            cacheUsuario.put(usuarioCreado.getId(), usuarioCreado);
            Notification<UsuarioDto> notificacionUsuarioCreado = new Notification<>(
                    Notification.Type.CREATE,
                    new UsuarioDto(usuarioCreado),
                    "Usuario creado con éxito " + usuario.getId()
            );
            userNotifications.send(notificacionUsuarioCreado);

            return Either.right(usuarioCreado);

        } catch (Exception e) {
            logger.error("Error al crear el usuario", e);
            Notification<UsuarioDto> notificacionErrorGeneral = new Notification<>(
                    Notification.Type.CREATE,
                    new UsuarioDto(usuario),
                    "Error al crear el usuario: " + usuario.getId() + e.getMessage()
            );
            userNotifications.send(notificacionErrorGeneral);
            return Either.left(new ServiceError.UserNotCreated("Error al crear el usuario " + usuario));
        }
    }


    /**
     * Crea una nueva tarjeta de credito.
     *
     * @param tarjetaCredito La tarjeta de credito a crear.
     * @return Either que contiene la tarjeta de credito creada o un error si no se pudo crear.
     * @author Alvaro Herrero, Javier Ruiz, Javier Hernandez, Raul Fernandez, Yahya El Hadri, Samuel Cortes.
     * @since 1.0
     */

    @Override
    public Either<ServiceError, TarjetaCredito> createTarjeta(TarjetaCredito tarjetaCredito) {
        try {
            Either<TarjetaErrors, TarjetaCredito> validationResult = tarjetaValidator.validarTarjetaCredito(tarjetaCredito);
            if (validationResult.isLeft()) {
                TarjetaErrors error = validationResult.getLeft();
                logger.error("Error al validar la tarjeta", error);
                Notification<TarjetaCreditoDto> notificacionErrorTarjeta = new Notification<>(
                        Notification.Type.CREATE,
                        new TarjetaCreditoDto(tarjetaCredito),
                        "Error al validar la tarjeta de crédito: " + tarjetaCredito.getNumero() + error.getMessage()
                );
                tarjetaNotificacion.send(notificacionErrorTarjeta);
                return Either.left(new ServiceError.tarjetaCreditValidatorError("Error al validar la tarjeta"));
            }

            TarjetaCredito tarjetaCreada = creditCardRepository.create(tarjetaCredito);
            cacheTarjeta.put(tarjetaCreada.getId(), tarjetaCreada);
            Notification<TarjetaCreditoDto> notificacionTarjetaCreada = new Notification<>(
                    Notification.Type.CREATE,
                    new TarjetaCreditoDto(tarjetaCredito),
                    "Tarjeta de crédito creada con éxito " + tarjetaCredito.getNumero()
            );
            tarjetaNotificacion.send(notificacionTarjetaCreada);

            return Either.right(tarjetaCreada);

        } catch (Exception e) {
            logger.error("Error al crear la tarjeta", e);
            Notification<TarjetaCreditoDto> notificacionErrorTarjeta = new Notification<>(
                    Notification.Type.CREATE,
                    new TarjetaCreditoDto(tarjetaCredito),
                    "Error al crear la tarjeta " + tarjetaCredito.getNumero() + e.getMessage()
            );
            tarjetaNotificacion.send(notificacionErrorTarjeta);
            return Either.left(new ServiceError.TarjeteNotCreated("Error al crear la tarjeta " + tarjetaCredito));
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
            return getClienteById(id).flatMap(
                    user -> {
                        Either<UserErrors, Usuario> validationResult = userValidator.ValidateUser(cliente.getUsuario());
                        if (validationResult.isLeft()) {
                            UserErrors validationError = validationResult.getLeft();
                            logger.error("Error al validar el usuario", validationError);
                            Notification<UsuarioDto> notificacionErrorValidar = new Notification<>(
                                    Notification.Type.UPDATE,
                                    new UsuarioDto(cliente.getUsuario()),
                                    "Error al validar el usuario: " + user.getId() + validationError.getMessage()
                            );
                            userNotifications.send(notificacionErrorValidar);
                            return Either.left(new ServiceError.UservalidatorError("Error al validar el usuario"));
                        }

                        cacheUsuario.remove(id);
                        Usuario usuarioActualizado = userRemoteRepository.updateUser(id, cliente.getUsuario());
                        cacheUsuario.put(usuarioActualizado.getId(), usuarioActualizado);
                        cliente.getTarjetas().forEach(tarjetaCredito -> cacheTarjeta.remove(tarjetaCredito.getId()));

                        for (TarjetaCredito tarjeta : cliente.getTarjetas()) {
                            tarjeta.setClientID(usuarioActualizado.getId());
                            Either<TarjetaErrors, TarjetaCredito> tarjetaValidationResult = tarjetaValidator.validarTarjetaCredito(tarjeta);
                            if (tarjetaValidationResult.isLeft()) {
                                TarjetaErrors tarjetaError = tarjetaValidationResult.getLeft();
                                logger.error("Error al validar la tarjeta", tarjetaError);
                                Notification<TarjetaCreditoDto> notificacionErrorValidar = new Notification<>(
                                        Notification.Type.UPDATE,
                                        new TarjetaCreditoDto(tarjeta),
                                        "Error al validar tarjeta: " + tarjeta.getNumero() + tarjetaError.getMessage()
                                );
                                tarjetaNotificacion.send(notificacionErrorValidar);
                                return Either.left(new ServiceError.tarjetaCreditValidatorError("Error al validar la tarjeta"));
                            }

                            TarjetaCredito tarjetaCredito = tarjetaValidationResult.get();
                            creditCardRepository.update(tarjetaCredito.getId(), tarjetaCredito);
                            cacheTarjeta.put(tarjetaCredito.getId(), tarjetaCredito);
                        }

                        Notification<UsuarioDto> notificacionUsuarioActualizado = new Notification<>(
                                Notification.Type.UPDATE,
                                new UsuarioDto(usuarioActualizado),
                                "Usuario actualizado con éxito " + usuarioActualizado.getId()
                        );
                        userNotifications.send(notificacionUsuarioActualizado);

                        return Either.right(new Cliente(usuarioActualizado, cliente.getTarjetas()));
                    }
            ).mapLeft(
                    error -> {
                        logger.error("Error al obtener el cliente con id: {}", id, error);
                        Notification<UsuarioDto> notificacionErrorObtener = new Notification<>(
                                Notification.Type.UPDATE,
                                null,
                                "Error al obtener el cliente con id: " + id
                        );
                        userNotifications.send(notificacionErrorObtener);
                        return new ServiceError.UserNotFound("Error al obtener el cliente con id: " + id);
                    }
            );
        } catch (Exception e) {
            logger.error("Error al actualizar el cliente con id: {}", id, e);
            Notification<UsuarioDto> notificacionErrorGeneral = new Notification<>(
                    Notification.Type.UPDATE,
                    null,
                    "Error al actualizar el cliente con id: " + id + ". Error: " + e.getMessage()
            );
            userNotifications.send(notificacionErrorGeneral);
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
            return getUserById(id).flatMap(
                    user -> {
                        Either<UserErrors, Usuario> validationResult = userValidator.ValidateUser(usuario);
                        if (validationResult.isLeft()) {
                            UserErrors validationError = validationResult.getLeft();
                            logger.error("Error al validar el usuario", validationError);
                            Notification<UsuarioDto> notificacionErrorValidar = new Notification<>(
                                    Notification.Type.UPDATE,
                                    new UsuarioDto(usuario),
                                    "Error al validar el usuario: " + user.getId() + validationError.getMessage()
                            );
                            userNotifications.send(notificacionErrorValidar);
                            return Either.left(new ServiceError.UservalidatorError("Error al validar el usuario"));
                        }

                        cacheUsuario.remove(id);
                        Usuario usuarioActualizado = userRemoteRepository.updateUser(id, usuario);
                        cacheUsuario.put(usuarioActualizado.getId(), usuarioActualizado);
                        Notification<UsuarioDto> notificacionUsuarioActualizado = new Notification<>(
                                Notification.Type.UPDATE,
                                new UsuarioDto(usuarioActualizado),
                                "Usuario actualizado con éxito " + usuarioActualizado.getId()
                        );
                        userNotifications.send(notificacionUsuarioActualizado);
                        return Either.right(usuarioActualizado);
                    }
            ).mapLeft(
                    error -> {
                        logger.error("Error al obtener el usuario con id: {}", id, error);
                        Notification<UsuarioDto> notificacionErrorObtener = new Notification<>(
                                Notification.Type.UPDATE,
                                null,
                                "Error al obtener el usuario con id: " + id
                        );
                        userNotifications.send(notificacionErrorObtener);
                        return new ServiceError.UserNotFound("Error al obtener el usuario con id: " + id);
                    }
            );
        } catch (Exception e) {
            logger.error("Error al actualizar el usuario con id: {}", id, e);
            Notification<UsuarioDto> notificacionErrorGeneral = new Notification<>(
                    Notification.Type.UPDATE,
                    null,
                    "Error al actualizar el usuario con id: " + id + ". Error: " + e.getMessage()
            );
            userNotifications.send(notificacionErrorGeneral);
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
            return getTarjetaById(id).flatMap(
                    tarjeta -> {
                        Either<TarjetaErrors, TarjetaCredito> validationResult = tarjetaValidator.validarTarjetaCredito(tarjetaCredito);
                        if (validationResult.isLeft()) {
                            TarjetaErrors validationError = validationResult.getLeft();
                            logger.error("Error al validar la tarjeta", validationError);
                            Notification<TarjetaCreditoDto> notificacionErrorValidar = new Notification<>(
                                    Notification.Type.UPDATE,
                                    new TarjetaCreditoDto(tarjeta),
                                    "Error al validar tarjeta: " + tarjetaCredito.getNumero() + validationError.getMessage()
                            );
                            tarjetaNotificacion.send(notificacionErrorValidar);
                            return Either.left(new ServiceError.tarjetaCreditValidatorError("Error al validar la tarjeta"));
                        }

                        cacheTarjeta.remove(id);
                        TarjetaCredito tarjetaActualizada = creditCardRepository.update(id, tarjetaCredito);
                        cacheTarjeta.put(tarjetaActualizada.getId(), tarjetaActualizada);
                        Notification<TarjetaCreditoDto> notificacionActualizado = new Notification<>(
                                Notification.Type.UPDATE,
                                new TarjetaCreditoDto(tarjetaActualizada),
                                "Tarjeta actualizada: " + tarjetaActualizada.getNumero()
                        );
                        tarjetaNotificacion.send(notificacionActualizado);
                        return Either.right(tarjetaActualizada);
                    }
            ).mapLeft(
                    error -> {
                        logger.error("Error al obtener la tarjeta con id: {}", id, error);
                        Notification<TarjetaCreditoDto> notificacionErrorEncontrar = new Notification<>(
                                Notification.Type.UPDATE,
                                null,
                                "Error al obtener tarjeta: " + tarjetaCredito.getNumero() + error.getMessage()
                        );
                        tarjetaNotificacion.send(notificacionErrorEncontrar);
                        return new ServiceError.TarjetasLoadError("Error al obtener la tarjeta con id: " + id);
                    }
            );
        } catch (Exception e) {
            logger.error("Error al actualizar la tarjeta con id: {}", id, e);
            Notification<TarjetaCreditoDto> notificacionErrorUpdate = new Notification<>(
                    Notification.Type.UPDATE,
                    null,
                    "Error al actualizar la tarjeta con id: " + id + ". Error: " + e.getMessage()
            );
            tarjetaNotificacion.send(notificacionErrorUpdate);
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
            return getClienteById(id).flatMap(
                    cliente -> {
                        try {
                            userRemoteRepository.deleteById(id);
                            cacheUsuario.remove(id);

                            cliente.getTarjetas().forEach(tarjeta -> {
                                creditCardRepository.delete(tarjeta.getId());
                                cacheTarjeta.remove(tarjeta.getId());
                                Notification<TarjetaCreditoDto> notificacionTarjetaEliminada = new Notification<>(
                                        Notification.Type.DELETE,
                                        new TarjetaCreditoDto(tarjeta),
                                        "Tarjeta eliminada con éxito " + tarjeta.getNumero()
                                );
                                tarjetaNotificacion.send(notificacionTarjetaEliminada);
                            });

                            Notification<UsuarioDto> notificacionClienteEliminado = new Notification<>(
                                    Notification.Type.DELETE,
                                    new UsuarioDto(cliente.getUsuario()),
                                    "Cliente eliminado con éxito " + cliente.getUsuario().getId()
                            );
                            userNotifications.send(notificacionClienteEliminado);
                            return Either.right(cliente);
                        } catch (Exception e) {
                            logger.error("Error al eliminar las tarjetas del cliente con id: {}", id, e);
                            return Either.left(new ServiceError.UserNotDeleted("Error al eliminar las tarjetas del cliente con id: " + id));
                        }
                    }
            ).mapLeft(
                    error -> {
                        logger.error("Error al obtener el cliente con id: {}", id, error);
                        Notification<UsuarioDto> notificacionErrorObtener = new Notification<>(
                                Notification.Type.DELETE,
                                null,
                                "Error al obtener el cliente con id: " + id
                        );
                        userNotifications.send(notificacionErrorObtener);
                        return new ServiceError.UserNotFound("Error al obtener el cliente con id: " + id);
                    }
            );
        } catch (Exception e) {
            logger.error("Error al eliminar el cliente con id: {}", id, e);
            Notification<UsuarioDto> notificacionErrorGeneral = new Notification<>(
                    Notification.Type.DELETE,
                    null,
                    "Error al eliminar el cliente con id: " + id + ". Error: " + e.getMessage()
            );
            userNotifications.send(notificacionErrorGeneral);
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
            return getUserById(id).flatMap(
                    user -> {
                        try {
                            userRemoteRepository.deleteById(id);
                            cacheUsuario.remove(id);

                            Notification<UsuarioDto> notificacionUsuarioEliminado = new Notification<>(
                                    Notification.Type.DELETE,
                                    new UsuarioDto(user),
                                    "Usuario eliminado con éxito " + user.getId()
                            );
                            userNotifications.send(notificacionUsuarioEliminado);

                            return Either.right(user);
                        } catch (Exception e) {
                            logger.error("Error al eliminar el usuario con id: {}", id, e);
                            return Either.left(new ServiceError.UserNotDeleted("Error al eliminar el usuario con id: " + id));
                        }
                    }
            ).mapLeft(
                    error -> {
                        logger.error("Error al obtener el usuario con id: {}", id, error);

                        Notification<UsuarioDto> notificacionErrorObtener = new Notification<>(
                                Notification.Type.DELETE,
                                null,
                                "Error al obtener el usuario con id: " + id
                        );
                        userNotifications.send(notificacionErrorObtener);

                        return new ServiceError.UserNotFound("Error al obtener el usuario con id: " + id);
                    }
            );
        } catch (Exception e) {
            logger.error("Error al eliminar el usuario con id: {}", id, e);

            Notification<UsuarioDto> notificacionErrorGeneral = new Notification<>(
                    Notification.Type.DELETE,
                    null,
                    "Error al eliminar el usuario con id: " + id + ". Error: " + e.getMessage()
            );
            userNotifications.send(notificacionErrorGeneral);

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
            return getTarjetaById(id).flatMap(
                    tarjeta -> {
                        try {
                            creditCardRepository.delete(id);
                            cacheTarjeta.remove(id);
                            Notification<TarjetaCreditoDto> notificacionTarjetaEliminado = new Notification<>(
                                    Notification.Type.DELETE,
                                    new TarjetaCreditoDto(tarjeta),
                                    "Tarjeta eliminada con éxito " + tarjeta.getNumero()
                            );
                            tarjetaNotificacion.send(notificacionTarjetaEliminado);
                            return Either.right(tarjeta);
                        } catch (Exception e) {
                            logger.error("Error al eliminar la tarjeta con id: {}", id, e);
                            return Either.left(new ServiceError.TarjeteNotDeleted("Error al eliminar la tarjeta con id: " + id));
                        }
                    }
            ).mapLeft(
                    error -> {
                        logger.error("Error al obtener la tarjeta con id: {}", id, error);
                        Notification<TarjetaCreditoDto> notificacionErrorObtener = new Notification<>(
                                Notification.Type.DELETE,
                                null,
                                "Error al obtener la tarjeta con id: " + id
                        );
                        tarjetaNotificacion.send(notificacionErrorObtener);
                        return new ServiceError.TarjetasLoadError("Error al obtener la tarjeta con id: " + id);
                    }
            );
        } catch (Exception e) {
            logger.error("Error al eliminar la tarjeta con id: {}", id, e);
            Notification<TarjetaCreditoDto> notificacionErrorGeneral = new Notification<>(
                    Notification.Type.DELETE,
                    null,
                    "Error al eliminar la tarjeta con id: " + id + ". Error: " + e.getMessage()
            );
            tarjetaNotificacion.send(notificacionErrorGeneral);
            return Either.left(new ServiceError.TarjeteNotDeleted("Error al eliminar la tarjeta con id: " + id));
        }
    }


    /**
     * @author Alvaro Herrero, Javier Ruiz, Javier Hernandez, Raul Fernandez, Yahya El Hadri, Samuel Cortes.
     * @since 1.0
     * Carga los datos remotos en la cache local.
     */

    @Override
    public Either<ServiceError, List<Cliente>> loadClientesJson(File file) {
        try {
            List<Cliente> clientes = (List<Cliente>) storageJsonClient.importList(file).collectList().block();
            return right(clientes);
        }catch (Exception e){
            logger.error("Error al cargar los clientes", e);
            return Either.left(new ServiceError.ClienteLoadErrors("Error al cargar los clientes"));
        }
    }

    @Override
    public Either<ServiceError, Void> saveClientesJson(List<Cliente> clientes, File file) {
        try {
            storageJsonClient.exportList(clientes, file );
            return right(null);
        }catch (Exception e){
            logger.error("Error al guardar los clientes", e);
            return Either.left(new ServiceError.ImportErrors("Error al guardar los clientes"));
        }
    }

    @Override
    public Either<ServiceError, List<Usuario>> loadUsersCsv(File file) {
        try {
            List<Usuario> usuarios = (List<Usuario>) storageCsvUser.importList(file);
            return right(usuarios);
        }catch (Exception e){
            logger.error("Error al cargar los usuarios", e);
            return Either.left(new ServiceError.ImportErrors("Error al cargar los usuarios"));
        }
    }

    @Override
    public Either<ServiceError, Void> saveUsersCsv(List<Usuario> usuarios, File file) {
        try {
            storageCsvUser.exportList(usuarios, file);
            return right(null);
        }catch (Exception e){
            logger.error("Error al guardar los usuarios", e);
            return Either.left(new ServiceError.ImportErrors("Error al guardar los usuarios"));
        }
    }

    @Override
    public Either<ServiceError, List<TarjetaCredito>>loadTarjetasCsv(File file) {
        try {
            List<TarjetaCredito> tarjetas = storageCsvCredCard.importList(file).subscribeOn(Schedulers.boundedElastic()).collectList().block();
            return  right(tarjetas);
        }catch (Exception e){
            logger.error("Error al cargar las tarjetas", e);
            return Either.left(new ServiceError.ImportErrors("Error al cargar las tarjetas"));
        }
    }

    @Override
    public Either<ServiceError, Void> saveTarjetasCsv(List<TarjetaCredito> tarjetasCredito, File file) {
        try {
            storageCsvCredCard.exportList(tarjetasCredito, file);
            return right(null);
        }catch (Exception e){
            logger.error("Error al guardar las tarjetas", e);
            return Either.left(new ServiceError.ImportErrors("Error al guardar las tarjetas"));
        }
    }

    @Override
    public void loadData() {
        try {
            logger.debug("Cargando datos remotos");
            usersRepository.deleteAllUsers();
            creditCardLocalRepository.deleteAllCreditCards();
            userRemoteRepository.getAll().forEach(usersRepository::saveUser);
            creditCardRepository.getAll().forEach(creditCardRepository::create);

            Notification<UsuarioDto> notificacionErrorCarga = new Notification<>(
                    Notification.Type.REFRESH,
                    null,
                    "Datos remotos cargados correctamente"
            );
            userNotifications.send(notificacionErrorCarga);
        }catch (Exception e){
            Notification<UsuarioDto> notificacionErrorCarga = new Notification<>(
                    Notification.Type.REFRESH,
                    null, 
                    "Error al cargar los datos remotos: " + e.getMessage()
            );
            userNotifications.send(notificacionErrorCarga);
        }

    }

}
