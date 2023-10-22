package com.skipcart.config;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

import com.skipcart.filter.JwtAuthenticationEntryPoint;
import com.skipcart.filter.JwtAuthenticationFilter;
import com.skipcart.providers.JwtAuthenticationProvider;
import java.util.Collections;

import com.skipcart.providers.JwtAuthenticationSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

  private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

  private final JwtAuthenticationProvider jwtAuthenticationProvider;

  private final JwtAuthenticationSuccessHandler jwtAuthenticationSuccessHandler;

  @Bean
  public AuthenticationManager authenticationManager() {
    return new ProviderManager(Collections.singletonList(jwtAuthenticationProvider));
  }

  @Bean
  public JwtAuthenticationFilter jwtAuthenticationFilter() {
    var authenticationTokenFilter = new JwtAuthenticationFilter();
    authenticationTokenFilter.setAuthenticationManager(authenticationManager());
    authenticationTokenFilter.setAuthenticationSuccessHandler(jwtAuthenticationSuccessHandler);
    return authenticationTokenFilter;
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    return http.authorizeHttpRequests(
            authOpen ->
                authOpen.requestMatchers("/api/**").permitAll().anyRequest().authenticated())
        .csrf(AbstractHttpConfigurer::disable)
        .cors(AbstractHttpConfigurer::disable)
        .exceptionHandling(
            httpSecurityExceptionHandlingConfigurer ->
                httpSecurityExceptionHandlingConfigurer.authenticationEntryPoint(
                    jwtAuthenticationEntryPoint))
        .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
        .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
        .build();
  }
}
