package fiap.adj.fase3.tech_challenge_hospital.infrastructure.gateways;

import fiap.adj.fase3.tech_challenge_hospital.application.dtos.internal.PacienteDto;
import fiap.adj.fase3.tech_challenge_hospital.infrastructure.ports.output.PacienteOutputPort;
import fiap.adj.fase3.tech_challenge_hospital.infrastructure.presenters.PacientePresenter;
import fiap.adj.fase3.tech_challenge_hospital.infrastructure.repositories.PacienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PacienteGateway implements PacienteOutputPort {

    private final PacienteRepository pacienteRepository;

    @Transactional
    @Override
    public PacienteDto salvar(PacienteDto dto) {
        return Optional.ofNullable(dto)
                .map(PacientePresenter::converterDtoParaDao)
                .map(pacienteRepository::save)
                .map(PacientePresenter::converterDaoParaDto)
                .orElseThrow();
    }

    @Override
    public Optional<PacienteDto> consultarPorId(Long id) {
        return pacienteRepository.findById(id)
                .map(PacientePresenter::converterDaoParaDto);
    }

    @Override
    public void apagarPorId(Long id) {
        pacienteRepository.deleteById(id);
    }
}
