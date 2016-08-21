package Evil_Code_Influence.commands;

import java.util.Set;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import Evil_Code_Influence.Influence;
import Evil_Code_Influence.InfluenceAPI;
import Evil_Code_Influence.master.Master;

public class CommandSetWageServant extends CommandBase{
	private Double MIN_WAGE;
	
	public CommandSetWageServant(){
		MIN_WAGE = Influence.getPlugin().getConfig().getDouble("min-daily-wage");
	}

	@SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command command, String label, String args[]){
		//cmd: /setwageservant <Name/all> <$>
		if(args.length < 2){
			sender.sendMessage("§cToo few arguments!");
			return false;
		}
		
		Set<OfflinePlayer> targetP;
		if(sender instanceof Player){
			OfflinePlayer p = sender.getServer().getOfflinePlayer(args[0]);
			if(p != null && p.hasPlayedBefore() && !InfluenceAPI.checkIsMaster(((Player)sender).getUniqueId(), p.getUniqueId())){
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
		
		double newWage = 0;
		try{newWage = Double.parseDouble(args[2]);}
		catch(NumberFormatException ex){}
		if(newWage < MIN_WAGE){
			sender.sendMessage("§cInvalid wage! Number must be a positive value" +
					(MIN_WAGE > 0 ? " above or equal to the minimum wage (§7"+MIN_WAGE+"§c)." : ""));
			return false;
		}
		
		for(OfflinePlayer player : targetP){
			InfluenceAPI.getServant(player.getUniqueId()).setWage(newWage);
		}
		
		return true;
	}
}
