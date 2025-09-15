package fiap.adj.fase3.tech_challenge_hospital.domain.entities;

import fiap.adj.fase3.tech_challenge_hospital.application.dtos.internal.MedicoDto;
import fiap.adj.fase3.tech_challenge_hospital.application.dtos.request.MedicoRequestDto;
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
public final class Medico {

    private Long id;

    private String nome;

    private Usuario user;

    public Medico(String nome, Usuario user) {
        this.nome = nome;
        this.user = user;
    }

    public static Medico converterRequestParaEntity(MedicoRequestDto request, RoleOutputPort roleOutputPort) {
        var usuario = Usuario.criarUsuarioEntity(request.getUser(), RoleEnum.ROLE_MEDICO, roleOutputPort);
        return new Medico(request.getNome(), usuario);
    }

    public static MedicoDto converterEntityParaDto(Medico medico) {
        var userDto = Usuario.converterEntityParaDto(medico.getUser());
        return new MedicoDto(medico.getId(), medico.getNome(), userDto);
    }

    public static Medico converterDtoParaEntity(MedicoDto dto) {
        var usuario = Usuario.converterDtoParaEntity(dto.user());
        return new Medico(dto.id(), dto.nome(), usuario);

    }

    public static Medico regraAtualizar(MedicoRequestDto request, MedicoDto dto) {

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

        return new Medico(dto.id(), request.getNome(), usuario);
    }
}
