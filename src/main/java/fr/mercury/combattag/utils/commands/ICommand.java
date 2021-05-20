package fr.mercury.combattag.utils.commands;

public abstract class ICommand {
	
  public ICommand() {}
  public abstract void onCommand(CommandArgs paramCommandArgs);
}
