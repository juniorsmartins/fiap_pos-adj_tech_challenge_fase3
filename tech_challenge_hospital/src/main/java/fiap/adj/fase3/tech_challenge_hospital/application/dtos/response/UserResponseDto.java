package fiap.adj.fase3.tech_challenge_hospital.application.dtos.response;

public record UserResponseDto(Long id, String username, String password, boolean enabled, RoleResponseDto role) {
}
