package personal.leaderboard.service;


import leaderboard.PlayerScoreEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;
import personal.leaderboard.common.LeaderboardUpdatedEvent;
import personal.leaderboard.repository.LeaderboardRedisRepository;


@Service
public class ScoreConsumer {

    private final LeaderboardRedisRepository leaderboardRedisRepository;
    private final Logger logger = LoggerFactory.getLogger(ScoreConsumer.class);
    private final ApplicationEventPublisher eventPublisher;

    public ScoreConsumer(final LeaderboardRedisRepository leaderboardRedisRepository,final ApplicationEventPublisher eventPublisher) {
        this.leaderboardRedisRepository = leaderboardRedisRepository;
        this.eventPublisher = eventPublisher;
    }

//    @KafkaListener(
//            topics = "leaderboard.raw-scores",
//            groupId = "leaderboard-service"
//    )
//    public void consume(
//            final PlayerScoreEvent event,
//            final Acknowledgment acknowledgment
//    ) {
//        long start = System.currentTimeMillis();
//        try {
//            leaderboardRedisRepository.updateScore(
//                    event.getGameId(),
//                    event.getPlayerId(),
//                    event.getScore());
//            acknowledgment.acknowledge();
//        } catch (Exception e) {
//            logger.error(
//                    "Failed to process score event. gameId={} playerId={} score={} error={}",
//                    event.getGameId(), event.getPlayerId(), event.getScore(), e.getMessage(), e
//            );
//            throw e;
//        }
//    }

    @KafkaListener(
            topics = "leaderboard.computed-scores",
            groupId = "leaderboard-computed-service"
    )
    public void consume(final PlayerScoreEvent event,final Acknowledgment ack){
        try {
            leaderboardRedisRepository.add(
                    event.getGameId(),
                    event.getPlayerId(),
                    event.getScore()
            );
            ack.acknowledge();
            eventPublisher.publishEvent(new LeaderboardUpdatedEvent(event.getGameId()));
        }
            catch (Exception e) {
                logger.error("Failed to process computed score event. gameId={} playerId={} score={}",
                        event.getGameId(), event.getPlayerId(), event.getScore(), e);
                throw e;
            }
    }

}
