package fiap.adj.fase3.tech_challenge_hospital.domain.entities;

import fiap.adj.fase3.tech_challenge_hospital.application.dtos.internal.HistoricoMedicoDto;
import fiap.adj.fase3.tech_challenge_hospital.application.dtos.request.HistoricoMedicoRequestDto;
import fiap.adj.fase3.tech_challenge_hospital.domain.entities.enums.ConsultaStatusEnum;
import fiap.adj.fase3.tech_challenge_hospital.infrastructure.ports.output.ConsultaOutputPort;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@Getter
@Setter
public final class HistoricoMedico {

    private Long id;

    private String diagnostico;

    private String prescricao;

    private String exames;

    private Consulta consulta;

    public HistoricoMedico(String diagnostico, String prescricao, String exames, Consulta consulta) {
        this.diagnostico = diagnostico;
        this.prescricao = prescricao;
        this.exames = exames;
        this.consulta = consulta;
    }

    public static HistoricoMedico converterRequestParaEntity(HistoricoMedicoRequestDto request, ConsultaOutputPort consultaOutputPort) {
        var consulta = consultaOutputPort.consultarPorIdComStatusNot(request.getConsultaId(), ConsultaStatusEnum.CANCELADO.getValue())
                .map(Consulta::converterDtoParaEntity)
                .orElseThrow();
        return new HistoricoMedico(request.getDiagnostico(), request.getPrescricao(), request.getExames(), consulta);
    }

    public static HistoricoMedicoDto converterEntityParaDto(HistoricoMedico historicoMedico) {
        var consultaDto = Consulta.converterEntityParaDto(historicoMedico.getConsulta());
        return new HistoricoMedicoDto(historicoMedico.getId(), historicoMedico.getDiagnostico(), historicoMedico.getPrescricao(), historicoMedico.getExames(), consultaDto);
    }
}
