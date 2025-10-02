package pvp.domain.character;

import pvp.domain.skill.JobSkill;

import java.util.Random;

import static pvp.network.Server.isCritical;
import static pvp.network.Server.isHit;

public class Job {
    private final String name;
    protected final JobSkill[] serverSkills;
    private final int hp;
    private final int mp;
    private final int bleeding;

    protected Job(String name, JobSkill[] serverSkills, int hp, int mp, int bleeding) {
        this.name = name;
        this.serverSkills = serverSkills;
        this.hp = hp;
        this.mp = mp;
        this.bleeding = bleeding;
    }

    public Job withHp(int hp) {
        return new Job(name, serverSkills, hp, mp, bleeding);
    }

    public Job applySkill(JobSkill skill) {
        if (this.mp < skill.mpCost()) {
            isHit = false;
            isCritical = false;
            return null;
        }

        this.mp -= skill.mpCost();

        if (!skill.hit()) {
            isHit = false;
            return null;
        }
        if (criticalDmg()) {
            int newHp = this.hp + (int) (skill.effectValue() * 1.3);
            Job newJob = this.withHp(newHp);
            bleeding += skill.bleeding();
            isHit = true;
            isCritical = true;
            return newJob;
        } else {
            int newHp = this.hp + skill.effectValue();
            Job newJob = this.withHp(newHp);
            bleeding += skill.bleeding();
            isHit = true;
            isCritical = false;
            return newJob;
        }
    }

    public JobSkill getServerSkill(int index) {
        return serverSkills[index];
    }

    public int getHp() {
        return hp;
    }

    public int getMp() {
        return mp;
    }

    public void setMp(int mp) {
        this.mp = mp;
    }

    public int getBleeding() {
        return bleeding;
    }

    private boolean criticalDmg() {
        return new Random().nextInt(100) + 1 <= 5;
    }

    public String getName() {
        return name;
    }
}
