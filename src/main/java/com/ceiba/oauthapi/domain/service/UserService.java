package com.ceiba.oauthapi.domain.service;

import com.ceiba.oauthapi.domain.gateway.IUserService;
import com.ceiba.oauthapi.domain.model.User;
import com.ceiba.oauthapi.infraestructure.adapters.clients.UserFeignClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService implements IUserService, UserDetailsService {

    private Logger log = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserFeignClient client;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = client.findByUsername(username);
        if(user == null) {
            log.error("Error en el login, no existe el usuario: " + username);
            throw new UsernameNotFoundException("Error en el login, no existe el usuario: " + username);
        }
        List<GrantedAuthority> authorithies = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .peek(authority -> log.info("Role: " + authority.getAuthority()))
                .collect(Collectors.toList());
        log.info("Usuario autenticado: " + username);
        log.info("Usuario: " + user.getUsername() + "  " + user.getPassword() + " " + user.getEnabled() + " " + authorithies);
        return new org.springframework.security.core.userdetails.User(user.getUsername(),user.getPassword(),
                user.getEnabled(), true, true, true, authorithies);
    }

    @Override
    public User findByUsername(String username) {
        return client.findByUsername(username);
    }
}
