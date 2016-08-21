package Evil_Code_Influence.commands;

import java.util.Set;
import java.util.UUID;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import Evil_Code_Influence.InfluenceAPI;
import Evil_Code_Influence.master.Master;

public class CommandReleaseServant extends CommandBase{

	@SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command command, String label, String args[]){
		//cmd: /releaseservant <Name/all>
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
			sender.sendMessage("§7Perhaps they have already been freed?");
			return true;
		}
		
		UUID masterUUID = (sender instanceof Player) ? ((Player)sender).getUniqueId() : null;
		
		for(OfflinePlayer servant : targetP){
			InfluenceAPI.releaseServantFromMaster(servant.getUniqueId(), masterUUID);
			sender.sendMessage(prefix+"§aYou have released §7"+servant.getName()+"§a from your service");
			if(servant.isOnline()) servant.getPlayer().sendMessage(prefix+"§aYou have released!");
		}
		
		return true;
	}
}
