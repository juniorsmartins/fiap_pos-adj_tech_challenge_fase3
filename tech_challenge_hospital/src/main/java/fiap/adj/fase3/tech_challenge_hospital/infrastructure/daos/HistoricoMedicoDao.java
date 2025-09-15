package fiap.adj.fase3.tech_challenge_hospital.infrastructure.daos;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "historicos_medicos")
@AllArgsConstructor
@NoArgsConstructor
@Data
public final class HistoricoMedicoDao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String diagnostico;

    @Column(columnDefinition = "TEXT")
    private String prescricao;

    @Column(columnDefinition = "TEXT")
    private String exames;

    @OneToOne(fetch = FetchType.LAZY, targetEntity = ConsultaDao.class, optional = false)
    @JoinColumn(name = "consulta", nullable = false)
    private ConsultaDao consulta;
}
