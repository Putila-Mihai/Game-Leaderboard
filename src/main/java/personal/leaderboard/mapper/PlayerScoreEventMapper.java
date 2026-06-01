package personal.leaderboard.mapper;

import com.leaderboard.external.generated.model.Score;
import leaderboard.PlayerScoreEvent;

import java.time.Instant;

public class PlayerScoreEventMapper {

    public static PlayerScoreEvent toPlayerScoreEvent(final Score score) {
        return  PlayerScoreEvent.newBuilder()
                .setPlayerId(score.getPlayerId())
                .setGameId(score.getGameId())
                .setScore(score.getScore())
                .setCreatedDate(Instant.now().toEpochMilli())
                .build();
    }
}
