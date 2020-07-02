package Evil_Code_Influence.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import Evil_Code_Influence.InfluenceAPI;

public class CommandEnderchestServant extends CommandBase{
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String args[]){
		//cmd:   /enderchestservant <Name>
		if(sender instanceof Player == false){
			sender.sendMessage(ChatColor.RED+"This command can only be run by in-game players");
			return true;
		}
		
		Player p =sender.getServer().getPlayer(args[0]);
		if(p == null){
			sender.sendMessage(ChatColor.RED+"Player not found!");
			return false;
		}
		if(InfluenceAPI.checkIsMaster(((Player)sender).getUniqueId(), p.getUniqueId()) == false){
			sender.sendMessage(ChatColor.RED+"You are not the master of "+ChatColor.GRAY+p.getName()+ChatColor.RED+"!");
			return true;
		}
		if(p.hasPermission("influence.enderchest.exempt")){
			sender.sendMessage(ChatColor.RED+"You do not have permission to view "+ChatColor.GRAY+p.getName()+ChatColor.RED+"'s enderchest");
			return true;
		}
		((Player)sender).openInventory(p.getEnderChest());
		
		return true;
	}
}
