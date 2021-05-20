package fr.mercury.combattag;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fr.mercury.combattag.engine.CombatManager;
import fr.mercury.combattag.utils.commands.CommandFramework;
import fr.mercury.combattag.utils.commands.ICommand;
import fr.mercury.combattag.utils.jsons.JsonPersist;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Modifier;
import java.util.List;

public class MercuryTag extends JavaPlugin {

    private static @Getter MercuryTag instance;
    private @Getter CommandFramework framework;
    private @Getter Gson gson;
    private List<JsonPersist> persists = Lists.newArrayList();

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();

        this.framework = new CommandFramework(this);
        this.registerPersist(new CombatManager(this));

        this.getDataFolder().mkdir();
        this.gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().excludeFieldsWithModifiers(Modifier.TRANSIENT, Modifier.VOLATILE).create();

        this.persists.forEach(persist -> persist.loadData());

        Bukkit.getConsoleSender().sendMessage("");
        Bukkit.getConsoleSender().sendMessage("§6MercuryCombatTag §7- [§aON§7]");
        Bukkit.getConsoleSender().sendMessage("");
    }

    @Override
    public void onDisable() {

        this.persists.forEach(p -> {
            try {
                p.saveData(true);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        Bukkit.getConsoleSender().sendMessage("");
        Bukkit.getConsoleSender().sendMessage("§6MercuryCombatTag §7- [§cOFF§7]");
        Bukkit.getConsoleSender().sendMessage("");
    }

    public void registerCommand(ICommand command) {
        this.framework.registerCommands(command);
    }

    public void registerListener(Listener listener) {
        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(listener, this);
    }

    public void registerPersist(JsonPersist persist)
    {
        this.persists.add(persist);
    }
}
