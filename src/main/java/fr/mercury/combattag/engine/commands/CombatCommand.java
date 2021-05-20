package fr.mercury.combattag.engine.commands;

import fr.mercury.combattag.MercuryTag;
import fr.mercury.combattag.engine.CombatManager;
import fr.mercury.combattag.utils.ChatUtils;
import fr.mercury.combattag.utils.commands.Command;
import fr.mercury.combattag.utils.commands.CommandArgs;
import fr.mercury.combattag.utils.commands.ICommand;
import org.bukkit.entity.Player;

public class CombatCommand extends ICommand {

    private CombatManager combatManager = CombatManager.getInstance();

    @Override
    @Command(name = {"ct", "combat", "ctags", "mercurytag", "mercurytags"})
    public void onCommand(CommandArgs args) {

        if(args.length() == 0) {

            Player player = (Player) args.getSender();

            if(this.combatManager.isInCombat(player))
                args.getSender().sendMessage(MercuryTag.getInstance().getConfig().getString("COMBAT.COMMAND_COMBAT").replace("%time%", String.valueOf(this.combatManager.getSecondsLeft((Player) args.getSender()))));
            else
                this.combatManager.remove(player);

        } else
            ChatUtils.sendBadFormatted(args.getSender());
    }
}
