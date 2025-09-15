package fiap.adj.fase3.tech_challenge_hospital.infrastructure.gateways;

import fiap.adj.fase3.tech_challenge_hospital.application.dtos.internal.HistoricoMedicoDto;
import fiap.adj.fase3.tech_challenge_hospital.application.dtos.request.FiltroHistoricoMedico;
import fiap.adj.fase3.tech_challenge_hospital.application.usecases.HistoricoMedicoPresenter;
import fiap.adj.fase3.tech_challenge_hospital.infrastructure.ports.output.HistoricoMedicoOutputPort;
import fiap.adj.fase3.tech_challenge_hospital.infrastructure.repositories.HistoricoMedicoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class HistoricoMedicoGateway implements HistoricoMedicoOutputPort {

    private final HistoricoMedicoRepository historicoMedicoRepository;

    @Transactional
    @Override
    public HistoricoMedicoDto salvar(HistoricoMedicoDto dto) {
        return Optional.ofNullable(dto)
                .map(HistoricoMedicoPresenter::converterDtoParaDao)
                .map(historicoMedicoRepository::save)
                .map(HistoricoMedicoPresenter::converterDaoParaDto)
                .orElseThrow();
    }

    @Override
    public Optional<HistoricoMedicoDto> consultarHistoricoMedicoPorIdConsulta(Long id) {
        return historicoMedicoRepository.consultarHistoricoMedicoPorIdConsulta(id)
                .map(HistoricoMedicoPresenter::converterDaoParaDto);
    }

    @Override
    public Set<HistoricoMedicoDto> listarHistoricoMedicoPorIdPaciente(Long id) {
        return historicoMedicoRepository.listarHistoricoMedicoPorIdPaciente(id)
                .stream()
                .map(HistoricoMedicoPresenter::converterDaoParaDto)
                .collect(Collectors.toSet());
    }

    @Override
    public Set<HistoricoMedicoDto> pesquisar(Long id, String diagnostico, String prescricao, String exames, Long consultaId) {
        return historicoMedicoRepository.pesquisar(id, diagnostico, prescricao, exames, consultaId)
                .stream()
                .map(HistoricoMedicoPresenter::converterDaoParaDto)
                .collect(Collectors.toSet());
    }
}
