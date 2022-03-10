package com.informatorio.ECommerce.usuario.service.impl;

import com.informatorio.ECommerce.apierror.SingUpException;
import com.informatorio.ECommerce.usuario.entity.UserEntity;
import com.informatorio.ECommerce.usuario.mapper.UserMapper;
import com.informatorio.ECommerce.usuario.repository.UserRepository;
import com.informatorio.ECommerce.usuario.usuarioDTO.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Collection;


@Service
public class UserDetailsCustomService implements UserDetailsService  {

    private UserRepository userRepository;
    private UserMapper userMapper;

    @Autowired
    public UserDetailsCustomService(UserRepository userRepository, UserMapper userMapper){
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    //SingUp
    public ResponseEntity<?> save(UserDTO userDTO){
        if(userRepository.existsByUsername(userDTO.getUsername())){
            throw  new SingUpException("El usuario ya existe");
        }
        UserEntity userEntity = userMapper.userDto2userEntity(userDTO);
        userRepository.save(userEntity);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


    //SingIn
    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByUsername(userName)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + userName));
        Collection<? extends GrantedAuthority> authorities = userMapper.userEntityRole2Colletion(userEntity);
        return new User(userEntity.getUsername(), userEntity.getPassword(), authorities);
    }
}
