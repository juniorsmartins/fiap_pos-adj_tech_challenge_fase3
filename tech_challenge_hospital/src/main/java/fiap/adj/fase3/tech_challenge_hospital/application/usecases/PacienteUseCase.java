package fiap.adj.fase3.tech_challenge_hospital.application.usecases;

import fiap.adj.fase3.tech_challenge_hospital.application.dtos.internal.PacienteDto;
import fiap.adj.fase3.tech_challenge_hospital.application.dtos.request.PacienteRequestDto;
import fiap.adj.fase3.tech_challenge_hospital.domain.entities.Paciente;
import fiap.adj.fase3.tech_challenge_hospital.domain.entities.Usuario;
import fiap.adj.fase3.tech_challenge_hospital.infrastructure.ports.input.PacienteInputPort;
import fiap.adj.fase3.tech_challenge_hospital.infrastructure.ports.output.PacienteOutputPort;
import fiap.adj.fase3.tech_challenge_hospital.infrastructure.ports.output.RoleOutputPort;
import fiap.adj.fase3.tech_challenge_hospital.infrastructure.ports.output.UsuarioOutputPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class PacienteUseCase implements PacienteInputPort {

    @Transactional
    @Override
    public PacienteDto criar(PacienteRequestDto request, PacienteOutputPort pacienteOutputPort, UsuarioOutputPort usuarioOutputPort, RoleOutputPort roleOutputPort) {
        return Optional.ofNullable(request)
                .map(dto -> {
                    Usuario.verificarDuplicidadeUsername(dto.getUser().getUsername(), usuarioOutputPort);
                    return dto;
                })
                .map(dto -> Paciente.converterRequestParaEntity(dto, roleOutputPort))
                .map(Paciente::converterEntityParaDto)
                .map(pacienteOutputPort::salvar)
                .orElseThrow();
    }

    @Transactional
    @Override
    public void apagarPorId(Long id, PacienteOutputPort outputPort) {
        outputPort.consultarPorId(id)
                .ifPresentOrElse(dto -> outputPort.apagarPorId(dto.id()), () -> {
                    throw new RuntimeException();
                });
    }

    @Transactional
    @Override
    public PacienteDto atualizar(Long id, PacienteRequestDto requestDto, PacienteOutputPort pacienteOutputPort) {
        return pacienteOutputPort.consultarPorId(id)
                .map(dto -> Paciente.regraAtualizar(dto, requestDto))
                .map(Paciente::converterEntityParaDto)
                .map(pacienteOutputPort::salvar)
                .orElseThrow();
    }
}
