package Evil_Code_Influence.commands;

import java.util.Set;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import Evil_Code_Influence.Influence;
import Evil_Code_Influence.InfluenceAPI;
import Evil_Code_Influence.master.Master;

public class CommandTphereServant implements CommandExecutor{
	private Influence plugin;
	
	public CommandTphereServant(){
		plugin = Influence.getPlugin();
		plugin.getCommand("tphereservant").setExecutor(this);
	}

	@SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command command, String label, String args[]){
		//cmd:   /tphereservant <Name/all>
		if(sender instanceof Player == false){
			sender.sendMessage("§cThis command can only be run by in-game players");
			return true;
		}
		if(args.length < 1){
			sender.sendMessage("§cToo few arguments!");
			return false;
		}
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
		
		Set<Player> targetP = CommandUtils.getTargetServants(master, args[0]);
		if(targetP.isEmpty()){
			sender.sendMessage("§cPlayer not found!");
			return true;
		}
		for(Player servant : targetP){
			servant.sendMessage(Influence.prefix+" §cYou are being teleported by §7"+sender.getName()+"§c...");
			servant.teleport((Player)sender);
		}
		
		return true;
	}
}
