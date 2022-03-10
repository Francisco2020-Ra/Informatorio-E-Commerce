package com.informatorio.ECommerce.usuario.controller;

import com.informatorio.ECommerce.apierror.SingUpException;
import com.informatorio.ECommerce.usuario.service.impl.JwtTokenUtilServiceImpl;
import com.informatorio.ECommerce.usuario.service.impl.UserDetailsCustomService;
import com.informatorio.ECommerce.usuario.usuarioDTO.AuthenticationRequest;
import com.informatorio.ECommerce.usuario.usuarioDTO.AuthenticationResponse;
import com.informatorio.ECommerce.usuario.usuarioDTO.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/user")
public class UserAuthController {

    private UserDetailsCustomService userDetailsService;
    private AuthenticationManager authenticationManager;
    private JwtTokenUtilServiceImpl jwtTokenUtil;


    @Autowired
    public UserAuthController (UserDetailsCustomService userDetailsService,
                               AuthenticationManager authenticationManager,
                               JwtTokenUtilServiceImpl jwtTokenUtil){
        this.userDetailsService = userDetailsService;
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtil = jwtTokenUtil;

    }

    @PostMapping("/singup")
    public ResponseEntity<?> singup(@Valid @RequestBody UserDTO userDTO) throws Exception{
            return userDetailsService.save(userDTO);
    }

    @PostMapping("/singin")
    public ResponseEntity<?> singin(@Valid @RequestBody AuthenticationRequest authRequest) throws Exception{
        UserDetails userDetails;

        try {
            Authentication auth = authenticationManager.authenticate(
                    //UsernamePasswordAuthenticationToken es una implementacion de Authentication para
                    //representaciones simples son usuario y password
                    new UsernamePasswordAuthenticationToken(
                            authRequest.getUsername(), authRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(auth);
            userDetails = (UserDetails) auth.getPrincipal();

        }catch(BadCredentialsException exception){
            throw new SingUpException("Incorrecto username or password");
        }
        final String jwt = jwtTokenUtil.generateToken(userDetails);
        return ResponseEntity.ok(new AuthenticationResponse(jwt));
    }



}
