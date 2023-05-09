package com.group35.motionguard.config;

import com.group35.motionguard.features.account.AuthDetailsSource;
import com.group35.motionguard.features.account.AuthProvider;
import com.group35.motionguard.features.account.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final AuthProvider authProvider;
    private final UserService userService;
    private final AuthDetailsSource authDetailsSource;

    private static final String[] PI_HOLDER_PAGES = new String[]{
            "/",
            "/devices",
            "/detections",
            "/register",
            "/profile",
            "/camera/**"
    };

    private static final String[] ADMIN_ONLY_PAGES = new String[]{
    };

    @Bean
    public AuthenticationManager authManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authManager = http.getSharedObject(AuthenticationManagerBuilder.class);
        authManager.authenticationProvider(authProvider);
        return authManager.build();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.authorizeHttpRequests(requests -> requests
                        .antMatchers(HttpMethod.GET, PI_HOLDER_PAGES).hasAnyAuthority("PI_HOLDER", "ADMIN")
                        .antMatchers(HttpMethod.GET, ADMIN_ONLY_PAGES).hasAnyAuthority("ADMIN")
                        .anyRequest().permitAll()
                )
                .formLogin(formLogin -> formLogin
                        .loginPage("/login")
                        .loginProcessingUrl("/perform-login")
                        .usernameParameter("username")
                        .passwordParameter("password")
                        .authenticationDetailsSource(authDetailsSource)
                        .failureHandler((request, response, e) -> response.sendError(403, e.getMessage()))
                        .successHandler((request, response, auth) -> response.setStatus(200))
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login")
                        .deleteCookies("JSESSIONID")
                )
                .rememberMe(rememberMe -> rememberMe
                        .userDetailsService(userService)
                        .tokenValiditySeconds(3600)
                        .key("uniqueAndSecret")
                )
                .csrf().disable() // TODO: Upon deployment, enable protection.
                .httpBasic().disable()
                .build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return userService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
