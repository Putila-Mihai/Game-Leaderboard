package personal.leaderboard.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.config.TopicConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    @Bean
    public NewTopic rawScoresTopic() {
        return TopicBuilder.name("leaderboard.raw-scores")
                .partitions(8)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic computedScoresTopic() {
        return TopicBuilder.name("leaderboard.computed-scores")
                .partitions(4)
                .replicas(1)
                .config(TopicConfig.MIN_IN_SYNC_REPLICAS_CONFIG, "1")
                .build();
    }
}
