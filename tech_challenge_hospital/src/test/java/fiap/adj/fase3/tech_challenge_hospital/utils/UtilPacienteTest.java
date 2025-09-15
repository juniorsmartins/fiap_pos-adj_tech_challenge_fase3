package fiap.adj.fase3.tech_challenge_hospital.utils;

import fiap.adj.fase3.tech_challenge_hospital.application.dtos.request.PacienteRequestDto;
import fiap.adj.fase3.tech_challenge_hospital.domain.entities.enums.RoleEnum;
import fiap.adj.fase3.tech_challenge_hospital.infrastructure.daos.PacienteDao;

public class UtilPacienteTest {

    public static PacienteRequestDto montarPacienteRequestDto(String nome, String email, String username, String password) {
        var userRequestDto = UtilUserTest.montarUserRequestDto(username, password);
        return new PacienteRequestDto(nome, email, userRequestDto);
    }

    public static PacienteDao montarPacienteDao(String nome, String email, String username, String password) {
        var userDao = UtilUserTest.montarUserDao(username, password, 4L, RoleEnum.ROLE_PACIENTE.getValue());
        return new PacienteDao(nome, email, userDao);
    }
}
