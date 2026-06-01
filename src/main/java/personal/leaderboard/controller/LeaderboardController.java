package personal.leaderboard.controller;

import com.leaderboard.external.generated.model.Player;
import com.leaderboard.external.generated.model.PlayerRank;
import com.leaderboard.external.generated.model.Score;
import com.leaderboard.external.generated.model.Top10PlayersInner;
import com.leaderboard.external.generated.web.rest.LeaderboardApi;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;
import personal.leaderboard.service.LeaderboardService;

import java.util.List;

@CrossOrigin
@RestController
public class LeaderboardController implements LeaderboardApi {

    private final LeaderboardService leaderboardService;

    public LeaderboardController(final LeaderboardService leaderboardService) {
        this.leaderboardService = leaderboardService;
    }

    @Override
    public ResponseEntity<Void> publishNewScore(final Score score) {
        leaderboardService.publishScore(score);
        return ResponseEntity.accepted().build();
    }

    @Override
    public ResponseEntity<List<PlayerRank>> getTop10(final String gameId) {
        final var players = leaderboardService.getTopPlayers(gameId);

        if (players.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return  ResponseEntity.ok(players);
    }
}