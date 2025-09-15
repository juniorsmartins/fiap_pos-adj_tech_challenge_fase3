package fiap.adj.fase3.tech_challenge_hospital.application.dtos.internal;

import java.util.Set;

public record UserDto(Long id, String username, String password, boolean enabled, Set<RoleDto> roles) {
}
