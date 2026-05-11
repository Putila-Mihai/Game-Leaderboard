package personal.leaderboard.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    @Bean
    public NewTopic rawScoresTopic() {
        return TopicBuilder.name("leaderboard.raw-scores")
                .partitions(4)
                .replicas(1)
                .build();
    }
}
