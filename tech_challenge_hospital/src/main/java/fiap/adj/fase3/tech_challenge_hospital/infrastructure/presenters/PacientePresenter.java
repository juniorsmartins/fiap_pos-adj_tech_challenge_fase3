package fiap.adj.fase3.tech_challenge_hospital.infrastructure.presenters;

import fiap.adj.fase3.tech_challenge_hospital.application.dtos.internal.PacienteDto;
import fiap.adj.fase3.tech_challenge_hospital.application.dtos.response.PacienteResponseDto;
import fiap.adj.fase3.tech_challenge_hospital.infrastructure.daos.PacienteDao;

public class PacientePresenter {

    public static PacienteResponseDto converterDtoParaResponse(PacienteDto dto) {
        var userResponse = UserPresenter.converterDtoParaResponse(dto.user());
        return new PacienteResponseDto(dto.id(), dto.nome(), dto.email(), userResponse);
    }

    public static PacienteDao converterDtoParaDao(PacienteDto dto) {
        var userDao = UserPresenter.converterDtoParaDao(dto.user());
        return new PacienteDao(dto.id(), dto.nome(), dto.email(), userDao);
    }

    public static PacienteDto converterDaoParaDto(PacienteDao dao) {
        var userDto = UserPresenter.converterDaoParaDto(dao.getUser());
        return new PacienteDto(dao.getId(), dao.getNome(), dao.getEmail(), userDto);
    }
}
