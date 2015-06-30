package Evil_Code_Influence.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import Evil_Code_Influence.Influence;

public class CommandHireServant implements CommandExecutor{
	private Influence plugin;
	
	public CommandHireServant(){
		plugin = Influence.getPlugin();
		plugin.getCommand("hireservant").setExecutor(this);
	}

	@SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command command, String label, String args[]){
		//cmd:   /hireservant <Name> <$>
		if(sender instanceof Player == false){
			sender.sendMessage("§cThis command can only be run by in-game players");
			return true;
		}
		if(args.length < 2){
			sender.sendMessage("§cToo few arguments!");
			return false;
		}
		Player p = plugin.getServer().getPlayer(args[0]);
		if(p == null){
			sender.sendMessage("§cPlayer not found!");
			return false;
		}
		double wage;
		try{wage = Double.parseDouble(args[1]);}
		catch(NumberFormatException e){
			sender.sendMessage("§cInvalid wage! Please enter a positive number");
			return false;
		}
		if(wage < 0) wage = 0;
		
		hireServant((Player)sender, p, wage);
		
		return true;
	}
	
	public static void hireServant(Player employer, Player target, Double wage){
		//TODO: Send message to target with offer
	}
}
