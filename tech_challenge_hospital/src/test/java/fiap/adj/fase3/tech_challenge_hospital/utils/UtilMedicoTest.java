package fiap.adj.fase3.tech_challenge_hospital.utils;

import fiap.adj.fase3.tech_challenge_hospital.application.dtos.request.MedicoRequestDto;
import fiap.adj.fase3.tech_challenge_hospital.domain.entities.enums.RoleEnum;
import fiap.adj.fase3.tech_challenge_hospital.infrastructure.daos.MedicoDao;

public class UtilMedicoTest {

    public static MedicoRequestDto montarMedicoRequestDto(String nome, String username, String password) {
        var userRequestDto = UtilUserTest.montarUserRequestDto(username, password);
        return new MedicoRequestDto(nome, userRequestDto);
    }

    public static MedicoDao montarMedicoDao(String nome, String username, String password) {
        var userDao = UtilUserTest.montarUserDao(username, password, 3L, RoleEnum.ROLE_MEDICO.getValue());
        var medicoDao = new MedicoDao();
        medicoDao.setNome(nome);
        medicoDao.setUser(userDao);
        return medicoDao;
    }
}
