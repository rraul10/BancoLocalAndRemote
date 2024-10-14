package org.example.users.api.getAll;

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
     * @author Alvaro Herrero, Javier Ruiz, Javier Hernandez, Raul Fernandez, Yahya El Hadri, Samuel Cortes.
     */
    public int getId() {
        return id;
    }

    /**
     *
     * @return name of the user
     * @version 1.0
     * @author Alvaro Herrero, Javier Ruiz, Javier Hernandez, Raul Fernandez, Yahya El Hadri, Samuel Cortes.
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @return username of the user
     * @version 1.0
     * @author Alvaro Herrero, Javier Ruiz, Javier Hernandez, Raul Fernandez, Yahya El Hadri, Samuel Cortes.
     */
    public String getUsername() {
        return username;
    }

    /**
     *
     * @return email of the user
     * @version 1.0
     * @author Alvaro Herrero, Javier Ruiz, Javier Hernandez, Raul Fernandez, Yahya El Hadri, Samuel Cortes.
     */
    public String getEmail() {
        return email;
    }
}
