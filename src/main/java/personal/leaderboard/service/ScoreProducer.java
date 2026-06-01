package personal.leaderboard.service;

import leaderboard.PlayerScoreEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class ScoreProducer {

    private final KafkaTemplate<String, PlayerScoreEvent> kafkaTemplate;

    @Value("${spring.kafka.topics.raw-scores}")
    private String topic;

    public ScoreProducer(final KafkaTemplate<String, PlayerScoreEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(final PlayerScoreEvent event) {
        kafkaTemplate.send(topic, event.getGameId(), event);
    }
}
