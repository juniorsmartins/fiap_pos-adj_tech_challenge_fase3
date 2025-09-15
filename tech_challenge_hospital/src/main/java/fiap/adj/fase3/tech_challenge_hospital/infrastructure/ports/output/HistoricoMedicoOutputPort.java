package fiap.adj.fase3.tech_challenge_hospital.infrastructure.ports.output;

import fiap.adj.fase3.tech_challenge_hospital.application.dtos.internal.HistoricoMedicoDto;

import java.util.Optional;
import java.util.Set;

public interface HistoricoMedicoOutputPort {

    HistoricoMedicoDto salvar(HistoricoMedicoDto dto);

    Optional<HistoricoMedicoDto> consultarHistoricoMedicoPorIdConsulta(Long id);

    Set<HistoricoMedicoDto> listarHistoricoMedicoPorIdPaciente(Long id);

    Set<HistoricoMedicoDto> pesquisar(Long id, String diagnostico, String prescricao, String exames, Long consultaId);
}
