package com.ceiba.oauthapi.domain.service;

import com.ceiba.oauthapi.domain.gateway.IUserService;
import com.ceiba.oauthapi.domain.model.User;
import com.ceiba.oauthapi.infraestructure.adapters.clients.UserFeignClient;
import feign.FeignException;
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
        try {
            User user = client.findByUsername(username);
            List<GrantedAuthority> authorities = user.getRoles().stream()
                    .map(role -> new SimpleGrantedAuthority(role.getName()))
                    .peek(authority -> log.info("Role: " + authority.getAuthority()))
                    .collect(Collectors.toList());
            log.info("User authenticated: " + username);
            log.info("User: " + user.getUsername() + "  " + user.getPassword() + " " + user.getEnabled() + " " + authorities);
            return new org.springframework.security.core.userdetails.User(user.getUsername(),user.getPassword(),
                    user.getEnabled(), true, true, true, authorities);
        } catch (FeignException e) {
            log.error("Login error, user does not exist: " + username);
            throw new UsernameNotFoundException("Login error, user does not exist: " + username);
        }
    }

    @Override
    public User findByUsername(String username) {
        return client.findByUsername(username);
    }

    @Override
    public User update(User user, Long id) {
        return client.update(user, id);
    }
}
