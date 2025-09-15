package fiap.adj.fase3.tech_challenge_hospital.infrastructure.daos;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "consultas")
@AllArgsConstructor
@NoArgsConstructor
@Data
public final class ConsultaDao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime dataHora;

    private String status;

    @ManyToOne
    @JoinColumn(name = "medico", nullable = false)
    private MedicoDao medico;

    @ManyToOne
    @JoinColumn(name = "paciente", nullable = false)
    private PacienteDao paciente;
}
