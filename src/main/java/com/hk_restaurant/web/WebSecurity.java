package com.hk_restaurant.web;

import com.hk_restaurant.service.NhanVienService;
import org.springframework.context.annotation.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import java.util.List;

@Configuration
public class WebSecurity {
    @Bean AuthenticationProvider restaurantAuthentication(NhanVienService employees) {
        return new AuthenticationProvider() {
            public Authentication authenticate(Authentication input) {
                try {
                    var nv = employees.dangNhap(input.getName(), String.valueOf(input.getCredentials()));
                    return UsernamePasswordAuthenticationToken.authenticated(nv.getTenDangNhap(), null,
                            List.of(new SimpleGrantedAuthority("ROLE_" + nv.getVaiTro().name())));
                } catch (com.hk_restaurant.exception.AuthenticationException |
                         com.hk_restaurant.exception.ValidationException e) {
                    throw new BadCredentialsException("Sai thong tin dang nhap hoac tai khoan da bi khoa.");
                }
            }
            public boolean supports(Class<?> type) { return UsernamePasswordAuthenticationToken.class.isAssignableFrom(type); }
        };
    }
    @Bean SecurityFilterChain security(HttpSecurity http, AuthenticationProvider provider) throws Exception {
        return http.authenticationProvider(provider)
                .addFilterBefore(new ActiveEmployeeFilter(), org.springframework.security.web.access.intercept.AuthorizationFilter.class)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/login", "/app.css", "/app.js", "/api/csrf", "/error").permitAll()
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated())
                .formLogin(login -> login.loginPage("/login").loginProcessingUrl("/login")
                        .successHandler((req, res, a) -> { res.setStatus(204); })
                        .failureHandler((req, res, e) -> { res.setStatus(401); }))
                .logout(logout -> logout.logoutUrl("/logout").logoutSuccessHandler((req, res, a) -> res.setStatus(204)))
                .exceptionHandling(errors -> errors.authenticationEntryPoint((req, res, e) -> res.sendError(401))
                        .accessDeniedHandler((req, res, e) -> res.sendError(403)))
                .build();
    }
}
