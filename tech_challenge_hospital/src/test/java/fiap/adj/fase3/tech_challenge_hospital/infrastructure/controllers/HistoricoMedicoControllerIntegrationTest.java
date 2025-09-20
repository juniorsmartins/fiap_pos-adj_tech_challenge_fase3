package fiap.adj.fase3.tech_challenge_hospital.infrastructure.controllers;

import fiap.adj.fase3.tech_challenge_hospital.application.dtos.request.FiltroHistoricoMedico;
import fiap.adj.fase3.tech_challenge_hospital.domain.entities.enums.ConsultaStatusEnum;
import fiap.adj.fase3.tech_challenge_hospital.infrastructure.daos.ConsultaDao;
import fiap.adj.fase3.tech_challenge_hospital.infrastructure.daos.MedicoDao;
import fiap.adj.fase3.tech_challenge_hospital.infrastructure.daos.PacienteDao;
import fiap.adj.fase3.tech_challenge_hospital.infrastructure.repositories.ConsultaRepository;
import fiap.adj.fase3.tech_challenge_hospital.infrastructure.repositories.HistoricoMedicoRepository;
import fiap.adj.fase3.tech_challenge_hospital.infrastructure.repositories.MedicoRepository;
import fiap.adj.fase3.tech_challenge_hospital.infrastructure.repositories.PacienteRepository;
import fiap.adj.fase3.tech_challenge_hospital.kafka.BaseIntegrationTest;
import fiap.adj.fase3.tech_challenge_hospital.utils.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class HistoricoMedicoControllerIntegrationTest extends BaseIntegrationTest {

    private static final String DIAGNOSTICO = "Diagnostico Inicial";

    private static final String DIAGNOSTICO_ATUAL = "Diagnostico Atual";

    private static final String PRESCRICAO = "Prescrição Inicial";

    private static final String PRESCRICAO_ATUAL = "Prescrição Atual";

    private static final String EXAMES = "Exames Inicial";

    private static final String EXAMES_ATUAL = "Exames Atual";

    @Autowired
    private HistoricoMedicoController controller;

    @Autowired
    private HistoricoMedicoRepository repository;

    @Autowired
    private ConsultaRepository consultaRepository;

    @Autowired
    private MedicoRepository medicoRepository;

    @Autowired
    private PacienteRepository pacienteRepository;

    private ConsultaDao consultaDao1;

    private ConsultaDao consultaDao2;

    private PacienteDao pacienteDao1;

    @BeforeEach
    void setUp() {
        var username1 = UtilUserTest.montarStringAleatoria();
        MedicoDao medicoDao1 = UtilMedicoTest.montarMedicoDao("MedicoConsulta 1", username1, "password111");
        medicoRepository.save(medicoDao1);

        var username2 = UtilUserTest.montarStringAleatoria();
        pacienteDao1 = UtilPacienteTest.montarPacienteDao("PacienteConsulta 1", "paciente1@email.com", username2, "password333");
        pacienteRepository.save(pacienteDao1);

        var dataHora1 = LocalDateTime.of(LocalDate.of(2025, 8, 10), LocalTime.of(14, 10));
        consultaDao1 = UtilConsultaTest.montarConsultaDao(dataHora1, ConsultaStatusEnum.AGENDADO.getValue(), medicoDao1, pacienteDao1);
        consultaRepository.save(consultaDao1);

        var dataHora2 = LocalDateTime.of(LocalDate.of(2025, 8, 10), LocalTime.of(14, 10));
        consultaDao2 = UtilConsultaTest.montarConsultaDao(dataHora2, ConsultaStatusEnum.AGENDADO.getValue(), medicoDao1, pacienteDao1);
        consultaRepository.save(consultaDao2);
    }

    @Nested
    @DisplayName("Criar")
    class Criar {

        @Test
        @WithMockUser(roles = "MEDICO")
        void dadaRequisicaoValida_quandoCriarHistoricoMedico_entaoRetornarResponseValido() {
            var request = UtilHistoricoMedicoTest.montarHistoricoMedicoRequestDto(DIAGNOSTICO, PRESCRICAO, EXAMES, consultaDao1.getId());
            var response = controller.criarHistoricoMedico(request);
            assertEquals(DIAGNOSTICO, response.diagnostico());
            assertEquals(PRESCRICAO, response.prescricao());
            assertEquals(EXAMES, response.exames());
            assertEquals(consultaDao1.getId(), response.consulta().id());
        }

        @Test
        @WithMockUser(roles = "MEDICO")
        void dadaRequisicaoValida_quandoCriarHistoricoMedico_entaoSalvarNoBanco() {
            var request = UtilHistoricoMedicoTest.montarHistoricoMedicoRequestDto(DIAGNOSTICO, PRESCRICAO, EXAMES, consultaDao1.getId());
            var response = controller.criarHistoricoMedico(request);
            var doBanco = repository.findById(response.id()).orElseThrow();
            assertEquals(doBanco.getDiagnostico(), response.diagnostico());
            assertEquals(doBanco.getPrescricao(), response.prescricao());
            assertEquals(doBanco.getExames(), response.exames());
            assertEquals(doBanco.getConsulta().getId(), response.consulta().id());
        }

        @Test
        @WithMockUser(roles = "ENFERMEIRO")
        void dadoUsuarioEnfermeiro_quandoCriarHistoricoMedico_entaoLancarAccessDeniedException() {
            var request = UtilHistoricoMedicoTest.montarHistoricoMedicoRequestDto(DIAGNOSTICO, PRESCRICAO, EXAMES, consultaDao1.getId());
            assertThrows(AccessDeniedException.class, () -> controller.criarHistoricoMedico(request));
        }

        @Test
        @WithMockUser(roles = "PACIENTE")
        void dadoUsuarioPaciente_quandoCriarHistoricoMedico_entaoLancarAccessDeniedException() {
            var request = UtilHistoricoMedicoTest.montarHistoricoMedicoRequestDto(DIAGNOSTICO, PRESCRICAO, EXAMES, consultaDao1.getId());
            assertThrows(AccessDeniedException.class, () -> controller.criarHistoricoMedico(request));
        }

        @Test
        void dadoUsuarioNaoAutenticado_quandoCriarHistoricoMedico_entaoLancarAuthenticationException() {
            var request = UtilHistoricoMedicoTest.montarHistoricoMedicoRequestDto(DIAGNOSTICO, PRESCRICAO, EXAMES, consultaDao1.getId());
            assertThrows(AuthenticationCredentialsNotFoundException.class, () -> controller.criarHistoricoMedico(request));
        }
    }

    @Nested
    @DisplayName("Atualizar")
    class Atualizar {

        @Test
        @WithMockUser(roles = "MEDICO")
        void dadoIdValidoAndRequisicaoValida_quandoAtualizar_entaoRetornarResponseValido() {
            var idConsulta = consultaDao1.getId();
            var request = UtilHistoricoMedicoTest.montarHistoricoMedicoRequestDto(DIAGNOSTICO, PRESCRICAO, EXAMES, idConsulta);
            var desatualizado = controller.criarHistoricoMedico(request);
            assertEquals(DIAGNOSTICO, desatualizado.diagnostico());
            assertEquals(PRESCRICAO, desatualizado.prescricao());
            assertEquals(EXAMES, desatualizado.exames());
            assertEquals(idConsulta, desatualizado.consulta().id());

            var requestAtual = UtilHistoricoMedicoTest.montarHistoricoMedicoRequestDto(DIAGNOSTICO_ATUAL, PRESCRICAO_ATUAL, EXAMES_ATUAL, idConsulta);
            var atualizado = controller.atualizarHistoricoMedico(requestAtual);
            assertEquals(DIAGNOSTICO_ATUAL, atualizado.diagnostico());
            assertEquals(PRESCRICAO_ATUAL, atualizado.prescricao());
            assertEquals(EXAMES_ATUAL, atualizado.exames());
            assertEquals(idConsulta, atualizado.consulta().id());
        }

        @Test
        @WithMockUser(roles = "MEDICO")
        void dadoIdValidoAndRequisicaoValida_quandoAtualizar_entaoSalvarNoBanco() {
            var idConsulta = consultaDao1.getId();
            var request = UtilHistoricoMedicoTest.montarHistoricoMedicoRequestDto(DIAGNOSTICO, PRESCRICAO, EXAMES, idConsulta);
            controller.criarHistoricoMedico(request);

            var requestAtual = UtilHistoricoMedicoTest.montarHistoricoMedicoRequestDto(DIAGNOSTICO_ATUAL, PRESCRICAO_ATUAL, EXAMES_ATUAL, idConsulta);
            var atualizado = controller.atualizarHistoricoMedico(requestAtual);

            var doBanco = repository.consultarHistoricoMedicoPorIdConsulta(idConsulta).orElseThrow();

            assertEquals(doBanco.getDiagnostico(), atualizado.diagnostico());
            assertEquals(doBanco.getPrescricao(), atualizado.prescricao());
            assertEquals(doBanco.getExames(), atualizado.exames());
            assertEquals(doBanco.getConsulta().getId(), atualizado.consulta().id());
        }

        @Test
        @WithMockUser(roles = "ENFERMEIRO")
        void dadoUsuarioEnfermeiro_quandoAtualizarHistoricoMedico_entaoLancarAccessDeniedException() {
            var request = UtilHistoricoMedicoTest.montarHistoricoMedicoRequestDto(DIAGNOSTICO_ATUAL, PRESCRICAO_ATUAL, EXAMES_ATUAL, consultaDao1.getId());
            assertThrows(AccessDeniedException.class, () -> controller.atualizarHistoricoMedico(request));
        }

        @Test
        @WithMockUser(roles = "PACIENTE")
        void dadoUsuarioPaciente_quandoAtualizarHistoricoMedico_entaoLancarAccessDeniedException() {
            var request = UtilHistoricoMedicoTest.montarHistoricoMedicoRequestDto(DIAGNOSTICO_ATUAL, PRESCRICAO_ATUAL, EXAMES_ATUAL, consultaDao1.getId());
            assertThrows(AccessDeniedException.class, () -> controller.atualizarHistoricoMedico(request));
        }

        @Test
        void dadoUsuarioNaoAutenticado_quandoAtualizarHistoricoMedico_entaoLancarAuthenticationException() {
            var request = UtilHistoricoMedicoTest.montarHistoricoMedicoRequestDto(DIAGNOSTICO_ATUAL, PRESCRICAO_ATUAL, EXAMES_ATUAL, consultaDao1.getId());
            assertThrows(AuthenticationCredentialsNotFoundException.class, () -> controller.atualizarHistoricoMedico(request));
        }
    }

    @Nested
    @DisplayName("Listar")
    class Listar {

        @Test
        @WithMockUser(roles = "MEDICO")
        void dadoIdValido_quandoListarHistoricoPorIdPaciente_entaoRetornarDoisItens() {
            var request1 = UtilHistoricoMedicoTest.montarHistoricoMedicoRequestDto(DIAGNOSTICO, PRESCRICAO, EXAMES, consultaDao1.getId());
            controller.criarHistoricoMedico(request1);

            var request2 = UtilHistoricoMedicoTest.montarHistoricoMedicoRequestDto(DIAGNOSTICO, PRESCRICAO, EXAMES, consultaDao2.getId());
            controller.criarHistoricoMedico(request2);

            var response = controller.listarHistoricoMedicoPorIdPaciente(pacienteDao1.getId());
            assertEquals(2, response.size());
        }

        @Test
        @WithMockUser(roles = "PACIENTE")
        void dadoUsuarioPaciente_quandoListarHistoricoPorIdPaciente_entaoLancarAccessDeniedException() {
            assertThrows(AccessDeniedException.class, () -> controller.listarHistoricoMedicoPorIdPaciente(pacienteDao1.getId()));
        }

        @Test
        void dadoUsuarioNaoAutenticado_quandoListarHistoricoPorIdPaciente_entaoLancarAuthenticationException() {
            assertThrows(AuthenticationCredentialsNotFoundException.class, () -> controller.listarHistoricoMedicoPorIdPaciente(pacienteDao1.getId()));
        }

        @Test
        @WithMockUser(roles = "MEDICO")
        void dadoIdPacienteInvalido_quandoListarHistoricoPorIdPaciente_entaoRetornarSetVazio() {
            var response = controller.listarHistoricoMedicoPorIdPaciente(999L);
            assertEquals(0, response.size());
        }
    }

    @Nested
    @DisplayName("Consultar")
    class Consultar {

        @Test
        @WithMockUser(roles = "MEDICO")
        void dadoIdValido_quandoConsultarHistoricoMedicoPorIdConsulta_entaoRetornarResponseValido() {
            var idConsulta = consultaDao1.getId();
            var request1 = UtilHistoricoMedicoTest.montarHistoricoMedicoRequestDto(DIAGNOSTICO, PRESCRICAO, EXAMES, idConsulta);
            controller.criarHistoricoMedico(request1);
            var response = controller.consultarHistoricoMedicoPorIdConsulta(idConsulta);
            assertEquals(DIAGNOSTICO, response.diagnostico());
            assertEquals(PRESCRICAO, response.prescricao());
            assertEquals(EXAMES, response.exames());
        }

        @Test
        @WithMockUser(roles = "PACIENTE")
        void dadoUsuarioPaciente_quandoConsultarHistoricoMedicoPorIdConsulta_entaoLancarAccessDeniedException() {
            var idConsulta = consultaDao1.getId();
            assertThrows(AccessDeniedException.class, () -> controller.consultarHistoricoMedicoPorIdConsulta(idConsulta));
        }

        @Test
        void dadoUsuarioNaoAutenticado_quandoConsultarHistoricoMedicoPorIdConsulta_entaoLancarAuthenticationException() {
            var idConsulta = consultaDao1.getId();
            assertThrows(AuthenticationCredentialsNotFoundException.class, () -> controller.consultarHistoricoMedicoPorIdConsulta(idConsulta));
        }
    }

    @Nested
    @DisplayName("Pesquisar")
    class Pesquisar {

        @Test
        @WithMockUser(roles = "MEDICO")
        void dadoFiltroValido_quandoPesquisarPorId_entaoRetornarSetComUmHistoricoMedico() {
            var idConsulta1 = consultaDao1.getId();
            var request1 = UtilHistoricoMedicoTest.montarHistoricoMedicoRequestDto(DIAGNOSTICO, PRESCRICAO, EXAMES, idConsulta1);
            controller.criarHistoricoMedico(request1);

            var idConsulta2 = consultaDao2.getId();
            var request2 = UtilHistoricoMedicoTest.montarHistoricoMedicoRequestDto(DIAGNOSTICO_ATUAL, PRESCRICAO_ATUAL, EXAMES_ATUAL, idConsulta2);
            var response2 = controller.criarHistoricoMedico(request2);

            var idHistorico = response2.id();
            var filtro = new FiltroHistoricoMedico(idHistorico, null, null, null, null);
            var resultado = controller.pesquisarHistoricoMedico(filtro);

            assertEquals(1, resultado.size());
            var historico = resultado.iterator().next();
            assertEquals(idHistorico, historico.id());
            assertEquals(DIAGNOSTICO_ATUAL, historico.diagnostico());
            assertEquals(PRESCRICAO_ATUAL, historico.prescricao());
            assertEquals(EXAMES_ATUAL, historico.exames());
            assertEquals(idConsulta2, historico.consulta().id());
        }

        @Test
        @WithMockUser(roles = "MEDICO")
        void dadoFiltroValido_quandoPesquisarPorDiagnostico_entaoRetornarSetComUmHistoricoMedico() {
            var idConsulta1 = consultaDao1.getId();
            var request1 = UtilHistoricoMedicoTest.montarHistoricoMedicoRequestDto(DIAGNOSTICO, PRESCRICAO, EXAMES, idConsulta1);
            controller.criarHistoricoMedico(request1);

            var idConsulta2 = consultaDao2.getId();
            var request2 = UtilHistoricoMedicoTest.montarHistoricoMedicoRequestDto(DIAGNOSTICO_ATUAL, PRESCRICAO_ATUAL, EXAMES_ATUAL, idConsulta2);
            var response2 = controller.criarHistoricoMedico(request2);

            var parametroPesquisa = "co At";
            var filtro = new FiltroHistoricoMedico(null, parametroPesquisa, null, null, null);
            var resultado = controller.pesquisarHistoricoMedico(filtro);

            assertEquals(1, resultado.size());
            var historico = resultado.iterator().next();
            assertEquals(response2.id(), historico.id());
            assertEquals(DIAGNOSTICO_ATUAL, historico.diagnostico());
            assertEquals(PRESCRICAO_ATUAL, historico.prescricao());
            assertEquals(EXAMES_ATUAL, historico.exames());
            assertEquals(idConsulta2, historico.consulta().id());
        }

        @Test
        @WithMockUser(roles = "MEDICO")
        void dadoFiltroValido_quandoPesquisarPorPrescricao_entaoRetornarSetComUmHistoricoMedico() {
            var idConsulta1 = consultaDao1.getId();
            var request1 = UtilHistoricoMedicoTest.montarHistoricoMedicoRequestDto(DIAGNOSTICO, PRESCRICAO, EXAMES, idConsulta1);
            controller.criarHistoricoMedico(request1);

            var idConsulta2 = consultaDao2.getId();
            var request2 = UtilHistoricoMedicoTest.montarHistoricoMedicoRequestDto(DIAGNOSTICO_ATUAL, PRESCRICAO_ATUAL, EXAMES_ATUAL, idConsulta2);
            var response2 = controller.criarHistoricoMedico(request2);

            var parametroPesquisa = "ção At";
            var filtro = new FiltroHistoricoMedico(null, null, parametroPesquisa, null, null);
            var resultado = controller.pesquisarHistoricoMedico(filtro);

            assertEquals(1, resultado.size());
            var historico = resultado.iterator().next();
            assertEquals(response2.id(), historico.id());
            assertEquals(DIAGNOSTICO_ATUAL, historico.diagnostico());
            assertEquals(PRESCRICAO_ATUAL, historico.prescricao());
            assertEquals(EXAMES_ATUAL, historico.exames());
            assertEquals(idConsulta2, historico.consulta().id());
        }

        @Test
        @WithMockUser(roles = "MEDICO")
        void dadoFiltroValido_quandoPesquisarPorExames_entaoRetornarSetComUmHistoricoMedico() {
            var idConsulta1 = consultaDao1.getId();
            var request1 = UtilHistoricoMedicoTest.montarHistoricoMedicoRequestDto(DIAGNOSTICO, PRESCRICAO, EXAMES, idConsulta1);
            controller.criarHistoricoMedico(request1);

            var idConsulta2 = consultaDao2.getId();
            var request2 = UtilHistoricoMedicoTest.montarHistoricoMedicoRequestDto(DIAGNOSTICO_ATUAL, PRESCRICAO_ATUAL, EXAMES_ATUAL, idConsulta2);
            var response2 = controller.criarHistoricoMedico(request2);

            var parametroPesquisa = "mes At";
            var filtro = new FiltroHistoricoMedico(null, null, null, parametroPesquisa, null);
            var resultado = controller.pesquisarHistoricoMedico(filtro);

            assertEquals(1, resultado.size());
            var historico = resultado.iterator().next();
            assertEquals(response2.id(), historico.id());
            assertEquals(DIAGNOSTICO_ATUAL, historico.diagnostico());
            assertEquals(PRESCRICAO_ATUAL, historico.prescricao());
            assertEquals(EXAMES_ATUAL, historico.exames());
            assertEquals(idConsulta2, historico.consulta().id());
        }

        @Test
        @WithMockUser(roles = "MEDICO")
        void dadoFiltroValido_quandoPesquisarPorConsultaId_entaoRetornarSetComUmHistoricoMedico() {
            var idConsulta1 = consultaDao1.getId();
            var request1 = UtilHistoricoMedicoTest.montarHistoricoMedicoRequestDto(DIAGNOSTICO, PRESCRICAO, EXAMES, idConsulta1);
            controller.criarHistoricoMedico(request1);

            var idConsulta2 = consultaDao2.getId();
            var request2 = UtilHistoricoMedicoTest.montarHistoricoMedicoRequestDto(DIAGNOSTICO_ATUAL, PRESCRICAO_ATUAL, EXAMES_ATUAL, idConsulta2);
            var response2 = controller.criarHistoricoMedico(request2);

            var filtro = new FiltroHistoricoMedico(null, null, null, null, idConsulta2);
            var resultado = controller.pesquisarHistoricoMedico(filtro);

            assertEquals(1, resultado.size());
            var historico = resultado.iterator().next();
            assertEquals(response2.id(), historico.id());
            assertEquals(DIAGNOSTICO_ATUAL, historico.diagnostico());
            assertEquals(PRESCRICAO_ATUAL, historico.prescricao());
            assertEquals(EXAMES_ATUAL, historico.exames());
            assertEquals(idConsulta2, historico.consulta().id());
        }

        @Test
        @WithMockUser(roles = "MEDICO")
        void dadoFiltroValido_quandoPesquisarPorDiagnosticoAndConsultaID_entaoRetornarSetComUmHistoricoMedico() {
            var idConsulta1 = consultaDao1.getId();
            var request1 = UtilHistoricoMedicoTest.montarHistoricoMedicoRequestDto(DIAGNOSTICO, PRESCRICAO, EXAMES, idConsulta1);
            controller.criarHistoricoMedico(request1);

            var idConsulta2 = consultaDao2.getId();
            var request2 = UtilHistoricoMedicoTest.montarHistoricoMedicoRequestDto(DIAGNOSTICO_ATUAL, PRESCRICAO_ATUAL, EXAMES_ATUAL, idConsulta2);
            var response2 = controller.criarHistoricoMedico(request2);

            var parametroPesquisa = "Atual";
            var filtro = new FiltroHistoricoMedico(null, parametroPesquisa, null, null, idConsulta2);
            var resultado = controller.pesquisarHistoricoMedico(filtro);

            assertEquals(1, resultado.size());
            var historico = resultado.iterator().next();
            assertEquals(response2.id(), historico.id());
            assertEquals(DIAGNOSTICO_ATUAL, historico.diagnostico());
            assertEquals(PRESCRICAO_ATUAL, historico.prescricao());
            assertEquals(EXAMES_ATUAL, historico.exames());
            assertEquals(idConsulta2, historico.consulta().id());
        }

        @Test
        @WithMockUser(roles = "MEDICO")
        void dadoFiltroValidoMasComValorInexistente_quandoPesquisarPorDiagnosticoAndConsultaID_entaoRetornarSetVazio() {
            var idConsulta1 = consultaDao1.getId();
            var request1 = UtilHistoricoMedicoTest.montarHistoricoMedicoRequestDto(DIAGNOSTICO, PRESCRICAO, EXAMES, idConsulta1);
            controller.criarHistoricoMedico(request1);

            var idConsulta2 = consultaDao2.getId();
            var request2 = UtilHistoricoMedicoTest.montarHistoricoMedicoRequestDto(DIAGNOSTICO_ATUAL, PRESCRICAO_ATUAL, EXAMES_ATUAL, idConsulta2);
            controller.criarHistoricoMedico(request2);

            var parametroPesquisa = "Abracadabra";
            var filtro = new FiltroHistoricoMedico(null, parametroPesquisa, null, null, idConsulta2);
            var resultado = controller.pesquisarHistoricoMedico(filtro);

            assertEquals(0, resultado.size());
        }

        @Test
        @WithMockUser(roles = "PACIENTE")
        void dadoUsuarioPaciente_quandoPesquisarHistoricoMedico_entaoLancarAccessDeniedException() {
            var filtro = new FiltroHistoricoMedico(null, null, null, null, consultaDao1.getId());
            assertThrows(AccessDeniedException.class, () -> controller.pesquisarHistoricoMedico(filtro));
        }

        @Test
        void dadoUsuarioNaoAutenticado_quandoPesquisarHistoricoMedico_entaoLancarAuthenticationException() {
            var filtro = new FiltroHistoricoMedico(null, null, null, null, consultaDao1.getId());
            assertThrows(AuthenticationCredentialsNotFoundException.class, () -> controller.pesquisarHistoricoMedico(filtro));
        }
    }
}