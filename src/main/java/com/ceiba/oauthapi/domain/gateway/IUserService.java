package com.ceiba.oauthapi.domain.gateway;

import com.ceiba.oauthapi.domain.model.User;

public interface IUserService {

    public User findByUsername(String username);
}
