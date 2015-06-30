package Evil_Code_Influence.commands;

import java.util.Set;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import Evil_Code_Influence.Influence;
import Evil_Code_Influence.InfluenceAPI;
import Evil_Code_Influence.master.Master;

public class CommandGiveServant implements CommandExecutor{
	private Influence plugin;
	
	public CommandGiveServant(){
		plugin = Influence.getPlugin();
		plugin.getCommand("giveservant").setExecutor(this);
	}

	@SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command command, String label, String args[]){
		//cmd:   /giveservant <Name/all> to <Name>
		if(args.length < 3){
			sender.sendMessage("§cToo few arguments!");
			return false;
		}
		
		Set<OfflinePlayer> targetP;
		if(sender instanceof Player){
			OfflinePlayer p = plugin.getServer().getOfflinePlayer(args[0]);
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
		else targetP = CommandUtils.getTargetPlayers(sender, args[0], true);
		
		if(targetP.isEmpty()){
			sender.sendMessage("§cPlayer[Servant] not found!");
			return true;
		}
		
		OfflinePlayer pTo = plugin.getServer().getOfflinePlayer(args[2]);
		if(pTo == null || pTo.hasPlayedBefore()==false){
			sender.sendMessage("§cPlayer[To] not found!");
			return false;
		}
		
		for(OfflinePlayer player : targetP){
			boolean success = giveServant(sender, pTo, player);
			if(success) sender.sendMessage("§aYou gave S:§7"+player.getName()+"§a to §7"+pTo.getName());
			else sender.sendMessage("§cUnable to give S:§7"+player.getName()+"§a to §7"+pTo.getName());
		}
		
		return true;
	}
	
	public static boolean giveServant(CommandSender sender, OfflinePlayer employer, OfflinePlayer servant){
		boolean success = InfluenceAPI.addServant(employer.getUniqueId(), servant.getUniqueId());
		if(success){
			if(employer.isOnline()){
				employer.getPlayer().sendMessage(
						Influence.prefix+"§7 "+sender.getName()+"§a gave S:§7"+servant.getName()+"§a to you as a servant!");
//				employer.getPlayer().sendMessage(Influence.prefix+"§7 "+servant.getName()+"§a is now your servant!");
			}
			if(servant.isOnline()){
				servant.getPlayer().sendMessage(
						Influence.prefix+"§7 "+sender.getName()+CommandManager.msgC+" gave you as a servant to §7"+employer.getName()
						+ CommandManager.msgC+'.');
			}
		}
		return success;
	}
}
