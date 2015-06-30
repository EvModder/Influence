package Evil_Code_Influence.commands;

import java.util.Set;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import Evil_Code_Influence.Influence;
import Evil_Code_Influence.InfluenceAPI;
import Evil_Code_Influence.master.Master;

@SuppressWarnings("unused")
public class CommandPunishServant implements CommandExecutor{
	private Influence plugin;
	
	public CommandPunishServant(){
		plugin = Influence.getPlugin();
		plugin.getCommand("punishservant").setExecutor(this);
	}

	@SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command command, String label, String args[]){
		//cmd:   /punishservant <Name/all> <#amt>
		if(args.length < 2){
			sender.sendMessage("§cToo few arguments!");
			return false;
		}
		Set<Player> targetP;
		if(sender instanceof Player){
			Player p = plugin.getServer().getPlayer(args[0]);
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
		else targetP = CommandUtils.getTargetPlayers(sender, args[0]);
		
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
		if(damage < 0 || damage > 10){
			sender.sendMessage("§cInvalid damage amount! Please specify an amount between 1 and 20");
			return false;
		}
		
		for(Player servant : targetP){
			sender.sendMessage(Influence.prefix+"§7"+servant.getName()+CommandManager.msgC+" has been punished.");
			
			servant.setHealth(servant.getHealth()-damage);
//			EntityDamageEvent event = new EntityDamageEvent(servant, DamageCause.BLOCK_EXPLOSION, damage);
//			plugin.getServer().getPluginManager().callEvent(event);
			
			servant.sendMessage(Influence.prefix+"§4You have been punished!");
		}
		
		return true;
	}
}
