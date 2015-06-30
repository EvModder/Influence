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
	private double sellerPrice, buyerPrice;
	
	public TradeOffer(CommandSender seller, Player buyer, Set<UUID> servantsForSale, Set<UUID> servantsToBuy,
			double sellerOffer, double buyerOffer)
	{
		sellerUUID = (seller instanceof Player) ? ((Player)seller).getUniqueId() : null;
		buyerUUID = buyer.getUniqueId();
		sellerServants = servantsForSale;
		buyerServants = servantsToBuy;
		sellerPrice = sellerOffer;
		buyerPrice = buyerOffer;
	}
	
	private void undoCarryOutOffer(){
		Influence plugin = Influence.getPlugin();
		if(sellerPrice != 0){
			CommandUtils.editEssentialsBalance(plugin.getServer().getOfflinePlayer(buyerUUID), sellerPrice);
		}
		if(buyerPrice != 0){
			CommandUtils.editEssentialsBalance(plugin.getServer().getOfflinePlayer(sellerUUID), buyerPrice);
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
		
		if(sellerPrice != 0){
			if(CommandUtils.editEssentialsBalance(plugin.getServer().getOfflinePlayer(buyerUUID), -sellerPrice) == false) return false;
		}
		if(buyerPrice != 0){
			if(CommandUtils.editEssentialsBalance(plugin.getServer().getOfflinePlayer(sellerUUID), -buyerPrice) == false){
				//undo the last money exchange
				if(sellerPrice != 0) CommandUtils.editEssentialsBalance(plugin.getServer().getOfflinePlayer(buyerUUID), sellerPrice);
				return false;
			}
		}
		
		//=======================================================================================================
		// Trade all/any involved servants.  If unable to trade a servant, undo the process and return false.
		boolean undo = false;
		if(sellerServants != null && sellerServants.isEmpty() == false){
			for(UUID servant : sellerServants){
				InfluenceAPI.releaseServantFromMaster(servant, sellerUUID);
				if(InfluenceAPI.addServant(buyerUUID, servant) == false){
					undo = true; break;
				}
			}
		}
		if(undo) { undoCarryOutOffer(); return false; }
		
		if(buyerServants != null && buyerServants.isEmpty() == false){
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
