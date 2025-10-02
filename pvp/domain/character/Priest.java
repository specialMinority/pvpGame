package pvp.domain.character;

import pvp.domain.skill.JobSkill;

public class Priest extends Job {
    JobSkill judgmentMaul = new JobSkill("참회의 망치", false, -30, 90, 0, 15);
    JobSkill deflectingWall = new JobSkill("디플렉트 월", false, -24, 95, 0, 10);
    JobSkill bladePure = new JobSkill("순백의 칼날", false, -12, 100, 0, 3);
    JobSkill perpectHeal = new JobSkill("영광의 축복", true, 10, 100, 0, 20);
    //체력 회복 스킬

    public Priest() {
        super("프리스트", new JobSkill[4], 225, 100, 0);
        super.serverSkills[0] = judgmentMaul;
        super.serverSkills[1] = deflectingWall;
        super.serverSkills[2] = bladePure;
        super.serverSkills[3] = perpectHeal;
    }
}
