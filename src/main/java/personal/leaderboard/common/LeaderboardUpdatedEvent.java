package personal.leaderboard.common;

public class LeaderboardUpdatedEvent {

    private final String gameId;

    public LeaderboardUpdatedEvent(String gameId) {
        this.gameId = gameId;
    }

    public String getGameId() {
        return gameId;
    }
}