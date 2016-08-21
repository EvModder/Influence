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

public class CommandSellServant extends CommandBase{

	@SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command command, String label, String args[]){
		//cmd: /i sell <Name/all> to <Name> for <$>
		if(args.length < 5){
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
		else targetP = CommandUtils.getTargetPlayers(sender, args[0], true);
		
		if(targetP.isEmpty()){
			sender.sendMessage("§cPlayer[Servant] not found!");
			return true;
		}
		Player pTo = sender.getServer().getPlayer(args[2]);
		if(pTo == null){
			sender.sendMessage("§cPlayer[To] not found!");
			return false;
		}
		if(pTo.getName().equals(sender.getName())){
			sender.sendMessage("§cYou already own this servant");
			return false;
		}
		double price;
		try{price = Double.parseDouble(args[4]);}
		catch(NumberFormatException ex){
			sender.sendMessage("§cInvalid price! Please enter a number value");
			return false;
		}
		if(targetP.size() == 1)sendSellServantRequest(sender, pTo, targetP.iterator().next(), price);
		else sendSellServantsRequest(sender, pTo, targetP, price);
		
		sender.sendMessage(prefix+"§aOffer sent!");
		return true;
	}
	
	public void sendSellServantRequest(CommandSender seller, Player buyer, OfflinePlayer servant, double salePrice){
		Set<UUID> uuids = new HashSet<UUID>();
		uuids.add(servant.getUniqueId());
		
		if(CommandInfluenceOffer.addTradeOffer(new TradeOffer(seller, buyer, uuids, null, salePrice))){
			buyer.sendMessage(prefix+"§7"+seller.getName()+msgC+" is offering to sell §7"+servant.getName()+
						msgC+" to you as a servant for the lump sum of §c"+salePrice+'$'+msgC+'.');
		}
	}
	
	public void sendSellServantsRequest(CommandSender seller, Player buyer, Set<OfflinePlayer> servants, double salePrice){
		StringBuilder servantNames = new StringBuilder();
		Set<UUID> uuids = new HashSet<UUID>();
		
		for(OfflinePlayer servant : servants){
			uuids.add(servant.getUniqueId());
			servantNames.append(servant.getName()); servantNames.append(msgC+", §7");
		}
		
		if(CommandInfluenceOffer.addTradeOffer(new TradeOffer(seller, buyer, uuids, null, salePrice))){
			buyer.sendMessage(prefix+"§7"+seller.getName()+msgC + 
				" is offering to sell you the following servants: §7"+servantNames.substring(0, servantNames.length()-4) +
				" for the lump sum of §c"+salePrice+'$'+msgC+'.');
		}
	}
}
