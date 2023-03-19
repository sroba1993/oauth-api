package com.ceiba.oauthapi.infraestructure.adapters.clients;

import com.ceiba.oauthapi.domain.model.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "service-users")
public interface UserFeignClient {

    @GetMapping("users/search/find-username")
    public User findByUsername(@RequestParam String name);
}
