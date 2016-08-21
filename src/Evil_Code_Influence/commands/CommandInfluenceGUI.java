package Evil_Code_Influence.commands;

import java.util.Set;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import Evil_Code_Influence.InfluenceAPI;
import Evil_Code_Influence.master.Master;

public class CommandInfluenceGUI extends CommandBase{
	
	@SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command command, String label, String args[]){
		//cmd:   /influencegui <Name/default>
		if(args.length < 1){
			sender.sendMessage("§cToo few arguments!");
			return false;
		}
		
		Set<OfflinePlayer> targetP;
		if(sender instanceof Player){
			OfflinePlayer p = sender.getServer().getOfflinePlayer(args[0]);
			if(p != null && InfluenceAPI.checkIsMaster(((Player)sender).getUniqueId(), p.getUniqueId()) == false){
				sender.sendMessage("§cYou are not the master of "+p.getName());
				return true;
			}
			
			Master master = InfluenceAPI.getMasterByUUID(((Player)sender).getUniqueId());
			if(master == null){
				sender.sendMessage("§4ERROR: §cYou do not own any servants");
				return true;
			}
			targetP = CommandUtils.getTargetServants(master, args[0], true);
		}
		else targetP = CommandUtils.getTargetServants(sender, args[0], true);
		
		if(targetP.isEmpty()){
			sender.sendMessage("§cPlayer[Servant] not found!");
			return true;
		}
		
		
		
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
			OfflinePlayer p = sender.getServer().getOfflinePlayer(args[0]);
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
