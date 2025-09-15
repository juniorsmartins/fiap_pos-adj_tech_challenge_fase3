package fiap.adj.fase3.tech_challenge_hospital.infrastructure.ports.input;

import fiap.adj.fase3.tech_challenge_hospital.application.dtos.internal.ConsultaDto;
import fiap.adj.fase3.tech_challenge_hospital.application.dtos.request.ConsultaRequestDto;
import fiap.adj.fase3.tech_challenge_hospital.infrastructure.ports.output.ConsultaOutputPort;
import fiap.adj.fase3.tech_challenge_hospital.infrastructure.ports.output.MedicoOutputPort;
import fiap.adj.fase3.tech_challenge_hospital.infrastructure.ports.output.PacienteOutputPort;

public interface ConsultaInputPort {

    ConsultaDto criar(ConsultaRequestDto request, MedicoOutputPort medicoOutputPort, PacienteOutputPort pacienteOutputPort, ConsultaOutputPort consultaOutputPort);

    ConsultaDto atualizar(Long id, ConsultaRequestDto request, MedicoOutputPort medicoOutputPort, PacienteOutputPort pacienteOutputPort, ConsultaOutputPort consultaOutputPort);

    ConsultaDto consultarPorId(Long id, ConsultaOutputPort consultaOutputPort);

    void concluir(Long id, ConsultaOutputPort consultaOutputPort);

    void cancelar(Long id, ConsultaOutputPort consultaOutputPort);
}
