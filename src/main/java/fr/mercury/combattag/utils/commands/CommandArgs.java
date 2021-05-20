package fr.mercury.combattag.utils.commands;

import net.minecraft.util.com.google.common.primitives.Doubles;
import net.minecraft.util.com.google.common.primitives.Ints;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandArgs
{
  private CommandSender sender;
  private Command command;
  private String label;
  private String[] args;
  
  protected CommandArgs(CommandSender sender, Command command, String label, String[] args, int subCommand)
  {
    String[] modArgs = new String[args.length - subCommand];
    for (int i = 0; i < args.length - subCommand; i++) {
      modArgs[i] = args[(i + subCommand)];
    }
    
    StringBuffer buffer = new StringBuffer();
    buffer.append(label);
    for (int x = 0; x < subCommand; x++) {
      buffer.append("." + args[x]);
    }
    String cmdLabel = buffer.toString();
    this.sender = sender;
    this.command = command;
    this.label = cmdLabel;
    this.args = modArgs;
  }
  
  public CommandSender getSender() {
    return sender;
  }
  
  public Command getCommand() {
    return command;
  }
  
  public String getLabel() {
    return label;
  }
  
  public String[] getArgs() {
    return args;
  }
  
  public String getArgs(int index) {
    return args[index];
  }
  
  public int length() {
    return args.length;
  }
  
  public boolean isPlayer() {
    return sender instanceof Player;
  }
  
  public Player getPlayer() {
    if ((sender instanceof Player)) {
      return (Player)sender;
    }
    return null;
  }
  
  public Player asPlayer(int index)
  {
    return Bukkit.getPlayer(getArgs(index));
  }
  
  public org.bukkit.OfflinePlayer asOfflinePlayer(int index) {
    return Bukkit.getOfflinePlayer(getArgs(index));
  }
  
  public Integer asInteger(int index) {
    return Ints.tryParse(getArgs(index));
  }
  
  public Double asDouble(int index) {
    return Doubles.tryParse(getArgs(index));
  }
  
  public String asString(int start, int end) {
    return StringUtils.join(args, ' ', start, end);
  }
}
