package pvp.character;

public class Skill {
    public String name;
    public int damage;
    public int accuracy;

    Skill(String name, int damage, int accuracy) {
        this.name = name;
        this.damage = damage;
        this.accuracy = accuracy;
    }

    boolean hit() {
        return new java.util.Random().nextInt(100) < accuracy;
    }
}
