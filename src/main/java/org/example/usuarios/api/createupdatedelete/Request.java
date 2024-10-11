package org.example.usuarios.api.createupdatedelete;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Request {

    @JsonProperty("id")
    private int id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("username")
    private String username;

    @JsonProperty("email")
    private String email;

    /**
     * Devuelve el id del usuario.
     *
     * @return el id del usuario.
     * @author Javier Hernández, Yahya el hadri, Javier Ruiz, Alvaro herrero, Samuel Cortes, Raul Fernandez.
     * @version 1.0
     */
    public int getId() {
        return id;
    }

    /**
     * Devuelve el nombre del usuario.
     *
     * @return el nombre del usuario.
     * @author Javier Hernández, Yahya el hadri, Javier Ruiz, Alvaro herrero, Samuel Cortes, Raul Fernandez.
     * @version 1.0
     */
    public String getName() {
        return name;
    }

    /**
     * Devuelve el username del usuario.
     *
     * @return el username del usuario.
     * @author Javier Hernández, Yahya el hadri, Javier Ruiz, Alvaro herrero, Samuel Cortes, Raul Fernandez.
     * @version 1.0
     */
    public String getUsername() {
        return username;
    }

    /**
     * Devuelve el email del usuario.
     *
     * @return el email del usuario.
     * @author Javier Hernández, Yahya el hadri, Javier Ruiz, Alvaro herrero, Samuel Cortes, Raul Fernandez.
     * @version 1.0
     */
    public String getEmail() {
        return email;
    }
}
