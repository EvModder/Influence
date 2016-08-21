package Evil_Code_Influence.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;

public abstract class CommandBase implements CommandExecutor{
	public final static transient ChatColor msgC = ChatColor.GRAY;
	public final static transient String prefix = "§8[§2Ifl§8]§f "+msgC;
}
