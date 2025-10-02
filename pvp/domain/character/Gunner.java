package pvp.domain.character;

import pvp.domain.skill.JobSkill;

public class Gunner extends Job {
    JobSkill quantumBomb = new JobSkill("양자폭탄", false, -60, 55, 0, 15);
    JobSkill raserBazooka = new JobSkill("레이저바주카", false, -35, 75, 0, 10);
    JobSkill gatlingGun = new JobSkill("게틀링건", false, -15, 95, 0, 3);
    JobSkill satelliteBeam = new JobSkill("새틀라이트 빔", false, -140, 10, 0, 20);

    public Gunner() {
        super("거너", new JobSkill[4], 170, 100, 0);
        super.serverSkills[0] = quantumBomb;
        super.serverSkills[1] = raserBazooka;
        super.serverSkills[2] = gatlingGun;
        super.serverSkills[3] = satelliteBeam;
    }
}
