package fiap.adj.fase3.tech_challenge_hospital.infrastructure.controllers;

import fiap.adj.fase3.tech_challenge_hospital.kafka.KafkaBaseIntegrationTest;
import fiap.adj.fase3.tech_challenge_hospital.utils.UtilMedicoTest;
import fiap.adj.fase3.tech_challenge_hospital.infrastructure.daos.MedicoDao;
import fiap.adj.fase3.tech_challenge_hospital.infrastructure.repositories.MedicoRepository;
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
class MedicoControllerIntegrationTest extends KafkaBaseIntegrationTest {

    private static final String NOME_INICIAL = "Médico Inicial";

    private static final String USERNAME = "username123";

    private static final String PASSWORD = "password123";

    private static final String NOME_ATUAL = "Médico Atual";

    private static final String USERNAME_ATUAL = "username999";

    private static final String PASSWORD_ATUAL = "password999";

    @Autowired
    private MedicoController controller;

    @Autowired
    private MedicoRepository repository;

    private MedicoDao dao;

    @BeforeEach
    void setUp() {
        dao = UtilMedicoTest.montarMedicoDao(NOME_INICIAL, USERNAME, PASSWORD);
        repository.save(dao);
    }

    @Nested
    @DisplayName("Criar")
    class Criar {

        @Test
        void dadoRequisicaoValida_quandoCriar_entaoRetornarResponseComDadosValidos() {
            // Arrange
            var requestDto = UtilMedicoTest.montarMedicoRequestDto(NOME_INICIAL, USERNAME, PASSWORD);
            // Act
            var response = controller.criarMedico(requestDto);
            // Assert
            assertNotNull(response.id());
            assertEquals(requestDto.getNome(), response.nome());
            assertEquals(requestDto.getUser().getUsername(), response.user().username());
        }

        @Test
        void dadoRequisicaoValida_quandoCriar_entaoSalvarDadosValidosNoBanco() {
            var requestDto = UtilMedicoTest.montarMedicoRequestDto(NOME_INICIAL, USERNAME, PASSWORD);
            var response = controller.criarMedico(requestDto);
            var dadoSalvo = repository.findById(response.id()).orElseThrow();
            assertEquals(requestDto.getNome(), dadoSalvo.getNome());
            assertEquals(requestDto.getUser().getUsername(), dadoSalvo.getUser().getUsername());
        }
    }

    @Nested
    @DisplayName("Consultar")
    class Consultar {

        @Test
        void dadoIdValido_quandoConsultarPorId_entaoRetornarResponseValido() {
            var response = controller.consultarMedicoPorId(dao.getId());
            assertEquals(dao.getId(), response.id());
            assertEquals(dao.getNome(), response.nome());
            assertEquals(dao.getUser().getUsername(), response.user().username());
        }
    }

    @Nested
    @DisplayName("Apagar")
    class Apagar {

        @Test
        void dadoIdValido_quandoApagarPorId_entaoRetornarTrue() {
            var response = controller.apagarMedico(dao.getId());
            assertTrue(response);
        }

        @Test
        void dadoIdValido_quandoApagarPorId_entaoDeletarDoBanco() {
            var id = dao.getId();
            var dao = repository.findById(id);
            assertFalse(dao.isEmpty());

            var response = controller.apagarMedico(id);
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
            assertEquals(USERNAME, desatualizado.get().getUser().getUsername());
            assertEquals(PASSWORD, desatualizado.get().getUser().getPassword());

            var atualizado = UtilMedicoTest.montarMedicoRequestDto(NOME_ATUAL, USERNAME_ATUAL, PASSWORD_ATUAL);
            var response = controller.atualizarMedico(dao.getId(), atualizado);

            assertEquals(atualizado.getNome(), response.nome());
            assertEquals(atualizado.getUser().getUsername(), response.user().username());
            assertEquals(atualizado.getUser().getPassword(), response.user().password());
            assertNotEquals(NOME_INICIAL, response.nome());
            assertNotEquals(USERNAME, response.user().username());
            assertNotEquals(PASSWORD, response.user().password());
        }

        @Test
        void dadoRequisicaoValida_quandoAtualizar_entaoAtualizarNoBanco() {
            var id = dao.getId();
            var atualizado = UtilMedicoTest.montarMedicoRequestDto(NOME_ATUAL, USERNAME_ATUAL, PASSWORD_ATUAL);
            var response = controller.atualizarMedico(id, atualizado);

            var doBanco = repository.findById(id).orElseThrow();

            assertEquals(doBanco.getNome(), response.nome());
            assertEquals(doBanco.getUser().getUsername(), response.user().username());
            assertEquals(doBanco.getUser().getPassword(), response.user().password());
            assertNotEquals(NOME_INICIAL, doBanco.getNome());
            assertNotEquals(USERNAME, doBanco.getUser().getUsername());
            assertNotEquals(PASSWORD, doBanco.getUser().getPassword());
        }
    }
}