package br.com.finsavior.config;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.http.HttpMethod;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import br.com.finsavior.security.CustomAuthenticationProvider;
import br.com.finsavior.security.JWTAuthenticationFilter;
import br.com.finsavior.security.TokenProvider;
import br.com.finsavior.security.UserSecurityDetails;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@EnableWebSecurity
@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
	
	private final CustomAuthenticationProvider customAuthenticationProvider;
	private static final String TEST_MOBILE_API_URL = "https://localhost"; //moveToConfigServer
	
	@Autowired
    UserSecurityDetails userSecurityDetails;
	
	@Autowired
	TokenProvider tokenProvider;

    public SecurityConfig(CustomAuthenticationProvider customAuthenticationProvider) {
        this.customAuthenticationProvider = customAuthenticationProvider;
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(customAuthenticationProvider);
    }

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

		http.authorizeHttpRequests(authorize -> authorize.requestMatchers("/**" ,"/login", "/login-auth").permitAll()
				.anyRequest().authenticated()
		).formLogin(form -> form.loginProcessingUrl("/login")
				.successHandler(this::loginSuccessHandler)
				.failureHandler(this::loginFailureHandler)
		).logout(logout -> logout.logoutUrl("/logout")
		).csrf(csrf -> csrf.disable());
		
		JWTAuthenticationFilter jwtAuthenticationFilter = new JWTAuthenticationFilter(tokenProvider, userSecurityDetails);
		http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
    AuthenticationManager authenticationManager() throws Exception {
        return new ProviderManager(Collections.singletonList(customAuthenticationProvider));
    }
	
	@Bean
    CorsFilter corsFilter() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedOrigins(Arrays.asList(
				"http://localhost:4200",
				"http://localhost:8100",
				TEST_MOBILE_API_URL
		));
        corsConfiguration.setAllowedMethods(Arrays.asList(
            HttpMethod.GET.name(),
            HttpMethod.POST.name(),
            HttpMethod.PUT.name(),
			HttpMethod.PATCH.name(),
            HttpMethod.DELETE.name()
        ));
        corsConfiguration.setAllowedHeaders(Arrays.asList(
            "Authorization",
            "Cache-Control",
            "Content-Type",
			"ngrok-skip-browser-warning"
        ));
		corsConfiguration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        
        return new CorsFilter(source);
    }

	private void loginSuccessHandler(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException {
		response.setStatus(HttpStatus.OK.value());
		response.getWriter().write("Login successful");
	}

	private void loginFailureHandler(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException {
		response.setStatus(HttpStatus.UNAUTHORIZED.value());
		response.getWriter().write("Login failed");
	}

}