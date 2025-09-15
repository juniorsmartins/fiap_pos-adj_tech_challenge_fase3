package fiap.adj.fase3.tech_challenge_hospital.infrastructure.ports.input;

import fiap.adj.fase3.tech_challenge_hospital.application.dtos.internal.PacienteDto;
import fiap.adj.fase3.tech_challenge_hospital.application.dtos.request.PacienteRequestDto;
import fiap.adj.fase3.tech_challenge_hospital.infrastructure.ports.output.PacienteOutputPort;
import fiap.adj.fase3.tech_challenge_hospital.infrastructure.ports.output.RoleOutputPort;

public interface PacienteInputPort {

    PacienteDto criar(PacienteRequestDto request, PacienteOutputPort pacienteOutputPort, RoleOutputPort roleOutputPort);

    void apagarPorId(Long id, PacienteOutputPort outputPort);

    PacienteDto atualizar(Long id, PacienteRequestDto requestDto, PacienteOutputPort outputPort);
}
