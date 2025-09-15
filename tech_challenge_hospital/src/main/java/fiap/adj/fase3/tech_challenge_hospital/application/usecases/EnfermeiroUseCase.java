package fiap.adj.fase3.tech_challenge_hospital.application.usecases;

import fiap.adj.fase3.tech_challenge_hospital.application.dtos.internal.EnfermeiroDto;
import fiap.adj.fase3.tech_challenge_hospital.application.dtos.request.EnfermeiroRequestDto;
import fiap.adj.fase3.tech_challenge_hospital.domain.entities.Enfermeiro;
import fiap.adj.fase3.tech_challenge_hospital.infrastructure.ports.input.EnfermeiroInputPort;
import fiap.adj.fase3.tech_challenge_hospital.infrastructure.ports.output.EnfermeiroOutputPort;
import fiap.adj.fase3.tech_challenge_hospital.infrastructure.ports.output.RoleOutputPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class EnfermeiroUseCase implements EnfermeiroInputPort {

    @Transactional
    @Override
    public EnfermeiroDto criar(EnfermeiroRequestDto request, EnfermeiroOutputPort enfermeiroOutputPort, RoleOutputPort roleOutputPort) {
        return Optional.ofNullable(request)
                .map(dto -> Enfermeiro.converterRequestParaEntity(dto, roleOutputPort))
                .map(Enfermeiro::converterEntityParaDto)
                .map(enfermeiroOutputPort::salvar)
                .orElseThrow();
    }

    @Transactional
    @Override
    public void apagarPorId(Long id, EnfermeiroOutputPort enfermeiroOutputPort) {
        enfermeiroOutputPort.consultarPorId(id)
                .ifPresentOrElse(dto -> enfermeiroOutputPort.apagarPorId(dto.id()), () -> {
                    throw new RuntimeException();
                });
    }

    @Transactional
    @Override
    public EnfermeiroDto atualizar(Long id, EnfermeiroRequestDto request, EnfermeiroOutputPort enfermeiroOutputPort) {
        return enfermeiroOutputPort.consultarPorId(id)
                .map(dto -> Enfermeiro.regraAtualizar(dto, request))
                .map(Enfermeiro::converterEntityParaDto)
                .map(enfermeiroOutputPort::salvar)
                .orElseThrow();
    }
}
