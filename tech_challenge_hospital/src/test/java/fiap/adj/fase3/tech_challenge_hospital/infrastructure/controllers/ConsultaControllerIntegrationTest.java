package fiap.adj.fase3.tech_challenge_hospital.infrastructure.controllers;

import fiap.adj.fase3.tech_challenge_hospital.application.dtos.external.MensagemKafka;
import fiap.adj.fase3.tech_challenge_hospital.application.dtos.request.FiltroConsulta;
import fiap.adj.fase3.tech_challenge_hospital.domain.entities.enums.ConsultaStatusEnum;
import fiap.adj.fase3.tech_challenge_hospital.domain.entities.enums.MotivoKafkaEnum;
import fiap.adj.fase3.tech_challenge_hospital.infrastructure.daos.ConsultaDao;
import fiap.adj.fase3.tech_challenge_hospital.infrastructure.daos.MedicoDao;
import fiap.adj.fase3.tech_challenge_hospital.infrastructure.daos.PacienteDao;
import fiap.adj.fase3.tech_challenge_hospital.infrastructure.repositories.ConsultaRepository;
import fiap.adj.fase3.tech_challenge_hospital.infrastructure.repositories.MedicoRepository;
import fiap.adj.fase3.tech_challenge_hospital.infrastructure.repositories.PacienteRepository;
import fiap.adj.fase3.tech_challenge_hospital.kafka.KafkaBaseIntegrationTest;
import fiap.adj.fase3.tech_challenge_hospital.utils.UtilConsultaTest;
import fiap.adj.fase3.tech_challenge_hospital.utils.UtilMedicoTest;
import fiap.adj.fase3.tech_challenge_hospital.utils.UtilPacienteTest;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ConsultaControllerIntegrationTest extends KafkaBaseIntegrationTest {

    public static final String DATA_HORA_INICIAL = "2025-07-12T10:10:22";

    @Autowired
    private ConsultaController controller;

    @Autowired
    private ConsultaRepository repository;

    @Autowired
    private MedicoRepository medicoRepository;

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafka; // Necessário para inicializar o contexto Kafka

    private ConsultaDao consultaDao1;

    private ConsultaDao consultaDao2;

    private MedicoDao medicoDao1;

    private MedicoDao medicoDao2;

    private PacienteDao pacienteDao1;

    private PacienteDao pacienteDao2;

    @BeforeEach
    void setUp() {
        medicoDao1 = UtilMedicoTest.montarMedicoDao("MedicoConsulta 1", "username111", "password111");
        medicoRepository.save(medicoDao1);

        medicoDao2 = UtilMedicoTest.montarMedicoDao("MedicoConsulta 2", "username222", "password222");
        medicoRepository.save(medicoDao2);

        pacienteDao1 = UtilPacienteTest.montarPacienteDao("PacienteConsulta 1", "paciente1@email.com", "username333", "password333");
        pacienteRepository.save(pacienteDao1);

        pacienteDao2 = UtilPacienteTest.montarPacienteDao("PacienteConsulta 2", "paciente2@email.com", "username444", "password444");
        pacienteRepository.save(pacienteDao2);

        var dataHora1 = LocalDateTime.of(LocalDate.of(2025, 8, 10), LocalTime.of(14, 10));
        consultaDao1 = UtilConsultaTest.montarConsultaDao(dataHora1, ConsultaStatusEnum.AGENDADO.getValue(), medicoDao1, pacienteDao1);
        repository.save(consultaDao1);

        var dataHora2 = LocalDateTime.of(LocalDate.of(2025, 9, 5), LocalTime.of(16, 12));
        consultaDao2 = UtilConsultaTest.montarConsultaDao(dataHora2, ConsultaStatusEnum.AGENDADO.getValue(), medicoDao2, pacienteDao1);
        repository.save(consultaDao2);

        // KAFKA

    }

    @Nested
    @DisplayName("Criar")
    class Criar {

        @Test
        void dadaRequisicaoValida_quandoCriar_entaoRetornarResponseComDadosValidos() {
            var request = UtilConsultaTest.montarConsultaRequestDto(DATA_HORA_INICIAL, medicoDao1.getId(), pacienteDao1.getId());
            var response = controller.criarConsulta(request);
            assertNotNull(response.id());
            assertEquals(request.getDataHora(), response.dataHora().toString());
            assertEquals(response.status(), ConsultaStatusEnum.AGENDADO.getValue());
            assertEquals(medicoDao1.getId(), response.medico().id());
            assertEquals(pacienteDao1.getId(), response.paciente().id());
        }

        @Test
        void dadaRequisicaoValida_quandoCriar_entaoSalvarDadosValidosNoBanco() {
            var request = UtilConsultaTest.montarConsultaRequestDto(DATA_HORA_INICIAL, medicoDao1.getId(), pacienteDao1.getId());
            var response = controller.criarConsulta(request);
            var dadoSalvo = repository.findById(response.id()).orElseThrow();
            assertEquals(request.getDataHora(), dadoSalvo.getDataHora().toString());
            assertEquals(ConsultaStatusEnum.AGENDADO.getValue(), dadoSalvo.getStatus());
            assertEquals(request.getMedicoId(), dadoSalvo.getMedico().getId());
            assertEquals(request.getPacienteId(), dadoSalvo.getPaciente().getId());
        }
    }

    @Nested
    @DisplayName("Atualizar")
    class Atualizar {

        @Test
        void dadaRequisicaoValida_quandoAtualizar_entaoRetornarResponseValido() {
            var id = consultaDao1.getId();
            var desatualizado = repository.findById(id).orElseThrow();
            assertNotEquals(DATA_HORA_INICIAL, desatualizado.getDataHora().toString());
            assertEquals(medicoDao1.getId(), desatualizado.getMedico().getId());
            assertEquals(pacienteDao1.getId(), desatualizado.getPaciente().getId());

            var atualizado = UtilConsultaTest
                    .montarConsultaRequestDto(DATA_HORA_INICIAL, medicoDao2.getId(), pacienteDao2.getId());
            var response = controller.atualizarConsulta(id, atualizado);

            assertEquals(DATA_HORA_INICIAL, response.dataHora().toString());
            assertEquals(medicoDao2.getId(), response.medico().id());
            assertEquals(pacienteDao2.getId(), response.paciente().id());
        }

        @Test
        void dadaRequisicaoValida_quandoAtualizar_entaoAtualizarNoBanco() {
            var id = consultaDao1.getId();
            var atualizado = UtilConsultaTest
                    .montarConsultaRequestDto(DATA_HORA_INICIAL, medicoDao2.getId(), pacienteDao2.getId());
            var response = controller.atualizarConsulta(id, atualizado);

            var doBanco = repository.findById(id).orElseThrow();

            assertEquals(doBanco.getDataHora(), response.dataHora());
            assertEquals(doBanco.getStatus(), response.status());
            assertEquals(doBanco.getMedico().getId(), response.medico().id());
            assertEquals(doBanco.getPaciente().getId(), response.paciente().id());
        }
    }

    @Nested
    @DisplayName("Consultar")
    class Consultar {

        @Test
        void dadoIdValido_quandoConsultarPorId_entaoRetornarResponseValido() {
            var response = controller.consultarConsultaPorId(consultaDao1.getId());
            assertEquals(consultaDao1.getId(), response.id());
            assertEquals(consultaDao1.getDataHora(), response.dataHora());
            assertEquals(consultaDao1.getMedico().getId(), response.medico().id());
            assertEquals(consultaDao1.getPaciente().getId(), response.paciente().id());
        }
    }

    @Nested
    @DisplayName("Concluir")
    class Concluir {

        @Test
        void dadoIdValido_quandoConcluirConsulta_entaoRetornarTrue() {
            var response = controller.concluirConsulta(consultaDao1.getId());
            assertTrue(response);
        }

        @Test
        void dadoIdValido_quandoConcluirConsulta_entaoSalvarConsultaComStatusConcluido() {
            var dadoAnterior = repository.findById(consultaDao1.getId()).orElseThrow();
            assertEquals(ConsultaStatusEnum.AGENDADO.getValue(), dadoAnterior.getStatus());

            var response = controller.concluirConsulta(consultaDao1.getId());
            assertTrue(response);

            var dadoPosterior = repository.findById(consultaDao1.getId()).orElseThrow();
            assertEquals(ConsultaStatusEnum.CONCLUIDO.getValue(), dadoPosterior.getStatus());
        }
    }

    @Nested
    @DisplayName("Cancelar")
    class Cancelar {

        @Test
        void dadoIdValido_quandoCancelarConsulta_entaoRetornarTrue() {
            var response = controller.cancelarConsulta(consultaDao1.getId());
            assertTrue(response);
        }

        @Test
        void dadoIdValido_quandoCancelarConsulta_entaoSalvarConsultaComStatusCancelado() {
            var dadoAnterior = repository.findById(consultaDao1.getId()).orElseThrow();
            assertEquals(ConsultaStatusEnum.AGENDADO.getValue(), dadoAnterior.getStatus());

            var response = controller.cancelarConsulta(consultaDao1.getId());
            assertTrue(response);

            var dadoPosterior = repository.findById(consultaDao1.getId()).orElseThrow();
            assertEquals(ConsultaStatusEnum.CANCELADO.getValue(), dadoPosterior.getStatus());
        }
    }

    @Nested
    @DisplayName("listar")
    class Listar {

        @Test
        void dadoIdValido_quandoListarHistoricoDeConsultasPorIdPaciente_entaoRetornarListaValida() {
            var colecao = controller.listarHistoricoDeConsultasPorIdPaciente(pacienteDao1.getId());
            assertEquals(2, colecao.size());
        }
    }

    @Nested
    @DisplayName("Pesquisar")
    class Pesquisar {

        @Test
        void dadoFiltroValido_quandoPesquisarPorId_entaoRetornarSetComUmConsultaResponseDto() {
            var idConsulta = consultaDao1.getId();
            var filtro = new FiltroConsulta(idConsulta, null, null, null, null);
            var response = controller.pesquisarConsulta(filtro);

            assertEquals(1, response.size());
            var consulta = response.iterator().next();
            assertEquals(idConsulta, consulta.id());
            assertEquals(consultaDao1.getDataHora(), consulta.dataHora());
            assertEquals(ConsultaStatusEnum.AGENDADO.getValue(), consulta.status());
            assertEquals(consultaDao1.getMedico().getId(), consulta.medico().id());
            assertEquals(consultaDao1.getPaciente().getId(), consulta.paciente().id());
        }

        @Test
        void dadoFiltroValido_quandoPesquisarPorDataHora_entaoRetornarSetComUmConsultaResponseDto() {
            var dataHora1 = LocalDateTime
                    .of(LocalDate.of(2025, 8, 10), LocalTime.of(14, 10)).toString();
            var filtro = new FiltroConsulta(null, dataHora1, null, null, null);
            var response = controller.pesquisarConsulta(filtro);

            assertEquals(1, response.size());
            var consulta = response.iterator().next();
            assertEquals(consultaDao1.getId(), consulta.id());
            assertEquals(dataHora1, consulta.dataHora().toString());
            assertEquals(ConsultaStatusEnum.AGENDADO.getValue(), consulta.status());
            assertEquals(consultaDao1.getMedico().getId(), consulta.medico().id());
            assertEquals(consultaDao1.getPaciente().getId(), consulta.paciente().id());
        }

        @Test
        void dadoFiltroValido_quandoPesquisarPorStatusAgendado_entaoRetornarSetComDoisConsultaResponseDto() {
            var status = ConsultaStatusEnum.AGENDADO.getValue();
            var filtro = new FiltroConsulta(null, null, status, null, null);
            var response = controller.pesquisarConsulta(filtro);

            assertEquals(2, response.size());
        }

        @Test
        void dadoFiltroValido_quandoPesquisarPorMedicoId_entaoRetornarSetComUmConsultaResponseDto() {
            var idMedico2 = consultaDao2.getMedico().getId();
            var filtro = new FiltroConsulta(null, null, null, idMedico2, null);
            var response = controller.pesquisarConsulta(filtro);

            assertEquals(1, response.size());
            var consulta = response.iterator().next();
            assertEquals(consultaDao2.getId(), consulta.id());
            assertEquals(consultaDao2.getDataHora(), consulta.dataHora());
            assertEquals(consultaDao2.getStatus(), consulta.status());
            assertEquals(consultaDao2.getMedico().getId(), consulta.medico().id());
            assertEquals(consultaDao2.getPaciente().getId(), consulta.paciente().id());
        }

        @Test
        void dadoFiltroValido_quandoPesquisarPorPacienteId_entaoRetornarSetComDoisConsultaResponseDto() {
            var idPaciente1 = consultaDao2.getPaciente().getId();
            var filtro = new FiltroConsulta(null, null, null, null, idPaciente1);
            var response = controller.pesquisarConsulta(filtro);

            assertEquals(2, response.size());
        }

        @Test
        void dadoFiltrosValidosMasComValorInexistente_quandoPesquisarPorStatusAndPacienteId_entaoRetornarSetVazio() {
            var status = ConsultaStatusEnum.CONCLUIDO.getValue();
            var idPaciente1 = consultaDao2.getPaciente().getId();
            var filtro = new FiltroConsulta(null, null, status, null, idPaciente1);
            var response = controller.pesquisarConsulta(filtro);

            assertEquals(0, response.size());
        }
    }

    @Nested
    @DisplayName("EventosKafka")
    class EventosKafka {

        private KafkaConsumer<String, MensagemKafka> consumer;

        @BeforeEach
        void setUpKafkaConsumer() {
            // Configura propriedades do consumidor Kafka
            Map<String, Object> consumerProps = KafkaTestUtils.consumerProps(
                    "test-group-" + UUID.randomUUID(), // group.id único
                    "true",                           // autoCommit
                    embeddedKafka
            );
            consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest"); // Consome apenas mensagens novas
            consumerProps.put(JsonDeserializer.TRUSTED_PACKAGES, "*");

            // Cria o consumer
            consumer = new KafkaConsumer<>(
                    consumerProps,
                    new StringDeserializer(),
                    new JsonDeserializer<>(MensagemKafka.class, false)
            );

            // Subscreve ao tópico
            consumer.subscribe(List.of("evento-informar-paciente-consulta"));

            // Consome todas as mensagens existentes para "limpar" o tópico
            consumer.poll(Duration.ofSeconds(1)); // Faz um poll inicial para avançar o offset
        }

        @AfterEach
        void tearDownKafkaConsumer() {
            if (consumer != null) {
                consumer.close();
            }
        }

        @Test
        void dadaRequisicaoValida_quandoCriar_entaoEnviarMensagemKafkaCorreta() throws InterruptedException {
            // Arrange
            var request = UtilConsultaTest
                    .montarConsultaRequestDto(DATA_HORA_INICIAL, medicoDao1.getId(), pacienteDao1.getId());

            // Act
            controller.criarConsulta(request);

            // Assert → captura a mensagem do tópico
            ConsumerRecord<String, MensagemKafka> record = KafkaTestUtils
                    .getSingleRecord(consumer, "evento-informar-paciente-consulta");

            assertNotNull(record);
            MensagemKafka mensagem = record.value();
            assertEquals(request.getDataHora(), mensagem.dataHora().toString());
            assertEquals(medicoDao1.getNome(), mensagem.nomeMedico());
            assertEquals(pacienteDao1.getNome(), mensagem.nomePaciente());
            assertEquals(MotivoKafkaEnum.AGENDAMENTO.getValue(), mensagem.motivo());
        }

        @Test
        void dadaRequisicaoValida_quandoAtualizar_entaoEnviarMensagemKafkaCorreta() throws InterruptedException {
            // Arrange
            var request = UtilConsultaTest
                    .montarConsultaRequestDto(DATA_HORA_INICIAL, medicoDao2.getId(), pacienteDao2.getId());

            // Act
            controller.atualizarConsulta(consultaDao2.getId(), request);

            // Assert → captura a mensagem do tópico
            ConsumerRecord<String, MensagemKafka> record = KafkaTestUtils
                    .getSingleRecord(consumer, "evento-informar-paciente-consulta");

            assertNotNull(record);
            MensagemKafka mensagem = record.value();
            assertEquals(request.getDataHora(), mensagem.dataHora().toString());
            assertEquals(medicoDao2.getNome(), mensagem.nomeMedico());
            assertEquals(pacienteDao2.getNome(), mensagem.nomePaciente());
            assertEquals(MotivoKafkaEnum.ALTERACAO.getValue(), mensagem.motivo());
        }

        @Test
        void dadaRequisicaoValida_quandoConcluir_entaoEnviarMensagemKafkaCorreta() throws InterruptedException {
            // Act
            var response = controller.concluirConsulta(consultaDao2.getId());

            // Assert → captura a mensagem do tópico
            ConsumerRecord<String, MensagemKafka> record = KafkaTestUtils
                    .getSingleRecord(consumer, "evento-informar-paciente-consulta");

            assertTrue(response);
            assertNotNull(record);
            MensagemKafka mensagem = record.value();
            assertEquals(medicoDao2.getNome(), mensagem.nomeMedico());
            assertEquals(pacienteDao1.getNome(), mensagem.nomePaciente());
            assertEquals(MotivoKafkaEnum.ALTERACAO.getValue(), mensagem.motivo());
        }

        @Test
        void dadaRequisicaoValida_quandoCancelar_entaoEnviarMensagemKafkaCorreta() throws InterruptedException {
            // Act
            var response = controller.cancelarConsulta(consultaDao2.getId());

            // Assert → captura a mensagem do tópico
            ConsumerRecord<String, MensagemKafka> record = KafkaTestUtils
                    .getSingleRecord(consumer, "evento-informar-paciente-consulta");

            assertTrue(response);
            assertNotNull(record);
            MensagemKafka mensagem = record.value();
            assertEquals(medicoDao2.getNome(), mensagem.nomeMedico());
            assertEquals(pacienteDao1.getNome(), mensagem.nomePaciente());
            assertEquals(MotivoKafkaEnum.ALTERACAO.getValue(), mensagem.motivo());
        }
    }
}