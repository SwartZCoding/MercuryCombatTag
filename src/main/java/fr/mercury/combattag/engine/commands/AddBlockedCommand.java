package fr.mercury.combattag.engine.commands;

import fr.mercury.combattag.MercuryTag;
import fr.mercury.combattag.engine.CombatManager;
import fr.mercury.combattag.utils.ChatUtils;
import fr.mercury.combattag.utils.commands.Command;
import fr.mercury.combattag.utils.commands.CommandArgs;
import fr.mercury.combattag.utils.commands.ICommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class AddBlockedCommand extends ICommand {

    private CombatManager combatManager = CombatManager.getInstance();
    private FileConfiguration config = MercuryTag.getInstance().getConfig();

    @Override
    @Command(name = {"lockcommand"}, permissionNode = "ct.admin")
    public void onCommand(CommandArgs args) {

        Player player = args.getPlayer();

        if(args.length() == 1 && args.getArgs(0).equalsIgnoreCase("list")) {
            this.combatManager.sendList(args.getSender());
            return;
        }

        if(args.length() == 2) {

            if(args.getArgs(0).equalsIgnoreCase("add")) {
                if(combatManager.getCommands().contains(args.getArgs(1))) {
                    player.sendMessage(this.config.getString("BLOCKED_COMMANDS.COMMAND_ALREADY_REGISTER"));
                    return;
                }
                combatManager.addCommand(args.getArgs(1));
                player.sendMessage(this.config.getString("BLOCKED_COMMANDS.COMMAND_LOCKED_SUCCESSFULY").replace("%command%", args.getArgs(1)));

            } else if(args.getArgs(0).equalsIgnoreCase("remove")) {
                if(!combatManager.getCommands().contains(args.getArgs(1))) {
                    player.sendMessage(this.config.getString("BLOCKED_COMMANDS.COMMAND_NOT_ALREADY_REGISTER"));
                    return;
                }
                combatManager.removeCommand(args.getArgs(1));
                player.sendMessage(this.config.getString("BLOCKED_COMMANDS.COMMAND_LOCKED_SUCCESSFULY").replace("%command%", args.getArgs(1)));

            }
        } else {
            ChatUtils.sendBadFormatted(player);
        }
    }
}
