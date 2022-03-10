package com.informatorio.ECommerce.usuario.mapper;

import com.informatorio.ECommerce.usuario.entity.RoleEntity;
import com.informatorio.ECommerce.usuario.entity.UserEntity;
import com.informatorio.ECommerce.usuario.entity.enumrole.ERole;
import com.informatorio.ECommerce.usuario.repository.RoleRepository;
import com.informatorio.ECommerce.usuario.usuarioDTO.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;


import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UserMapper {


    private PasswordEncoder encoder;
    private RoleRepository roleRepository;

    @Autowired
    public UserMapper(PasswordEncoder encoder, RoleRepository roleRepository){
        this.encoder = encoder;
        this.roleRepository = roleRepository;
    }

    public UserEntity userDto2userEntity(UserDTO userDTO){
     UserEntity userEntity = new UserEntity();
     userEntity.setNombre(userDTO.getNombre());
     userEntity.setApellido(userDTO.getApellido());
     userEntity.setDireccion(userDTO.getDireccion());
     userEntity.setFechaDeCreacion(userDTO.getFechaDeCreacion());
     userEntity.setUsername(userDTO.getUsername());
     userEntity.setPassword(encriptPass(userDTO));
     userEntity.setRole(roles(userDTO));
     return userEntity;
    }

    public String encriptPass(UserDTO userDTO){
        return encoder.encode(userDTO.getPassword());
    }

    public Set<RoleEntity> roles(UserDTO userDTO){

        Set<String> strRoles = userDTO.getRole();
        Set<RoleEntity> roles = new HashSet<>();
        if (strRoles == null) {
            RoleEntity userRole = roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        RoleEntity adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(adminRole);
                        break;
                    default:
                        RoleEntity userRole = roleRepository.findByName(ERole.ROLE_USER).orElseThrow(()->new RuntimeException("Error: Role is not found."));
                        roles.add(userRole);
                }
            });
        }
        return roles;
    }

    public Collection<? extends GrantedAuthority> userEntityRole2Colletion (UserEntity userEntity){
        return userEntity.getRole().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().name()))
                .collect(Collectors.toList());
    }



}
