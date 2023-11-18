package com.training.denmit.helpdeskApi.config.security;

import com.training.denmit.helpdeskApi.config.security.jwt.JwtConfigurer;
import com.training.denmit.helpdeskApi.config.security.jwt.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.web.cors.CorsConfiguration;

import java.util.Arrays;
import java.util.Collections;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private static final String EMPLOYEE = "EMPLOYEE";
    private static final String MANAGER = "MANAGER";
    private static final String ENGINEER = "ROLE_ENGINEER";

    private final JwtTokenProvider jwtTokenProvider;

    @Value("${spring.permitted.url}")
    private String permittedUrl;

    public SecurityConfig(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return new CustomAuthenticationEntryPoint();
    }

    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        http.headers().frameOptions().disable();

        CorsConfiguration corsConfiguration = new CorsConfiguration();

        corsConfiguration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type"));
        corsConfiguration.setAllowedOrigins(Collections.singletonList(permittedUrl));
        corsConfiguration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PUT", "OPTIONS", "PATCH", "DELETE"));
        corsConfiguration.setExposedHeaders(Collections.singletonList("Authorization"));

        http.cors().configurationSource(request -> corsConfiguration);

        http
                .httpBasic().disable()
                .cors()
                .and()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers("/", "/auth", "/tickets/**", "/swagger-ui/**", "/swagger-ui.html/**", "/v2/api-docs/**", "/swagger-resources/**", "/webjars/**", "/configuration/**").permitAll()
                .antMatchers("/tickets").hasAnyRole(EMPLOYEE, MANAGER, ENGINEER)
                .antMatchers("/add-ticket/**").hasAnyRole(EMPLOYEE, MANAGER)
                .antMatchers("/update-ticket/**").hasAnyRole(EMPLOYEE, MANAGER)
                .antMatchers("/feedbacks/**").hasAnyRole(EMPLOYEE, MANAGER, ENGINEER)
                .anyRequest().authenticated()
                .and()
                .exceptionHandling().authenticationEntryPoint(authenticationEntryPoint())
                .and()
                .apply(new JwtConfigurer(jwtTokenProvider));
        http.headers().frameOptions().disable();
    }
}
