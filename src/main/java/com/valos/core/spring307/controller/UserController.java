package com.valos.core.spring307.controller;

import com.valos.core.spring307.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    UserService service;

    @GetMapping("/list")
    public Object getUser (){
        return service.getList("");
    }

    @PostMapping("/create")
    public Object createUser (@RequestBody Object request){
        return service.create(request);
    }

    @PostMapping("/login")
    public String token(@RequestBody Object request) {
//        LOG.debug("Token requested for user: '{}'", authentication.getName());
        // String token = tokenService.generateToken(authentication);
//        LOG.debug("Token granted: {}", token);
// return token;
    return "";
    }
}
