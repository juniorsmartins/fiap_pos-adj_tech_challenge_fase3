package fiap.adj.fase3.tech_challenge_hospital.application.usecases;

import fiap.adj.fase3.tech_challenge_hospital.application.configs.kafka.KafkaProducer;
import fiap.adj.fase3.tech_challenge_hospital.application.dtos.internal.ConsultaDto;
import fiap.adj.fase3.tech_challenge_hospital.application.dtos.request.ConsultaRequestDto;
import fiap.adj.fase3.tech_challenge_hospital.domain.entities.Consulta;
import fiap.adj.fase3.tech_challenge_hospital.domain.entities.enums.ConsultaStatusEnum;
import fiap.adj.fase3.tech_challenge_hospital.domain.entities.enums.MotivoKafkaEnum;
import fiap.adj.fase3.tech_challenge_hospital.infrastructure.ports.input.ConsultaInputPort;
import fiap.adj.fase3.tech_challenge_hospital.infrastructure.ports.output.ConsultaOutputPort;
import fiap.adj.fase3.tech_challenge_hospital.infrastructure.ports.output.MedicoOutputPort;
import fiap.adj.fase3.tech_challenge_hospital.infrastructure.ports.output.PacienteOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ConsultaUseCase implements ConsultaInputPort {

    private final KafkaProducer kafkaProducer;

    @Transactional
    @Override
    public ConsultaDto criar(ConsultaRequestDto request, MedicoOutputPort medicoOutputPort, PacienteOutputPort pacienteOutputPort, ConsultaOutputPort consultaOutputPort) {
        return Optional.ofNullable(request)
                .map(dto -> Consulta.converterRequestParaEntity(dto, ConsultaStatusEnum.AGENDADO, medicoOutputPort, pacienteOutputPort))
                .map(Consulta::converterEntityParaDto)
                .map(consultaOutputPort::salvar)
                .map(dto -> kafkaProducer.enviarEventoConsulta(dto, MotivoKafkaEnum.AGENDAMENTO))
                .orElseThrow();
    }

    @Override
    public ConsultaDto atualizar(Long id, ConsultaRequestDto request, MedicoOutputPort medicoOutputPort, PacienteOutputPort pacienteOutputPort, ConsultaOutputPort consultaOutputPort) {
        return consultaOutputPort.consultarPorIdAndStatus(id, ConsultaStatusEnum.AGENDADO.getValue())
                .map(dtoDoBanco -> {
                    var entity = Consulta.converterRequestParaEntity(request, ConsultaStatusEnum.AGENDADO, medicoOutputPort, pacienteOutputPort);
                    entity.setId(dtoDoBanco.getId());
                    return entity;
                })
                .map(Consulta::converterEntityParaDto)
                .map(consultaOutputPort::salvar)
                .map(dto -> kafkaProducer.enviarEventoConsulta(dto, MotivoKafkaEnum.ALTERACAO))
                .orElseThrow();
    }

    @Transactional
    @Override
    public ConsultaDto consultarPorId(Long id, ConsultaOutputPort consultaOutputPort) {
        return consultaOutputPort.consultarPorIdAndStatus(id, ConsultaStatusEnum.AGENDADO.getValue())
                .orElseThrow();
    }

    @Transactional
    @Override
    public void concluir(Long id, ConsultaOutputPort consultaOutputPort) {
        consultaOutputPort.consultarPorIdAndStatus(id, ConsultaStatusEnum.AGENDADO.getValue())
                .ifPresentOrElse(dto -> {
                    dto.setStatus(ConsultaStatusEnum.CONCLUIDO.getValue());
                    consultaOutputPort.salvar(dto);
                    kafkaProducer.enviarEventoConsulta(dto, MotivoKafkaEnum.ALTERACAO);
                }, () -> {
                    throw new RuntimeException();
                });
    }

    @Transactional
    @Override
    public void cancelar(Long id, ConsultaOutputPort consultaOutputPort) {
        consultaOutputPort.consultarPorIdAndStatus(id, ConsultaStatusEnum.AGENDADO.getValue())
                .ifPresentOrElse(dto -> {
                    dto.setStatus(ConsultaStatusEnum.CANCELADO.getValue());
                    consultaOutputPort.salvar(dto);
                    kafkaProducer.enviarEventoConsulta(dto, MotivoKafkaEnum.ALTERACAO);
                }, () -> {
                    throw new RuntimeException();
                });
    }
}
