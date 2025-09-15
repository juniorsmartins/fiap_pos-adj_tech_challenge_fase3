package fiap.adj.fase3.tech_challenge_hospital.infrastructure.controllers;

import fiap.adj.fase3.tech_challenge_hospital.application.dtos.request.EnfermeiroRequestDto;
import fiap.adj.fase3.tech_challenge_hospital.application.dtos.response.EnfermeiroResponseDto;
import fiap.adj.fase3.tech_challenge_hospital.infrastructure.ports.input.EnfermeiroInputPort;
import fiap.adj.fase3.tech_challenge_hospital.infrastructure.ports.output.EnfermeiroOutputPort;
import fiap.adj.fase3.tech_challenge_hospital.infrastructure.ports.output.RoleOutputPort;
import fiap.adj.fase3.tech_challenge_hospital.infrastructure.presenters.EnfermeiroPresenter;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class EnfermeiroController {

    private final EnfermeiroInputPort enfermeiroInputPort;

    private final EnfermeiroOutputPort enfermeiroOutputPort;

    private final RoleOutputPort roleOutputPort;

    @MutationMapping
    public EnfermeiroResponseDto criarEnfermeiro(@Argument EnfermeiroRequestDto request) {
        return Optional.ofNullable(request)
                .map(dto -> enfermeiroInputPort.criar(dto, enfermeiroOutputPort, roleOutputPort))
                .map(EnfermeiroPresenter::converterDtoParaResponse)
                .orElseThrow();
    }

    @QueryMapping
    public EnfermeiroResponseDto consultarEnfermeiroPorId(@Argument Long id) {
        return enfermeiroOutputPort.consultarPorId(id)
                .map(EnfermeiroPresenter::converterDtoParaResponse)
                .orElseThrow();
    }

    @MutationMapping
    public Boolean apagarEnfermeiro(@Argument Long id) {
        enfermeiroInputPort.apagarPorId(id, enfermeiroOutputPort);
        return true;
    }

    @MutationMapping
    public EnfermeiroResponseDto atualizarEnfermeiro(@Argument Long id, @Argument EnfermeiroRequestDto request) {
        return Optional.ofNullable(request)
                .map(dto -> enfermeiroInputPort.atualizar(id, dto, enfermeiroOutputPort))
                .map(EnfermeiroPresenter::converterDtoParaResponse)
                .orElseThrow();
    }
}
