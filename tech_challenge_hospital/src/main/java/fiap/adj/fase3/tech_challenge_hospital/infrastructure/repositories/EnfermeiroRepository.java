package fiap.adj.fase3.tech_challenge_hospital.infrastructure.repositories;

import fiap.adj.fase3.tech_challenge_hospital.infrastructure.daos.EnfermeiroDao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnfermeiroRepository extends JpaRepository<EnfermeiroDao, Long> {
}
