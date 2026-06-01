package personal.leaderboard.repository;

import com.leaderboard.external.generated.model.Player;
import com.leaderboard.external.generated.model.PlayerRank;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import personal.leaderboard.mapper.PlayerMapper;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Repository
public class LeaderboardRedisRepository {

    private static final String KEY_PREFIX = "leaderboard:";

    private final RedisTemplate<String, String> redisTemplate;

    public LeaderboardRedisRepository(final RedisTemplate<String,String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void updateScore(String gameId, String playerId, double score) {
        final var key = KEY_PREFIX + gameId;
        final var currentScore = redisTemplate.opsForZSet().score(key, playerId);

        if (currentScore == null || score > currentScore) {
            redisTemplate.opsForZSet().add(key, playerId, score);
        }
    }


    public List<PlayerRank> getTop10Players(final String gameId) {
        final var key = KEY_PREFIX + gameId;
        final var results = redisTemplate.opsForZSet().reverseRangeWithScores(key,0,9);

        if (results == null || results.isEmpty()) {
            return List.of();
        }

        List<PlayerRank> leaderboard = new ArrayList<>();
        int rank = 1 ;
        for (var entry : results){
            leaderboard.add(PlayerMapper.toPlayer(entry,rank));
            rank++;
        }
        return leaderboard;
    }
}
