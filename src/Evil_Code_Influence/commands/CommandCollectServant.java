package Evil_Code_Influence.commands;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import Evil_Code_Influence.Influence;
import Evil_Code_Influence.InfluenceAPI;
import Evil_Code_Influence.master.Master;
import Evil_Code_Influence.servant.Servant;

public class CommandCollectServant extends CommandBase{
	private Set<String> canCollect;
	public enum CollectType{
		ITEMS(){@Override public void collect(Player servant, Player master){collectItems(servant, master);}},
		ARMOR(){@Override public void collect(Player servant, Player master){collectArmor(servant, master);}},
		XP(){@Override public void collect(Player servant, Player master){collectXP(servant, master);}},
		SERVANTS(){@Override public void collect(Player servant, Player master){collectServants(servant, master);}},
		ALL(){@Override public void collect(Player servant, Player master){
			collectItems(servant, master);
			collectArmor(servant, master);
			collectXP(servant, master);
		}};
		
		public abstract void collect(Player servant, Player master);
	};
	
	public CommandCollectServant(){
		canCollect = new HashSet<String>();
		for(String collect : Influence.getPlugin().getConfig().getStringList("master-can-collect")){
			canCollect.add(collect.toUpperCase());
		}
	}

	@Override @SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command command, String label, String args[]){
		//cmd: /i collect <Name/all> <items/xp/servants/all>
		if(sender instanceof Player == false){
			sender.sendMessage("�cThis command can only be run by in-game players");
			return true;
		}
		if(args.length < 2){
			sender.sendMessage("�cToo few arguments!");
			return false;
		}
		Player p = sender.getServer().getPlayer(args[0]);
		if(p != null && InfluenceAPI.checkIsMaster(((Player)sender).getUniqueId(), p.getUniqueId()) == false){
			sender.sendMessage("�cYou are not the master of "+p.getName());
			return true;
		}
		Master master = InfluenceAPI.getMasterByUUID(((Player)sender).getUniqueId());
		if(master == null){
			sender.sendMessage("�4ERROR: �cYou do not own any servants");
			return true;
		}
		
		Set<Player> targetP = CommandUtils.getTargetServants(master, args[0]);
		if(targetP.isEmpty()){
			sender.sendMessage("�cPlayer not found!");
			return true;
		}
		args[1] = args[1].toUpperCase();
		if(args[1].equals("A") || args[1].equals("@A")) args[1] = "ALL";
		else if(args[1].startsWith("EXP")) args[1] = "XP";
		
		CollectType type;
		try{type = CollectType.valueOf(args[1]);}
		catch(IllegalArgumentException  e){type=CollectType.ITEMS;}
		
		if(!canCollect.contains(type.name())){
			sender.sendMessage(prefix+"�cYou do not have permission to collect �7"+type.name()+"�c.");
			return true;
		}
		//NOTE: 'all' should not collect sub-servants, as stated in command description in plugin.yml
		for(Player servant : targetP){
			type.collect(servant, (Player)sender);
			servant.sendMessage(new StringBuilder(prefix).append("Your �c").append(type).append(msgC)
					.append(type.toString().endsWith("S") ? " have " : " has ")
					.append("been collected by �7").append(sender.getName()).append(msgC).append('.').toString());
			sender.sendMessage(prefix+"�aCollected all �7"+type.toString()+"�a from �7"+servant.getName()+"�a.");
		}
		
		return true;
	}
	
	public static void collectArmor(Player servant, Player master){
		if(master.getInventory().firstEmpty() != -1){
			if(servant.getEquipment().getChestplate() != null){
				master.getInventory().addItem(servant.getEquipment().getChestplate());
				servant.getEquipment().setChestplate(null);
			}
			
			if(master.getInventory().firstEmpty() != -1){
				if(servant.getEquipment().getLeggings() != null){
					master.getInventory().addItem(servant.getEquipment().getLeggings());
					servant.getEquipment().setLeggings(null);
				}
				
				if(master.getInventory().firstEmpty() != -1){
					if(servant.getEquipment().getHelmet() != null){
						master.getInventory().addItem(servant.getEquipment().getHelmet());
						servant.getEquipment().setHelmet(null);
					}
					
					if(master.getInventory().firstEmpty() != -1){
						if(servant.getEquipment().getBoots() != null){
							master.getInventory().addItem(servant.getEquipment().getBoots());
							servant.getEquipment().setBoots(null);
						}
					}
				}
			}
		}
	}
	
	public static void collectItems(Player servant, Player master){
		int index = 0;
		while(master.getInventory().firstEmpty() != -1 && index < servant.getInventory().getSize()){
			if(servant.getInventory().getItem(index) != null){
				master.getInventory().addItem(servant.getInventory().getItem(index));
				servant.getInventory().setItem(index, null);
			}
			++index;
		}
	}
	
	public static void collectXP(Player servant, Player master){
		master.setTotalExperience(master.getTotalExperience()+servant.getTotalExperience());
		servant.setTotalExperience(0);
	}
	
	public static void collectServants(Player servant, Player master){
		Master servantMaster = InfluenceAPI.getMasterByUUID(servant.getUniqueId());
		if(servantMaster != null){
			// If this servant is a master of their own servants, send all sub-servants to the high-master
			// and then clear them from the lower-master
			Master newMaster = InfluenceAPI.getMasterByUUID(master.getUniqueId());
			for(Servant s : servantMaster.getServants()){
				if(newMaster.addServant(s.getPlayerUUID(), false) == false){
					String unaddable = master.getServer().getOfflinePlayer(s.getPlayerUUID()).getName();
					
					master.sendMessage(prefix+"�cUnable to collect S:�7"+unaddable+"�c from S:�7"+servant.getName()+"�c.");
					master.sendMessage("�cThe servant (�7"+unaddable+"�c) has escaped from bondage!");
				}
			}
			InfluenceAPI.freeAllServantsFromMaster(servant.getUniqueId());
		}
	}
}
