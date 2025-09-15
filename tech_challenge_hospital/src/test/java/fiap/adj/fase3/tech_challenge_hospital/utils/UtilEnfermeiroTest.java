package fiap.adj.fase3.tech_challenge_hospital.utils;

import fiap.adj.fase3.tech_challenge_hospital.application.dtos.request.EnfermeiroRequestDto;
import fiap.adj.fase3.tech_challenge_hospital.domain.entities.enums.RoleEnum;
import fiap.adj.fase3.tech_challenge_hospital.infrastructure.daos.EnfermeiroDao;

public class UtilEnfermeiroTest {

    public static EnfermeiroRequestDto montarEnfermeiroRequestDto(String nome, String username, String password) {
        var userRequestDto = UtilUserTest.montarUserRequestDto(username, password);
        return new EnfermeiroRequestDto(nome, userRequestDto);
    }

    public static EnfermeiroDao montarEnfermeiroDao(String nome, String username, String password) {
        var userDao = UtilUserTest.montarUserDao(username, password, 3L, RoleEnum.ROLE_MEDICO.getValue());
        var enfermeiroDao = new EnfermeiroDao();
        enfermeiroDao.setNome(nome);
        enfermeiroDao.setUser(userDao);
        return enfermeiroDao;
    }
}
