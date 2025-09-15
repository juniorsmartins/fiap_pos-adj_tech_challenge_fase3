package fiap.adj.fase3.tech_challenge_hospital.infrastructure.ports.output;

import fiap.adj.fase3.tech_challenge_hospital.application.dtos.internal.EnfermeiroDto;

import java.util.Optional;

public interface EnfermeiroOutputPort {

    EnfermeiroDto salvar(EnfermeiroDto dto);

    Optional<EnfermeiroDto> consultarPorId(Long id);

    void apagarPorId(Long id);
}
