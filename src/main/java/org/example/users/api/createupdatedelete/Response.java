package org.example.users.api.createupdatedelete;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class Response {

    @JsonProperty("createdAt")
    private String createdAt;

    @JsonProperty("updatedAt")
    private String updatedAt;

    @JsonProperty("id")
    private int id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("username")
    private String username;

    @JsonProperty("email")
    private String email;

    /**
     * Retorna el id del usuario
     * @return el id del usuario
     * @author Alvaro Herrero, Javier Ruiz, Javier Hernandez, Raul Fernandez, Yahya El Hadri, Samuel Cortes.
     * @version 1.0
     */
    public int getId() {
        return id;
    }

    /**
     * Retorna el nombre del usuario
     * @return el nombre del usuario
     * @author Alvaro Herrero, Javier Ruiz, Javier Hernandez, Raul Fernandez, Yahya El Hadri, Samuel Cortes.
     * @version 1.0
     */
    public String getName() {
        return name;
    }

    /**
     * Retorna el nombre de usuario del usuario
     * @return el nombre de usuario del usuario
     * @author Alvaro Herrero, Javier Ruiz, Javier Hernandez, Raul Fernandez, Yahya El Hadri, Samuel Cortes.
     * @version 1.0
     */
    public String getUsername() {
        return username;
    }

    /**
     * Retorna el correo del usuario
     * @return el correo del usuario
     * @author Alvaro Herrero, Javier Ruiz, Javier Hernandez, Raul Fernandez, Yahya El Hadri, Samuel Cortes.
     * @version 1.0
     */
    public String getEmail() {
        return email;
    }

    /**
     * Retorna la fecha de creacion del usuario
     * @return la fecha de creacion del usuario
     * @author Alvaro Herrero, Javier Ruiz, Javier Hernandez, Raul Fernandez, Yahya El Hadri, Samuel Cortes.
     * @version 1.0
     */
    public String getCreatedAt() {
        return createdAt;
    }

    /**
     * Retorna la fecha de actualizacion del usuario
     * @return la fecha de actualizacion del usuario
     * @author Alvaro Herrero, Javier Ruiz, Javier Hernandez, Raul Fernandez, Yahya El Hadri, Samuel Cortes.
     * @version 1.0
     */
    public String getUpdatedAt() {
        return updatedAt;
    }
}
