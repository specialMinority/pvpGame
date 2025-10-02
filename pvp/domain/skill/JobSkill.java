package pvp.domain.skill;

public record JobSkill(
        String name,
        boolean isSelfHeal,
        int effectValue,
        int accuracy,
        int bleeding,
        int mpCost) {

    public boolean hit() {
        return new java.util.Random().nextInt(100) + 1 < accuracy;
    }
}
