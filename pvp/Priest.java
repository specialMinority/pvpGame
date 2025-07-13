package pvp;

class Priest extends Character {
    Skill judgmentMaul = new Skill("참회의 망치", 30, 90);
    Skill deflectingWall = new Skill("디플픽트 월", 24, 95);
    Skill bladePure = new Skill("순백의 칼날", 12, 100);

    Priest(String name, GameUI gameUI) {
        super(name, 225, gameUI);
        super.skills[0] = judgmentMaul;
        super.skills[1] = deflectingWall;
        super.skills[2] = bladePure;
    }

    @Override
    void ultimate(Character target) {
        gameUI.append(name + "가 참회의 망치를 시전합니다!");
        if (judgmentMaul.hit()) {
            target.takeDamage(judgmentMaul.damage);
            gameUI.append("명중! " + target.name + "의 체력: " + target.hp);
        } else {
            gameUI.append("실패!");
        }
    }

    @Override
    void mainSkill(Character target) {
        gameUI.append(name + "가 디플렉트 월을 시전합니다!");
        if (deflectingWall.hit()) {
            target.takeDamage(deflectingWall.damage);
            gameUI.append("명중! " + target.name + "의 체력: " + target.hp);
        } else {
            gameUI.append("실패!");
        }
    }

    @Override
    void normalSkill(Character target) {
        gameUI.append(name + "가 순백의 칼날을 시전합니다!");
        target.takeDamage(bladePure.damage);
        gameUI.append("명중! " + target.name + "의 체력: " + target.hp);
    }
}
