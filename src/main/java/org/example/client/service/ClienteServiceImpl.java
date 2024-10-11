package org.example.client.service;

import io.vavr.control.Either;
import org.example.client.repository.creditcard.CreditCardLocalRepository;
import org.example.client.repository.user.UsersRepository;
import org.example.client.service.errors.ServiceError;
import org.example.creditcard.repositories.CreditCardRepository;
import org.example.creditcard.validator.TarjetaValidator;
import org.example.models.Cliente;
import org.example.models.TarjetaCredito;
import org.example.models.Usuario;
import org.example.users.validator.UserValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ClienteServiceImpl implements ClienteService {
    private final Logger logger = LoggerFactory.getLogger(ClienteServiceImpl.class);
    private final UsersRepository usersRepository;
    private final CreditCardLocalRepository creditCardLocalRepository;
    private final UserValidator userValidator;
    private final TarjetaValidator tarjetaValidator;

    public ClienteServiceImpl(UsersRepository usersRepository, CreditCardLocalRepository creditCardLocalRepository, UserValidator userValidator, TarjetaValidator tarjetaValidator) {
        this.usersRepository = usersRepository;
        this.creditCardLocalRepository = creditCardLocalRepository;
        this.userValidator = userValidator;
        this.tarjetaValidator = tarjetaValidator;
    }


    @Override
    public Either<ServiceError, List<Cliente>> getAllClientes(Boolean fromRemote) {
        try {
            List<Usuario> usuarios = usersRepository.findAllUsers();
            List<TarjetaCredito> tarjetas = creditCardLocalRepository.findAllCreditCards();

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

            // Filtrar tarjetas que no tienen un idCliente asociado a un usuario
            List<TarjetaCredito> tarjetasSinUsuario = tarjetas.stream()
                    .filter(tarjeta -> tarjeta.getClientID() == null || usuarios.stream().noneMatch(u -> u.getId().equals(tarjeta.getIdCliente())))
                    .collect(Collectors.toList());

            // Crear clientes para las tarjetas sin usuario
            List<Cliente> clientesSinUsuario = tarjetasSinUsuario.stream()
                    .map(tarjeta -> new Cliente(null, Collections.singletonList(tarjeta)))
                    .collect(Collectors.toList());

            // Combinar ambas listas y devolver el resultado
            clientes.addAll(clientesSinUsuario);
            return Either.right(clientes);
        }catch(Exception e){
            logger.error("Error al obtener los clientes", e);
            return Either.left(new ServiceError.UsersLoadError("Error al obtener los clientes"));
        }
    }

    @Override
    public Either<ServiceError, Cliente> getClienteById(Long id) {
        return null;
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
}