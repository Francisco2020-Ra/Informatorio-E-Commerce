package com.informatorio.ECommerce.usuario.usuarioDTO;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
public class AuthenticationRequest {
    @NotEmpty(message = "El campo usuario no debe estar en blanco")
    @Email(message = "debe ser una dirección de correo electrónico con formato correcto")
    private String username;
    @Size(min = 8)
    private String password;

}
