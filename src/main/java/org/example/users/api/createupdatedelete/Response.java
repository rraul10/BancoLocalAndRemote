package org.example.users.api.createupdatedelete;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public class Response {

    @JsonProperty("creadoEn")
    private String creadoEn;

    @JsonProperty("actualizadoEn")
    private String actualizadoEn;

    @JsonProperty("id")
    private int id;

    @JsonProperty("nombre")
    private String nombre;

    @JsonProperty("nombreUsuario")
    private String nombreUsuario;

    @JsonProperty("correo")
    private String correo;

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
    public String getNombre() {
        return nombre;
    }

    /**
     * Retorna el nombre de usuario del usuario
     * @return el nombre de usuario del usuario
     * @author Alvaro Herrero, Javier Ruiz, Javier Hernandez, Raul Fernandez, Yahya El Hadri, Samuel Cortes.
     * @version 1.0
     */
    public String getNombreUsuario() {
        return nombreUsuario;
    }

    /**
     * Retorna el correo del usuario
     * @return el correo del usuario
     * @author Alvaro Herrero, Javier Ruiz, Javier Hernandez, Raul Fernandez, Yahya El Hadri, Samuel Cortes.
     * @version 1.0
     */
    public String getCorreo() {
        return correo;
    }

    /**
     * Retorna la fecha de creacion del usuario
     * @return la fecha de creacion del usuario
     * @author Alvaro Herrero, Javier Ruiz, Javier Hernandez, Raul Fernandez, Yahya El Hadri, Samuel Cortes.
     * @version 1.0
     */
    public String getCreadoEn() {
        return creadoEn;
    }

    /**
     * Retorna la fecha de actualizacion del usuario
     * @return la fecha de actualizacion del usuario
     * @author Alvaro Herrero, Javier Ruiz, Javier Hernandez, Raul Fernandez, Yahya El Hadri, Samuel Cortes.
     * @version 1.0
     */
    public String getActualizadoEn() {
        return actualizadoEn;
    }
}
