package fiap.adj.fase3.tech_challenge_hospital.infrastructure.controllers;

import fiap.adj.fase3.tech_challenge_hospital.application.dtos.request.PacienteRequestDto;
import fiap.adj.fase3.tech_challenge_hospital.application.dtos.response.PacienteResponseDto;
import fiap.adj.fase3.tech_challenge_hospital.infrastructure.ports.input.PacienteInputPort;
import fiap.adj.fase3.tech_challenge_hospital.infrastructure.ports.output.PacienteOutputPort;
import fiap.adj.fase3.tech_challenge_hospital.infrastructure.ports.output.RoleOutputPort;
import fiap.adj.fase3.tech_challenge_hospital.infrastructure.presenters.PacientePresenter;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class PacienteController {

    private final PacienteInputPort pacienteInputPort;

    private final PacienteOutputPort pacienteOutputPort;

    private final RoleOutputPort roleOutputPort;

    @MutationMapping(value = "criarPaciente")
    public PacienteResponseDto criarPaciente(@Argument PacienteRequestDto request) {
        return Optional.ofNullable(request)
                .map(dto -> pacienteInputPort.criar(dto, pacienteOutputPort, roleOutputPort))
                .map(PacientePresenter::converterDtoParaResponse)
                .orElseThrow();
    }

    @QueryMapping(value = "consultarPacientePorId")
    public PacienteResponseDto consultarPacientePorId(@Argument Long id) {
        return pacienteOutputPort.consultarPorId(id)
                .map(PacientePresenter::converterDtoParaResponse)
                .orElseThrow();
    }

    @MutationMapping
    public Boolean apagarPaciente(@Argument Long id) {
        pacienteInputPort.apagarPorId(id, pacienteOutputPort);
        return true;
    }

    @MutationMapping
    public PacienteResponseDto atualizarPaciente(@Argument Long id, @Argument PacienteRequestDto request) {
        return Optional.ofNullable(request)
                .map(dto -> pacienteInputPort.atualizar(id, dto, pacienteOutputPort))
                .map(PacientePresenter::converterDtoParaResponse)
                .orElseThrow();
    }
}
