package org.example.users.api.createupdatedelete;

import com.fasterxml.jackson.annotation.JsonProperty;

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
     * Returns the id of the user
     * @return the id of the user
     * @author Javier Hernández, Yahya el hadri, Javier Ruiz, Alvaro herrero, Samuel Cortes, Raul Fernandez
     * @version 1.0
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the name of the user
     * @return the name of the user
     * @author Javier Hernández, Yahya el hadri, Javier Ruiz, Alvaro herrero, Samuel Cortes, Raul Fernandez
     * @version 1.0
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the username of the user
     * @return the username of the user
     * @author Javier Hernández, Yahya el hadri, Javier Ruiz, Alvaro herrero, Samuel Cortes, Raul Fernandez
     * @version 1.0
     */
    public String getUsername() {
        return username;
    }

    /**
     * Returns the email of the user
     * @return the email of the user
     * @author Javier Hernández, Yahya el hadri, Javier Ruiz, Alvaro herrero, Samuel Cortes, Raul Fernandez
     * @version 1.0
     */
    public String getEmail() {
        return email;
    }

    /**
     * Returns the createdAt of the user
     * @return the createdAt of the user
     * @author Javier Hernández, Yahya el hadri, Javier Ruiz, Alvaro herrero, Samuel Cortes, Raul Fernandez
     * @version 1.0
     */
    public String getCreatedAt() {
        return createdAt;
    }

    /**
     * Returns the updatedAt of the user
     * @return the updatedAt of the user
     * @author Javier Hernández, Yahya el hadri, Javier Ruiz, Alvaro herrero, Samuel Cortes, Raul Fernandez
     * @version 1.0
     */
    public String getUpdatedAt() {
        return updatedAt;
    }
}
