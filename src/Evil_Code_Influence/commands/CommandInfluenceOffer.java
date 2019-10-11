package Evil_Code_Influence.commands;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import Evil_Code_Influence.Influence;

public class CommandInfluenceOffer extends CommandBase{
	private static Influence plugin;
	private static Map<Long, TradeOffer> pendingTrades;
	private static int offerTimer; public static int getOfferTimer(){return offerTimer;}
	
	public CommandInfluenceOffer(){
		plugin = Influence.getPlugin();
		offerTimer = plugin.getConfig().getInt("trade-offer-timeout");
		pendingTrades = new HashMap<Long, TradeOffer>();
	}
	
	@Override public boolean onCommand(CommandSender sender, Command command, String label, String args[]){
		//cmd: /i offer <accept/reject>
		if(sender instanceof Player == false){
			sender.sendMessage("�cThis command can only be run by in-game players");
			return true;
		}
		String choice = args.length == 0 ? label.toLowerCase() : args[0].toLowerCase();
		boolean foundOffer = false;

		if(choice.equals("accept") || choice.equals("yes") || choice.equals("agree")){
			UUID senderUUID = ((Player)sender).getUniqueId();

			for(long key : pendingTrades.keySet()){
				TradeOffer offer = pendingTrades.get(key);

				if(senderUUID.equals(offer.buyerUUID)){
					foundOffer = true;
					OfflinePlayer seller = plugin.getServer().getOfflinePlayer(offer.sellerUUID);

					sender.sendMessage(prefix+"�aYou accepted �7"+seller.getName()+"�a's offer!");
					if(seller.isOnline()){
						seller.getPlayer().sendMessage(prefix+"�7"+sender.getName()+"�a accepted your offer!");
					}
					if(pendingTrades.get(key).carryOutOffer() == false){
						sender.sendMessage("�4ERROR: Could not carry out transaction, trade cancelled");
						if(seller.isOnline()){
							seller.getPlayer().sendMessage("�4ERROR: Could not carry out transaction, trade cancelled");
						}
					}
					pendingTrades.remove(key);
					break;
				}
			}
			return true;
		}
		else if(args[0].equals("reject") || args[0].endsWith("no") || args[0].equals("deny")){
			UUID senderUUID = ((Player)sender).getUniqueId();

			for(long key : pendingTrades.keySet()){
				if(senderUUID.equals(pendingTrades.get(key).buyerUUID)){
					foundOffer = true;
					OfflinePlayer seller = plugin.getServer().getOfflinePlayer(pendingTrades.get(key).sellerUUID);

					sender.sendMessage(prefix+"�cYou denied "+seller.getName()+"�c's offer");
					if(seller.isOnline()){
						seller.getPlayer().sendMessage(prefix+"�7"+sender.getName()+"�c denied your offer");
					}

					pendingTrades.remove(key);
					break;
				}
			}
			return true;
		}
		else{
			sender.sendMessage(prefix+"�cCould not figure out your choice, please use 'accept' or 'reject'");
		}
		
		if(foundOffer == false){
			sender.sendMessage(prefix+"You do not have any currently pending offers");
		}
		return true;
	}

	//Offer details
	public static boolean addTradeOffer(TradeOffer tradeOffer){
		for(TradeOffer offer : pendingTrades.values()){
			if(offer.buyerUUID.equals(tradeOffer.buyerUUID)){
				Player seller = plugin.getServer().getPlayer(tradeOffer.sellerUUID);
				Player buyer = plugin.getServer().getPlayer(tradeOffer.buyerUUID);
				if(seller != null && buyer != null){
					seller.sendMessage(new StringBuilder(prefix).append("�7").append(buyer.getName())
							.append("�c already has pending offers at this time, try again later").toString());
				}
				return false;
			}
		}
		
		//put a timestamp on the offer. Once the offer has expired, it will be removed automatically.
		pendingTrades.put(Calendar.getInstance().getTimeInMillis(), tradeOffer);
		
		Player seller = plugin.getServer().getPlayer(tradeOffer.sellerUUID);
		if(seller != null) seller.sendMessage(prefix+"�aOffer sent!");
		
//		Player buyer = plugin.getServer().getPlayer(tradeOffer.buyerUUID);
//		if(buyer != null) buyer.sendMessage(
//				prefix+"Type �2/i accept"+msgC+" to take this offer or �2/i deny"+msgC+" to reject it.\n"+
//				prefix+"Offer expires in �c"+offerTimer+msgC+" seconds.");
		final UUID buyerUUID = tradeOffer.buyerUUID;
		new BukkitRunnable(){@Override public void run(){
			Player buyer = plugin.getServer().getPlayer(buyerUUID);
			if(buyer != null){
				buyer.sendMessage(prefix+"Type �2/i accept"+msgC+" to take this offer or �2/i deny"+msgC+" to reject it.\n"+
								  prefix+"Offer expires in �c"+offerTimer+msgC+" seconds.");
			}
		}}.runTaskLater(plugin, 1);// 1 tick delay
		
		clearOffersLoop();
		return true;
	}
	
	static boolean running = false;
	private static void clearOffersLoop(){
		if(running) return;
		running = true;
		
		new BukkitRunnable(){
			@Override public void run(){
				long currentTime = Calendar.getInstance().getTimeInMillis();
				for(Long timeStamp : pendingTrades.keySet()){
					if((currentTime - timeStamp)/1000 >= offerTimer) pendingTrades.remove(timeStamp);
				}
				if(!pendingTrades.isEmpty()){
					running = false;
					clearOffersLoop();
				}
			}
		}.runTaskLater(plugin, 20);//20 ticks = 1 second
	}
}
