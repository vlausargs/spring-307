package com.valos.core.spring307.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;

import javax.sql.DataSource;

@Configuration
public class ApplicationConfig {

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public UserDetailsManager userDetailsManager(DataSource dataSource) {

        JdbcUserDetailsManager jdbcUserDetailsManager = new JdbcUserDetailsManager(dataSource);

        // define query to retrieve a user by username
        jdbcUserDetailsManager.setUsersByUsernameQuery(
                "select id, password, enabled from users where id=?");

        // define query to retrieve the authorities/roles by username
        jdbcUserDetailsManager.setAuthoritiesByUsernameQuery(
                "select user_id, auth_id from auth_authority_member where user_id=?");

        return jdbcUserDetailsManager;
    }
    // @Bean
    // public UserDetailsService userDetailsService(JdbcUserDetailsManager userDetailsManager) {
    //     return userDetailsManager.get
    // }

    // @Bean
    // public AuthenticationProvider authenticationProvider() {
    //     DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
    //     authProvider.setUserDetailsService(userDetailsService());
    //     authProvider.setPasswordEncoder(passwordEncoder());
    //     return authProvider;
    // }
}
