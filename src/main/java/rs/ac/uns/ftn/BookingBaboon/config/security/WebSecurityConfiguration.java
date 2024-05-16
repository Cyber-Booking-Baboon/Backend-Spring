package rs.ac.uns.ftn.BookingBaboon.config.security;

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
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

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
@EnableMethodSecurity
//@EnableGlobalMethodSecurity(prePostEnabled = true)  // <-- Obavezno za @PreAuthorize
public class WebSecurityConfiguration{

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

//    private static final String GROUPS = "groups";
//    private static final String REALM_ACCESS_CLAIM = "realm_access";
//    private static final String ROLES_CLAIM = "roles";

    @Autowired
    JwtAuthConverter jwtAuthConverter;
//    private final KeycloakLogoutHandler keycloakLogoutHandler;
//
//    WebSecurityConfiguration(KeycloakLogoutHandler keycloakLogoutHandler) {
//        this.keycloakLogoutHandler = keycloakLogoutHandler;
//    }

//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http
//                .csrf(AbstractHttpConfigurer::disable)
//                .authorizeHttpRequests((requests) -> requests
//                        //.requestMatchers(("/*")).permitAll()
//                        .anyRequest().permitAll())
//                .sessionManagement(customizer -> customizer.sessionCreationPolicy(SessionCreationPolicy.STATELESS)); // ne koristimo HttpSession i kukije
//        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class); // JWT procesiramo pre autentikacije
//
//        return http.build();
//    }

//    @Bean
//    public SessionRegistry sessionRegistry() {
//        return new SessionRegistryImpl();
//    }
//
//    @Bean
//    protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
//        return new RegisterSessionAuthenticationStrategy(sessionRegistry());
//    }
//
//    @Bean
//    public HttpSessionEventPublisher httpSessionEventPublisher() {
//        return new HttpSessionEventPublisher();
//    }

    @Bean
    public SecurityFilterChain keycloakFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                    .requestMatchers(new AntPathRequestMatcher("/*"))
                    .permitAll()
                    .anyRequest()
                    .authenticated());
        http.oauth2ResourceServer((oauth2) -> oauth2
                .jwt(Customizer.withDefaults()));
        http.sessionManagement(
                t -> t.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );
        http.oauth2ResourceServer(t -> {
            t.jwt(jwtConfigurer -> jwtConfigurer.jwtAuthenticationConverter(jwtAuthConverter));
        });
//        http.oauth2Login(Customizer.withDefaults())
//                .logout(logout -> logout.addLogoutHandler(keycloakLogoutHandler).logoutSuccessUrl("/"));
        return http.build();
    }

//    @Bean
//    public GrantedAuthoritiesMapper userAuthoritiesMapperForKeycloak() {
//        return authorities -> {
//            Set<GrantedAuthority> mappedAuthorities = new HashSet<>();
//            var authority = authorities.iterator().next();
//            boolean isOidc = authority instanceof OidcUserAuthority;
//
//            if (isOidc) {
//                var oidcUserAuthority = (OidcUserAuthority) authority;
//                var userInfo = oidcUserAuthority.getUserInfo();
//
//                // Tokens can be configured to return roles under
//                // Groups or REALM ACCESS hence have to check both
//                if (userInfo.hasClaim(REALM_ACCESS_CLAIM)) {
//                    var realmAccess = userInfo.getClaimAsMap(REALM_ACCESS_CLAIM);
//                    var roles = (Collection<String>) realmAccess.get(ROLES_CLAIM);
//                    mappedAuthorities.addAll(generateAuthoritiesFromClaim(roles));
//                } else if (userInfo.hasClaim(GROUPS)) {
//                    Collection<String> roles = (Collection<String>) userInfo.getClaim(
//                            GROUPS);
//                    mappedAuthorities.addAll(generateAuthoritiesFromClaim(roles));
//                }
//            } else {
//                var oauth2UserAuthority = (OAuth2UserAuthority) authority;
//                Map<String, Object> userAttributes = oauth2UserAuthority.getAttributes();
//
//                if (userAttributes.containsKey(REALM_ACCESS_CLAIM)) {
//                    Map<String, Object> realmAccess = (Map<String, Object>) userAttributes.get(
//                            REALM_ACCESS_CLAIM);
//                    Collection<String> roles = (Collection<String>) realmAccess.get(ROLES_CLAIM);
//                    mappedAuthorities.addAll(generateAuthoritiesFromClaim(roles));
//                }
//            }
//            return mappedAuthorities;
//        };
//    }
//
//    Collection<GrantedAuthority> generateAuthoritiesFromClaim(Collection<String> roles) {
//        return roles.stream().map(role -> new SimpleGrantedAuthority("ROLE_" + role)).collect(
//                Collectors.toList());
//    }

    @Bean
    public DefaultMethodSecurityExpressionHandler msecurity() {
        DefaultMethodSecurityExpressionHandler defaultMethodSecurityExpressionHandler =
                new DefaultMethodSecurityExpressionHandler();
        defaultMethodSecurityExpressionHandler.setDefaultRolePrefix("");
        return defaultMethodSecurityExpressionHandler;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        PasswordEncoder encoder = new BCryptPasswordEncoder();// PasswordEncoderFactories.createDelegatingPasswordEncoder();
        return encoder;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}