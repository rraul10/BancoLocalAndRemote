package org.example.usuarios.api.getById;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResponseUserGetByid {

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
     * @return the id
     * @author Javier Hern ndez, Yahya el hadri, Javier Ruiz, Alvaro herrero, Samuel Cortes, Raul Fernandez
     * @version 1.0
     */
    public int getId() {
        return id;
    }

    /**
     *
     * @return the name
     * @author Javier Hern ndez, Yahya el hadri, Javier Ruiz, Alvaro herrero, Samuel Cortes, Raul Fernandez
     * @version 1.0
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @return the username
     * @author Javier Hern ndez, Yahya el hadri, Javier Ruiz, Alvaro herrero, Samuel Cortes, Raul Fernandez
     * @version 1.0
     */
    public String getUsername() {
        return username;
    }

    /**
     *
     * @return the email
     * @author Javier Hern ndez, Yahya el hadri, Javier Ruiz, Alvaro herrero, Samuel Cortes, Raul Fernandez
     * @version 1.0
     */
    public String getEmail() {
        return email;
    }
}
