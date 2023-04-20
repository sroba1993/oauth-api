package com.ceiba.oauthapi.infraestructure.adapters.clients;

import com.ceiba.oauthapi.domain.model.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "service-users")
public interface UserFeignClient {

    @GetMapping("users/search/find-username")
    public User findByUsername(@RequestParam String name);

    @PutMapping("users/{id}")
    public User update(@RequestBody User user, @PathVariable Long id);
}
