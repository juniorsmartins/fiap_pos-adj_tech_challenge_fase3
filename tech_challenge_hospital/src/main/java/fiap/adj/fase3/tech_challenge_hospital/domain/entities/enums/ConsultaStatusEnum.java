package fiap.adj.fase3.tech_challenge_hospital.domain.entities.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ConsultaStatusEnum {

    AGENDADO("AGENDADO"),
    CONCLUIDO("CONCLUIDO"),
    CANCELADO("CANCELADO");

    private final String value;
}
