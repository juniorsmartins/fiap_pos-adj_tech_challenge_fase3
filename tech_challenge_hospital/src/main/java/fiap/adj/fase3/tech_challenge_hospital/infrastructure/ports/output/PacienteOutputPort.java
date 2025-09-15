package fiap.adj.fase3.tech_challenge_hospital.infrastructure.ports.output;

import fiap.adj.fase3.tech_challenge_hospital.application.dtos.internal.PacienteDto;

import java.util.Optional;

public interface PacienteOutputPort {

    PacienteDto salvar(PacienteDto dto);

    Optional<PacienteDto> consultarPorId(Long id);

    void apagarPorId(Long id);
}
