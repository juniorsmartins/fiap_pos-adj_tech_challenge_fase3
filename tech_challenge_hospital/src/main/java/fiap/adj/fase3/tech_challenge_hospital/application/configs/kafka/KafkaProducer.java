package fiap.adj.fase3.tech_challenge_hospital.application.configs.kafka;

import fiap.adj.fase3.tech_challenge_hospital.application.dtos.external.MensagemKafka;
import fiap.adj.fase3.tech_challenge_hospital.application.dtos.internal.ConsultaDto;
import fiap.adj.fase3.tech_challenge_hospital.domain.entities.enums.MotivoKafkaEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public final class KafkaProducer {

    private final KafkaTemplate<String, MensagemKafka> kafkaTemplate;

    private final KafkaPropertiesConfig kafkaPropertiesConfig;

    public ConsultaDto enviarEventoConsulta(ConsultaDto consultaDto, MotivoKafkaEnum motivo) {
        var mensagem = embalarEventoConsulta(consultaDto, motivo);
        kafkaTemplate.send(kafkaPropertiesConfig.topicoEventoInformarPacienteConsulta, UUID.randomUUID().toString(), mensagem);
        log.info("\n\n Mensagem enviada ao tópico de eventos de consulta: {}. \n\n", mensagem);
        return consultaDto;
    }

    private MensagemKafka embalarEventoConsulta(ConsultaDto dto, MotivoKafkaEnum motivo) {
        return new MensagemKafka(dto.getId(), dto.getDataHora(), dto.getStatus(), dto.getMedico().nome(), dto.getPaciente().nome(), dto.getPaciente().email(), motivo.getValue());
    }
}
