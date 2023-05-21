package com.valos.core.spring307.config;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import javax.sql.DataSource;
import java.security.interfaces.RSAPublicKey;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurity {

   private final RsaKeyProperties jwtConfigProperties;

   public WebSecurity(RsaKeyProperties jwtConfigProperties) {
       this.jwtConfigProperties = jwtConfigProperties;
   }
    // add support for JDBC ... no more hardcoded users :-)



   @Bean
   public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
       return http.csrf(csrf -> csrf.disable())
               .authorizeHttpRequests(configurer ->
                       configurer
                               .requestMatchers("/api/user/create").permitAll()
                               .requestMatchers("/api/user/login").permitAll()
                               .anyRequest().authenticated()
               )
               .oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt)
               .sessionManagement(session-> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
               .build();
   }

    /*
    @Bean
    public InMemoryUserDetailsManager userDetailsManager() {

        UserDetails john = User.builder()
                .username("john")
                .password("{noop}test123")
                .roles("EMPLOYEE")
                .build();

        UserDetails mary = User.builder()
                .username("mary")
                .password("{noop}test123")
                .roles("EMPLOYEE", "MANAGER")
                .build();

        UserDetails susan = User.builder()
                .username("susan")
                .password("{noop}test123")
                .roles("EMPLOYEE", "MANAGER", "ADMIN")
                .build();

        return new InMemoryUserDetailsManager(john, mary, susan);
    }
    */

   @Bean
   JwtDecoder jwtDecoder() {
       return NimbusJwtDecoder.withPublicKey(jwtConfigProperties.publicKey()).build();
   }

   @Bean
   JwtEncoder jwtEncoder() {
       JWK jwk = new RSAKey.Builder(jwtConfigProperties.publicKey()).privateKey(jwtConfigProperties.privateKey()).build();
       JWKSource<SecurityContext> jwks = new ImmutableJWKSet<>(new JWKSet(jwk));
       return new NimbusJwtEncoder(jwks);
   }
}
