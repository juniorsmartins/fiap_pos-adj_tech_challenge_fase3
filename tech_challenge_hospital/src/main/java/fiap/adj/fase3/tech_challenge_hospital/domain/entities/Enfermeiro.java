package fiap.adj.fase3.tech_challenge_hospital.domain.entities;

import fiap.adj.fase3.tech_challenge_hospital.application.dtos.internal.EnfermeiroDto;
import fiap.adj.fase3.tech_challenge_hospital.application.dtos.request.EnfermeiroRequestDto;
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
public final class Enfermeiro {

    private Long id;

    private String nome;

    private Usuario user;

    public Enfermeiro(String nome, Usuario user) {
        this.nome = nome;
        this.user = user;
    }

    public static Enfermeiro converterRequestParaEntity(EnfermeiroRequestDto request, RoleOutputPort roleOutputPort) {
        var usuario = Usuario.criarUsuarioEntity(request.getUser(), RoleEnum.ROLE_ENFERMEIRO, roleOutputPort);
        return new Enfermeiro(request.getNome(), usuario);
    }

    public static EnfermeiroDto converterEntityParaDto(Enfermeiro enfermeiro) {
        var userDto = Usuario.converterEntityParaDto(enfermeiro.getUser());
        return new EnfermeiroDto(enfermeiro.getId(), enfermeiro.getNome(), userDto);
    }

    public static Enfermeiro regraAtualizar(EnfermeiroDto dto, EnfermeiroRequestDto request) {

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

        return new Enfermeiro(dto.id(), request.getNome(), usuario);
    }
}
