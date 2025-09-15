package fiap.adj.fase3.tech_challenge_hospital.application.dtos.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public final class HistoricoMedicoRequestDto {

    private String diagnostico;

    private String prescricao;

    private String exames;

    @NotNull
    private Long consultaId;
}
