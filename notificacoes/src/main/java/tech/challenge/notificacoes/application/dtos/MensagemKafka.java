package tech.challenge.notificacoes.application.dtos;

import java.time.LocalDateTime;

public record MensagemKafka(Long id, LocalDateTime dataHora, String status, String nomeMedico, String nomePaciente, String emailPaciente, String motivo) {
}
