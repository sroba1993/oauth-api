package com.ceiba.oauthapi.infraestructure.event;

import com.ceiba.oauthapi.domain.gateway.IUserService;
import com.ceiba.oauthapi.domain.model.User;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AuthenticationSuccessErrorHandler implements AuthenticationEventPublisher {

    @Autowired
    private IUserService userService;

    @Override
    public void publishAuthenticationSuccess(Authentication authentication) {
        if(authentication.getDetails() instanceof WebAuthenticationDetails) {
            return;
        }
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        log.info("Success Login: " + userDetails.getUsername());

        User user = userService.findByUsername(authentication.getName());
        if(user.getAttempts() != null && user.getAttempts() > 0) {
            user.setAttempts(0);
        }
        userService.update(user, user.getId());
    }

    @Override
    public void publishAuthenticationFailure(AuthenticationException exception, Authentication authentication) {
        log.info("Login error: " + exception.getMessage());
        try {
            User user = userService.findByUsername(authentication.getName());
            if(user.getAttempts() == null){
                user.setAttempts(0);
            }
            user.setAttempts(user.getAttempts() + 1);
            log.error(String.format("Number of attempts made: %s", user.getAttempts()));

            if(user.getAttempts() >= 3) {
                user.setEnabled(false);
                log.error(String.format("User %s was disabled for the maximum number of attempts allowed: ",
                        authentication.getName()));
            }
            userService.update(user, user.getId());
        } catch (FeignException e) {
            log.error(String.format("User %s does not exist in system: ", authentication.getName()));
        }
    }
}
