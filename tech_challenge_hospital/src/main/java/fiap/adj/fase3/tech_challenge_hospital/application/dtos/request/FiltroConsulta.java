package fiap.adj.fase3.tech_challenge_hospital.application.dtos.request;

public record FiltroConsulta(Long id, String dataHora, String status, Long medicoId, Long pacienteId) {
}
