package fiap.adj.fase3.tech_challenge_hospital.application.dtos.response;

import java.time.LocalDateTime;

public record ConsultaResponseDto(Long id, LocalDateTime dataHora, String status, MedicoResponseDto medico, PacienteResponseDto paciente) {
}
