package Evil_Code_Influence.commands;

import java.util.Set;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import Evil_Code_Influence.InfluenceAPI;
import Evil_Code_Influence.master.Master;

public class CommandPunishServant extends CommandBase{

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String args[]){
		//cmd:   /punishservant <Name/all> <#amt>
		if(args.length < 2){
			sender.sendMessage("§cToo few arguments!");
			return false;
		}
		Set<Player> targetP;
		if(sender instanceof Player){
			Player p = sender.getServer().getPlayer(args[0]);
			if(p != null && InfluenceAPI.checkIsMaster(((Player)sender).getUniqueId(), p.getUniqueId()) == false){
				sender.sendMessage("§cYou are not the master of "+p.getName());
				return true;
			}
			Master master = InfluenceAPI.getMasterByUUID(((Player)sender).getUniqueId());
			if(master == null){
				sender.sendMessage("§4ERROR: §cYou do not own any servants");
				return true;
			}
			targetP = CommandUtils.getTargetServants(master, args[0]);
		}
		else targetP = CommandUtils.getTargetServants(sender, args[0]);
		
		if(targetP.isEmpty()){
			sender.sendMessage("§cPlayer not found!");
			return true;
		}
		double damage;
		try{damage = Double.parseDouble(args[1].replace("h", ""));}
		catch(NumberFormatException e){
			sender.sendMessage("§cInvalid damage! Please enter a number between 1 and 20");
			return false;
		}
		if(damage < 0 || damage > 20){
			sender.sendMessage("§cInvalid damage amount! Please specify an amount between 1 and 20");
			return false;
		}
		
		for(Player servant : targetP){
			servant.setHealth(servant.getHealth()-damage);
//			EntityDamageEvent event = new EntityDamageEvent(servant, DamageCause.BLOCK_EXPLOSION, damage);
//			plugin.getServer().getPluginManager().callEvent(event);
			
			sender.sendMessage(prefix+"§7"+servant.getName()+msgC+" has been punished.");
			servant.sendMessage(prefix+"§4You have been punished!");
		}
		
		return true;
	}
}
