package fiap.adj.fase3.tech_challenge_hospital.infrastructure.ports.input;

import fiap.adj.fase3.tech_challenge_hospital.application.dtos.internal.EnfermeiroDto;
import fiap.adj.fase3.tech_challenge_hospital.application.dtos.request.EnfermeiroRequestDto;
import fiap.adj.fase3.tech_challenge_hospital.infrastructure.ports.output.EnfermeiroOutputPort;
import fiap.adj.fase3.tech_challenge_hospital.infrastructure.ports.output.RoleOutputPort;

public interface EnfermeiroInputPort {

    EnfermeiroDto criar(EnfermeiroRequestDto request, EnfermeiroOutputPort enfermeiroOutputPort, RoleOutputPort roleOutputPort);

    void apagarPorId(Long id, EnfermeiroOutputPort enfermeiroOutputPort);

    EnfermeiroDto atualizar(Long id, EnfermeiroRequestDto request, EnfermeiroOutputPort enfermeiroOutputPort);
}
