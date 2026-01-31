package pvp.domain.battle;

import pvp.domain.character.Job;
import pvp.domain.skill.JobSkill;

public record BattleResult(
    Job nextState,
    boolean isHit,
    boolean isCritical,
    int damage
) {}
