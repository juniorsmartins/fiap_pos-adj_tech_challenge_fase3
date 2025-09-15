package fiap.adj.fase3.tech_challenge_hospital.application.dtos.response;

import java.util.Set;

public record UserResponseDto(Long id, String username, String password, boolean enabled, Set<RoleResponseDto> roles) {
}
