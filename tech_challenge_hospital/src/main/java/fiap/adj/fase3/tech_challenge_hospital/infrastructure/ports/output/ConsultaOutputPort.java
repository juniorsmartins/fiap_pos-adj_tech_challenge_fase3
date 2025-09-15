package fiap.adj.fase3.tech_challenge_hospital.infrastructure.ports.output;

import fiap.adj.fase3.tech_challenge_hospital.application.dtos.internal.ConsultaDto;

import java.util.Optional;
import java.util.Set;

public interface ConsultaOutputPort {

    ConsultaDto salvar(ConsultaDto dto);

    Optional<ConsultaDto> consultarPorIdAndStatus(Long id, String status);

    Set<ConsultaDto> buscarHistoricoDeConsultasPorId(Long id);

    Optional<ConsultaDto> consultarPorIdComStatusNot(Long id, String status);

    Set<ConsultaDto> pesquisar(Long id, String dataHora, String status, Long medicoId, Long pacienteId);
}
