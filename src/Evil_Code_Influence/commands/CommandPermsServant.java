package Evil_Code_Influence.commands;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import Evil_Code_Influence.InfluenceAPI;
import Evil_Code_Influence.master.Master;
import Evil_Code_Influence.servant.AbilityConfig.Ability;
import Evil_Code_Influence.servant.Servant;

public class CommandPermsServant extends CommandBase{

	@SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command command, String label, String args[]){
		//cmd: /permsservant <Name/all> <perm> <allow/deny>
		if(args.length == 0){
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
			sender.sendMessage("§cPlayer not found!");
			return true;
		}
		Set<Servant> targetS = new HashSet<Servant>();
		if(sender instanceof Player){
			Master master = InfluenceAPI.getMasterByUUID(((Player) sender).getUniqueId());
			for(OfflinePlayer player : targetP) targetS.add(master.getServant(player.getUniqueId()));
		}
		else{
			for(OfflinePlayer player : targetP) targetS.add(InfluenceAPI.getServant(player.getUniqueId()));
		}
		
		if(args.length == 1){
			for(Servant servant : targetS){
				String servantName = sender.getServer().getOfflinePlayer(servant.getPlayerUUID()).getName();
				
//				StringBuilder builder = new StringBuilder(prefix).append("Showing §7")
//						.append(servantName).append(msgC).append("'s permissions: \n");
				StringBuilder builder = new StringBuilder(prefix).append("§7").append(servantName).append(msgC).append("> ");
				
				for(Ability ability : Ability.values()){
//					builder.append("§7").append(ability.name().toLowerCase().replace('_', '-')).append(msgC)
//						   .append(": ").append(servant.hasAbility(ability) ? "§aallowed" : "§cdenied").append(msgC).append(", ");
					builder.append(servant.hasAbility(ability) ? "§a" : "§c").append(ability.name().toLowerCase()
							.replace('_', '-')).append(msgC).append(", ");
				}
				sender.sendMessage(builder.substring(0, builder.length()-2)+'.');
			}
		}
		else if(args.length == 2){
			Ability ability;
			try{ability = Ability.valueOf(args[1].replace('-', '_').toUpperCase());}
			catch(IllegalArgumentException ex){
				sender.sendMessage(new StringBuilder(prefix).append("§cUnknown permission '§7").append(args[1]).append("§c'\n")
						.append(prefix).append("To view all permissions, try §2/i perms ").append(args[0]).toString());
				return true;
			}
			for(Servant servant : targetS){
				String servantName = sender.getServer().getOfflinePlayer(servant.getPlayerUUID()).getName();
				
				sender.sendMessage(new StringBuilder(prefix).append("§7").append(servantName).append(msgC)
						.append(" > ").append("§7").append(ability.name().toLowerCase().replace('_', '-')).append(msgC)
						.append(" is ").append(servant.hasAbility(ability) ? "§aallowed" : "§cdenied").append(msgC).append('.')
						.toString());
			}
		}
		if(args.length == 3){
			Ability ability;
			try{ability = Ability.valueOf(args[1].replace('-', '_').toUpperCase());}
			catch(IllegalArgumentException ex){
				sender.sendMessage(new StringBuilder(prefix).append("§cUnknown permission '§7").append(args[1]).append("§c'\n")
						.append(prefix).append("To view all permissions, try §2/i perms ").append(args[0]).toString());
				return true;
			}
			boolean newValue = args[2].equals("allow") || args[2].equals("yes") || args[2].equals("give") || args[2].equals("y");
			
			for(Servant servant : targetS){
				String servantName = sender.getServer().getOfflinePlayer(servant.getPlayerUUID()).getName();
				
				servant.setAbility(ability, newValue);//Poof!
				
				sender.sendMessage(new StringBuilder(prefix).append("§oServant permissions updated\n")
						.append(prefix).append("§7").append(servantName).append(msgC)
						.append(" > ").append("§7").append(ability.name().toLowerCase().replace('_', '-')).append(msgC)
						.append(" is now ").append(newValue ? "§aallowed" : "§cdenied").append(msgC).append('.')
						.toString());
			}
		}
		return true;
	}
}
