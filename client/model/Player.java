package client.model;

public class Player {
    public String id;
    public String nickname;
    public float x;
    public float y;
    public float rotation;
    public int health;
    public String team;

    public Player(String id, String nickname, float x, float y, float rotation, int health, String team) {
            this.id = id;
            this.nickname = nickname;
            this.x = x;
            this.y = y;
            this.rotation = rotation;
            this.health = health;
            this.team = team;
        }
}