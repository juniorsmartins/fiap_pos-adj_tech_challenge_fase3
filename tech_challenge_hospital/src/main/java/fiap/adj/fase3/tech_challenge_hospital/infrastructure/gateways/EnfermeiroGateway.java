package fiap.adj.fase3.tech_challenge_hospital.infrastructure.gateways;

import fiap.adj.fase3.tech_challenge_hospital.application.dtos.internal.EnfermeiroDto;
import fiap.adj.fase3.tech_challenge_hospital.infrastructure.ports.output.EnfermeiroOutputPort;
import fiap.adj.fase3.tech_challenge_hospital.infrastructure.presenters.EnfermeiroPresenter;
import fiap.adj.fase3.tech_challenge_hospital.infrastructure.repositories.EnfermeiroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class EnfermeiroGateway implements EnfermeiroOutputPort {

    private final EnfermeiroRepository enfermeiroRepository;

    @Transactional
    @Override
    public EnfermeiroDto salvar(EnfermeiroDto dto) {
        return Optional.ofNullable(dto)
                .map(EnfermeiroPresenter::converterDtoParaDao)
                .map(enfermeiroRepository::save)
                .map(EnfermeiroPresenter::converterDaoParaDto)
                .orElseThrow();
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<EnfermeiroDto> consultarPorId(Long id) {
        return enfermeiroRepository.findById(id)
                .map(EnfermeiroPresenter::converterDaoParaDto);
    }

    @Transactional
    @Override
    public void apagarPorId(Long id) {
        enfermeiroRepository.deleteById(id);
    }
}
