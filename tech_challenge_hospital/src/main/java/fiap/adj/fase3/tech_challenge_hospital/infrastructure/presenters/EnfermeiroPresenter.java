package fiap.adj.fase3.tech_challenge_hospital.infrastructure.presenters;

import fiap.adj.fase3.tech_challenge_hospital.application.dtos.internal.EnfermeiroDto;
import fiap.adj.fase3.tech_challenge_hospital.application.dtos.response.EnfermeiroResponseDto;
import fiap.adj.fase3.tech_challenge_hospital.infrastructure.daos.EnfermeiroDao;

public final class EnfermeiroPresenter {

    public static EnfermeiroResponseDto converterDtoParaResponse(EnfermeiroDto dto) {
        var userResponse = UserPresenter.converterDtoParaResponse(dto.user());
        return new EnfermeiroResponseDto(dto.id(), dto.nome(), userResponse);
    }

    public static EnfermeiroDao converterDtoParaDao(EnfermeiroDto dto) {
        var userDao = UserPresenter.converterDtoParaDao(dto.user());
        return new EnfermeiroDao(dto.id(), dto.nome(), userDao);
    }

    public static EnfermeiroDto converterDaoParaDto(EnfermeiroDao dao) {
        var userDto = UserPresenter.converterDaoParaDto(dao.getUser());
        return new EnfermeiroDto(dao.getId(), dao.getNome(), userDto);
    }
}
