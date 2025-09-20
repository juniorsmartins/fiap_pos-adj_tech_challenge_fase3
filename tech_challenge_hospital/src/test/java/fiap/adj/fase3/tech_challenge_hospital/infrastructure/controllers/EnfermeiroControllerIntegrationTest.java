package fiap.adj.fase3.tech_challenge_hospital.infrastructure.controllers;

import fiap.adj.fase3.tech_challenge_hospital.domain.entities.enums.RoleEnum;
import fiap.adj.fase3.tech_challenge_hospital.infrastructure.daos.EnfermeiroDao;
import fiap.adj.fase3.tech_challenge_hospital.infrastructure.repositories.EnfermeiroRepository;
import fiap.adj.fase3.tech_challenge_hospital.infrastructure.repositories.UserRepository;
import fiap.adj.fase3.tech_challenge_hospital.kafka.BaseIntegrationTest;
import fiap.adj.fase3.tech_challenge_hospital.utils.UtilEnfermeiroTest;
import fiap.adj.fase3.tech_challenge_hospital.utils.UtilUserTest;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@WithMockUser(username = "admin", roles = {"ADMIN"})
class EnfermeiroControllerIntegrationTest extends BaseIntegrationTest {

    private static final String NOME_INICIAL = "Enfermeiro Inicial";

    private static final String PASSWORD = "password123";

    private static final String NOME_ATUAL = "Enfermeiro Atual";

    private static final String PASSWORD_ATUAL = "password999";

    private String username1;

    @Autowired
    private EnfermeiroController controller;

    @Autowired
    private EnfermeiroRepository repository;

    @Autowired
    private UserRepository userRepository;

    private EnfermeiroDao enfermeiroDao;

    @BeforeEach
    void setUp() {
        username1 = UtilUserTest.montarStringAleatoria();
        enfermeiroDao = UtilEnfermeiroTest.montarEnfermeiroDao(NOME_INICIAL, username1, PASSWORD);
        repository.save(enfermeiroDao);
    }

    @AfterEach
    void tearDown() {
        repository.deleteById(enfermeiroDao.getId());
        userRepository.deleteById(enfermeiroDao.getUser().getId());
    }

    @Nested
    @DisplayName("Criar")
    class Criar {

        @Test
        void dadoRequisicaoValida_quandoCriar_entaoRetornarResponseComDadosValidos() {
            // Arrange
            var username = UtilUserTest.montarStringAleatoria();
            var requestDto = UtilEnfermeiroTest.montarEnfermeiroRequestDto(NOME_INICIAL, username, PASSWORD);
            // Act
            var response = controller.criarEnfermeiro(requestDto);
            // Assert
            assertNotNull(response.id());
            assertEquals(requestDto.getNome(), response.nome());
            assertEquals(requestDto.getUser().getUsername(), response.user().username());
            assertEquals(RoleEnum.ROLE_ENFERMEIRO.getValue(), response.user().role().name());
        }

        @Test
        void dadoRequisicaoValida_quandoCriar_entaoSalvarDadosValidosNoBanco() {
            var username = UtilUserTest.montarStringAleatoria();
            var requestDto = UtilEnfermeiroTest.montarEnfermeiroRequestDto(NOME_INICIAL, username, PASSWORD);
            var response = controller.criarEnfermeiro(requestDto);
            var dadoSalvo = repository.findById(response.id()).orElseThrow();
            assertEquals(requestDto.getNome(), dadoSalvo.getNome());
            assertEquals(requestDto.getUser().getUsername(), dadoSalvo.getUser().getUsername());
            assertEquals(RoleEnum.ROLE_ENFERMEIRO.getValue(), dadoSalvo.getUser().getRole().getName());
        }
    }

    @Nested
    @DisplayName("Consultar")
    class Consultar {

        @Test
        void dadoIdValido_quandoConsultarPorId_entaoRetornarResponseValido() {
            var response = controller.consultarEnfermeiroPorId(enfermeiroDao.getId());
            assertEquals(enfermeiroDao.getId(), response.id());
            assertEquals(enfermeiroDao.getNome(), response.nome());
            assertEquals(enfermeiroDao.getUser().getUsername(), response.user().username());
            assertEquals(RoleEnum.ROLE_ENFERMEIRO.getValue(), response.user().role().name());
        }
    }

    @Nested
    @DisplayName("Apagar")
    class Apagar {

        @Test
        void dadoIdValido_quandoApagarPorId_entaoRetornarTrue() {
            var response = controller.apagarEnfermeiro(enfermeiroDao.getId());
            assertTrue(response);
        }

        @Test
        void dadoIdValido_quandoApagarPorId_entaoDeletarDoBanco() {
            var id = enfermeiroDao.getId();
            var dao = repository.findById(id);
            assertFalse(dao.isEmpty());

            var response = controller.apagarEnfermeiro(id);
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
            var desatualizado = repository.findById(enfermeiroDao.getId());
            assertFalse(desatualizado.isEmpty());
            assertEquals(NOME_INICIAL, desatualizado.get().getNome());
            assertEquals(username1, desatualizado.get().getUser().getUsername());
            assertEquals(PASSWORD, desatualizado.get().getUser().getPassword());

            var username = UtilUserTest.montarStringAleatoria();
            var atualizado = UtilEnfermeiroTest.montarEnfermeiroRequestDto(NOME_ATUAL, username, PASSWORD_ATUAL);
            var response = controller.atualizarEnfermeiro(enfermeiroDao.getId(), atualizado);

            assertEquals(atualizado.getNome(), response.nome());
            assertEquals(atualizado.getUser().getUsername(), response.user().username());
            assertEquals(atualizado.getUser().getPassword(), response.user().password());
            assertNotEquals(NOME_INICIAL, response.nome());
            assertNotEquals(username1, response.user().username());
            assertNotEquals(PASSWORD, response.user().password());
        }

        @Test
        void dadoRequisicaoValida_quandoAtualizar_entaoAtualizarNoBanco() {
            var id = enfermeiroDao.getId();
            var username = UtilUserTest.montarStringAleatoria();
            var atualizado = UtilEnfermeiroTest.montarEnfermeiroRequestDto(NOME_ATUAL, username, PASSWORD_ATUAL);
            var response = controller.atualizarEnfermeiro(id, atualizado);

            var doBanco = repository.findById(id).orElseThrow();

            assertEquals(doBanco.getNome(), response.nome());
            assertEquals(doBanco.getUser().getUsername(), response.user().username());
            assertEquals(doBanco.getUser().getPassword(), response.user().password());
            assertNotEquals(NOME_INICIAL, doBanco.getNome());
            assertNotEquals(username1, doBanco.getUser().getUsername());
            assertNotEquals(PASSWORD, doBanco.getUser().getPassword());
        }
    }
}