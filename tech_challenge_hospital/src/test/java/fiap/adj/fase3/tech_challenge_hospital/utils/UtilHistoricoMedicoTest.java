package fiap.adj.fase3.tech_challenge_hospital.utils;

import fiap.adj.fase3.tech_challenge_hospital.application.dtos.request.HistoricoMedicoRequestDto;

public class UtilHistoricoMedicoTest {

    public static HistoricoMedicoRequestDto montarHistoricoMedicoRequestDto(String diagnostico, String prescricao, String exames, Long consultaId) {
        return new HistoricoMedicoRequestDto(diagnostico, prescricao, exames, consultaId);

    }
}
