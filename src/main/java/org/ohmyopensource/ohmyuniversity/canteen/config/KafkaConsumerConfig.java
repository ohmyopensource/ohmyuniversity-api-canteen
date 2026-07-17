package org.ohmyopensource.ohmyuniversity.canteen.config;

import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.ohmyopensource.ohmyuniversity.canteen.kafka.event.CampusAssignmentDiscoveredEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer;

/**
 * Kafka consumer configuration for the canteen microservice.
 *
 * <p>Each topic has its own typed ConsumerFactory and
 * ConcurrentKafkaListenerContainerFactory so that JacksonJsonDeserializer
 * knows exactly which class to deserialize into without needing type headers.
 *
 * <p>ErrorHandlingDeserializer wraps each deserializer so that malformed
 * messages are logged and skipped instead of crashing the consumer thread.
 */
@Configuration
public class KafkaConsumerConfig {

  @Value("${spring.kafka.bootstrap-servers}")
  private String bootstrapServers;

  private Map<String, Object> baseProps() {
    Map<String, Object> props = new HashMap<>();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    props.put(ConsumerConfig.GROUP_ID_CONFIG, "ohmyuniversity-canteen");
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    return props;
  }

  private <T> ConsumerFactory<String, T> consumerFactory(Class<T> targetType) {
    JacksonJsonDeserializer<T> jsonDeserializer = new JacksonJsonDeserializer<>(targetType);
    jsonDeserializer.addTrustedPackages(
        "org.ohmyopensource.ohmyuniversity.canteen.kafka.event"
    );

    ErrorHandlingDeserializer<T> errorHandlingDeserializer =
        new ErrorHandlingDeserializer<>(jsonDeserializer);

    return new DefaultKafkaConsumerFactory<>(
        baseProps(),
        new StringDeserializer(),
        errorHandlingDeserializer
    );
  }

  private <T> ConcurrentKafkaListenerContainerFactory<String, T> containerFactory(
      Class<T> targetType) {
    ConcurrentKafkaListenerContainerFactory<String, T> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(consumerFactory(targetType));
    return factory;
  }

  // ================================
  // campus-assignment.discovered
  // ================================

  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, CampusAssignmentDiscoveredEvent>
  campusAssignmentDiscoveredContainerFactory() {
    return containerFactory(CampusAssignmentDiscoveredEvent.class);
  }
}