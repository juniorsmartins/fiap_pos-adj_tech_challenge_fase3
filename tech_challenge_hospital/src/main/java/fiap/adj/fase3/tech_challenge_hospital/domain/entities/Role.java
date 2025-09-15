package fiap.adj.fase3.tech_challenge_hospital.domain.entities;

import fiap.adj.fase3.tech_challenge_hospital.application.dtos.internal.RoleDto;
import fiap.adj.fase3.tech_challenge_hospital.infrastructure.ports.output.RoleOutputPort;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public final class Role {

    private Long id;

    private String name;

    public Role(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Role(String name) {
        this.name = name;
    }

    public static Role consultarRolePorNome(String nome, RoleOutputPort roleOutputPort) {
        return roleOutputPort.consultarPorNome(nome)
                .map(Role::converterDtoParaEntity)
                .orElseThrow();
    }

    public static Role converterDtoParaEntity(RoleDto dto) {
        return new Role(dto.id(), dto.name());
    }

    public static RoleDto converterEntityParaDto(Role role) {
        return new RoleDto(role.getId(), role.getName());
    }
}
