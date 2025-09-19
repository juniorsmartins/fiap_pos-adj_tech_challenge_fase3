package fiap.adj.fase3.tech_challenge_hospital.infrastructure.controllers;

import fiap.adj.fase3.tech_challenge_hospital.application.dtos.request.ConsultaRequestDto;
import fiap.adj.fase3.tech_challenge_hospital.application.dtos.request.FiltroConsulta;
import fiap.adj.fase3.tech_challenge_hospital.application.dtos.response.ConsultaResponseDto;
import fiap.adj.fase3.tech_challenge_hospital.infrastructure.ports.input.ConsultaInputPort;
import fiap.adj.fase3.tech_challenge_hospital.infrastructure.ports.output.ConsultaOutputPort;
import fiap.adj.fase3.tech_challenge_hospital.infrastructure.ports.output.MedicoOutputPort;
import fiap.adj.fase3.tech_challenge_hospital.infrastructure.ports.output.PacienteOutputPort;
import fiap.adj.fase3.tech_challenge_hospital.infrastructure.presenters.ConsultaPresenter;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.stereotype.Controller;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class ConsultaController {

    private final ConsultaInputPort consultaInputPort;

    private final ConsultaOutputPort consultaOutputPort;

    private final MedicoOutputPort medicoOutputPort;

    private final PacienteOutputPort pacienteOutputPort;

    @Secured({"ROLE_ENFERMEIRO"})
    @MutationMapping
    public ConsultaResponseDto criarConsulta(@Argument ConsultaRequestDto request) {
        return Optional.ofNullable(request)
                .map(dto -> consultaInputPort.criar(dto, medicoOutputPort, pacienteOutputPort, consultaOutputPort))
                .map(ConsultaPresenter::converterDtoParaResponse)
                .orElseThrow();
    }

    @Secured({"ROLE_MEDICO"})
    @MutationMapping
    public ConsultaResponseDto atualizarConsulta(@Argument Long id, @Argument ConsultaRequestDto request) {
        return Optional.ofNullable(request)
                .map(dto -> consultaInputPort.atualizar(id, dto, medicoOutputPort, pacienteOutputPort, consultaOutputPort))
                .map(ConsultaPresenter::converterDtoParaResponse)
                .orElseThrow();
    }

    @Secured({"ROLE_MEDICO"})
    @MutationMapping
    public Boolean concluirConsulta(@Argument Long id) {
        consultaInputPort.concluir(id, consultaOutputPort);
        return true;
    }

    @Secured({"ROLE_MEDICO", "ROLE_ENFERMEIRO"})
    @MutationMapping
    public Boolean cancelarConsulta(@Argument Long id) {
        consultaInputPort.cancelar(id, consultaOutputPort);
        return true;
    }

    @PostAuthorize("hasAnyRole('MEDICO', 'ENFERMEIRO') or (hasRole('PACIENTE') and returnObject != null and returnObject.paciente != null and returnObject.paciente.user.username == authentication.name)")
    @QueryMapping
    public ConsultaResponseDto consultarConsultaPorId(@Argument Long id) {
        return Optional.ofNullable(id)
                .map(codigo -> consultaInputPort.consultarPorId(codigo, consultaOutputPort))
                .map(ConsultaPresenter::converterDtoParaResponse)
                .orElse(null);
    }

    @PostFilter("hasAnyRole('MEDICO', 'ENFERMEIRO') or "
            + "(hasRole('PACIENTE') and filterObject.paciente != null "
            + "and filterObject.paciente.user != null "
            + "and filterObject.paciente.user.username == authentication.name)")
    @QueryMapping
    public Set<ConsultaResponseDto> listarHistoricoDeConsultasPorIdPaciente(@Argument Long id) {
        return consultaOutputPort.buscarHistoricoDeConsultasPorId(id)
                .stream()
                .map(ConsultaPresenter::converterDtoParaResponse)
                .sorted(Comparator.comparing(ConsultaResponseDto::dataHora, Comparator.reverseOrder()))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @PostFilter("hasAnyRole('MEDICO', 'ENFERMEIRO') or "
            + "(hasRole('PACIENTE') and filterObject.paciente != null "
            + "and filterObject.paciente.user != null "
            + "and filterObject.paciente.user.username == authentication.name)")
    @QueryMapping
    public Set<ConsultaResponseDto> pesquisarConsulta(@Argument("filtro") FiltroConsulta filtro) {
        return consultaOutputPort.pesquisar(filtro.id(), filtro.dataHora(), filtro.status(), filtro.medicoId(), filtro.pacienteId())
                .stream()
                .map(ConsultaPresenter::converterDtoParaResponse)
                .collect(Collectors.toSet());
    }
}
