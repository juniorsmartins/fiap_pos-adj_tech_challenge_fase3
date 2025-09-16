package fiap.adj.fase3.tech_challenge_hospital.domain.entities.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum MotivoEnum {

    AGENDAMENTO_CONSULTA("AGENDAMENTO_CONSULTA"),
    ALTERACAO_CONSULTA("ALTERACAO_CONSULTA");

    private final String value;
}
