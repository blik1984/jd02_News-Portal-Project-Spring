package com.epam.edu.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

		http.authorizeHttpRequests(
				auth -> auth.requestMatchers("/", "/page_registration", "/user/registration", "/page_auth",
						"/page_privacy", "/css/**", "/js/**", "/images/**").permitAll().anyRequest().authenticated())
				.formLogin(form -> form.loginPage("/page_auth").loginProcessingUrl("/do_auth")
						.defaultSuccessUrl("/", true).failureUrl("/page_auth?error").permitAll())
				.logout(logout -> logout.permitAll());

		return http.build();
	}
}
