package Evil_Code_Influence.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import Evil_Code_Influence.Influence;
import Evil_Code_Influence.InfluenceAPI;

public class CommandEnderchestServant implements CommandExecutor{
	private Influence plugin;
	
	public CommandEnderchestServant(){
		plugin = Influence.getPlugin();
		plugin.getCommand("enderchestservant").setExecutor(this);
	}
	
	@SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command command, String label, String args[]){
		//cmd:   /enderchestservant <Name>
		if(sender instanceof Player == false){
			sender.sendMessage("§cThis command can only be run by in-game players");
			return true;
		}
		
		Player p = plugin.getServer().getPlayer(args[0]);
		if(p == null){
			sender.sendMessage("§cPlayer not found!");
			return false;
		}
		if(InfluenceAPI.checkIsMaster(((Player)sender).getUniqueId(), p.getUniqueId()) == false){
			sender.sendMessage("§cYou are not the master of §7"+p.getName()+"§c!");
			return true;
		}
		if(p.hasPermission("influence.enderchest.exempt")){
			sender.sendMessage("§cYou do not have permission to view §7"+p.getName()+"§c's enderchest");
			return true;
		}
		((Player)sender).openInventory(p.getEnderChest());
		
		return true;
	}
}
