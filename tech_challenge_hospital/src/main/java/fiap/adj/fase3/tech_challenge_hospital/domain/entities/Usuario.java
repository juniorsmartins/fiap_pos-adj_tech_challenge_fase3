package fiap.adj.fase3.tech_challenge_hospital.domain.entities;

import fiap.adj.fase3.tech_challenge_hospital.application.dtos.internal.UserDto;
import fiap.adj.fase3.tech_challenge_hospital.application.dtos.request.UserRequestDto;
import fiap.adj.fase3.tech_challenge_hospital.domain.entities.enums.RoleEnum;
import fiap.adj.fase3.tech_challenge_hospital.infrastructure.ports.output.RoleOutputPort;
import fiap.adj.fase3.tech_challenge_hospital.infrastructure.ports.output.UsuarioOutputPort;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public final class Usuario {

    private Long id;

    private String username;

    private String password;

    private boolean enabled;

    private Role role;

    public Usuario(Long id, String username, String password, boolean enabled, Role role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.enabled = enabled;
        this.role = role;
    }

    public Usuario(String username, String password, boolean enabled, Role role) {
        this.username = username;
        this.password = password;
        this.enabled = enabled;
        this.role = role;
    }

    public Usuario(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public static Usuario criarUsuarioEntity(UserRequestDto dto, RoleEnum roleEnum, RoleOutputPort roleOutputPort) {
        var role = Role.consultarRolePorNome(roleEnum.getValue(), roleOutputPort);
        return new Usuario(dto.getUsername(), dto.getPassword(), true, role);
    }

    public static UserDto converterEntityParaDto(Usuario usuario) {
        var roleDto = Role.converterEntityParaDto(usuario.getRole());
        return new UserDto(usuario.getId(), usuario.getUsername(), usuario.getPassword(), usuario.isEnabled(), roleDto);
    }

    public static Usuario converterDtoParaEntity(UserDto dto) {
        var role = Role.converterDtoParaEntity(dto.role());
        return new Usuario(dto.id(), dto.username(), dto.password(), dto.enabled(), role);
    }

    public static void verificarDuplicidadeUsername(String username, UsuarioOutputPort usuarioOutputPort) {
        if (usuarioOutputPort.existsByUsername(username)) {
            throw new RuntimeException("Método = verificarDuplicidadeUsername - Username já existe: %s".formatted(username));
        }
    }
}
