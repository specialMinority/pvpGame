package pvp.domain.character;

import pvp.domain.skill.JobSkill;

public class SwordMaster extends Job {
    JobSkill swordDance = new JobSkill("환영검무", false, -40, 80, 0, 15);
    JobSkill drawSlash = new JobSkill("발도", false, -28, 90, 0, 10);
    JobSkill swordArt = new JobSkill("리귀검술", false, -14, 100, 0, 3);
    JobSkill Swordfall = new JobSkill("유성락", false, -45, 60, 2, 20);
    //자상 데미지로 지속딜(턴이 지나도 딜이 조금씩 들어감, 스킬 사용 후 2턴까지 내 차례에 합산, 10씩 데미지)

    public SwordMaster() {
        super("소드마스터", new JobSkill[4], 180, 100, 0);
        super.serverSkills[0] = swordDance;
        super.serverSkills[1] = drawSlash;
        super.serverSkills[2] = swordArt;
        super.serverSkills[3] = Swordfall;
    }
}
