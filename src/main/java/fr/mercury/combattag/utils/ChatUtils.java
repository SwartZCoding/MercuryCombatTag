package fr.mercury.combattag.utils;

import fr.mercury.combattag.MercuryTag;
import net.minecraft.util.org.apache.commons.lang3.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class ChatUtils {

    public static FileConfiguration config = MercuryTag.getInstance().getConfig();

    public static void sendConsole(String message) {

        MercuryTag.getInstance().getServer().getConsoleSender().sendMessage(message);
    }

    public static void sendNoPermission(CommandSender sender) {

        sender.sendMessage(config.getString("GENERAL.NO_PERMISSION"));
    }

    public static void sendNoPermission(Player player) {

        player.sendMessage(config.getString("GENERAL.NO_PERMISSION"));
    }

    public static void sendBadFormatted(CommandSender sender) {

        sender.sendMessage(config.getString("GENERAL.BAD_FORMAT"));
    }

}
