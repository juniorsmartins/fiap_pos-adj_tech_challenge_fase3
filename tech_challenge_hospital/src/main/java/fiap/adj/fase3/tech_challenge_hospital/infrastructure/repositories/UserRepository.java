package fiap.adj.fase3.tech_challenge_hospital.infrastructure.repositories;

import fiap.adj.fase3.tech_challenge_hospital.infrastructure.daos.UserDao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserDao, Long> {

    Optional<UserDao> findByUsername(String username);
}
