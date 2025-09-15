package fiap.adj.fase3.tech_challenge_hospital.infrastructure.controllers;

import fiap.adj.fase3.tech_challenge_hospital.kafka.KafkaBaseIntegrationTest;
import fiap.adj.fase3.tech_challenge_hospital.utils.UtilPacienteTest;
import fiap.adj.fase3.tech_challenge_hospital.infrastructure.daos.PacienteDao;
import fiap.adj.fase3.tech_challenge_hospital.infrastructure.repositories.PacienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class PacienteControllerIntegrationTest extends KafkaBaseIntegrationTest {

    private static final String NOME_INICIAL = "Paciente Inicial";

    private static final String EMAIL_INICIAL = "inicial@email.com";

    private static final String USERNAME = "username123";

    private static final String PASSWORD = "password123";

    private static final String NOME_ATUAL = "Paciente Atual";

    private static final String EMAIL_ATUAL = "atual@email.com";

    private static final String USERNAME_ATUAL = "username999";

    private static final String PASSWORD_ATUAL = "password999";

    @Autowired
    private PacienteController controller;

    @Autowired
    private PacienteRepository repository;

    private PacienteDao dao;

    @BeforeEach
    void setUp() {
        dao = UtilPacienteTest.montarPacienteDao(NOME_INICIAL, EMAIL_INICIAL, USERNAME, PASSWORD);
        repository.save(dao);
    }

    @Nested
    @DisplayName("Criar")
    class Criar {

        @Test
        void dadoRequisicaoValida_quandoCriar_entaoRetornarResponseComDadosValidos() {
            var requestDto = UtilPacienteTest.montarPacienteRequestDto(NOME_INICIAL, EMAIL_INICIAL, USERNAME, PASSWORD);
            var response = controller.criarPaciente(requestDto);
            assertNotNull(response.id());
            assertEquals(requestDto.getNome(), response.nome());
            assertEquals(requestDto.getEmail(), response.email());
            assertEquals(requestDto.getUser().getUsername(), response.user().username());
        }

        @Test
        void dadoRequisicaoValida_quandoCriar_entaoSalvarDadosValidosNoBanco() {
            var requestDto = UtilPacienteTest.montarPacienteRequestDto(NOME_INICIAL, EMAIL_INICIAL, USERNAME, PASSWORD);
            var response = controller.criarPaciente(requestDto);
            var dadoSalvo = repository.findById(response.id()).orElseThrow();
            assertEquals(requestDto.getNome(), dadoSalvo.getNome());
            assertEquals(requestDto.getEmail(), dadoSalvo.getEmail());
            assertEquals(requestDto.getUser().getUsername(), dadoSalvo.getUser().getUsername());
        }
    }

    @Nested
    @DisplayName("Consultar")
    class Consultar {

        @Test
        void dadoIdValido_quandoConsultarPorId_entaoRetornarResponseValido() {
            var response = controller.consultarPacientePorId(dao.getId());
            assertEquals(dao.getId(), response.id());
            assertEquals(dao.getNome(), response.nome());
            assertEquals(dao.getEmail(), response.email());
            assertEquals(dao.getUser().getUsername(), response.user().username());
        }
    }

    @Nested
    @DisplayName("Apagar")
    class Apagar {

        @Test
        void dadoIdValido_quandoApagarPorId_entaoRetornarTrue() {
            var response = controller.apagarPaciente(dao.getId());
            assertTrue(response);
        }

        @Test
        void dadoIdValido_quandoApagarPorId_entaoDeletarDoBanco() {
            var id = dao.getId();
            var dao = repository.findById(id);
            assertFalse(dao.isEmpty());

            var response = controller.apagarPaciente(id);
            assertTrue(response);

            var daoApagado = repository.findById(id);
            assertTrue(daoApagado.isEmpty());
        }
    }

    @Nested
    @DisplayName("Atualizar")
    class Atualizar {

        @Test
        void dadoRequisicaoValida_quandoAtualizar_entaoRetornarResponseValido() {
            var desatualizado = repository.findById(dao.getId());
            assertFalse(desatualizado.isEmpty());
            assertEquals(NOME_INICIAL, desatualizado.get().getNome());
            assertEquals(EMAIL_INICIAL, desatualizado.get().getEmail());
            assertEquals(USERNAME, desatualizado.get().getUser().getUsername());
            assertEquals(PASSWORD, desatualizado.get().getUser().getPassword());

            var atualizado = UtilPacienteTest.montarPacienteRequestDto(NOME_ATUAL, EMAIL_ATUAL, USERNAME_ATUAL, PASSWORD_ATUAL);
            var response = controller.atualizarPaciente(dao.getId(), atualizado);

            assertEquals(atualizado.getNome(), response.nome());
            assertEquals(atualizado.getEmail(), response.email());
            assertEquals(atualizado.getUser().getUsername(), response.user().username());
            assertEquals(atualizado.getUser().getPassword(), response.user().password());
            assertNotEquals(NOME_INICIAL, response.nome());
            assertNotEquals(EMAIL_INICIAL, response.email());
            assertNotEquals(USERNAME, response.user().username());
            assertNotEquals(PASSWORD, response.user().password());
        }

        @Test
        void dadoRequisicaoValida_quandoAtualizar_entaoAtualizarNoBanco() {
            var id = dao.getId();
            var atualizado = UtilPacienteTest.montarPacienteRequestDto(NOME_ATUAL, EMAIL_ATUAL, USERNAME_ATUAL, PASSWORD_ATUAL);
            var response = controller.atualizarPaciente(id, atualizado);

            var doBanco = repository.findById(id).orElseThrow();

            assertEquals(doBanco.getNome(), response.nome());
            assertEquals(doBanco.getEmail(), response.email());
            assertEquals(doBanco.getUser().getUsername(), response.user().username());
            assertEquals(doBanco.getUser().getPassword(), response.user().password());
            assertNotEquals(NOME_INICIAL, doBanco.getNome());
            assertNotEquals(EMAIL_INICIAL, doBanco.getEmail());
            assertNotEquals(USERNAME, doBanco.getUser().getUsername());
            assertNotEquals(PASSWORD, doBanco.getUser().getPassword());
        }
    }
}