package com.informatorio.ECommerce.usuario.service.impl;

import com.informatorio.ECommerce.usuario.service.JwtTokenService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtTokenUtilServiceImpl implements JwtTokenService {

    @Value("${informatorio.app.ecommerce.SECRET_KEY}")
    private String SECRET_KEY;
    @Value("${informatorio.app.ecommerce.JWT_EXPIRATION_MS}")
    private int JWT_EXPIRATION_MS;


    /*************************** Extraer el USUARIO y DATOS DE EXPIRACION ****************/
    //Claims::getSubject: extrae el valor del token
    public String extractUsername(String token){return extractClaim(token, Claims::getSubject);}

    //Claims::getExpiration: extrae el valor de expiracion
    public Date extractExpiration(String token){ return extractClaim(token, Claims::getExpiration);}


    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver){
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token){
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
    }

    /*************************** Extraer el USUARIO y DATOS DE EXPIRACION ****************/


    private Boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date());
    }

    /***************** Proporciona métodos para generar, analizar y validar JWT ********/
    public String generateToken(UserDetails userDetails){
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDetails.getUsername());
    }

    private String createToken(Map<String, Object> claims, String subject){
        return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + JWT_EXPIRATION_MS))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    /****************************** Valida el Token ***************************************/
    public Boolean validateToken(String token, UserDetails userDetails){
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }




}
