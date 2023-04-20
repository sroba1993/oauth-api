package com.ceiba.oauthapi.domain.gateway;

import com.ceiba.oauthapi.domain.model.User;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

public interface IUserService {

    public User findByUsername(String username);

    public User update(User user, Long id);
}
