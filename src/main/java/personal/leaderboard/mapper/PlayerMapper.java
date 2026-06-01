package personal.leaderboard.mapper;

import com.leaderboard.external.generated.model.Player;
import com.leaderboard.external.generated.model.PlayerRank;
import org.springframework.data.redis.core.ZSetOperations;

import java.math.BigDecimal;
import java.math.BigInteger;

public class PlayerMapper {

        public static PlayerRank toPlayer(final ZSetOperations.TypedTuple<String> entry, final int rank){
            PlayerRank player = new PlayerRank();
            player.setPlayerId(entry.getValue());
            player.setRank(BigInteger.valueOf(rank));
            player.setScore(BigDecimal.valueOf(entry.getScore()));
            return player;
        }
}
