package fiap.adj.fase3.tech_challenge_hospital.application.dtos.internal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public final class HistoricoMedicoDto {

    private Long id;

    private String diagnostico;

    private String prescricao;

    private String exames;

    private ConsultaDto consulta;
}
