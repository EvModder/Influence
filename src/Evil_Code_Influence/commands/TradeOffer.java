package Evil_Code_Influence.commands;

import java.util.Set;
import java.util.UUID;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import Evil_Code_Influence.Influence;
import Evil_Code_Influence.InfluenceAPI;

public class TradeOffer {
	final UUID sellerUUID, buyerUUID;
	private Set<UUID> sellerServants, buyerServants;
	private double priceBuyerPays;
	
	public TradeOffer(CommandSender seller, Player buyer, Set<UUID> servantsForSale, Set<UUID> servantsToBuy,
			double priceBuyerPays)
	{
		sellerUUID = (seller instanceof Player) ? ((Player)seller).getUniqueId() : null;
		buyerUUID = buyer.getUniqueId();
		sellerServants = servantsForSale;
		buyerServants = servantsToBuy;
		this.priceBuyerPays = priceBuyerPays;//buyerOffer-sellerOffer;
	}
	
	private void undoCarryOutOffer(){
		Influence plugin = Influence.getPlugin();
		
		if(CommandUtils.transferMoneyFromTo(plugin.getServer().getOfflinePlayer(sellerUUID),
			plugin.getServer().getOfflinePlayer(buyerUUID), priceBuyerPays) == false)
		{
			plugin.getServer().getConsoleSender().sendMessage(CommandBase.prefix+"§cUnable to undo a sale offer between "+
					plugin.getServer().getOfflinePlayer(buyerUUID).getName() + " and " +
					plugin.getServer().getOfflinePlayer(sellerUUID) + '!');
		}
		
		if(sellerServants != null && sellerServants.isEmpty() == false){
			for(UUID servant : sellerServants){
				InfluenceAPI.releaseServantFromMaster(servant, buyerUUID);
				InfluenceAPI.addServant(sellerUUID, servant);
			}
		}
		if(buyerServants != null && buyerServants.isEmpty() == false){
			for(UUID servant : buyerServants){
				InfluenceAPI.releaseServantFromMaster(servant, sellerUUID);
				InfluenceAPI.addServant(buyerUUID, servant);
			}
		}
	}
	
	public boolean carryOutOffer(){
		Influence plugin = Influence.getPlugin();
		
		if(CommandUtils.transferMoneyFromTo(plugin.getServer().getOfflinePlayer(buyerUUID),
			plugin.getServer().getOfflinePlayer(sellerUUID), priceBuyerPays) == false)
		{
			return false;
		}
		
		//=======================================================================================================
		// Trade all/any involved servants.  If unable to trade a servant, undo the process and return false.
		boolean undo = false;
		if(sellerServants != null && !sellerServants.isEmpty()){
			for(UUID servant : sellerServants){
				InfluenceAPI.releaseServantFromMaster(servant, sellerUUID);
				if(InfluenceAPI.addServant(buyerUUID, servant) == false){
					undo = true; break;
				}
			}
		}
		if(undo) { undoCarryOutOffer(); return false; }
		
		if(buyerServants != null && !buyerServants.isEmpty()){
			for(UUID servant : buyerServants){
				InfluenceAPI.releaseServantFromMaster(servant, buyerUUID);
				if(InfluenceAPI.addServant(sellerUUID, servant) == false){
					undo = true; break;
				}
			}
		}
		if(undo) { undoCarryOutOffer(); return false; }
		//=======================================================================================================
		return true;
	}
}
