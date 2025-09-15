package fiap.adj.fase3.tech_challenge_hospital.infrastructure.presenters;

import fiap.adj.fase3.tech_challenge_hospital.application.dtos.internal.RoleDto;
import fiap.adj.fase3.tech_challenge_hospital.application.dtos.response.RoleResponseDto;
import fiap.adj.fase3.tech_challenge_hospital.infrastructure.daos.RoleDao;

public final class RolePresenter {

    public static RoleDto converterDaoParaDto(RoleDao role) {
        return new RoleDto(role.getId(), role.getName());
    }

    public static RoleResponseDto converterDaoParaResponse(RoleDao role) {
        return new RoleResponseDto(role.getId(), role.getName());
    }

    public static RoleDao converterDtoParaDao(RoleDto role) {
        return new RoleDao(role.id(), role.name());
    }

    public static RoleResponseDto converterDtoParaResponse(RoleDto role) {
        return new RoleResponseDto(role.id(), role.name());
    }
}
