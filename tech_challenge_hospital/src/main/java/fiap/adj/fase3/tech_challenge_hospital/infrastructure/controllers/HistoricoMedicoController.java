package fiap.adj.fase3.tech_challenge_hospital.infrastructure.controllers;

import fiap.adj.fase3.tech_challenge_hospital.application.dtos.request.FiltroHistoricoMedico;
import fiap.adj.fase3.tech_challenge_hospital.application.dtos.request.HistoricoMedicoRequestDto;
import fiap.adj.fase3.tech_challenge_hospital.application.dtos.response.HistoricoMedicoResponseDto;
import fiap.adj.fase3.tech_challenge_hospital.infrastructure.presenters.HistoricoMedicoPresenter;
import fiap.adj.fase3.tech_challenge_hospital.infrastructure.ports.input.HistoricoMedicoInputPort;
import fiap.adj.fase3.tech_challenge_hospital.infrastructure.ports.output.ConsultaOutputPort;
import fiap.adj.fase3.tech_challenge_hospital.infrastructure.ports.output.HistoricoMedicoOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class HistoricoMedicoController {

    private final HistoricoMedicoInputPort historicoMedicoInputPort;

    private final HistoricoMedicoOutputPort historicoMedicoOutputPort;

    private final ConsultaOutputPort consultaOutputPort;

    @Secured({"ROLE_MEDICO"})
    @MutationMapping
    public HistoricoMedicoResponseDto criarHistoricoMedico(@Argument HistoricoMedicoRequestDto request) {
        return Optional.ofNullable(request)
                .map(dto -> historicoMedicoInputPort.criar(dto, consultaOutputPort, historicoMedicoOutputPort))
                .map(HistoricoMedicoPresenter::converterDtoParaResponse)
                .orElseThrow();
    }

    @Secured({"ROLE_MEDICO"})
    @MutationMapping
    public HistoricoMedicoResponseDto atualizarHistoricoMedico(@Argument HistoricoMedicoRequestDto request) {
        return Optional.ofNullable(request)
                .map(dto -> historicoMedicoInputPort.atualizar(dto, consultaOutputPort, historicoMedicoOutputPort))
                .map(HistoricoMedicoPresenter::converterDtoParaResponse)
                .orElseThrow();
    }

    @Secured({"ROLE_MEDICO", "ROLE_ENFERMEIRO"})
    @QueryMapping
    public HistoricoMedicoResponseDto consultarHistoricoMedicoPorIdConsulta(@Argument Long id) {
        return historicoMedicoOutputPort.consultarHistoricoMedicoPorIdConsulta(id)
                .map(HistoricoMedicoPresenter::converterDtoParaResponse)
                .orElseThrow();
    }

    @Secured({"ROLE_MEDICO", "ROLE_ENFERMEIRO"})
    @QueryMapping
    public Set<HistoricoMedicoResponseDto> listarHistoricoMedicoPorIdPaciente(@Argument Long id) {
        return historicoMedicoOutputPort.listarHistoricoMedicoPorIdPaciente(id)
                .stream()
                .map(HistoricoMedicoPresenter::converterDtoParaResponse)
                .collect(Collectors.toSet());
    }

    @Secured({"ROLE_MEDICO", "ROLE_ENFERMEIRO"})
    @QueryMapping
    public Set<HistoricoMedicoResponseDto> pesquisarHistoricoMedico(@Argument("filtro") FiltroHistoricoMedico filtro) {
        return historicoMedicoOutputPort.pesquisar(filtro.id(), filtro.diagnostico(), filtro.prescricao(), filtro.exames(), filtro.consultaId())
                .stream()
                .map(HistoricoMedicoPresenter::converterDtoParaResponse)
                .collect(Collectors.toSet());
    }
}
