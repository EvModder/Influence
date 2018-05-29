package Evil_Code_Influence.commands;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import Evil_Code_Influence.InfluenceAPI;
import Evil_Code_Influence.master.Master;

public class CommandListServants extends CommandBase{
	
	@SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command command, String label, String args[]){
		//cmd: /i list
		Set<UUID> targetP = new HashSet<UUID>();
		
		if(args.length == 0){
			if(sender instanceof Player){
				Master master = InfluenceAPI.getMasterByUUID(((Player)sender).getUniqueId());
				if(master == null){
					sender.sendMessage(prefix+"You do not own any servants");
					return true;
				}
				else targetP.add(master.getPlayerUUID());
			}
			else targetP.addAll(InfluenceAPI.getAllMasterUUIDs());
		}
		else{
			args[0] = args[0].toLowerCase();
			OfflinePlayer p = sender.getServer().getOfflinePlayer(args[0]);
			if(p != null && p.hasPlayedBefore()){
				Master master = InfluenceAPI.getMasterByUUID(p.getUniqueId());
				if(master == null || master.hasServants() == false){
					sender.sendMessage("�7"+p.getName()+"�c does not own any servants");
					return true;
				}
				else targetP.add(p.getUniqueId());
			}
			else if(args[0].equals("all") || args[0].equals("@a")){
				targetP.addAll(InfluenceAPI.getAllMasterUUIDs());
			}
			else{
				sender.sendMessage("�cPlayer[Master] not found!");
				return true;
			}
		}
		StringBuilder builder = new StringBuilder();
		for(UUID master : targetP){
			builder.append(prefix).append("�7").append(sender.getServer().getOfflinePlayer(master).getName())
			.append(msgC).append(" > \n");
			for(UUID servant : InfluenceAPI.getMasterByUUID(master).getServantUUIDs()){
				builder.append("�7").append(sender.getServer().getOfflinePlayer(servant).getName()).append(msgC).append(", ");
			}
			builder.delete(builder.length()-2, builder.length()).append(".\n");
		}
		String list = builder.substring(0, builder.length()-1);
		if(sender instanceof Player) list = list.replace(sender.getName()+' ', "�lYou ");
		sender.sendMessage(list);
		
		return true;
	}
}
