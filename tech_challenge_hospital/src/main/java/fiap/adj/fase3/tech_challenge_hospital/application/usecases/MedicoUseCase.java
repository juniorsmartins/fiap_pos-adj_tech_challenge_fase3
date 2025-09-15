package fiap.adj.fase3.tech_challenge_hospital.application.usecases;

import fiap.adj.fase3.tech_challenge_hospital.application.dtos.internal.MedicoDto;
import fiap.adj.fase3.tech_challenge_hospital.application.dtos.request.MedicoRequestDto;
import fiap.adj.fase3.tech_challenge_hospital.domain.entities.Medico;
import fiap.adj.fase3.tech_challenge_hospital.infrastructure.ports.input.MedicoInputPort;
import fiap.adj.fase3.tech_challenge_hospital.infrastructure.ports.output.MedicoOutputPort;
import fiap.adj.fase3.tech_challenge_hospital.infrastructure.ports.output.RoleOutputPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class MedicoUseCase implements MedicoInputPort {

    @Transactional
    @Override
    public MedicoDto criar(MedicoRequestDto request, MedicoOutputPort medicoOutputPort, RoleOutputPort roleOutputPort) {
        return Optional.ofNullable(request)
            .map(dto -> Medico.converterRequestParaEntity(dto, roleOutputPort))
            .map(Medico::converterEntityParaDto)
            .map(medicoOutputPort::salvar)
            .orElseThrow();
    }

    @Transactional
    @Override
    public void apagarPorId(Long id, MedicoOutputPort medicoOutputPort) {
        medicoOutputPort.consultarPorId(id)
            .ifPresentOrElse(dto -> medicoOutputPort.apagarPorId(dto.id()), () -> {
                throw new RuntimeException();
            });
    }

    @Transactional
    @Override
    public MedicoDto atualizar(Long id, MedicoRequestDto request, MedicoOutputPort medicoOutputPort) {
        return medicoOutputPort.consultarPorId(id)
            .map(dto -> Medico.regraAtualizar(request, dto))
            .map(Medico::converterEntityParaDto)
            .map(medicoOutputPort::salvar)
            .orElseThrow();
    }
}
