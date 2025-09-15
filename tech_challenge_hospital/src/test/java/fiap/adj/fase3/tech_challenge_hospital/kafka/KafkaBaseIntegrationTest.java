package fiap.adj.fase3.tech_challenge_hospital.kafka;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest
@EmbeddedKafka(partitions = 1,
        topics = {"evento-informar-paciente-consulta", "evento-atualizar-status-consulta"},
        brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"}) // Configuração do Kafka embutido para testes
@DirtiesContext // Para garantir que o contexto seja reiniciado entre os testes, evitando interferências
public abstract class KafkaBaseIntegrationTest {
}
