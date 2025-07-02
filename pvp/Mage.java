package pvp;

class Mage extends Character {
    Skill meteor = new Skill("메테오", 200, 5);
    Skill fireball = new Skill("파이어볼", 80, 20);
    Skill rapidFire = new Skill("연속발사", 20, 100);

    Mage(String name, GameUI gameUI) {
        super(name, 100, gameUI);
        {
            super.skills[0] = meteor;
            super.skills[1] = fireball;
            super.skills[2] = rapidFire;
        }
    }

    @Override
    void ultimate(Character target) {
        gameUI.append(name + "가 메테오를 시전합니다!");
        if (meteor.hit()) {
            target.takeDamage(meteor.damage);
            gameUI.append("명중! " + target.name + "의 체력: " + target.hp);
        } else {
            gameUI.append("실패!");
        }
    }

    @Override
    void mainSkill(Character target) {
        gameUI.append(name + "가 파이어볼을 시전합니다!");
        if (fireball.hit()) {
            target.takeDamage(fireball.damage);
            gameUI.append("명중! " + target.name + "의 체력: " + target.hp);
        } else {
            gameUI.append("실패!");
        }
    }

    @Override
    void normalSkill(Character target) {
        gameUI.append(name + "가 연속발사를 시전합니다!");
        target.takeDamage(rapidFire.damage);
        gameUI.append("명중! " + target.name + "의 체력: " + target.hp);
    }
}
