package org.example.usuarios.api.getAll;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResponseUserGetAll {
    
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
     * @return id of the user
     * @version 1.0
     * @author Javier Hernández, Yahya el hadri, Javier Ruiz, Alvaro herrero, Samuel Cortes, Raul Fernandez
     */
    public int getId() {
        return id;
    }

    /**
     *
     * @return name of the user
     * @version 1.0
     * @author Javier Hernández, Yahya el hadri, Javier Ruiz, Alvaro herrero, Samuel Cortes, Raul Fernandez
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @return username of the user
     * @version 1.0
     * @author Javier Hernández, Yahya el hadri, Javier Ruiz, Alvaro herrero, Samuel Cortes, Raul Fernandez
     */
    public String getUsername() {
        return username;
    }

    /**
     *
     * @return email of the user
     * @version 1.0
     * @author Javier Hernández, Yahya el hadri, Javier Ruiz, Alvaro herrero, Samuel Cortes, Raul Fernandez
     */
    public String getEmail() {
        return email;
    }
}
