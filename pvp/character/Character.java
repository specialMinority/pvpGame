package pvp.character;

import pvp.GameUI;

public class Character {
    String name;
    public int hp;
    GameUI gameUI;

    public Skill[] serverSkills = new Skill[3];

    Character(String name, int hp, GameUI gameUI) {
        this.name = name;
        this.hp = hp;
        this.gameUI = gameUI;
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
