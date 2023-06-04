package com.valos.core.spring307.base.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.valos.core.spring307.base.component.ConsMessage;
import com.valos.core.spring307.base.component.ResponseWrapper;
import com.valos.core.spring307.base.dao.AuthorityDao;
import com.valos.core.spring307.base.dao.TokenRepository;
import com.valos.core.spring307.base.dao.UserRepository;
import com.valos.core.spring307.base.model.Token;
import com.valos.core.spring307.base.model.TokenType;
import com.valos.core.spring307.base.model.User;
import com.valos.core.spring307.base.model.dto.AuthenticationRequest;
import com.valos.core.spring307.base.model.dto.AuthenticationResponse;
import com.valos.core.spring307.base.model.dto.RegisterRequest;
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
    private final AuthorityDao authDao;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    private final UserDetailsManager userDetailsManager;

    public ResponseWrapper register(RegisterRequest request) {
        ResponseWrapper result = new ResponseWrapper();

        //create user
        User user = new User();
        user.setEmail(request.getEmail());
        user.setName(request.getFirstname().concat(" ").concat(request.getLastname()));
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        try{
            var savedUser = repository.save(user);

            //add default authority
            Map<String, Object> requestMap = new HashMap<>();
            requestMap.put("user_id",savedUser.getId());
            authDao.assignAuthorityToUser(requestMap);

           //generate token from UserDetailManager
            UserDetails userDetails = userDetailsManager.loadUserByUsername(savedUser.getId());
            var jwtToken = jwtService.generateToken(userDetails);
            var refreshToken = jwtService.generateRefreshToken(userDetails);

            //save token to db
            saveUserToken(savedUser, jwtToken);
            result.setResult(AuthenticationResponse.builder()
                    .accessToken(jwtToken)
                    .refreshToken(refreshToken)
                    .build());
            result.setCode(1);
            result.setMessage(ConsMessage.MESSAGE_SUCCESS);
        }catch (Exception e){
            result.setCode(0);
            System.err.println("Error AUTH");
            System.err.println(e.getMessage());
            result.setMessage(ConsMessage.MESSAGE_FAILED);
        }
        return result;
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        User user = repository.findByEmail(request.getEmail()).orElseThrow();

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.getId(),
                        request.getPassword()
                )
        );

        //generate token from authenticated UserDetailManager
        UserDetails userDetails = userDetailsManager.loadUserByUsername(user.getId());

        //generate authority to List<String>
        Map<String,Object> extra = new HashMap<>();
        extra.put("authority",userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()));

        var jwtToken = jwtService.generateToken(extra,userDetails);
        var refreshToken = jwtService.generateRefreshToken(userDetails);

        //save new token to db & revoke previous token

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
        final String userId;
        if (authHeader == null ||!authHeader.startsWith("Bearer ")) {
            return;
        }
        refreshToken = authHeader.substring(7);
        userId = jwtService.extractUsername(refreshToken);
        if (userId != null) {

            User user = this.repository.findById(userId)
                    .orElseThrow();
            UserDetails userDetails = userDetailsManager.loadUserByUsername(userId);
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