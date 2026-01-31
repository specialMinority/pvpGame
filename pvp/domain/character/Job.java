package pvp.domain.character;

import pvp.domain.skill.JobSkill;
import java.util.Random;

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

    public Job withMp(int newMp) {
        return new Job(name, serverSkills, hp, newMp, bleeding);
    }

    public Job withBleeding(int newBleeding) {
        return new Job(name, serverSkills, hp, mp, newBleeding);
    }

    public pvp.domain.battle.BattleResult applySkill(JobSkill skill) {
        if (this.mp < skill.mpCost()) {
            return new pvp.domain.battle.BattleResult(this, false, false, 0);
        }

        Job afterMp = this.withMp(this.mp - skill.mpCost());

        if (!skill.hit()) {
            return new pvp.domain.battle.BattleResult(afterMp, false, false, 0);
        }

        if (criticalDmg()) {
            int damage = (int) (skill.effectValue() * 1.3);
            int newHp = this.hp + damage;
            Job newJob = afterMp.withHp(newHp);
            Job afterBleeding = newJob.withBleeding(newJob.bleeding + skill.bleeding());
            return new pvp.domain.battle.BattleResult(afterBleeding, true, true, damage);
        } else {
            int damage = skill.effectValue();
            int newHp = this.hp + damage;
            Job newJob = afterMp.withHp(newHp);
            Job afterBleeding = newJob.withBleeding(newJob.bleeding + skill.bleeding());
            return new pvp.domain.battle.BattleResult(afterBleeding, true, false, damage);
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
