package io.SesProject.controller.command.combatCommand;

import io.SesProject.controller.CombatController;
import io.SesProject.controller.command.Command;
import io.SesProject.model.game.Skill;

public class UseSkillCommand implements Command {
    private CombatController receiver;
    private Skill skill;

    public UseSkillCommand(CombatController receiver, Skill skill) {
        this.receiver = receiver;
        this.skill = skill;
    }

    @Override
    public void execute() {
        // Delega al controller l'uso di QUESTA specifica skill
        receiver.executeSkillUsage(skill);
    }
}
