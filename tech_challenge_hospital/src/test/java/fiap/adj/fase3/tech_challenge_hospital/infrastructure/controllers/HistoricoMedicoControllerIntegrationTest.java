package fiap.adj.fase3.tech_challenge_hospital.infrastructure.controllers;

import fiap.adj.fase3.tech_challenge_hospital.kafka.KafkaBaseIntegrationTest;
import fiap.adj.fase3.tech_challenge_hospital.utils.UtilConsultaTest;
import fiap.adj.fase3.tech_challenge_hospital.utils.UtilHistoricoMedicoTest;
import fiap.adj.fase3.tech_challenge_hospital.utils.UtilMedicoTest;
import fiap.adj.fase3.tech_challenge_hospital.utils.UtilPacienteTest;
import fiap.adj.fase3.tech_challenge_hospital.application.dtos.request.FiltroHistoricoMedico;
import fiap.adj.fase3.tech_challenge_hospital.domain.entities.enums.ConsultaStatusEnum;
import fiap.adj.fase3.tech_challenge_hospital.infrastructure.daos.ConsultaDao;
import fiap.adj.fase3.tech_challenge_hospital.infrastructure.daos.MedicoDao;
import fiap.adj.fase3.tech_challenge_hospital.infrastructure.daos.PacienteDao;
import fiap.adj.fase3.tech_challenge_hospital.infrastructure.repositories.ConsultaRepository;
import fiap.adj.fase3.tech_challenge_hospital.infrastructure.repositories.HistoricoMedicoRepository;
import fiap.adj.fase3.tech_challenge_hospital.infrastructure.repositories.MedicoRepository;
import fiap.adj.fase3.tech_challenge_hospital.infrastructure.repositories.PacienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
class HistoricoMedicoControllerIntegrationTest extends KafkaBaseIntegrationTest {

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
        MedicoDao medicoDao1 = UtilMedicoTest.montarMedicoDao("MedicoConsulta 1", "username111", "password111");
        medicoRepository.save(medicoDao1);

        pacienteDao1 = UtilPacienteTest.montarPacienteDao("PacienteConsulta 1", "paciente1@email.com", "username333", "password333");
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
        void dadaRequisicaoValida_quandoCriarHistoricoMedico_entaoRetornarResponseValido() {
            var request = UtilHistoricoMedicoTest.montarHistoricoMedicoRequestDto(DIAGNOSTICO, PRESCRICAO, EXAMES, consultaDao1.getId());
            var response = controller.criarHistoricoMedico(request);
            assertEquals(DIAGNOSTICO, response.diagnostico());
            assertEquals(PRESCRICAO, response.prescricao());
            assertEquals(EXAMES, response.exames());
            assertEquals(consultaDao1.getId(), response.consulta().id());
        }

        @Test
        void dadaRequisicaoValida_quandoCriarHistoricoMedico_entaoSalvarNoBanco() {
            var request = UtilHistoricoMedicoTest.montarHistoricoMedicoRequestDto(DIAGNOSTICO, PRESCRICAO, EXAMES, consultaDao1.getId());
            var response = controller.criarHistoricoMedico(request);
            var doBanco = repository.findById(response.id()).orElseThrow();
            assertEquals(doBanco.getDiagnostico(), response.diagnostico());
            assertEquals(doBanco.getPrescricao(), response.prescricao());
            assertEquals(doBanco.getExames(), response.exames());
            assertEquals(doBanco.getConsulta().getId(), response.consulta().id());
        }
    }

    @Nested
    @DisplayName("Atualizar")
    class Atualizar {

        @Test
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
    }

    @Nested
    @DisplayName("Listar")
    class Listar {

        @Test
        void dadoIdValido_quandoListarHistoricoPorIdPaciente_entaoRetornarDoisItens() {
            var request1 = UtilHistoricoMedicoTest.montarHistoricoMedicoRequestDto(DIAGNOSTICO, PRESCRICAO, EXAMES, consultaDao1.getId());
            controller.criarHistoricoMedico(request1);

            var request2 = UtilHistoricoMedicoTest.montarHistoricoMedicoRequestDto(DIAGNOSTICO, PRESCRICAO, EXAMES, consultaDao2.getId());
            controller.criarHistoricoMedico(request2);

            var response = controller.listarHistoricoMedicoPorIdPaciente(pacienteDao1.getId());
            assertEquals(2, response.size());
        }
    }

    @Nested
    @DisplayName("Consultar")
    class Consultar {

        @Test
        void dadoIdValido_quandoConsultarHistoricoMedicoPorIdConsulta() {
            var idConsulta = consultaDao1.getId();
            var request1 = UtilHistoricoMedicoTest.montarHistoricoMedicoRequestDto(DIAGNOSTICO, PRESCRICAO, EXAMES, idConsulta);
            controller.criarHistoricoMedico(request1);
            var response = controller.consultarHistoricoMedicoPorIdConsulta(idConsulta);
            assertEquals(DIAGNOSTICO, response.diagnostico());
            assertEquals(PRESCRICAO, response.prescricao());
            assertEquals(EXAMES, response.exames());
        }
    }

    @Nested
    @DisplayName("Pesquisar")
    class Pesquisar {

        @Test
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
    }
}