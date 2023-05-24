package com.valos.core.spring307.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.valos.core.spring307.dao.TokenRepository;
import com.valos.core.spring307.dao.UserRepository;
import com.valos.core.spring307.model.Token;
import com.valos.core.spring307.model.TokenType;
import com.valos.core.spring307.model.User;
import com.valos.core.spring307.model.dto.AuthenticationRequest;
import com.valos.core.spring307.model.dto.AuthenticationResponse;
import com.valos.core.spring307.model.dto.RegisterRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository repository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    private final UserDetailsManager userDetailsManager;

    public AuthenticationResponse register(RegisterRequest request) {
        //create user
        User user = new User();
        user.setId(request.getEmail());
        user.setName(request.getFirstname().concat(" ").concat(request.getLastname()));
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        var savedUser = repository.save(user);

       //generate token from UserDetailManager
        UserDetails userDetails = userDetailsManager.loadUserByUsername(request.getEmail());
        var jwtToken = jwtService.generateToken(userDetails);
        var refreshToken = jwtService.generateRefreshToken(userDetails);

        //save token to db
        saveUserToken(savedUser, jwtToken);

        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        //generate token from authenticated UserDetailManager
        UserDetails userDetails = userDetailsManager.loadUserByUsername(request.getEmail());

        //generate authority to List<String>
        Map<String,Object> extra = new HashMap<>();
        extra.put("authority",userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()));

        var jwtToken = jwtService.generateToken(extra,userDetails);
        var refreshToken = jwtService.generateRefreshToken(userDetails);

        //save new token to db & revoke previous token
        User user = repository.findById(request.getEmail()).orElseThrow();
        revokeAllUserTokens(user);
        saveUserToken(user, jwtToken);

        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    private void saveUserToken(User user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }

    private void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }

    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String userEmail;
        if (authHeader == null ||!authHeader.startsWith("Bearer ")) {
            return;
        }
        refreshToken = authHeader.substring(7);
        userEmail = jwtService.extractUsername(refreshToken);
        if (userEmail != null) {

            User user = this.repository.findById(userEmail)
                    .orElseThrow();
            UserDetails userDetails = userDetailsManager.loadUserByUsername(userEmail);
            if (jwtService.isTokenValid(refreshToken, userDetails)) {
                Map<String,Object> extra = new HashMap<>();
                extra.put("authority",userDetails.getAuthorities());
                var accessToken = jwtService.generateToken(extra,userDetails);
                revokeAllUserTokens(user);
                saveUserToken(user, accessToken);
                var authResponse = AuthenticationResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build();
                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
            }
        }
    }
}