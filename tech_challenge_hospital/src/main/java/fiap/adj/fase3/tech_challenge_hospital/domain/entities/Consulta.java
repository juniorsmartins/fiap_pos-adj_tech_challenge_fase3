package fiap.adj.fase3.tech_challenge_hospital.domain.entities;

import fiap.adj.fase3.tech_challenge_hospital.application.dtos.internal.ConsultaDto;
import fiap.adj.fase3.tech_challenge_hospital.application.dtos.request.ConsultaRequestDto;
import fiap.adj.fase3.tech_challenge_hospital.domain.entities.enums.ConsultaStatusEnum;
import fiap.adj.fase3.tech_challenge_hospital.infrastructure.ports.output.MedicoOutputPort;
import fiap.adj.fase3.tech_challenge_hospital.infrastructure.ports.output.PacienteOutputPort;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Slf4j
@AllArgsConstructor
@Getter
@Setter
public final class Consulta {

    private Long id;

    private LocalDateTime dataHora;

    private ConsultaStatusEnum status;

    private Medico medico;

    private Paciente paciente;

    public Consulta(LocalDateTime dataHora, ConsultaStatusEnum status, Medico medico, Paciente paciente) {
        this.dataHora = dataHora;
        this.status = status;
        this.medico = medico;
        this.paciente = paciente;
    }

    public static Consulta converterRequestParaEntity(ConsultaRequestDto request, ConsultaStatusEnum status, MedicoOutputPort medicoOutputPort, PacienteOutputPort pacienteOutputPort) {
        var medico = consultarMedicoPorId(request.getMedicoId(), medicoOutputPort);
        var paciente = consultarPacientePorId(request.getPacienteId(), pacienteOutputPort);
        var dataHora = converterDataHora(request.getDataHora());
        return new Consulta(dataHora, status, medico, paciente);
    }

    public static Consulta converterDtoParaEntity(ConsultaDto dto) {
        var medico = Medico.converterDtoParaEntity(dto.getMedico());
        var paciente = Paciente.converterDtoParaEntity(dto.getPaciente());
        return new Consulta(dto.getId(), dto.getDataHora(), ConsultaStatusEnum.valueOf(dto.getStatus()), medico, paciente);
    }

    private static LocalDateTime converterDataHora(String dataHora) {
        try {
            return LocalDateTime.parse(dataHora, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (DateTimeParseException e) {
            log.error("Erro na conversão de dataHora: {}.", dataHora);
            throw new RuntimeException();
        }
    }

    public static ConsultaDto converterEntityParaDto(Consulta consulta) {
        var medicoDto = Medico.converterEntityParaDto(consulta.medico);
        var pacienteDto = Paciente.converterEntityParaDto(consulta.paciente);
        return new ConsultaDto(consulta.getId(), consulta.getDataHora(), consulta.getStatus().getValue(), medicoDto, pacienteDto);
    }

    private static Medico consultarMedicoPorId(Long id, MedicoOutputPort medicoOutputPort) {
        return medicoOutputPort.consultarPorId(id)
                .map(Medico::converterDtoParaEntity)
                .orElseThrow();
    }

    private static Paciente consultarPacientePorId(Long id, PacienteOutputPort pacienteOutputPort) {
        return pacienteOutputPort.consultarPorId(id)
                .map(Paciente::converterDtoParaEntity)
                .orElseThrow();
    }
}
