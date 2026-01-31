package pvp.character;

import pvp.GameUI;

public class SwordMaster extends Character {
    Skill swordDance = new Skill("剣の舞", 60, 60, 30);
    Skill balDo = new Skill("抜刀", 35, 75, 20);
    Skill slash = new Skill("斬り", 15, 95, 10);

    public SwordMaster(String name, GameUI gameUI) {
        super(name, 180, 100, gameUI, Type.SWORDMASTER);
        super.serverSkills[0] = swordDance;
        super.serverSkills[1] = balDo;
        super.serverSkills[2] = slash;
    }

    @Override
    public void ultimate(Character target) {
        gameUI.append(name + "が剣の舞を使用しました！");
        if (swordDance.hit()) {
            target.takeDamage(swordDance.damage);
            gameUI.append("命中！ " + target.name + "の体力: " + target.hp);
        } else {
            gameUI.append("失敗！");
        }
    }

    @Override
    public void mainSkill(Character target) {
        gameUI.append(name + "が抜刀を使用しました！");
        if (balDo.hit()) {
            target.takeDamage(balDo.damage);
            gameUI.append("命中！ " + target.name + "の体力: " + target.hp);
        } else {
            gameUI.append("失敗！");
        }
    }

    @Override
    public void normalSkill(Character target) {
        gameUI.append(name + "が斬りを使用しました！");
        target.takeDamage(slash.damage);
        gameUI.append("命中！ " + target.name + "の体力: " + target.hp);
    }
}
