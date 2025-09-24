package com.kcmc.complaints.config;

import com.kcmc.complaints.security.JwtAuthenticationFilter;
import com.kcmc.complaints.security.JwtTokenProvider;
import com.kcmc.complaints.security.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(JwtTokenProvider jwtTokenProvider, CustomUserDetailsService userDetailsService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> {})
                .csrf(csrf -> csrf.disable())
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth

                        // Public authentication endpoints
                        .requestMatchers("/api/auth/**").permitAll()

                        // Public endpoints
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/api/users/register").permitAll()
                        .requestMatchers("/api/roles").permitAll()

                        // Attachments (open to STAFF, ICT_AGENT, ADMINISTRATOR)
                        .requestMatchers(HttpMethod.GET, "/api/attachments/**").hasAnyRole("STAFF", "ICT_AGENT", "ADMINISTRATOR")
                        .requestMatchers(HttpMethod.POST, "/api/attachments/**").hasAnyRole("STAFF", "ICT_AGENT", "ADMINISTRATOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/attachments/**").hasAnyRole("STAFF", "ICT_AGENT", "ADMINISTRATOR")

                        // Ticket Status rules
                        .requestMatchers(HttpMethod.GET, "/api/statuses/**").hasAnyRole("STAFF", "ICT_AGENT", "ADMINISTRATOR")
                        .requestMatchers(HttpMethod.POST, "/api/statuses/**").hasRole("ADMINISTRATOR")
                        .requestMatchers(HttpMethod.PUT, "/api/statuses/**").hasRole("ADMINISTRATOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/statuses/**").hasRole("ADMINISTRATOR")

                        // Category APIs rules
                        .requestMatchers(HttpMethod.GET, "/api/categories/**").hasAnyRole("ADMINISTRATOR", "STAFF", "ICT_AGENT")
                        .requestMatchers(HttpMethod.POST, "/api/categories/**").hasRole("ADMINISTRATOR")
                        .requestMatchers(HttpMethod.PUT, "/api/categories/**").hasRole("ADMINISTRATOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/categories/**").hasRole("ADMINISTRATOR")

                        // Department APIs rules
                        .requestMatchers(HttpMethod.GET, "/api/departments/**").hasAnyRole("ADMINISTRATOR", "STAFF", "ICT_AGENT")
                        .requestMatchers(HttpMethod.POST, "/api/departments/**").hasRole("ADMINISTRATOR")
                        .requestMatchers(HttpMethod.PUT, "/api/departments/**").hasRole("ADMINISTRATOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/departments/**").hasRole("ADMINISTRATOR")

                        // Users (ADMINISTRATOR and ICT_AGENT for most operations, but @PreAuthorize in UserController restricts further)
                        .requestMatchers("/api/users/**").hasAnyRole("ADMINISTRATOR", "ICT_AGENT")

                        // Tickets
                        .requestMatchers(HttpMethod.GET, "/api/tickets/**").hasAnyRole("STAFF", "ICT_AGENT", "ADMINISTRATOR")
                        .requestMatchers(HttpMethod.POST, "/api/tickets/**").hasAnyRole("STAFF", "ICT_AGENT", "ADMINISTRATOR")
                        .requestMatchers(HttpMethod.PUT, "/api/tickets/**").hasAnyRole("ICT_AGENT", "ADMINISTRATOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/tickets/**").hasRole("ADMINISTRATOR")

                        // Catch-all: All other API endpoints require authentication
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtTokenProvider, userDetailsService);
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration cfg) throws Exception {
        return cfg.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOrigins(List.of(
                "http://localhost:3000",
                "http://localhost:5173",
                "http://localhost:4200"
        ));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}