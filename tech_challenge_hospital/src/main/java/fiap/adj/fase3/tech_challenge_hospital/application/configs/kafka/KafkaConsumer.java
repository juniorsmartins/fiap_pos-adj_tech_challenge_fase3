package fiap.adj.fase3.tech_challenge_hospital.application.configs.kafka;

import fiap.adj.fase3.tech_challenge_hospital.application.dtos.external.MensagemKafka;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public final class KafkaConsumer {

    @KafkaListener(topics = "${spring.kafka.topic.evento-informar-paciente-consulta}", groupId = "grupo-mensagem-kafka", containerFactory = "kafkaListenerContainerFactory")
    public void consumirEventoConsulta(MensagemKafka mensagem, Acknowledgment ack) {

        try {
            log.info("\n\n Mensagem recebida no tópico de eventos de consulta: {}. \n\n", mensagem);
            ack.acknowledge(); // Confirmar o processamento da mensagem
        } catch (Exception e) {
            log.error("\n\n Erro ao processar a mensagem: {}.\n\n", e.getMessage());
        }
    }
}
