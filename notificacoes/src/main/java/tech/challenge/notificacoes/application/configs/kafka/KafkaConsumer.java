package tech.challenge.notificacoes.application.configs.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import tech.challenge.notificacoes.application.dtos.MensagemKafka;

@Slf4j
@Component
@RequiredArgsConstructor
public final class KafkaConsumer {

    @KafkaListener(topics = "${spring.kafka.topic.evento-informar-paciente-consulta}", groupId = "grupo-notificacoes", containerFactory = "kafkaListenerContainerFactory")
    public void consumirEventoConsulta(MensagemKafka mensagem, Acknowledgment ack) {

        try {
            log.info("\n\n Notificações - Mensagem recebida no tópico de eventos de consulta: {}. \n\n", mensagem);
            ack.acknowledge(); // Confirmar o processamento da mensagem

        } catch (Exception e) {
            log.error("\n\n Notificações - Erro ao processar a mensagem: {}.\n\n", e.getMessage());
        }
    }
}
