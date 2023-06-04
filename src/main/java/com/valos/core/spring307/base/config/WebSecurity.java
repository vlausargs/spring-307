package com.valos.core.spring307.base.config;

import org.springframework.context.annotation.Configuration;

@Configuration
//@EnableWebSecurity
//@EnableMethodSecurity
public class WebSecurity {

//    @Bean
//    @Order(1)
//    public SecurityFilterChain asSecurityFilterChain(HttpSecurity http) throws Exception {
//        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);
//        http.getConfigurer(OAuth2AuthorizationServerConfigurer.class).oidc(Customizer.withDefaults());
//        http.exceptionHandling(e -> e
//                .authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/login")));
//
//        return http.build();
//    }
//
//    @Bean
//    @Order(2)
//    public SecurityFilterChain appSecurityFilterChain(HttpSecurity http) throws Exception {
//        http
//                .csrf().disable()
//                .httpBasic()
//                .and()
//                .authorizeHttpRequests((authorize)->  authorize.requestMatchers("/login*")
//                        .permitAll()
//                        .requestMatchers("/oauth2*").permitAll()
//                        .anyRequest().authenticated());
//                ;
//        return http.build();
//    }
//
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        PasswordEncoder encoder = new BCryptPasswordEncoder();
//        return encoder;
//    }
//
//    @Bean
//    public RegisteredClientRepository registeredClientRepository() {
//        RegisteredClient registeredClient = RegisteredClient.withId(UUID.randomUUID().toString())
//                .clientId("client")
//                .clientSecret("$2a$10$Bqsb0CwqQnbnrZ3mVT4yfucwPtOM.fFm6bX1MM8u8yF5uCofgKrli")
//                .scope("openid,read")
////                .redirectUri("https://oidcdebugger.com/debug")
////                .redirectUri("https://oauthdebugger.com/debug")
//                .redirectUri("https://springone.io/authorized")
//                .redirectUri("http://localhost.com:4200/dashboard")
//                .redirectUri("http:///127.0.0.1:4200/dashboard")
//                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
//                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
//                .build();
//
//        return new InMemoryRegisteredClientRepository(registeredClient);
//    }
//
//    @Bean
//    public AuthorizationServerSettings authorizationServerSettings() {
//        return AuthorizationServerSettings.builder().build();
//    }
//
//    @Bean
//    public TokenSettings tokenSettings() {
//        return TokenSettings.builder().build();
//    }
//
//    @Bean
//    public ClientSettings clientSettings() {
//        return ClientSettings.builder()
//                .requireAuthorizationConsent(false)
//                .requireProofKey(false)
//                .build();
//    }
//
//    @Bean
//    public JWKSource<SecurityContext> jwkSource() {
//        RSAKey rsaKey = generateRsa();
//        JWKSet jwkSet = new JWKSet(rsaKey);
//        return (jwkSelector, securityContext) -> jwkSelector.select(jwkSet);
//    }
//
//    public static RSAKey generateRsa() {
//        KeyPair keyPair = generateRsaKey();
//        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
//        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
//        return new RSAKey.Builder(publicKey).privateKey(privateKey).keyID(UUID.randomUUID().toString()).build();
//    }
//
//    static KeyPair generateRsaKey() {
//        KeyPair keyPair;
//        try {
//            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
//            keyPairGenerator.initialize(2048);
//            keyPair = keyPairGenerator.generateKeyPair();
//        } catch (Exception ex) {
//            throw new IllegalStateException(ex);
//        }
//        return keyPair;
//    }
}
