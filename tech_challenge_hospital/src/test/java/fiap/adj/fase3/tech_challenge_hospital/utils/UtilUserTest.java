package fiap.adj.fase3.tech_challenge_hospital.utils;

import fiap.adj.fase3.tech_challenge_hospital.application.dtos.request.UserRequestDto;
import fiap.adj.fase3.tech_challenge_hospital.infrastructure.daos.UserDao;

import java.util.Collections;
import java.util.HashSet;

public class UtilUserTest {

    public static UserRequestDto montarUserRequestDto(String username, String password) {
        return new UserRequestDto(username, password);
    }

    public static UserDao montarUserDao(String username, String password, Long idRole, String role) {
        var userDao = new UserDao();
        userDao.setUsername(username);
        userDao.setPassword(password);
        userDao.setEnabled(true);

        var roleDao = UtilRoleTest.montarRoleDao(idRole, role);
        userDao.setRoles(new HashSet<>(Collections.singleton(roleDao)));

        return userDao;
    }
}
