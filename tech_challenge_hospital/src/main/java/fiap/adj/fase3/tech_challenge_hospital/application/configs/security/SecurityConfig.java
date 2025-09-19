package fiap.adj.fase3.tech_challenge_hospital.application.configs.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration // Marca a classe como fonte de definições de beans - configurações do Spring.
@EnableWebSecurity // Habilita a segurança web do Spring Security - protege endpoints por padrão.
@EnableMethodSecurity(securedEnabled = true)
// Habilita segurança baseada em anotações nos métodos (ex: @PreAuthorize) - controle fino - quem pode chamar o quê.
public class SecurityConfig {

    @Bean // Configura as políticas de segurança para diferentes endpoints - quem pode acessar o quê
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity, CustomUserDetailsUseCase customUserDetailsUseCase) throws Exception {

        // Filtra os paths e aplica políticas de segurança
        httpSecurity
                .csrf(AbstractHttpConfigurer::disable) // Desabilita CSRF para simplificar (não recomendado para produção).
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        .requestMatchers("/h2-console/**").permitAll() // Permite acesso ao console H2 sem autenticação
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .defaultSuccessUrl("/graphiql", true)) // Redireciona para /graphql após login bem-sucedido
                .logout(LogoutConfigurer::permitAll); // Permite logout para todos os usuários

        httpSecurity.headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable)); // Permite exibir o console H2 em um iframe

        return httpSecurity.build();
    }

    @Bean // Define o provedor de autenticação - como buscar o usuário e validar a senha
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
