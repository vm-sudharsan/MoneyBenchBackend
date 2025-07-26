package com.moneybench.MoneyBenchBackend.config;

import com.moneybench.MoneyBenchBackend.security.JwtRequestFilter;
import com.moneybench.MoneyBenchBackend.service.AppUserDetailsService;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig
{

    private final AppUserDetailsService appUserDetailsService;
    private final JwtRequestFilter jwtRequestFilter;

    // Uncomment this and modify if you want to use in-memory users for testing
    // @Bean
    // public UserDetailsService userDetailsService() {
    //     UserDetails admin = User.builder()
    //             .username("admin")
    //             .password(passwordEncoder().encode("admin123")) // encoded password
    //             .roles("ADMIN")
    //             .build();
    //
    //     UserDetails user = User.builder()
    //             .username("user")
    //             .password(passwordEncoder().encode("user123"))
    //             .roles("USER")
    //             .build();
    //
    //     return new InMemoryUserDetailsManager(admin, user);
    // }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/v1.0/profile/register",
                                "/api/v1.0/profile/activate",
                                "/api/v1.0/profile/login",
                                "/api/v1.0/health/**",
                                "/api/v1.0/status/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                // Disable default HTTP Basic auth for JWT-based stateless security
                // .httpBasic(Customizer.withDefaults())
                // Add JWT filter before username/password authentication filter
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class)
                // Set session management to stateless because we use JWTs
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

    // Commented legacy alternative; keep if you want formLogin or other settings later
    // @Bean
    // public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception
    // {
    //     httpSecurity.cors(Customizer.withDefaults())
    //             .csrf(AbstractHttpConfigurer::disable)
    //             .authorizeHttpRequests(auth -> auth.requestMatchers(
    //                             "/api/v1.0/profile/register",
    //                             "/api/v1.0/profile/activate",
    //                             "/api/v1.0/profile/login",
    //                             "/api/v1.0/health",
    //                             "/api/v1.0/status"
    //                     ).permitAll()
    //                     .and().formLogin(Customizer.withDefaults())
    //                     .anyRequest().authenticated());
    //     //.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
    //     //.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
    //     return httpSecurity.build();
    // }

    @Bean
    public PasswordEncoder passwordEncoder()
    {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource()
    {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization","Content-Type", "Accept"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager()
    {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(appUserDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return new ProviderManager(authenticationProvider);
    }
}
