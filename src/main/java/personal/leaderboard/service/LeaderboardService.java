package personal.leaderboard.service;

import com.leaderboard.external.generated.model.Player;
import com.leaderboard.external.generated.model.PlayerRank;
import com.leaderboard.external.generated.model.Score;
import leaderboard.PlayerScoreEvent;
import org.springframework.stereotype.Service;
import personal.leaderboard.mapper.PlayerMapper;
import personal.leaderboard.repository.LeaderboardRedisRepository;

import java.time.Instant;
import java.util.List;

import static personal.leaderboard.mapper.PlayerScoreEventMapper.toPlayerScoreEvent;

@Service
public class LeaderboardService {

    private final ScoreProducer scoreProducer;
    private final LeaderboardRedisRepository leaderboardRedisRepository;

    public LeaderboardService(final ScoreProducer scoreProducer, final LeaderboardRedisRepository leaderboardRedisRepository) {
        this.scoreProducer = scoreProducer;
        this.leaderboardRedisRepository = leaderboardRedisRepository;
    }

    public void publishScore(final Score score) {
        final var platerScoreEvent = toPlayerScoreEvent(score);
        scoreProducer.publish(platerScoreEvent);
    }

    public List<PlayerRank> getTopPlayers(final String gameId) {
       return leaderboardRedisRepository.getTop10Players(gameId);
    }
}
