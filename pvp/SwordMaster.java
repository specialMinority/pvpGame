package pvp;

class SwordMaster extends Character {
    Skill swordDance = new Skill("환영검무", 40, 80);
    Skill drawSlash = new Skill("발도", 28, 90);
    Skill swordArt = new Skill("리귀검술", 14, 100);

    SwordMaster(String name, GameUI gameUI) {
        super(name, 180, gameUI);
        super.skills[0] = swordDance;
        super.skills[1] = drawSlash;
        super.skills[2] = swordArt;
    }

    @Override
    void ultimate(Character target) {
        gameUI.append(name + "가 환영검무를 사용합니다!");
        if (swordDance.hit()) {
            target.takeDamage(swordDance.damage);
            gameUI.append("명중! " + target.name + "의 체력: " + target.hp);
        } else {
            gameUI.append("실패!");
        }
    }

    @Override
    void mainSkill(Character target) {
        gameUI.append(name + "가 발도를 사용합니다!");
        if (drawSlash.hit()) {
            target.takeDamage(drawSlash.damage);
            gameUI.append("명중! " + target.name + "의 체력: " + target.hp);
        } else {
            gameUI.append("실패!");
        }
    }

    @Override
    void normalSkill(Character target) {
        gameUI.append(name + "가 리귀검술을 사용합니다!");
        target.takeDamage(swordArt.damage);
        gameUI.append("명중! " + target.name + "의 체력: " + target.hp);
    }
}
