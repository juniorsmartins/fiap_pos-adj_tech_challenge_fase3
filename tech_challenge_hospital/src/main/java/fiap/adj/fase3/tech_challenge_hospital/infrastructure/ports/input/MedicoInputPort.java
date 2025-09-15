package fiap.adj.fase3.tech_challenge_hospital.infrastructure.ports.input;

import fiap.adj.fase3.tech_challenge_hospital.application.dtos.internal.MedicoDto;
import fiap.adj.fase3.tech_challenge_hospital.application.dtos.request.MedicoRequestDto;
import fiap.adj.fase3.tech_challenge_hospital.infrastructure.ports.output.MedicoOutputPort;
import fiap.adj.fase3.tech_challenge_hospital.infrastructure.ports.output.RoleOutputPort;

public interface MedicoInputPort {

    MedicoDto criar(MedicoRequestDto request, MedicoOutputPort medicoOutputPort, RoleOutputPort roleOutputPort);

    void apagarPorId(Long id, MedicoOutputPort medicoOutputPort);

    MedicoDto atualizar(Long id, MedicoRequestDto request, MedicoOutputPort medicoOutputPort);
}
