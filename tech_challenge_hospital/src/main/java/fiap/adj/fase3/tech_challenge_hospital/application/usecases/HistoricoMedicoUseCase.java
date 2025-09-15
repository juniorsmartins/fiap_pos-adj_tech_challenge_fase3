package fiap.adj.fase3.tech_challenge_hospital.application.usecases;

import fiap.adj.fase3.tech_challenge_hospital.application.dtos.internal.HistoricoMedicoDto;
import fiap.adj.fase3.tech_challenge_hospital.application.dtos.request.HistoricoMedicoRequestDto;
import fiap.adj.fase3.tech_challenge_hospital.domain.entities.HistoricoMedico;
import fiap.adj.fase3.tech_challenge_hospital.infrastructure.ports.input.HistoricoMedicoInputPort;
import fiap.adj.fase3.tech_challenge_hospital.infrastructure.ports.output.ConsultaOutputPort;
import fiap.adj.fase3.tech_challenge_hospital.infrastructure.ports.output.HistoricoMedicoOutputPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class HistoricoMedicoUseCase implements HistoricoMedicoInputPort {

    @Transactional
    @Override
    public HistoricoMedicoDto criar(HistoricoMedicoRequestDto request, ConsultaOutputPort consultaOutputPort, HistoricoMedicoOutputPort historicoMedicoOutputPort) {
        return Optional.ofNullable(request)
                .map(dto -> HistoricoMedico.converterRequestParaEntity(request, consultaOutputPort))
                .map(HistoricoMedico::converterEntityParaDto)
                .map(historicoMedicoOutputPort::salvar)
                .orElseThrow();
    }

    @Override
    public HistoricoMedicoDto atualizar(HistoricoMedicoRequestDto request, ConsultaOutputPort consultaOutputPort, HistoricoMedicoOutputPort historicoMedicoOutputPort) {
        return historicoMedicoOutputPort.consultarHistoricoMedicoPorIdConsulta(request.getConsultaId())
                .map(dtoDoBanco -> {
                    var entity = HistoricoMedico.converterRequestParaEntity(request, consultaOutputPort);
                    entity.setId(dtoDoBanco.getId());
                    return entity;
                })
                .map(HistoricoMedico::converterEntityParaDto)
                .map(historicoMedicoOutputPort::salvar)
                .orElseThrow();
    }
}
