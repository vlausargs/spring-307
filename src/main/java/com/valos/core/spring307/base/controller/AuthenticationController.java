package com.valos.core.spring307.base.controller;

import com.valos.core.spring307.base.component.ResponseWrapper;
import com.valos.core.spring307.base.model.dto.AuthenticationRequest;
import com.valos.core.spring307.base.model.dto.AuthenticationResponse;
import com.valos.core.spring307.base.model.dto.RegisterRequest;
import com.valos.core.spring307.base.services.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService service;

    @PostMapping("/register")
    public ResponseEntity<Object> register(
            @RequestBody RegisterRequest request
    ) {
        ResponseWrapper res = service.register(request);
        return res.getCode() == 0?ResponseEntity.badRequest().body(res):ResponseEntity.ok(res.getResult());

    }
    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest request
    ) {
        return ResponseEntity.ok(service.authenticate(request));
    }

    @PostMapping("/refresh-token")
    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        service.refreshToken(request, response);
    }


}
