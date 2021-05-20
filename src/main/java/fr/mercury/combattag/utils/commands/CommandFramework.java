package fr.mercury.combattag.utils.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.SimplePluginManager;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandFramework implements CommandExecutor {
	private Map<String, Map.Entry<Method, Object>> commandMap = new HashMap<>();
	public CommandMap map;
	private Plugin plugin;

	public CommandFramework(Plugin plugin) {
		this.plugin = plugin;
		if ((plugin.getServer().getPluginManager() instanceof SimplePluginManager)) {
			SimplePluginManager manager = (SimplePluginManager) plugin.getServer().getPluginManager();
			try {
				Field field = SimplePluginManager.class.getDeclaredField("commandMap");
				field.setAccessible(true);
				map = ((CommandMap) field.get(manager));
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String label, String[] args) {
		return handleCommand(sender, cmd, label, args);
	}

	public boolean handleCommand(CommandSender sender, org.bukkit.command.Command cmd, String label, String[] args) {
		for (int i = args.length; i >= 0; i--) {
			StringBuffer buffer = new StringBuffer();
			buffer.append(label.toLowerCase());
			for (int x = 0; x < i; x++) {
				buffer.append("." + args[x].toLowerCase());
			}
			String cmdLabel = buffer.toString();
			if (commandMap.containsKey(cmdLabel)) {
				Method method = commandMap.get(cmdLabel).getKey();
				Object methodObject = commandMap.get(cmdLabel).getValue();
				Command command = method.getAnnotation(Command.class);
				boolean hasPerm = true;

				if ((!command.isConsole()) && (!(sender instanceof Player))) {
					sender.sendMessage("Commande uniquement disponible en jeu");
					return true;
				}

				if (!command.permissionNode().isEmpty()) {
					if ((command.permissionNode().equalsIgnoreCase("op")) && (!sender.isOp())) {
						hasPerm = false;
					} else if (!sender.hasPermission(command.permissionNode())) {
						hasPerm = false;
					}
				}

				if (!hasPerm) {
					sender.sendMessage(ChatColor.RED
							+ "Vous n'avez pas la permission d'executer cette commande.");
					return true;
				}

				CommandArgs commandArgs = new CommandArgs(sender, cmd, label, args, cmdLabel.split("\\.").length - 1);
				try {
					method.invoke(methodObject, new Object[] { commandArgs });
				} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
					e.printStackTrace();
				}
				return true;
			}
		}
		defaultCommand(new CommandArgs(sender, cmd, label, args, 0));
		return true;
	}

	public void registerCommands(Object obj) {
		for (Method m : obj.getClass().getMethods()) {
			if (m.getAnnotation(Command.class) != null) {
				Command command = m
						.getAnnotation(Command.class);
				if ((m.getParameterTypes().length > 1) || (m.getParameterTypes()[0] != CommandArgs.class)) {
					System.out.println("Unable to register command " + m.getName() + ". Unexpected method arguments");
				} else {
			
					for (String alias : command.name())
						registerCommand(command, alias, m, obj);
				}
			} else if (m.getAnnotation(Completer.class) != null) {
				Completer comp = m.getAnnotation(Completer.class);
				if ((m.getParameterTypes().length > 1) || (m.getParameterTypes().length == 0)
						|| (m.getParameterTypes()[0] != CommandArgs.class)) {
					System.out.println(
							"Unable to register tab completer " + m.getName() + ". Unexpected method arguments");

				} else if (m.getReturnType() != List.class) {
					System.out.println("Unable to register tab completer " + m.getName() + ". Unexpected return type");
				} else {
					for (String alias : comp.name()) {
						registerCompleter(alias, m, obj);
					}
				}
			}
		}
	}

	public void registerCommand(Command command, String label, Method m, Object obj) {
		
		commandMap.put(label.toLowerCase(), new AbstractMap.SimpleEntry<Method, Object>(m, obj));
		commandMap.put(this.plugin.getName() + ':' + label.toLowerCase(), new AbstractMap.SimpleEntry<Method, Object>(m, obj));
		String cmdLabel = label.split("\\.")[0].toLowerCase();
		
		if (map.getCommand(cmdLabel) == null) {
			org.bukkit.command.Command cmd = new BukkitCommand(cmdLabel, this, plugin);
			map.register(plugin.getName(), cmd);
		}
	}

	public void registerCompleter(String label, Method m, Object obj) {
		String cmdLabel = label.split("\\.")[0].toLowerCase();
		if (map.getCommand(cmdLabel) == null) {
			org.bukkit.command.Command command = new BukkitCommand(cmdLabel, this, plugin);
			map.register(plugin.getName(), command);
		}
		
		if (map.getCommand(cmdLabel) instanceof BukkitCommand) {
			BukkitCommand command = (BukkitCommand) map.getCommand(cmdLabel);
			if (command.completer == null) {
				command.completer = new BukkitCompleter();
			}
			command.completer.addCompleter(label, m, obj);
		} else if (map.getCommand(cmdLabel) instanceof PluginCommand) {
			try {
				Object command = map.getCommand(cmdLabel);
				Field field = command.getClass().getDeclaredField("completer");
				field.setAccessible(true);
				if (field.get(command) == null) {
					BukkitCompleter completer = new BukkitCompleter();
					completer.addCompleter(label, m, obj);
					field.set(command, completer);
				} else if (field.get(command) instanceof BukkitCompleter) {
					BukkitCompleter completer = (BukkitCompleter) field.get(command);
					completer.addCompleter(label, m, obj);
				} else {
					System.out.println("Unable to register tab completer " + m.getName()
							+ ". A tab completer is already registered for that command!");
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
	private void defaultCommand(CommandArgs args) {
		args.getSender().sendMessage(args.getLabel() + " is not handled! Oh noes!");
	}
}
