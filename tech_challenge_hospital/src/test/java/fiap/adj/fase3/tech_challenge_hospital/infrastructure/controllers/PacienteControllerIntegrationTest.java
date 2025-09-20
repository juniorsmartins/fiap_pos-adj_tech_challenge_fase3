package fiap.adj.fase3.tech_challenge_hospital.infrastructure.controllers;

import fiap.adj.fase3.tech_challenge_hospital.domain.entities.enums.RoleEnum;
import fiap.adj.fase3.tech_challenge_hospital.infrastructure.repositories.UserRepository;
import fiap.adj.fase3.tech_challenge_hospital.kafka.BaseIntegrationTest;
import fiap.adj.fase3.tech_challenge_hospital.utils.UtilPacienteTest;
import fiap.adj.fase3.tech_challenge_hospital.infrastructure.daos.PacienteDao;
import fiap.adj.fase3.tech_challenge_hospital.infrastructure.repositories.PacienteRepository;
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
class PacienteControllerIntegrationTest extends BaseIntegrationTest {

    private static final String NOME_INICIAL = "Paciente Inicial";

    private static final String EMAIL_INICIAL = "inicial@email.com";

    private static final String NOME_ATUAL = "Paciente Atual";

    private static final String EMAIL_ATUAL = "atual@email.com";

    private static final String PASSWORD = "password123";

    private static final String PASSWORD_ATUAL = "password999";

    @Autowired
    private PacienteController controller;

    @Autowired
    private PacienteRepository repository;

    @Autowired
    private UserRepository userRepository;

    private PacienteDao pacienteDao;

    @BeforeEach
    void setUp() {
        var username = UtilUserTest.montarStringAleatoria();
        pacienteDao = UtilPacienteTest.montarPacienteDao(NOME_INICIAL, EMAIL_INICIAL, username, PASSWORD);
        repository.save(pacienteDao);
    }

    @AfterEach
    void tearDown() {
        repository.deleteById(pacienteDao.getId());
        userRepository.deleteById(pacienteDao.getUser().getId());
    }

    @Nested
    @DisplayName("Criar")
    class Criar {

        @Test
        void dadoRequisicaoValida_quandoCriar_entaoRetornarResponseComDadosValidos() {
            var username = UtilUserTest.montarStringAleatoria();

            var requestDto = UtilPacienteTest.montarPacienteRequestDto(NOME_INICIAL, EMAIL_INICIAL, username, PASSWORD);
            var response = controller.criarPaciente(requestDto);
            assertNotNull(response.id());
            assertEquals(requestDto.getNome(), response.nome());
            assertEquals(requestDto.getEmail(), response.email());
            assertEquals(requestDto.getUser().getUsername(), response.user().username());
            assertEquals(RoleEnum.ROLE_PACIENTE.getValue(), response.user().role().name());
        }

        @Test
        void dadoRequisicaoValida_quandoCriar_entaoSalvarDadosValidosNoBanco() {
            var username = UtilUserTest.montarStringAleatoria();

            var requestDto = UtilPacienteTest.montarPacienteRequestDto(NOME_INICIAL, EMAIL_INICIAL, username, PASSWORD);
            var response = controller.criarPaciente(requestDto);
            var dadoSalvo = repository.findById(response.id()).orElseThrow();
            assertEquals(requestDto.getNome(), dadoSalvo.getNome());
            assertEquals(requestDto.getEmail(), dadoSalvo.getEmail());
            assertEquals(requestDto.getUser().getUsername(), dadoSalvo.getUser().getUsername());
            assertEquals(RoleEnum.ROLE_PACIENTE.getValue(), dadoSalvo.getUser().getRole().getName());
        }
    }

    @Nested
    @DisplayName("Consultar")
    class Consultar {

        @Test
        void dadoIdValido_quandoConsultarPorId_entaoRetornarResponseValido() {
            var response = controller.consultarPacientePorId(pacienteDao.getId());
            assertEquals(pacienteDao.getId(), response.id());
            assertEquals(pacienteDao.getNome(), response.nome());
            assertEquals(pacienteDao.getEmail(), response.email());
            assertEquals(pacienteDao.getUser().getUsername(), response.user().username());
            assertEquals(RoleEnum.ROLE_PACIENTE.getValue(), response.user().role().name());
        }
    }

    @Nested
    @DisplayName("Apagar")
    class Apagar {

        @Test
        void dadoIdValido_quandoApagarPorId_entaoRetornarTrue() {
            var response = controller.apagarPaciente(pacienteDao.getId());
            assertTrue(response);
        }

        @Test
        void dadoIdValido_quandoApagarPorId_entaoDeletarDoBanco() {
            var id = pacienteDao.getId();
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
            var username_antigo = pacienteDao.getUser().getUsername();
            var username_novo = UtilUserTest.montarStringAleatoria();

            var desatualizado = repository.findById(pacienteDao.getId());
            assertFalse(desatualizado.isEmpty());
            assertEquals(NOME_INICIAL, desatualizado.get().getNome());
            assertEquals(EMAIL_INICIAL, desatualizado.get().getEmail());
            assertEquals(username_antigo, desatualizado.get().getUser().getUsername());
            assertEquals(PASSWORD, desatualizado.get().getUser().getPassword());

            var atualizado = UtilPacienteTest.montarPacienteRequestDto(NOME_ATUAL, EMAIL_ATUAL, username_novo, PASSWORD_ATUAL);
            var response = controller.atualizarPaciente(pacienteDao.getId(), atualizado);

            assertEquals(atualizado.getNome(), response.nome());
            assertEquals(atualizado.getEmail(), response.email());
            assertEquals(atualizado.getUser().getUsername(), response.user().username());
            assertEquals(atualizado.getUser().getPassword(), response.user().password());
            assertNotEquals(NOME_INICIAL, response.nome());
            assertNotEquals(EMAIL_INICIAL, response.email());
            assertNotEquals(username_antigo, response.user().username());
            assertNotEquals(PASSWORD, response.user().password());
        }

        @Test
        void dadoRequisicaoValida_quandoAtualizar_entaoAtualizarNoBanco() {
            var username1 = UtilUserTest.montarStringAleatoria();
            var username2 = UtilUserTest.montarStringAleatoria();

            var id = pacienteDao.getId();
            var atualizado = UtilPacienteTest.montarPacienteRequestDto(NOME_ATUAL, EMAIL_ATUAL, username2, PASSWORD_ATUAL);
            var response = controller.atualizarPaciente(id, atualizado);

            var doBanco = repository.findById(id).orElseThrow();

            assertEquals(doBanco.getNome(), response.nome());
            assertEquals(doBanco.getEmail(), response.email());
            assertEquals(doBanco.getUser().getUsername(), response.user().username());
            assertEquals(doBanco.getUser().getPassword(), response.user().password());
            assertNotEquals(NOME_INICIAL, doBanco.getNome());
            assertNotEquals(EMAIL_INICIAL, doBanco.getEmail());
            assertNotEquals(username1, doBanco.getUser().getUsername());
            assertNotEquals(PASSWORD, doBanco.getUser().getPassword());
        }
    }
}