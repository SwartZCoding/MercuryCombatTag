package fr.mercury.combattag.engine;

import fr.mercury.combattag.MercuryTag;
import fr.mercury.combattag.engine.commands.AddBlockedCommand;
import fr.mercury.combattag.engine.commands.CombatCommand;
import fr.mercury.combattag.utils.Utils;
import fr.mercury.combattag.utils.jsons.DiscUtil;
import fr.mercury.combattag.utils.jsons.Saveable;
import lombok.Getter;
import net.minecraft.util.com.google.gson.reflect.TypeToken;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class CombatManager extends Saveable {

    private static @Getter CombatManager instance;
    private Map<String, Long> combats;
    private @Getter List<String> commands;
    private FileConfiguration config = MercuryTag.getInstance().getConfig();

    public CombatManager(MercuryTag plugin) {
        super(plugin, "CombatTag");

        this.instance = this;
        this.combats = new HashMap<>();
        this.commands = Arrays.asList("/eat", "/feed", "ping", "/etpyes", "/etpaccept", "/tpyes", "/tpaccept", "/tpno", "/tpdeny", "/ct", "/combattag", "/eat", "/feed", "/near", "/msg", "/r", "/reply", "/f", "/faction", "kit", "/f");

        plugin.registerCommand(new CombatCommand());
        plugin.registerCommand(new AddBlockedCommand());
        plugin.registerListener(new CombatListener());
    }

    public void setCombat(Player player) {

        if(!this.isInCombat(player) && !player.hasPermission(this.config.getString("COMBAT.PERMISSION_BYPASS_COMBAT"))) {
            player.sendMessage(this.config.getString("COMBAT.START_COMBAT").replace("%time%", String.valueOf(this.config.getInt("COMBAT.COOLDOWN"))));

            // TODO Faire un cooldown

            this.combats.remove(player.getName());
            this.combats.put(player.getName(), System.currentTimeMillis());
            return;
        }

        this.combats.remove(player.getName());

        // TODO Faire un cooldown

        this.combats.put(player.getName(), System.currentTimeMillis());
    }

    public void addCommand(String string) {
        this.commands.add(string);
    }

    public void removeCommand(String string) {
        this.commands.remove(string);
    }

    public void sendList(CommandSender sender) {

        StringBuilder commands = new StringBuilder();

        this.commands.forEach(current -> commands.append("§b§l" + current + "§7, "));


        this.config.getStringList("BLOCKED_COMMANDS.MESSAGES").forEach(msg -> sender.sendMessage(msg.replace("%list%", commands.toString())));
    }

    public int getSecondsLeft(Player player) {

        return (int) ((this.combats.get(player.getName()) + this.config.getInt("COMBAT.COOLDOWN") * 1000 - System.currentTimeMillis()) / 1000);
    }

    public boolean isInCombat(Player player) {

        if(player.hasPermission(this.config.getString("COMBAT.PERMISSION_BYPASS_COMBAT")))
            return false;

        return this.combats.containsKey(player.getName()) && this.combats.get(player.getName()) + this.config.getInt("COMBAT.COOLDOWN") * 1000 > System.currentTimeMillis();
    }

    public void remove(Player player) {

        this.combats.remove(player.getName());
        player.sendMessage(this.config.getString("COMBAT.FINISH_COMBAT"));
    }

    /*
    @author https://github.com/QuiiBz - "Tom - QuiiBz#7533"
    */
    public boolean canExecute(Player player, String command) {

        if(this.isInCombat(player)) {

            AtomicBoolean canExecute = new AtomicBoolean(false);

            this.commands.forEach(current -> {

                if(command.startsWith(current) && !canExecute.get())
                    canExecute.set(true);
            });

            return canExecute.get();

        } else
            this.combats.remove(player.getName());

        return true;
    }

    @Override
    public File getFile() {
        return Utils.getFormatedFile("commands.json");
    }

    @Override
    public void loadData() {
        String content = DiscUtil.readCatch(getFile());
        if (content != null) {
            this.commands = MercuryTag.getInstance().getGson().fromJson(content, new TypeToken<List<String>>() {}.getType());
        }
    }

    @Override
    public void saveData(boolean sync) {
        DiscUtil.writeCatch(MercuryTag.getInstance(), this.getFile(), MercuryTag.getInstance().getGson().toJson(this.commands), sync);
    }
}
