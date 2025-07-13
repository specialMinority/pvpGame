package pvp;

public class Skill {
    String name;
    int damage;
    int accuracy;

    Skill(String name, int damage, int accuracy) {
        this.name = name;
        this.damage = damage;
        this.accuracy = accuracy;
    }

    boolean hit() {
        return new java.util.Random().nextInt(100) < accuracy;
    }
}
