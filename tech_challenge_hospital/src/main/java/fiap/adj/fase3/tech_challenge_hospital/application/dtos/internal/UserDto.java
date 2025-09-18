package fiap.adj.fase3.tech_challenge_hospital.application.dtos.internal;

public record UserDto(Long id, String username, String password, boolean enabled, RoleDto role) {
}
