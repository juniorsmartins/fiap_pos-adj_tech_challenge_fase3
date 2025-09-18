package fiap.adj.fase3.tech_challenge_hospital.infrastructure.presenters;

import fiap.adj.fase3.tech_challenge_hospital.application.dtos.internal.UserDto;
import fiap.adj.fase3.tech_challenge_hospital.application.dtos.response.UserResponseDto;
import fiap.adj.fase3.tech_challenge_hospital.infrastructure.daos.UserDao;

public final class UserPresenter {

    public static UserResponseDto converterDtoParaResponse(UserDto dto) {
        var role = RolePresenter.converterDtoParaResponse(dto.role());
        return new UserResponseDto(dto.id(), dto.username(), dto.password(), dto.enabled(), role);
    }

    public static UserDao converterDtoParaDao(UserDto dto) {
        var role = RolePresenter.converterDtoParaDao(dto.role());
        return new UserDao(dto.id(), dto.username(), dto.password(), dto.enabled(), role);
    }

    public static UserDto converterDaoParaDto(UserDao dao) {
        var role = RolePresenter.converterDaoParaDto(dao.getRole());
        return new UserDto(dao.getId(), dao.getUsername(), dao.getPassword(), dao.isEnabled(), role);
    }
}
