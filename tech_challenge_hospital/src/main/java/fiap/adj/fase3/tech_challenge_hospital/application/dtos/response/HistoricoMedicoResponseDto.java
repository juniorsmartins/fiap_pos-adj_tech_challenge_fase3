package fiap.adj.fase3.tech_challenge_hospital.application.dtos.response;

public record HistoricoMedicoResponseDto(Long id, String diagnostico, String prescricao, String exames, ConsultaResponseDto consulta) {
}
