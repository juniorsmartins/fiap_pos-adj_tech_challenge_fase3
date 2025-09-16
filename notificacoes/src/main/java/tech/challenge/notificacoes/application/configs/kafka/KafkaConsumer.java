package tech.challenge.notificacoes.application.configs.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import tech.challenge.notificacoes.application.dtos.MensagemKafka;
import tech.challenge.notificacoes.infrastructure.ports.output.EmailOutputPort;

@Slf4j
@Component
@RequiredArgsConstructor
public final class KafkaConsumer {

    private final EmailOutputPort emailOutputPort;

    @KafkaListener(topics = "${spring.kafka.topic.evento-informar-paciente-consulta}", groupId = "grupo-notificacoes", containerFactory = "kafkaListenerContainerFactory")
    public void consumirEventoConsulta(MensagemKafka mensagem, Acknowledgment ack) {

        try {
            log.info("\n\n Notificações - Mensagem recebida no tópico de eventos de consulta: {}. \n\n", mensagem);
            emailOutputPort.enviarEmail(mensagem.emailPaciente(), mensagem.motivo(), formatarEmail(mensagem));
            ack.acknowledge(); // Confirmar o processamento da mensagem

        } catch (Exception e) {
            log.error("\n\n Notificações - Erro ao processar a mensagem: {}.\n\n", e.getMessage());
        }
    }

    private String formatarEmail(MensagemKafka mensagem) {
        return String.format("""
               Prezado(a), %s.\s

               Este é um e-mail informativo sobre sua consulta médica.\s

               ** Detalhes da consulta **
               Data: %s.
               Médico: %s.
               Status da consulta: %s.
              \s
               Atenciosamente, equipe da Clínica XPTO.
              \s""", mensagem.nomePaciente(), mensagem.dataHora(), mensagem.nomeMedico(), mensagem.status());
    }
}
