package pvp.character;

import pvp.GameUI;

public class Mage extends Character {
    Skill meteor = new Skill("メテオ", 90, 45, 40);
    Skill fireBall = new Skill("ファイアボール", 60, 65, 30);
    Skill iceBall = new Skill("アイスボール", 20, 95, 10);

    public Mage(String name, GameUI gameUI) {
        super(name, 150, 200, gameUI, Type.MAGE);
        super.serverSkills[0] = meteor;
        super.serverSkills[1] = fireBall;
        super.serverSkills[2] = iceBall;
    }

    @Override
    public void ultimate(Character target) {
        gameUI.append(name + "がメテオを使用しました！");
        if (meteor.hit()) {
            target.takeDamage(meteor.damage);
            gameUI.append("命中！ " + target.name + "の体力: " + target.hp);
        } else {
            gameUI.append("失敗！");
        }
    }

    @Override
    public void mainSkill(Character target) {
        gameUI.append(name + "がファイアボールを使用しました！");
        if (fireBall.hit()) {
            target.takeDamage(fireBall.damage);
            gameUI.append("命中！ " + target.name + "の体力: " + target.hp);
        } else {
            gameUI.append("失敗！");
        }
    }

    @Override
    public void normalSkill(Character target) {
        gameUI.append(name + "がアイスボールを使用しました！");
        target.takeDamage(iceBall.damage);
        gameUI.append("命中！ " + target.name + "の体力: " + target.hp);
    }
}
