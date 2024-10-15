package org.example.client.service;

import io.vavr.control.Either;
import org.apache.ibatis.jdbc.Null;
import org.example.client.repository.creditcard.CreditCardLocalRepository;
import org.example.client.repository.user.UsersRepository;
import org.example.client.service.errors.ServiceError;
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
    private final UserNotifications userNotifications;
    private final TarjetaNotificacion tarjetaNotificacion;

    public ClienteServiceImpl(
            UsersRepository usersRepository,
            CreditCardLocalRepository creditCardLocalRepository,
            CreditCardRepository creditCardRepository,
            CacheUsuario cacheUsuario,
            CacheTarjetaImpl cacheTarjeta,
            UserValidator userValidator,
            UserRemoteRepository userRemoteRepository,
            TarjetaValidator tarjetaValidator,
            UserNotifications userNotifications,
            TarjetaNotificacion tarjetaNotificacion
            ) {
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
                            tarjetasPorUsuario.getOrDefault(usuario.getId(), new ArrayList<>())  // Obtener las tarjetas o una lista vac�a
                    ))
                    .collect(Collectors.toList());

            return Either.right(clientes);
        }catch(Exception e){
            logger.error("Error al obtener los clientes", e);
            return Either.left(new ServiceError.ClienteLoadErrors("Error al obtener los clientes"));
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
            return Either.left(new ServiceError.TarjetasLoadError("Error al obtener los usuarios"));
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
                return Either.left(new ServiceError.TarjetasLoadError("Tarjeta no encontrada con id: " + id));
            }
            return Either.right(tarjeta.get());
        }catch (Exception e){
            logger.error("Error al obtener la tarjeta con id: {}", id, e);
            return Either.left(new ServiceError.TarjetasLoadError("Error al obtener la tarjeta con id: " + id));
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

                        Notification<UsuarioDto> notificacionErrorUsuario = new Notification<>(
                                Notification.Type.CREATE,
                                new UsuarioDto(cliente.getUsuario()),
                                "Error al validar el usuario: " + cliente.usuario.getId() + error.getMessage()
                        );
                        userNotifications.send(notificacionErrorUsuario);
                        return new ServiceError.tarjetaCreditValidatorError("Error al validar el usuario");
                    },
                    usuario -> {
                        Usuario usuarioCreado = userRemoteRepository.createUser(cliente.getUsuario());
                        cacheUsuario.put(usuarioCreado.getId(), usuarioCreado);
                        Notification<UsuarioDto> notificacionUsuarioCreado = new Notification<>(
                                Notification.Type.CREATE,
                                new UsuarioDto(usuarioCreado),
                                "Usuario creado con éxito"+ cliente.usuario.getId()
                        );
                        userNotifications.send(notificacionUsuarioCreado);
                        cliente.getTarjetas().forEach(tarjeta -> {
                            tarjeta.setClientID(usuarioCreado.getId());
                            tarjetaValidator.validarTarjetaCredito(tarjeta).bimap(
                                    error -> {
                                        logger.error("Error al validar la tarjeta", error);
                                        Notification<TarjetaCreditoDto> notificacionErrorTarjeta = new Notification<>(
                                                Notification.Type.CREATE,
                                                new TarjetaCreditoDto(tarjeta),
                                                "Error al validar la tarjeta de crédito: " + tarjeta.getNumero() + error.getMessage()
                                        );
                                        tarjetaNotificacion.send(notificacionErrorTarjeta);
                                        return new ServiceError.tarjetaCreditValidatorError("Error al validar la tarjeta");
                                    },
                                    tarjetaCredito -> {
                                        creditCardRepository.create(tarjetaCredito);
                                        cacheTarjeta.put(tarjetaCredito.getId(), tarjetaCredito);
                                        Notification<TarjetaCreditoDto> notificacionTarjetaCreada = new Notification<>(
                                                Notification.Type.CREATE,
                                                new TarjetaCreditoDto(tarjetaCredito),
                                                "Tarjeta de crédito creada con éxito" + tarjeta.getNumero()
                                        );
                                        tarjetaNotificacion.send(notificacionTarjetaCreada);
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
            Notification<UsuarioDto> notificacionErrorGeneral = new Notification<>(
                    Notification.Type.CREATE,
                    new UsuarioDto(cliente.getUsuario()),
                    "Error al crear el cliente: "+ cliente.usuario.getId() + e.getMessage()
            );
            userNotifications.send(notificacionErrorGeneral);
            return Either.left(new ServiceError.TarjetasLoadError("Error al crear el cliente" + cliente));
        }
    }

    @Override
    public Either<ServiceError, Usuario> createUser(Usuario usuario) {
        try {
            userValidator.ValidateUser(usuario).bimap(
                    error -> {
                        logger.error("Error al validar el usuario", error);
                        Notification<UsuarioDto> notificacionError = new Notification<>(
                                Notification.Type.CREATE,
                                new UsuarioDto(usuario),
                                "Error al validar el usuario: " + usuario.getId() + error.getMessage()
                        );
                        userNotifications.send(notificacionError);
                        return new ServiceError.UservalidatorError("Error al validar el usuario");
                    },
                    user -> {
                        Usuario usuarioCreado = userRemoteRepository.createUser(usuario);
                        cacheUsuario.put(usuarioCreado.getId(), usuarioCreado);
                        Notification<UsuarioDto> notificacionUsuarioCreado = new Notification<>(
                                Notification.Type.CREATE,
                                new UsuarioDto(usuarioCreado),
                                "Usuario creado con éxito" + usuario.getId()
                        );
                        userNotifications.send(notificacionUsuarioCreado);
                        return Either.right(usuarioCreado);
                    }
            );
            return Either.right(usuario);
        }catch (Exception e){
            logger.error("Error al crear el usuario", e);
            Notification<UsuarioDto> notificacionErrorGeneral = new Notification<>(
                    Notification.Type.CREATE,
                    new UsuarioDto(usuario),
                    "Error al crear el usuario: " + usuario.getId() + e.getMessage()
            );
            userNotifications.send(notificacionErrorGeneral);
            return Either.left(new ServiceError.UserNotCreated("Error al crear el usuario" + usuario));
        }
    }

    @Override
    public Either<ServiceError, TarjetaCredito> createTarjeta(TarjetaCredito tarjetaCredito) {
        try {
            tarjetaValidator.validarTarjetaCredito(tarjetaCredito).bimap(
                    error -> {
                        logger.error("Error al validar la tarjeta", error);
                        Notification<TarjetaCreditoDto> notificacionErrorTarjeta = new Notification<>(
                                Notification.Type.CREATE,
                                new TarjetaCreditoDto(tarjetaCredito),
                                "Error al validar la tarjeta de crédito: " + tarjetaCredito.getNumero() + error.getMessage()
                        );
                        tarjetaNotificacion.send(notificacionErrorTarjeta);
                        return new ServiceError.tarjetaCreditValidatorError("Error al validar la tarjeta");
                    },
                    tarjeta -> {
                        TarjetaCredito tarjetaCreada = creditCardRepository.create(tarjeta);
                        cacheTarjeta.put(tarjetaCreada.getId(), tarjetaCreada);
                        Notification<TarjetaCreditoDto> notificacionTarjetaCreada = new Notification<>(
                                Notification.Type.CREATE,
                                new TarjetaCreditoDto(tarjetaCredito),
                                "Tarjeta de crédito creada con éxito" + tarjetaCredito.getNumero()
                        );
                        tarjetaNotificacion.send(notificacionTarjetaCreada);
                        return Either.right(tarjetaCreada);
                    }
            );
            return Either.right(tarjetaCredito);
        }catch (Exception e){
            logger.error("Error al crear la tarjeta", e);
            Notification<TarjetaCreditoDto> notificacionErrorTarjeta = new Notification<>(
                    Notification.Type.CREATE,
                    new TarjetaCreditoDto(tarjetaCredito),
                    "Error al crear la tarjeta " + tarjetaCredito.getNumero() + e.getMessage()
            );
            tarjetaNotificacion.send(notificacionErrorTarjeta);
            return Either.left(new ServiceError.TarjeteNotDeleted("Error al crear la tarjeta" + tarjetaCredito));
        }
    }


    @Override
    public Either<ServiceError, Cliente> updateCliente(Long id, Cliente cliente) {
        try {
            getClienteById(id).bimap(
                    error -> {
                        logger.error("Error al obtener el cliente con id: {}", id, error);
                        Notification<UsuarioDto> notificacionErrorObtener = new Notification<>(
                                Notification.Type.UPDATE,
                               null,
                                "Error al obtener el cliente con id: " + id
                        );
                        userNotifications.send(notificacionErrorObtener);
                        return new ServiceError.UserNotFound("Error al obtener el cliente con id: " + id);
                    },
                    user -> {
                        userValidator.ValidateUser(cliente.getUsuario()).bimap(
                                error -> {
                                    logger.error("Error al validar el usuario", error);
                                    Notification<UsuarioDto> notificacionErrorValidar = new Notification<>(
                                            Notification.Type.UPDATE,
                                            new UsuarioDto(cliente.getUsuario()),
                                            "Error al validar el usuario: "+ user.usuario.getId() + error.getMessage()
                                    );
                                    userNotifications.send(notificacionErrorValidar);
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
                                                    Notification<TarjetaCreditoDto> notificacionErrorValidar = new Notification<>(
                                                            Notification.Type.UPDATE,
                                                            new TarjetaCreditoDto(tarjeta),
                                                            "Error al validar tarjeta: "+ tarjeta.getNumero() + error.getMessage()
                                                    );
                                                    tarjetaNotificacion.send(notificacionErrorValidar);
                                                    return new ServiceError.tarjetaCreditValidatorError("Error al validar la tarjeta");
                                                },
                                                tarjetaCredito -> {
                                                    creditCardRepository.update(tarjetaCredito.getId(), tarjetaCredito);
                                                    cacheTarjeta.put(tarjetaCredito.getId(), tarjetaCredito);
                                                    Notification<UsuarioDto> notificacionUsuarioActualizado = new Notification<>(
                                                            Notification.Type.UPDATE,
                                                            new UsuarioDto(usuarioActualizado),
                                                            "Usuario actualizado con éxito" + usuario.getId()
                                                    );
                                                    userNotifications.send(notificacionUsuarioActualizado);
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
            Notification<UsuarioDto> notificacionErrorGeneral = new Notification<>(
                    Notification.Type.UPDATE,
                    null,
                    "Error al actualizar el cliente con id: " + id + ". Error: " + e.getMessage()
            );
            userNotifications.send(notificacionErrorGeneral);
            return Either.left(new ServiceError.UserNotUpdated("Error al actualizar el cliente con id: " + id));
        }
    }

    @Override
    public Either<ServiceError, Usuario> updateUser(Long id, Usuario usuario) {
        try {
            getUserById(id).bimap(
                    error -> {
                        logger.error("Error al obtener el usuario con id: {}", id, error);
                        Notification<UsuarioDto> notificacionErrorObtener = new Notification<>(
                                Notification.Type.UPDATE,
                                null,
                                "Error al obtener el usuario con id: " + id
                        );
                        userNotifications.send(notificacionErrorObtener);
                        return new ServiceError.UserNotFound("Error al obtener el usuario con id: " + id);
                    },
                    user -> {
                        userValidator.ValidateUser(usuario).bimap(
                                error -> {
                                    logger.error("Error al validar el usuario", error);
                                    Notification<UsuarioDto> notificacionErrorValidar = new Notification<>(
                                            Notification.Type.UPDATE,
                                            new UsuarioDto(usuario),
                                            "Error al validar el usuario: " + user.getId() + error.getMessage()
                                    );
                                    userNotifications.send(notificacionErrorValidar);
                                    return new ServiceError.UservalidatorError("Error al validar el usuario");
                                },
                                usuarioValidado -> {
                                    cacheUsuario.remove(id);
                                    Usuario usuarioActualizado = userRemoteRepository.updateUser(id, usuario);
                                    cacheUsuario.put(usuarioActualizado.getId(), usuarioActualizado);
                                    Notification<UsuarioDto> notificacionUsuarioActualizado = new Notification<>(
                                            Notification.Type.UPDATE,
                                            new UsuarioDto(usuarioActualizado),
                                            "Usuario actualizado con éxito" + user.getId()
                                    );
                                    userNotifications.send(notificacionUsuarioActualizado);
                                    return Either.right(usuarioActualizado);
                                }
                        );
                        return Either.right(user);
                    }
            );
            return Either.right(usuario);
        }catch (Exception e){
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

    @Override
    public Either<ServiceError, TarjetaCredito> updateTarjeta(UUID id, TarjetaCredito tarjetaCredito) {
        try {
            getTarjetaById(id).bimap(
                    error -> {
                        logger.error("Error al obtener la tarjeta con id: {}", id, error);
                        Notification<TarjetaCreditoDto> notificacionErrorEncontrar = new Notification<>(
                                Notification.Type.UPDATE,
                                 null,
                                "Error al obtener tarjeta: " + tarjetaCredito.getNumero() + error.getMessage()
                        );
                        tarjetaNotificacion.send(notificacionErrorEncontrar);
                        return new ServiceError.TarjetasLoadError("Error al obtener la tarjeta con id: " + id);
                    },
                    tarjeta -> {
                        tarjetaValidator.validarTarjetaCredito(tarjetaCredito).bimap(
                                error -> {
                                    logger.error("Error al validar la tarjeta", error);
                                    Notification<TarjetaCreditoDto> notificacionErrorValidar = new Notification<>(
                                            Notification.Type.UPDATE,
                                            new TarjetaCreditoDto(tarjeta),
                                            "Error al validar tarjeta: " + tarjetaCredito.getNumero() + error.getMessage()
                                    );
                                    tarjetaNotificacion.send(notificacionErrorValidar);
                                    return new ServiceError.tarjetaCreditValidatorError("Error al validar la tarjeta");
                                },
                                tarjetaValidada -> {
                                    cacheTarjeta.remove(id);
                                    TarjetaCredito tarjetaActualizada = creditCardRepository.update(id, tarjetaCredito);
                                    cacheTarjeta.put(tarjetaActualizada.getId(), tarjetaActualizada);
                                    Notification<TarjetaCreditoDto> notificacionActualizado = new Notification<>(
                                            Notification.Type.UPDATE,
                                            new TarjetaCreditoDto(tarjetaValidada),
                                            "Tarjeta actualizada: " + tarjetaValidada.getNumero()
                                    );
                                    tarjetaNotificacion.send(notificacionActualizado);
                                    return Either.right(tarjetaActualizada);
                                }
                        );
                        return Either.right(tarjeta);
                    }
            );
            return Either.right(tarjetaCredito);
        }catch (Exception e){
            logger.error("Error al actualizar la tarjeta con id: {}", id, e);
            Notification<TarjetaCreditoDto> notificacionErrorUpdate = new Notification<>(
                    Notification.Type.UPDATE,
                   null,
                    "Error al actualizar la tarjeta con id: {}"+ id + e.getMessage()
            );
            tarjetaNotificacion.send(notificacionErrorUpdate);
            return Either.left(new ServiceError.TarjeteNotDeleted("Error al actualizar la tarjeta con id: " + id));
        }
    }

    @Override
    public Either<ServiceError, Cliente> deleteCliente(Long id) {
        try {
            getClienteById(id).bimap(
                    error -> {
                        logger.error("Error al obtener el cliente con id: {}", id, error);
                        Notification<UsuarioDto> notificacionErrorObtener = new Notification<>(
                                Notification.Type.DELETE,
                                null,
                                "Error al obtener el cliente con id: " + id
                        );
                        userNotifications.send(notificacionErrorObtener);
                        return new ServiceError.UserNotFound("Error al obtener el cliente con id: " + id);
                    },
                    user -> {
                        userRemoteRepository.deleteById(id);
                        cacheUsuario.remove(id);
                        user.getTarjetas().forEach(tarjeta -> {
                            creditCardRepository.delete(tarjeta.getId());
                            Notification<TarjetaCreditoDto> notificacionTarjetaEliminada = new Notification<>(
                                    Notification.Type.DELETE,
                                    new TarjetaCreditoDto(tarjeta),
                                    "Tarjeta eliminada con éxito" + tarjeta.getNumero()
                            );
                            tarjetaNotificacion.send(notificacionTarjetaEliminada);
                        });
                        cacheUsuario.remove(id);
                        user.getTarjetas().forEach(tarjeta -> {
                            cacheTarjeta.remove(tarjeta.getId());
                        });

                        Notification<UsuarioDto> notificacionClienteEliminado = new Notification<>(
                                Notification.Type.DELETE,
                                new UsuarioDto(user.getUsuario()),
                                "Cliente eliminado con éxito" + user.usuario.getId()
                        );
                        userNotifications.send(notificacionClienteEliminado);
                        return Either.right(user);
                    }
            );
            return Either.left(new ServiceError.UserNotDeleted("Error al eliminar el cliente con id: " + id));
        }catch (Exception e){
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

    @Override
    public Either<ServiceError, Usuario> deleteUser(Long id) {
        try {
            getUserById(id).bimap(
                    error -> {
                        logger.error("Error al obtener el usuario con id: {}", id, error);
                        Notification<UsuarioDto> notificacionErrorObtener = new Notification<>(
                                Notification.Type.DELETE,
                                null,
                                "Error al obtener el usuario con id: " + id
                        );
                        userNotifications.send(notificacionErrorObtener);
                        return new ServiceError.UserNotFound("Error al obtener el usuario con id: " + id);
                    },
                    user -> {
                        userRemoteRepository.deleteById(id);
                        cacheUsuario.remove(id);
                        Notification<UsuarioDto> notificacionUsuarioEliminado = new Notification<>(
                                Notification.Type.DELETE,
                                new UsuarioDto(user),
                                "Usuario eliminado con éxito" + user.getId()
                        );
                        userNotifications.send(notificacionUsuarioEliminado);
                        return Either.right(user);
                    }
            );
            return Either.left(new ServiceError.UserNotDeleted("Error al eliminar el usuario con id: " + id));
        }catch (Exception e){
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

    @Override
    public Either<ServiceError, TarjetaCredito> deleteTarjeta(UUID id) {
        try {
            getTarjetaById(id).bimap(
                    error -> {
                        logger.error("Error al obtener la tarjeta con id: {}", id, error);
                        Notification<TarjetaCreditoDto> notificacionErrorObtener = new Notification<>(
                                Notification.Type.DELETE,
                                null,
                                "Error al obtener la tarjeta con id: " + id
                        );
                        tarjetaNotificacion.send(notificacionErrorObtener);
                        return new ServiceError.TarjetasLoadError("Error al obtener la tarjeta con id: " + id);
                    },
                    tarjeta -> {
                        creditCardRepository.delete(id);
                        cacheTarjeta.remove(id);
                        Notification<TarjetaCreditoDto> notificacionTarjetaEliminado = new Notification<>(
                                Notification.Type.DELETE,
                                new TarjetaCreditoDto(tarjeta),
                                "Tarjeta eliminada con éxito" + tarjeta.getNumero()
                        );
                        tarjetaNotificacion.send(notificacionTarjetaEliminado);
                        return Either.right(tarjeta);
                    }
            );
            return Either.left(new ServiceError.TarjeteNotDeleted("Error al eliminar la tarjeta con id: " + id));
        }catch (Exception e){
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
