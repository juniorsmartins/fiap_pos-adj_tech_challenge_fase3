package tech.challenge.notificacoes.application.configs.kafka;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import tech.challenge.notificacoes.application.dtos.MensagemKafka;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
@RequiredArgsConstructor
public class KafkaConsumerConfig {

    private final KafkaPropertiesConfig kafkaPropertiesConfig; // Injetar a configuração de propriedades do Kafka

    @Bean
    public Map<String, Object> consumerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaPropertiesConfig.bootstrapServers); // Conexão com o servidor Kafka
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class); // Usar StringDeserializer para desserializar chaves
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class); // Usar JsonDeserializer para desserializar mensagens JSON
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "grupo-notificacoes"); // ID do grupo de consumidores
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest"); // earliest = lê tudo desde o começo se não houver offset, latest = começa do fim
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*"); // 🔑 Aceitar objetos de qualquer pacote
        props.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false); // 🔑 Ignora headers __TypeId__
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, "tech.challenge.notificacoes.application.dtos.MensagemKafka"); // 🔑 Sempre deserializar para essa classe
        props.put(JsonDeserializer.TYPE_MAPPINGS, "MensagemKafka:tech.challenge.notificacoes.application.dtos.MensagemKafka"); // Opcional: alias para tipos
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 100); // Quantas mensagens por poll
        props.put(ConsumerConfig.FETCH_MIN_BYTES_CONFIG, 1); // Bytes mínimos por poll
        props.put(ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG, 500); // Tempo máximo para completar poll
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false); // Desabilitar commit automático de offsets
        return props;
    }

    @Bean
    public ConsumerFactory<String, MensagemKafka> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerConfigs()); // Criar a fábrica de consumidores
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, MensagemKafka> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, MensagemKafka> factory = new ConcurrentKafkaListenerContainerFactory<>(); // Criar a fábrica de listeners
        factory.setConsumerFactory(consumerFactory()); // Configurar a fábrica de consumidores
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL); // Usar confirmação manual - mais controle - pode ser útil para garantir que a mensagem foi processada antes de confirmar
        return factory;
    }
}
