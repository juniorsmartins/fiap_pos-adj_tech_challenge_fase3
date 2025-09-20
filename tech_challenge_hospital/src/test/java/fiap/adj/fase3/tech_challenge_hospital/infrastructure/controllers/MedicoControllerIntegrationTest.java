package fiap.adj.fase3.tech_challenge_hospital.infrastructure.controllers;

import fiap.adj.fase3.tech_challenge_hospital.domain.entities.enums.RoleEnum;
import fiap.adj.fase3.tech_challenge_hospital.infrastructure.repositories.UserRepository;
import fiap.adj.fase3.tech_challenge_hospital.kafka.BaseIntegrationTest;
import fiap.adj.fase3.tech_challenge_hospital.utils.UtilMedicoTest;
import fiap.adj.fase3.tech_challenge_hospital.infrastructure.daos.MedicoDao;
import fiap.adj.fase3.tech_challenge_hospital.infrastructure.repositories.MedicoRepository;
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
class MedicoControllerIntegrationTest extends BaseIntegrationTest {

    private static final String NOME_INICIAL = "Médico Inicial";

    private static final String PASSWORD = "password123";

    private static final String NOME_ATUAL = "Médico Atual";

    private static final String PASSWORD_ATUAL = "password999";

    private String username1;

    @Autowired
    private MedicoController controller;

    @Autowired
    private MedicoRepository repository;

    @Autowired
    private UserRepository userRepository;

    private MedicoDao medicoDao;

    @BeforeEach
    void setUp() {
        username1 = UtilUserTest.montarStringAleatoria();
        medicoDao = UtilMedicoTest.montarMedicoDao(NOME_INICIAL, username1, PASSWORD);
        repository.save(medicoDao);
    }

    @AfterEach
    void tearDown() {
        repository.deleteById(medicoDao.getId());
        userRepository.deleteById(medicoDao.getUser().getId());
    }

    @Nested
    @DisplayName("Criar")
    class Criar {

        @Test
        void dadoRequisicaoValida_quandoCriar_entaoRetornarResponseComDadosValidos() {
            // Arrange
            var username = UtilUserTest.montarStringAleatoria();
            var requestDto = UtilMedicoTest.montarMedicoRequestDto(NOME_INICIAL, username, PASSWORD);
            // Act
            var response = controller.criarMedico(requestDto);
            // Assert
            assertNotNull(response.id());
            assertEquals(requestDto.getNome(), response.nome());
            assertEquals(requestDto.getUser().getUsername(), response.user().username());
            assertEquals(RoleEnum.ROLE_MEDICO.getValue(), response.user().role().name());
        }

        @Test
        void dadoRequisicaoValida_quandoCriar_entaoSalvarDadosValidosNoBanco() {
            var username = UtilUserTest.montarStringAleatoria();
            var requestDto = UtilMedicoTest.montarMedicoRequestDto(NOME_INICIAL, username, PASSWORD);
            var response = controller.criarMedico(requestDto);
            var dadoSalvo = repository.findById(response.id()).orElseThrow();
            assertEquals(requestDto.getNome(), dadoSalvo.getNome());
            assertEquals(requestDto.getUser().getUsername(), dadoSalvo.getUser().getUsername());
            assertEquals(RoleEnum.ROLE_MEDICO.getValue(), dadoSalvo.getUser().getRole().getName());
        }
    }

    @Nested
    @DisplayName("Consultar")
    class Consultar {

        @Test
        void dadoIdValido_quandoConsultarPorId_entaoRetornarResponseValido() {
            var response = controller.consultarMedicoPorId(medicoDao.getId());
            assertEquals(medicoDao.getId(), response.id());
            assertEquals(medicoDao.getNome(), response.nome());
            assertEquals(medicoDao.getUser().getUsername(), response.user().username());
            assertEquals(RoleEnum.ROLE_MEDICO.getValue(), response.user().role().name());
        }
    }

    @Nested
    @DisplayName("Apagar")
    class Apagar {

        @Test
        void dadoIdValido_quandoApagarPorId_entaoRetornarTrue() {
            var response = controller.apagarMedico(medicoDao.getId());
            assertTrue(response);
        }

        @Test
        void dadoIdValido_quandoApagarPorId_entaoDeletarDoBanco() {
            var id = medicoDao.getId();
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
            var desatualizado = repository.findById(medicoDao.getId());
            assertFalse(desatualizado.isEmpty());
            assertEquals(NOME_INICIAL, desatualizado.get().getNome());
            assertEquals(username1, desatualizado.get().getUser().getUsername());
            assertEquals(PASSWORD, desatualizado.get().getUser().getPassword());

            var username2 = UtilUserTest.montarStringAleatoria();
            var atualizado = UtilMedicoTest.montarMedicoRequestDto(NOME_ATUAL, username2, PASSWORD_ATUAL);
            var response = controller.atualizarMedico(medicoDao.getId(), atualizado);

            assertEquals(atualizado.getNome(), response.nome());
            assertEquals(atualizado.getUser().getUsername(), response.user().username());
            assertEquals(atualizado.getUser().getPassword(), response.user().password());
            assertNotEquals(NOME_INICIAL, response.nome());
            assertNotEquals(username1, response.user().username());
            assertNotEquals(PASSWORD, response.user().password());
        }

        @Test
        void dadoRequisicaoValida_quandoAtualizar_entaoAtualizarNoBanco() {
            var id = medicoDao.getId();
            var username = UtilUserTest.montarStringAleatoria();
            var atualizado = UtilMedicoTest.montarMedicoRequestDto(NOME_ATUAL, username, PASSWORD_ATUAL);
            var response = controller.atualizarMedico(id, atualizado);

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