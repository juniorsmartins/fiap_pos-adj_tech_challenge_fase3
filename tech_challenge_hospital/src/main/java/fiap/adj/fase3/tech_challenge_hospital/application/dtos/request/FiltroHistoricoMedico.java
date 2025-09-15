package fiap.adj.fase3.tech_challenge_hospital.application.dtos.request;

public record FiltroHistoricoMedico(Long id, String diagnostico, String prescricao, String exames, Long consultaId) {
}
