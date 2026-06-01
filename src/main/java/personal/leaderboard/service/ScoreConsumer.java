package personal.leaderboard.service;


import leaderboard.PlayerScoreEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;
import personal.leaderboard.repository.LeaderboardRedisRepository;

@Service
@Slf4j
public class ScoreConsumer {

    private final LeaderboardRedisRepository leaderboardRedisRepository;

    public ScoreConsumer(final LeaderboardRedisRepository leaderboardRedisRepository) {
        this.leaderboardRedisRepository = leaderboardRedisRepository;
    }

    @KafkaListener(
            topics = "leaderboard.raw-scores",
            groupId = "leaderboard-service"
    )
    public void consume(
            final PlayerScoreEvent event,
            final Acknowledgment acknowledgment
    ) {
        try {
            leaderboardRedisRepository.updateScore(
                    event.getGameId(),
                    event.getPlayerId(),
                    event.getScore());
            acknowledgment.acknowledge();
        } catch (Exception e) {
            log.error(
                    "Failed to process score event. gameId={} playerId={} score={} error={}",
                    event.getGameId(), event.getPlayerId(), event.getScore(), e.getMessage(), e
            );
            throw e;
        }
    }
}
