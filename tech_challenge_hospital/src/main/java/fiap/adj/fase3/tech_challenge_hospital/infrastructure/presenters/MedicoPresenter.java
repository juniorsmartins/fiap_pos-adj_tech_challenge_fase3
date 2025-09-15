package fiap.adj.fase3.tech_challenge_hospital.infrastructure.presenters;

import fiap.adj.fase3.tech_challenge_hospital.application.dtos.internal.MedicoDto;
import fiap.adj.fase3.tech_challenge_hospital.application.dtos.response.MedicoResponseDto;
import fiap.adj.fase3.tech_challenge_hospital.infrastructure.daos.MedicoDao;

public final class MedicoPresenter {

    public static MedicoResponseDto converterDtoParaResponse(MedicoDto dto) {
        var userResponse = UserPresenter.converterDtoParaResponse(dto.user());
        return new MedicoResponseDto(dto.id(), dto.nome(), userResponse);
    }

    public static MedicoDao converterDtoParaDao(MedicoDto dto) {
        var userDao = UserPresenter.converterDtoParaDao(dto.user());
        return new MedicoDao(dto.id(), dto.nome(), userDao);
    }

    public static MedicoDto converterDaoParaDto(MedicoDao dao) {
        var userDto = UserPresenter.converterDaoParaDto(dao.getUser());
        return new MedicoDto(dao.getId(), dao.getNome(), userDto);
    }
}
