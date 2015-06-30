package Evil_Code_Influence.commands;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import Evil_Code_Influence.Influence;
import Evil_Code_Influence.InfluenceAPI;

public class CommandInfluenceGUI implements CommandExecutor{
	private Influence plugin;
	
	public CommandInfluenceGUI(){
		plugin = Influence.getPlugin();
		plugin.getCommand("influencegui").setExecutor(this);
	}
	
	@SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command command, String label, String args[]){
		//cmd:   /influencegui <Name/default>
		if(sender instanceof Player == false){
			sender.sendMessage("§cThis command can only be run by in-game players");
			return true;
		}
		
		if(args.length < 1){
			sender.sendMessage("§cToo few arguments!");
			return false;
		}
		
		if(args[0].equalsIgnoreCase("default") || args[0].equalsIgnoreCase("all")){
			// default preferences
		}
		else{
			//specific preferences
			OfflinePlayer p = plugin.getServer().getOfflinePlayer(args[0]);
			if(p == null){
				sender.sendMessage("§cPlayer not found!");
				return false;
			}
			if(InfluenceAPI.checkIsMaster(((Player)sender).getUniqueId(), p.getUniqueId()) == false){
				sender.sendMessage("§cYou are not the master of "+p.getName());
				return true;
			}
		}
		//TODO: Nice, fancy, inventory GUI using green,red, and orange dyes.
		// Allow for editing of default preferences or the abilities of just a specific servant
		
		return true;
	}
}
