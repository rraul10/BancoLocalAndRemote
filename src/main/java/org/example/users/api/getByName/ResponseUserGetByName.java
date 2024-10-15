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

    @JsonProperty("nombre")
    private String nombre;

    @JsonProperty("nombreUsuario")
    private String nombreUsuario;

    @JsonProperty("correo")
    private String correo;

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
    public String getNombre() {
        return nombre;
    }

    /**
     *
     * @return el nombre de usuario
     * @author Javier Hernandez, Yahya El Hadri, Javier Ruiz, Alvaro Herrero, Samuel Cortes, Raul Fernandez
     * @version 1.0
     */
    public String getNombreUsuario() {
        return nombreUsuario;
    }

    /**
     *
     * @return el correo
     * @author Javier Hernandez, Yahya El Hadri, Javier Ruiz, Alvaro Herrero, Samuel Cortes, Raul Fernandez
     * @version 1.0
     */
    public String getCorreo() {
        return correo;
    }
}
