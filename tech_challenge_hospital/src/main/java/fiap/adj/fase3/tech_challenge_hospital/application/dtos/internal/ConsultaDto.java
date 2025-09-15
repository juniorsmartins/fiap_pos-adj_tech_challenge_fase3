package fiap.adj.fase3.tech_challenge_hospital.application.dtos.internal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Setter
public final class ConsultaDto {

    private Long id;

    private LocalDateTime dataHora;

    private String status;

    private MedicoDto medico;

    private PacienteDto paciente;
}
