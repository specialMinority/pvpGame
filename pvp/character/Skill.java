package pvp.character;

public class Skill {
    public String name;
    public int damage;
    public int accuracy;
    public int mpCost;

    Skill(String name, int damage, int accuracy, int mpCost) {
        this.name = name;
        this.damage = damage;
        this.accuracy = accuracy;
        this.mpCost = mpCost;
    }

    boolean hit() {
        return new java.util.Random().nextInt(100) + 1 < accuracy;
    }
}
