package fiap.adj.fase3.tech_challenge_hospital.application.configs.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // SecurityFilterChain - Sequência de filtros de URL para segurança
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity, CustomUserDetailsUseCase customUserDetailsUseCase) throws Exception {

        // Filtra os paths e aplica políticas de segurança
        httpSecurity.authorizeHttpRequests(authorizeRequests ->
            authorizeRequests
                    .requestMatchers("/h2-console/**").permitAll()
                    .requestMatchers("/graphiql/**").permitAll()
                    .requestMatchers("/graphql/**").permitAll()
                    .requestMatchers("/admin/**").hasRole("ADMIN")
                    .requestMatchers("/user/**").hasRole("USER")
                            .anyRequest().authenticated()
        ).formLogin(form -> form.successHandler(customAuthenticationSuccessHandler())
        ).logout(LogoutConfigurer::permitAll);

        httpSecurity.headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable));
        httpSecurity.csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**"));
        httpSecurity.csrf(csrf -> csrf.ignoringRequestMatchers("/graphiql/**"));
        httpSecurity.csrf(csrf -> csrf.ignoringRequestMatchers("/graphql/**"));

        return httpSecurity.build();
    }

    @Bean
    public AuthenticationSuccessHandler customAuthenticationSuccessHandler() {

        return new AuthenticationSuccessHandler() {
            @Override
            public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
                if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
                    response.sendRedirect("/admin");
                } else if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_USER"))) {
                    response.sendRedirect("/user");
                } else {
                    response.sendRedirect("/");
                }
            }
        };
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(CustomUserDetailsUseCase userDetailsUseCase, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authentication = new DaoAuthenticationProvider(userDetailsUseCase);
        authentication.setPasswordEncoder(passwordEncoder);
        return authentication;
    }

    // Define como será a codificação do password
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new PlainTextPasswordEncoder();
    }

    private class PlainTextPasswordEncoder implements PasswordEncoder {

        @Override
        public String encode(CharSequence rawPassword) {
            return rawPassword.toString();
        }

        @Override
        public boolean matches(CharSequence rawPassword, String encodedPassword) {
            return rawPassword.toString().equals(encodedPassword);
        }
    }
}
