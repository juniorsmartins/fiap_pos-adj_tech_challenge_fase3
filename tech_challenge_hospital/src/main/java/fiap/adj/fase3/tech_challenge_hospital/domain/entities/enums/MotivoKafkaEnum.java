package fiap.adj.fase3.tech_challenge_hospital.domain.entities.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum MotivoKafkaEnum {

    AGENDAMENTO("AGENDAMEMTO"),
    ALTERACAO("ALTERACAO");

    private final String value;
}
