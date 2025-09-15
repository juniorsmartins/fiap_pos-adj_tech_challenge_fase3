package fiap.adj.fase3.tech_challenge_hospital.utils;

import fiap.adj.fase3.tech_challenge_hospital.infrastructure.daos.RoleDao;

public class UtilRoleTest {

    public static RoleDao montarRoleDao(Long id, String role) {
        var roleDao = new RoleDao();
        roleDao.setId(id);
        roleDao.setName(role);
        return roleDao;
    }
}
