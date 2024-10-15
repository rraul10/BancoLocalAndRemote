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

    @JsonProperty("nombre")
    private String nombre;

    @JsonProperty("nombreUsuario")
    private String nombreUsuario;

    @JsonProperty("correo")
    private String correo;

    /**
     *
     * @return el id del usuario
     * @version 1.0
     * @author Alvaro Herrero, Javier Ruiz, Javier Hernandez, Raul Fernandez, Yahya El Hadri, Samuel Cortes.
     */
    public int getId() {
        return id;
    }

    /**
     *
     * @return el nombre del usuario
     * @version 1.0
     * @author Alvaro Herrero, Javier Ruiz, Javier Hernandez, Raul Fernandez, Yahya El Hadri, Samuel Cortes.
     */
    public String getNombre() {
        return nombre;
    }

    /**
     *
     * @return el nombre de usuario del usuario
     * @version 1.0
     * @author Alvaro Herrero, Javier Ruiz, Javier Hernandez, Raul Fernandez, Yahya El Hadri, Samuel Cortes.
     */
    public String getNombreUsuario() {
        return nombreUsuario;
    }

    /**
     *
     * @return el correo del usuario
     * @version 1.0
     * @author Alvaro Herrero, Javier Ruiz, Javier Hernandez, Raul Fernandez, Yahya El Hadri, Samuel Cortes.
     */
    public String getCorreo() {
        return correo;
    }
}
