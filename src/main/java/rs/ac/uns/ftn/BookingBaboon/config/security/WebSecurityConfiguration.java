package rs.ac.uns.ftn.BookingBaboon.config.security;

import org.keycloak.adapters.authorization.integration.jakarta.ServletPolicyEnforcerFilter;
import org.keycloak.adapters.authorization.spi.ConfigurationResolver;
import org.keycloak.adapters.authorization.spi.HttpRequest;
import org.keycloak.representations.adapters.config.PolicyEnforcerConfig;
import org.keycloak.util.JsonSerialization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.security.config.Customizer.withDefaults;
import static org.springframework.security.config.annotation.web.builders.HttpSecurity.*;

// Other imports...


@Configuration
@EnableWebSecurity
//@EnableMethodSecurity
//@EnableGlobalMethodSecurity(prePostEnabled = true)  // <-- Obavezno za @PreAuthorize
public class WebSecurityConfiguration{

//    @Autowired
//    private JwtRequestFilter jwtRequestFilter;


//    @Autowired
//    JwtAuthConverter jwtAuthConverter;

//    @Bean
//    public SecurityFilterChain keycloakFilterChain(HttpSecurity http) throws Exception {
//        http
//                .csrf(AbstractHttpConfigurer::disable)
//                .authorizeHttpRequests(auth -> auth
//                    .requestMatchers(new AntPathRequestMatcher("/*"))
//                    .permitAll()
//                    .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
//                    .anyRequest()
//                    .authenticated());
//        http.oauth2ResourceServer((oauth2) -> oauth2
//                .jwt(Customizer.withDefaults()));
//        http.sessionManagement(
//                t -> t.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//        );
//        http.oauth2ResourceServer(t-> {
//            t.jwt(Customizer.withDefaults());
//        });
//       return http.build();
//    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {
        http.csrf(AbstractHttpConfigurer::disable);
        http.addFilterAfter(createPolicyEnforcerFilter(),
                BearerTokenAuthenticationFilter.class);

        http.sessionManagement(
                t -> t.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );
        return http.build();
    }

    private ServletPolicyEnforcerFilter createPolicyEnforcerFilter() {
        return new ServletPolicyEnforcerFilter(new ConfigurationResolver() {
            @Override
            public PolicyEnforcerConfig resolve(HttpRequest request) {
                try {
                    return JsonSerialization.
                            readValue(getClass().getResourceAsStream("/policy-enforcer.json"),
                                    PolicyEnforcerConfig.class);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }


    //VERSION 3 BOUALI ALI
//    @Bean
//    public SecurityFilterChain keycloakFilterChain(HttpSecurity http) throws Exception {
//        http
//                .csrf(AbstractHttpConfigurer::disable)
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers(new AntPathRequestMatcher("/*"))
//                        .permitAll()
//                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
//                        .anyRequest()
//                        .authenticated());
//        http.oauth2ResourceServer(auth ->
//                auth.jwt(token -> token.jwtAuthenticationConverter(new JwtAuthConverter())));
//        http.sessionManagement(
//                t -> t.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//        );
//        http.oauth2ResourceServer(t-> {
//            t.jwt(Customizer.withDefaults());
//        });
//        return http.build();
//    }




//    @Bean
//    public DefaultMethodSecurityExpressionHandler msecurity() {
//        DefaultMethodSecurityExpressionHandler defaultMethodSecurityExpressionHandler =
//                new DefaultMethodSecurityExpressionHandler();
//        defaultMethodSecurityExpressionHandler.setDefaultRolePrefix("");
//        return defaultMethodSecurityExpressionHandler;
//    }
//
//    @Bean
//    public JwtAuthenticationConverter con() {
//        JwtAuthenticationConverter c =new JwtAuthenticationConverter();
//        JwtGrantedAuthoritiesConverter cv = new JwtGrantedAuthoritiesConverter();
//        cv.setAuthorityPrefix(""); // Default "SCOPE_"
//        cv.setAuthoritiesClaimName("roles"); // Default "scope" or "scp"
//        c.setJwtGrantedAuthoritiesConverter(cv);
//        return c;
//    }

//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        PasswordEncoder encoder = new BCryptPasswordEncoder();// PasswordEncoderFactories.createDelegatingPasswordEncoder();
//        return encoder;
//    }

//    @Bean
//    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
//            throws Exception {
//        return authenticationConfiguration.getAuthenticationManager();
//    }
}