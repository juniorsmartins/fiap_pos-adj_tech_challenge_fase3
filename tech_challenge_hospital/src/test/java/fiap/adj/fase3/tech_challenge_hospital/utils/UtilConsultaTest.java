package fiap.adj.fase3.tech_challenge_hospital.utils;

import fiap.adj.fase3.tech_challenge_hospital.application.dtos.request.ConsultaRequestDto;
import fiap.adj.fase3.tech_challenge_hospital.infrastructure.daos.ConsultaDao;
import fiap.adj.fase3.tech_challenge_hospital.infrastructure.daos.MedicoDao;
import fiap.adj.fase3.tech_challenge_hospital.infrastructure.daos.PacienteDao;

import java.time.LocalDateTime;

public class UtilConsultaTest {

    public static ConsultaDao montarConsultaDao(LocalDateTime dataHora, String status, MedicoDao medicoDao, PacienteDao pacienteDao) {
        var dao = new ConsultaDao();
        dao.setDataHora(dataHora);
        dao.setStatus(status);
        dao.setMedico(medicoDao);
        dao.setPaciente(pacienteDao);
        return dao;
    }

    public static ConsultaRequestDto montarConsultaRequestDto(String dataHora, Long idMedico, Long idPaciente) {
        return new ConsultaRequestDto(dataHora, idMedico, idPaciente);
    }
}
