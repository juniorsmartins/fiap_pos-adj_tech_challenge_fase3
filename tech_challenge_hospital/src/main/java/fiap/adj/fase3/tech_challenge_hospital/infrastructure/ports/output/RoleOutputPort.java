package fiap.adj.fase3.tech_challenge_hospital.infrastructure.ports.output;

import fiap.adj.fase3.tech_challenge_hospital.application.dtos.internal.RoleDto;

import java.util.Optional;

public interface RoleOutputPort {

    Optional<RoleDto> consultarPorNome(String nome);
}
