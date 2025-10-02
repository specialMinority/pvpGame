package pvp.domain.character;

import pvp.domain.skill.JobSkill;

public class Mage extends Job {
    JobSkill meteor = new JobSkill("메테오", false, -90, 40, 0, 15);
    JobSkill fireball = new JobSkill("파이어볼", false, -45, 70, 0, 10 );
    JobSkill rapidFire = new JobSkill("연속발사", false, -20, 95, 0, 3);
    JobSkill explosion = new JobSkill("멸화폭풍", false, -140, 10, 0, 20);

    public Mage() {
        super("마법사", new JobSkill[4], 150, 100, 0);
        {
            super.serverSkills[0] = meteor;
            super.serverSkills[1] = fireball;
            super.serverSkills[2] = rapidFire;
            super.serverSkills[3] = explosion;
        }
    }
}
