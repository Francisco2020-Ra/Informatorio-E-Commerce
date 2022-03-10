package com.informatorio.ECommerce.usuario.usuarioDTO;


import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Set;

@Data
public class UserDTO {

    @NotEmpty(message = "El campo nombre no debe estar en blanco")
    private String nombre;
    @NotEmpty(message = "El campo apellido no debe estar en blanco")
    private String apellido;
    @NotEmpty(message = "El campo direccion no debe estar en blanco")
    private String direccion;
    private LocalDateTime fechaDeCreacion = LocalDateTime.now();
    @NotEmpty(message = "El campo usuario no debe estar en blanco")
    @Email(message = "debe ser una dirección de correo electrónico con formato correcto")
    private String username;
    @Size(min = 8)
    private String password;

    private Set<String> role;
}
