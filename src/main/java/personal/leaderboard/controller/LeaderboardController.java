package personal.leaderboard.controller;

import com.leaderboard.external.generated.model.Player;
import com.leaderboard.external.generated.model.PlayerRank;
import com.leaderboard.external.generated.model.Score;
import com.leaderboard.external.generated.model.Top10PlayersInner;
import com.leaderboard.external.generated.web.rest.LeaderboardApi;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import personal.leaderboard.common.LeaderboardUpdatedEvent;
import personal.leaderboard.service.LeaderboardService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@CrossOrigin
@RestController
public class LeaderboardController implements LeaderboardApi {

    private final Map<String, List<SseEmitter>> emitters = new ConcurrentHashMap<>();
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

    @GetMapping("/leaderboard/{gameId}/stream")
    public SseEmitter stream(@PathVariable String gameId){
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        emitters.computeIfAbsent(gameId, k -> new CopyOnWriteArrayList<>()).add(emitter);

        emitter.onCompletion(() -> emitters.getOrDefault(gameId, List.of()).remove(emitter));
        emitter.onTimeout(() -> emitters.getOrDefault(gameId, List.of()).remove(emitter));
        emitter.onError(e -> emitters.getOrDefault(gameId, List.of()).remove(emitter));

        try {
            List<PlayerRank> current = leaderboardService.getTopPlayers(gameId);
            if (!current.isEmpty()) {
                emitter.send(SseEmitter.event().data(current));
            }
        } catch (IOException e) {
            emitter.completeWithError(e);
        }
        return emitter;
    }

    @EventListener
    public void onLeaderboardUpdated(final LeaderboardUpdatedEvent event) {
        List<SseEmitter> gameEmitters = emitters.getOrDefault(event.getGameId(), List.of());
        if (gameEmitters.isEmpty()) return;

        List<PlayerRank> top10 = leaderboardService.getTopPlayers(event.getGameId());
        List<SseEmitter> deadEmitters = new ArrayList<>();

        for (SseEmitter emitter : gameEmitters) {
            try {
                emitter.send(SseEmitter.event().data(top10));
            } catch (IOException e) {
                deadEmitters.add(emitter);
            }
        }

        gameEmitters.removeAll(deadEmitters);
    }
}