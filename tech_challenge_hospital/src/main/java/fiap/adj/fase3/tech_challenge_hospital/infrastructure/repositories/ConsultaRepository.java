package fiap.adj.fase3.tech_challenge_hospital.infrastructure.repositories;

import fiap.adj.fase3.tech_challenge_hospital.infrastructure.daos.ConsultaDao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.Set;

public interface ConsultaRepository extends JpaRepository<ConsultaDao, Long> {

    Optional<ConsultaDao> findByIdAndStatus(Long id, String status);

    Set<ConsultaDao> findAllByPacienteId(Long pacienteId);

    Optional<ConsultaDao> findByIdAndStatusNot(Long id, String status);

    @Query(
            value = "SELECT con.id, con.data_hora, con.status, con.medico, con.paciente " +
                    "FROM consultas AS con " +
                    "INNER JOIN medicos AS med " +
                    "   ON con.medico = med.id " +
                    "INNER JOIN pacientes AS pac " +
                    "   ON con.paciente = pac.id " +
                    "WHERE 1 = 1 " +
                    "   AND (:id IS NULL OR con.id = :id) " +
                    "   AND (:dataHora IS NULL OR con.data_hora = :dataHora) " +
                    "   AND (:status IS NULL OR con.status = :status) " +
                    "   AND (:medicoId IS NULL OR med.id = :medicoId) " +
                    "   AND (:pacienteId IS NULL OR pac.id = :pacienteId) ",
            nativeQuery = true
    )
    Set<ConsultaDao> pesquisar(@Param("id") Long id, @Param("dataHora") String dataHora, @Param("status") String status, @Param("medicoId") Long medicoId, @Param("pacienteId") Long pacienteId);
}
