package shared.model;
public class PlayerData {
    private String id;
    // private String nickname;
    private float x;
    private float y;
    // private float rotation;
    // private int health;
    // private String team;

    Player(String id, String nickname, float x, float y, float rotation, int health, String team) {
            this.id = id;
            this.nickname = nickname;
            this.x = x;
            this.y = y;
            // this.rotation = rotation;
            // this.health = health;
            // this.team = team;
        }

    // Gettery i settery
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    // public String getNickname() { return nickname; }
    // public void setNickname(String nickname) { this.nickname = nickname; }
    
    public float getX() { return x; }
    public void setX(float x) { this.x = x; }
    
    public float getY() { return y; }
    public void setY(float y) { this.y = y; }
    
    // public float getRotation() { return rotation; }
    // public void setRotation(float rotation) { this.rotation = rotation; }
    
    // public int getHealth() { return health; }
    // public void setHealth(int health) { this.health = health; }
    
    // public String getTeam() { return team; }
    // public void setTeam(String team) { this.team = team; }
}