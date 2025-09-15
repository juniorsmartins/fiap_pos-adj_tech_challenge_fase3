package fiap.adj.fase3.tech_challenge_hospital.application.usecases;

import fiap.adj.fase3.tech_challenge_hospital.application.dtos.internal.HistoricoMedicoDto;
import fiap.adj.fase3.tech_challenge_hospital.application.dtos.response.HistoricoMedicoResponseDto;
import fiap.adj.fase3.tech_challenge_hospital.infrastructure.daos.HistoricoMedicoDao;
import fiap.adj.fase3.tech_challenge_hospital.infrastructure.presenters.ConsultaPresenter;

public final class HistoricoMedicoPresenter {

    public static HistoricoMedicoResponseDto converterDtoParaResponse(HistoricoMedicoDto dto) {
        var consultaResponse = ConsultaPresenter.converterDtoParaResponse(dto.getConsulta());
        return new HistoricoMedicoResponseDto(dto.getId(), dto.getDiagnostico(), dto.getPrescricao(), dto.getExames(), consultaResponse);
    }

    public static HistoricoMedicoDao converterDtoParaDao(HistoricoMedicoDto dto) {
        var consultaDao = ConsultaPresenter.converterDtoParaDao(dto.getConsulta());
        return new HistoricoMedicoDao(dto.getId(), dto.getDiagnostico(), dto.getPrescricao(), dto.getExames(), consultaDao);
    }

    public static HistoricoMedicoDto converterDaoParaDto(HistoricoMedicoDao dao) {
        var consultaDto = ConsultaPresenter.converterDaoParaDto(dao.getConsulta());
        return new HistoricoMedicoDto(dao.getId(), dao.getDiagnostico(), dao.getPrescricao(), dao.getExames(), consultaDto);
    }
}
