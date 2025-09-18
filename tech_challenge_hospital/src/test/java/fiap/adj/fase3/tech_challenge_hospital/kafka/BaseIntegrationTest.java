package fiap.adj.fase3.tech_challenge_hospital.kafka;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;

@SpringBootTest
@EmbeddedKafka(partitions = 1,
        topics = {"evento-informar-paciente-consulta", "evento-atualizar-status-consulta"},
        brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"}) // Configuração do Kafka embutido para testes
@DirtiesContext // Para garantir que o contexto seja reiniciado entre os testes, evitando interferências
@Sql(scripts = "/sql/before.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public abstract class BaseIntegrationTest {
}
