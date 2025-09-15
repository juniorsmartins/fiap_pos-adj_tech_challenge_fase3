package fiap.adj.fase3.tech_challenge_hospital.domain.entities;

import fiap.adj.fase3.tech_challenge_hospital.application.dtos.internal.UserDto;
import fiap.adj.fase3.tech_challenge_hospital.application.dtos.request.UserRequestDto;
import fiap.adj.fase3.tech_challenge_hospital.domain.entities.enums.RoleEnum;
import fiap.adj.fase3.tech_challenge_hospital.infrastructure.ports.output.RoleOutputPort;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
public final class Usuario {

    private Long id;

    private String username;

    private String password;

    private boolean enabled;

    private Set<Role> roles;

    public Usuario(Long id, String username, String password, boolean enabled, Set<Role> roles) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.enabled = enabled;
        this.roles = roles;
    }

    public Usuario(String username, String password, boolean enabled, Set<Role> roles) {
        this.username = username;
        this.password = password;
        this.enabled = enabled;
        this.roles = roles;
    }

    public Usuario(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public static Usuario criarUsuarioEntity(UserRequestDto dto, RoleEnum roleEnum, RoleOutputPort roleOutputPort) {
        var role = Role.consultarRolePorNome(roleEnum.getValue(), roleOutputPort);
        return new Usuario(dto.getUsername(), dto.getPassword(), true, Set.of(role));
    }

    public static UserDto converterEntityParaDto(Usuario usuario) {
        var roleDto = usuario.getRoles().stream()
                .map(Role::converterEntityParaDto)
                .collect(Collectors.toSet());

        return new UserDto(usuario.getId(), usuario.getUsername(), usuario.getPassword(), usuario.isEnabled(), roleDto);
    }

    public static Usuario converterDtoParaEntity(UserDto dto) {
        var role = dto.roles().stream()
                .map(Role::converterDtoParaEntity)
                .collect(Collectors.toSet());

        return new Usuario(dto.id(), dto.username(), dto.password(), dto.enabled(), role);
    }
}
