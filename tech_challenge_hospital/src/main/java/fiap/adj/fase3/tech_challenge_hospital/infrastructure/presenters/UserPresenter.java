package fiap.adj.fase3.tech_challenge_hospital.infrastructure.presenters;

import fiap.adj.fase3.tech_challenge_hospital.application.dtos.internal.RoleDto;
import fiap.adj.fase3.tech_challenge_hospital.application.dtos.internal.UserDto;
import fiap.adj.fase3.tech_challenge_hospital.application.dtos.response.RoleResponseDto;
import fiap.adj.fase3.tech_challenge_hospital.application.dtos.response.UserResponseDto;
import fiap.adj.fase3.tech_challenge_hospital.infrastructure.daos.RoleDao;
import fiap.adj.fase3.tech_challenge_hospital.infrastructure.daos.UserDao;

import java.util.Set;
import java.util.stream.Collectors;

public final class UserPresenter {

    public static UserResponseDto converterDtoParaResponse(UserDto dto) {
        Set<RoleResponseDto> roles = dto.roles().stream()
                .map(RolePresenter::converterDtoParaResponse)
                .collect(Collectors.toSet());

        return new UserResponseDto(dto.id(), dto.username(), dto.password(), dto.enabled(), roles);
    }

    public static UserDao converterDtoParaDao(UserDto dto) {
        Set<RoleDao> roles = dto.roles().stream()
                .map(RolePresenter::converterDtoParaDao)
                .collect(Collectors.toSet());

        return new UserDao(dto.id(), dto.username(), dto.password(), dto.enabled(), roles);
    }

    public static UserDto converterDaoParaDto(UserDao dao) {
        Set<RoleDto> roles = dao.getRoles().stream()
                .map(RolePresenter::converterDaoParaDto)
                .collect(Collectors.toSet());

        return new UserDto(dao.getId(), dao.getUsername(), dao.getPassword(), dao.isEnabled(), roles);
    }
}
