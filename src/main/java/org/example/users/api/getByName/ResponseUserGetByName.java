package org.example.users.api.getByName;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResponseUserGetByName {

    @JsonProperty("id")
    private int id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("username")
    private String username;

    @JsonProperty("email")
    private String email;

    /**
     *
     * @return el id
     * @author Javier Hernandez, Yahya El Hadri, Javier Ruiz, Alvaro Herrero, Samuel Cortes, Raul Fernandez
     * @version 1.0
     */
    public int getId() {
        return id;
    }

    /**
     *
     * @return el nombre
     * @author Javier Hernandez, Yahya El Hadri, Javier Ruiz, Alvaro Herrero, Samuel Cortes, Raul Fernandez
     * @version 1.0
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @return el nombre de usuario
     * @author Javier Hernandez, Yahya El Hadri, Javier Ruiz, Alvaro Herrero, Samuel Cortes, Raul Fernandez
     * @version 1.0
     */
    public String getUsername() {
        return username;
    }

    /**
     *
     * @return el correo
     * @author Javier Hernandez, Yahya El Hadri, Javier Ruiz, Alvaro Herrero, Samuel Cortes, Raul Fernandez
     * @version 1.0
     */
    public String getEmail() {
        return email;
    }
}
