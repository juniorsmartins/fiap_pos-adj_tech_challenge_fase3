package fiap.adj.fase3.tech_challenge_hospital.infrastructure.ports.input;

import fiap.adj.fase3.tech_challenge_hospital.application.dtos.internal.HistoricoMedicoDto;
import fiap.adj.fase3.tech_challenge_hospital.application.dtos.request.HistoricoMedicoRequestDto;
import fiap.adj.fase3.tech_challenge_hospital.infrastructure.ports.output.ConsultaOutputPort;
import fiap.adj.fase3.tech_challenge_hospital.infrastructure.ports.output.HistoricoMedicoOutputPort;

public interface HistoricoMedicoInputPort {

    HistoricoMedicoDto criar(HistoricoMedicoRequestDto request, ConsultaOutputPort consultaOutputPort, HistoricoMedicoOutputPort historicoMedicoOutputPort);

    HistoricoMedicoDto atualizar(HistoricoMedicoRequestDto request, ConsultaOutputPort consultaOutputPort, HistoricoMedicoOutputPort historicoMedicoOutputPort);
}
