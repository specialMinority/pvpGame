package pvp.character;

import pvp.GameUI;

/**
 * Abstract base class representing a game character.
 * Uses an Enum {@link Type} for safe type identification instead of relying on HP values.
 */
public class Character {
    /**
     * Enumeration of all available character class types.
     */
    public enum Type { MAGE, GUNNER, PRIEST, SWORDMASTER }
    
    String name;
    public int hp;
    public int maxHp; // 체력바 업데이트용
    public int mp;
    public int maxMp; // 마나바 업데이트용
    GameUI gameUI;
    public Type type;

    public Skill[] serverSkills = new Skill[3];

    public Character(String name, int hp, int mp, GameUI gameUI, Type type) {
        this.name = name;
        this.hp = hp;
        this.maxHp = hp;
        this.mp = mp;
        this.maxMp = mp;
        this.gameUI = gameUI;
        this.type = type;
    }

    void takeDamage(int damage) {
        hp = hp - damage;
    }

    public void ultimate(Character target) {
        //궁극기
    }

    public void mainSkill(Character target) {
        //주력기
    }

    public void normalSkill(Character target) {
        //확정기
    }

    public boolean alive() {
        return hp > 0;
    }
}
