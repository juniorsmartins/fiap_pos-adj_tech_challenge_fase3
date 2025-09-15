package fiap.adj.fase3.tech_challenge_hospital.infrastructure.ports.output;

import fiap.adj.fase3.tech_challenge_hospital.application.dtos.internal.MedicoDto;

import java.util.Optional;

public interface MedicoOutputPort {

    MedicoDto salvar(MedicoDto dto);

    Optional<MedicoDto> consultarPorId(Long id);

    void apagarPorId(Long id);
}
