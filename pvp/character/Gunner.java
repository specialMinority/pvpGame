package pvp.character;

import pvp.GameUI;

public class Gunner extends Character {
    Skill quantumBomb = new Skill("量子爆弾", 60, 55, 30);
    Skill laserBazooka = new Skill("レーザーバズーカ", 35, 75, 20);
    Skill gatlingGun = new Skill("ガトリングガン", 15, 95, 10);

    public Gunner(String name, GameUI gameUI) {
        super(name, 170, 150, gameUI, Type.GUNNER);
        super.serverSkills[0] = quantumBomb;
        super.serverSkills[1] = laserBazooka;
        super.serverSkills[2] = gatlingGun;
    }

    @Override
    public void ultimate(Character target) {
        gameUI.append(name + "が量子爆弾を使用しました");
        if (quantumBomb.hit()) {
            target.takeDamage(quantumBomb.damage);
            gameUI.append("命中！ " + target.name + "の体力: " + target.hp);
        } else {
            gameUI.append("失敗！");
        }
    }

    @Override
    public void mainSkill(Character target) {
        gameUI.append(name + "がレーザーバズーカを使用しました！");
        if (laserBazooka.hit()) {
            target.takeDamage(laserBazooka.damage);
            gameUI.append("命中！ " + target.name + "の体力: " + target.hp);
        } else {
            gameUI.append("失敗！");
        }
    }

    @Override
    public void normalSkill(Character target) {
        gameUI.append(name + "がガトリングガンを使用しました！");
        target.takeDamage(gatlingGun.damage);
        gameUI.append("命中！ " + target.name + "の体力: " + target.hp);
    }
}
