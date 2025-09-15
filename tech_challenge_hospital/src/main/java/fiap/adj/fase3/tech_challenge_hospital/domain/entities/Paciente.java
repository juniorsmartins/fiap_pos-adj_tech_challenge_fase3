package fiap.adj.fase3.tech_challenge_hospital.domain.entities;

import fiap.adj.fase3.tech_challenge_hospital.application.dtos.internal.PacienteDto;
import fiap.adj.fase3.tech_challenge_hospital.application.dtos.request.PacienteRequestDto;
import fiap.adj.fase3.tech_challenge_hospital.domain.entities.enums.RoleEnum;
import fiap.adj.fase3.tech_challenge_hospital.infrastructure.ports.output.RoleOutputPort;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@Getter
@Setter
public final class Paciente {

    private Long id;

    private String nome;

    private String email;

    private Usuario user;

    public Paciente(String nome, String email, Usuario user) {
        this.nome = nome;
        this.email = email;
        this.user = user;
    }

    public static Paciente converterRequestParaEntity(PacienteRequestDto request, RoleOutputPort roleOutputPort) {
        var usuario = Usuario.criarUsuarioEntity(request.getUser(), RoleEnum.ROLE_PACIENTE, roleOutputPort);
        return new Paciente(request.getNome(), request.getEmail(), usuario);
    }

    public static PacienteDto converterEntityParaDto(Paciente paciente) {
        var userDto = Usuario.converterEntityParaDto(paciente.getUser());
        return new PacienteDto(paciente.getId(), paciente.getNome(), paciente.getEmail(), userDto);
    }

    public static Paciente converterDtoParaEntity(PacienteDto dto) {
        var usuario = Usuario.converterDtoParaEntity(dto.user());
        return new Paciente(dto.id(), dto.nome(), dto.email(), usuario);
    }

    public static Paciente regraAtualizar(PacienteDto dto, PacienteRequestDto request) {

        var userRequest = request.getUser();
        var usuario = new Usuario(userRequest.getUsername(), userRequest.getPassword());
        usuario.setId(dto.user().id());
        usuario.setEnabled(dto.user().enabled());

        Set<Role> roles = new HashSet<>();
        dto.user().roles()
                .forEach(roleDto -> {
                    roles.add(new Role(roleDto.id(), roleDto.name()));
                });
        usuario.setRoles(roles);

        return new Paciente(dto.id(), request.getNome(), request.getEmail(), usuario);
    }
}
