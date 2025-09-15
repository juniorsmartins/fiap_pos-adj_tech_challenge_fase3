package fiap.adj.fase3.tech_challenge_hospital.infrastructure.controllers;

import fiap.adj.fase3.tech_challenge_hospital.application.dtos.request.MedicoRequestDto;
import fiap.adj.fase3.tech_challenge_hospital.application.dtos.response.MedicoResponseDto;
import fiap.adj.fase3.tech_challenge_hospital.infrastructure.ports.input.MedicoInputPort;
import fiap.adj.fase3.tech_challenge_hospital.infrastructure.ports.output.MedicoOutputPort;
import fiap.adj.fase3.tech_challenge_hospital.infrastructure.ports.output.RoleOutputPort;
import fiap.adj.fase3.tech_challenge_hospital.infrastructure.presenters.MedicoPresenter;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class MedicoController {

    private final MedicoInputPort medicoInputPort;

    private final MedicoOutputPort medicoOutputPort;

    private final RoleOutputPort roleOutputPort;

    @MutationMapping
    public MedicoResponseDto criarMedico(@Argument MedicoRequestDto request) {
        return Optional.ofNullable(request)
                .map(dto -> medicoInputPort.criar(dto, medicoOutputPort, roleOutputPort))
                .map(MedicoPresenter::converterDtoParaResponse)
                .orElseThrow();
    }

    @QueryMapping
    public MedicoResponseDto consultarMedicoPorId(@Argument Long id) {
        return medicoOutputPort.consultarPorId(id)
                .map(MedicoPresenter::converterDtoParaResponse)
                .orElseThrow();
    }

    @MutationMapping
    public Boolean apagarMedico(@Argument Long id) {
        medicoInputPort.apagarPorId(id, medicoOutputPort);
        return true;
    }

    @MutationMapping
    public MedicoResponseDto atualizarMedico(@Argument Long id, @Argument MedicoRequestDto request) {
        return Optional.ofNullable(request)
                .map(dto -> medicoInputPort.atualizar(id, dto, medicoOutputPort))
                .map(MedicoPresenter::converterDtoParaResponse)
                .orElseThrow();
    }
}
