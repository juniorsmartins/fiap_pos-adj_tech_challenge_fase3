package fiap.adj.fase3.tech_challenge_hospital.application.configs.security;

import fiap.adj.fase3.tech_challenge_hospital.infrastructure.daos.UserDao;
import fiap.adj.fase3.tech_challenge_hospital.infrastructure.repositories.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsUseCase implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        UserDao userDao = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username));

        var authorities = userDao.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .toList();

        return new org.springframework.security.core.userdetails.User(
                userDao.getUsername(), userDao.getPassword(), userDao.isEnabled(), true,
                true, true, authorities
        );
    }
}
