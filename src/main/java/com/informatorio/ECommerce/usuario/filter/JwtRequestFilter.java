package com.informatorio.ECommerce.usuario.filter;

import com.informatorio.ECommerce.usuario.service.JwtTokenService;
import com.informatorio.ECommerce.usuario.service.impl.UserDetailsCustomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {


    private JwtTokenService jwtTokenService;
    private UserDetailsCustomService userDetailsCustomService;

    @Autowired
    public  JwtRequestFilter(@Lazy UserDetailsCustomService userDetailsCustomService, JwtTokenService jwtTokenService){
        this.userDetailsCustomService = userDetailsCustomService;
        this.jwtTokenService = jwtTokenService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        /***********************Extraemos el username *********************/
        //Del header obtenemos el contenido con titulo Authorization
        final String authorizationHeader = request.getHeader("Authorization");

        String username = null;
        String jwt = null;


        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer")) {
            //Quitamos el bearer de la cadena
            jwt = authorizationHeader.substring(7);
            //decodificamos el username
            username = jwtTokenService.extractUsername(jwt);
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            /******************* Cargamos el usaurio por nombre de usuario *********/
            UserDetails userDetails = userDetailsCustomService.loadUserByUsername(username);


            if(jwtTokenService.validateToken(jwt, userDetails)){
                UsernamePasswordAuthenticationToken authReq =
                        new UsernamePasswordAuthenticationToken(userDetails.getUsername(),
                                userDetails.getPassword(), userDetails.getAuthorities());
                /**AuthenticationManager tiene un DaoAuthenticationProvider
                 * (con la ayuda de UserDetailsService & PasswordEncoder)
                 * para validar el UsernamePasswordAuthenticationTokenobjeto.
                 * Si tiene éxito, AuthenticationManager devuelve un objeto de autenticación completo
                 * (incluidas las autorizaciones otorgadas).
                 * */

                authReq.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authReq);
            }

        }
        filterChain.doFilter(request, response);
    }
}
