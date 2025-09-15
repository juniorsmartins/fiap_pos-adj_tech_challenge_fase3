package fiap.adj.fase3.tech_challenge_hospital.infrastructure.repositories;

import fiap.adj.fase3.tech_challenge_hospital.infrastructure.daos.MedicoDao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicoRepository extends JpaRepository<MedicoDao, Long> {
}
