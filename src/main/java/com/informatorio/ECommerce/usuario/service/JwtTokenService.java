package com.informatorio.ECommerce.usuario.service;


import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;

public interface JwtTokenService {

    String extractUsername(String token);

    Date extractExpiration(String token);

    /*****************Proporciona métodos para generar, analizar y validar JWT ********/
    public String generateToken(UserDetails userDetails);

    /******************************Valida el Token***************************************/
    public Boolean validateToken(String token, UserDetails userDetails);

}
