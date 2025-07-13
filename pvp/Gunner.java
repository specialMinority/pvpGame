package pvp;

class Gunner extends Character {
    Skill quantumBomb = new Skill("양자폭탄", 60, 55);
    Skill raserBazooka = new Skill("레이저바주카", 35, 75);
    Skill gatlingGun = new Skill("게틀링건", 15, 95);

    Gunner(String name, GameUI gameUI) {
        super(name, 170, gameUI);
        super.skills[0] = quantumBomb;
        super.skills[1] = raserBazooka;
        super.skills[2] = gatlingGun;
    }

    @Override
    void ultimate(Character target) {
        gameUI.append(name + "가 양자폭탄을 발사합니다!");
        if (quantumBomb.hit()) {
            target.takeDamage(quantumBomb.damage);
            gameUI.append("명중! " + target.name + "의 체력: " + target.hp);
        } else {
            gameUI.append("실패!");
        }
    }

    @Override
    void mainSkill(Character target) {
        gameUI.append(name + "가 레이저바주카를 발사합니다!");
        if (raserBazooka.hit()) {
            target.takeDamage(raserBazooka.damage);
            gameUI.append("명중! " + target.name + "의 체력: " + target.hp);
        } else {
            gameUI.append("실패!");
        }
    }

    @Override
    void normalSkill(Character target) {
        gameUI.append(name + "가 게틀링건을 발사합니다!");
        target.takeDamage(gatlingGun.damage);
        gameUI.append("명중! " + target.name + "의 체력: " + target.hp);
    }
}
