package pvp.character;

import pvp.GameUI;

public class Priest extends Character {
    Skill holyGrace = new Skill("神聖なる恩寵", 50, 60, 30);
    Skill holyLight = new Skill("聖なる光", 30, 80, 20);
    Skill punch = new Skill("拳", 15, 95, 0);

    public Priest(String name, GameUI gameUI) {
        super(name, 225, 180, gameUI, Type.PRIEST);
        super.serverSkills[0] = holyGrace;
        super.serverSkills[1] = holyLight;
        super.serverSkills[2] = punch;
    }

    @Override
    public void ultimate(Character target) {
        gameUI.append(name + "が神聖なる恩寵を使用しました！");
        if (holyGrace.hit()) {
            target.takeDamage(holyGrace.damage);
            gameUI.append("命中！ " + target.name + "の体力: " + target.hp);
        } else {
            gameUI.append("失敗！");
        }
    }

    @Override
    public void mainSkill(Character target) {
        gameUI.append(name + "が聖なる光を使用しました！");
        if (holyLight.hit()) {
            target.takeDamage(holyLight.damage);
            gameUI.append("命中！ " + target.name + "の体力: " + target.hp);
        } else {
            gameUI.append("失敗！");
        }
    }

    @Override
    public void normalSkill(Character target) {
        gameUI.append(name + "が拳を使用しました！");
        target.takeDamage(punch.damage);
        gameUI.append("命中！ " + target.name + "の体力: " + target.hp);
    }
}
