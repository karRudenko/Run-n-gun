import java.util.List;

public class GameStateResponse {
    private List<PlayerData> players;
    private long timestamp;
    private String currentMao;

    public List<PlayerData> getPlayers() { return players; }
    public void setPlayers(List<PlayerData> players) { this.players = players; }
}
