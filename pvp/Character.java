package pvp;

public class Character {
    String name;
    int hp;
    GameUI gameUI;

    Skill[] skills = new Skill[3];

    Character(String name, int hp, GameUI gameUI) {
        this.name = name;
        this.hp = hp;
        this.gameUI = gameUI;
    }

    void takeDamage(int damage) {
        hp = hp - damage;
    }

    void ultimate(Character target) {
        //궁극기
    }

    void mainSkill(Character target) {
        //주력기
    }

    void normalSkill(Character target) {
        //확정기
    }

    boolean alive() {
        return hp > 0;
    }
}
