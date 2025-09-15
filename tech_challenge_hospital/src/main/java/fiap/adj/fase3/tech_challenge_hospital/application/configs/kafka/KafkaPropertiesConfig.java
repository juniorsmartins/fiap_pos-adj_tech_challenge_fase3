package fiap.adj.fase3.tech_challenge_hospital.application.configs.kafka;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
@Setter
public class KafkaPropertiesConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    public String bootstrapServers;

    @Value(value = "${spring.kafka.topic.evento-informar-paciente-consulta}")
    public String topicoEventoInformarPacienteConsulta;

    @Value(value = "${spring.kafka.topic.evento-atualizar-status-consulta}")
    public String topicoEventoAtualizarStatusConsulta;
}
