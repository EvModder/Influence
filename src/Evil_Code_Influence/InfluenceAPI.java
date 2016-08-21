package Evil_Code_Influence;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.bukkit.entity.Player;
import Evil_Code_Influence.master.Master;
import Evil_Code_Influence.servant.AbilityConfig;
import Evil_Code_Influence.servant.Servant;
import Evil_Code_Influence.servant.AbilityConfig.Ability;

public class InfluenceAPI {
	private static Influence plugin;
	public InfluenceAPI(){
		plugin = Influence.getPlugin();
	}
	
	public static boolean isServant(UUID playerUUID){
		for(Master master : plugin.masterList.values()) if(master.hasServant(playerUUID)) return true;
		return false;
	}
	
	public static Servant getServant(UUID playerUUID){
		for(Master master : plugin.masterList.values()) if(master.hasServant(playerUUID)) return master.getServant(playerUUID);
		return null;
	}
	
	public static Master getMasterByUUID(UUID playerUUID){
		return plugin.masterList.get(playerUUID);
	}
	
	public static Master getMasterOf(UUID playerUUID){
		for(Master master : plugin.masterList.values()) if(master.hasServant(playerUUID)) return master;
		return null;
	}
	
	public static boolean hasServants(UUID playerUUID){
		return plugin.masterList.containsKey(playerUUID) && plugin.masterList.get(playerUUID).hasServants();
	}
	
	public static boolean checkIsMaster(UUID masterUUID, UUID servantUUID){
		return (plugin.masterList.containsKey(masterUUID) && plugin.masterList.get(masterUUID).hasServant(servantUUID));
	}
	
	public static boolean checkIsMasterOrAboveMaster(UUID masterUUID, UUID servantUUID){
		if(plugin.masterList.containsKey(masterUUID) == false || isServant(servantUUID) == false) return false;
		
		Master master = plugin.masterList.get(masterUUID);
		return (getAllServantsBelow(master).contains(getServant(servantUUID)));
	}
	
	public static boolean addServant(UUID masterUUID, UUID servantUUID){
		if(masterUUID.equals(servantUUID)) return false;
		else if(isServant(masterUUID)){
			if(plugin.getConfig().getBoolean("allow-servants-to-own-servants") == false) return false;
			
			if((checkIsMasterOrAboveMaster(servantUUID, masterUUID) ||
				checkIfServantHasPermission(masterUUID, Ability.OWN_SERVANTS)) == false) return false;
		}
		
		if(plugin.masterList.containsKey(masterUUID)){
			plugin.masterList.get(masterUUID).addServant(servantUUID, true);
		}
		else{
			Master master = new Master(masterUUID, null, plugin.MIN_WAGE);
			master.addServant(servantUUID, true);
			plugin.addMaster(masterUUID, master);
		}
		return true;
	}
	
	public static void addServantlessMaster(UUID masterUUID, AbilityConfig prefs){
		plugin.masterList.put(masterUUID, new Master(masterUUID, prefs, plugin.MIN_WAGE));
	}
	
	public static void releaseServantFromMaster(UUID servantUUID, UUID masterUUID){
		if(isServant(servantUUID) == false) return;
		
		if(masterUUID == null){
			for(Master master : plugin.masterList.values()){
				master.removeServant(servantUUID);
				if(master.hasServants()){
					if(master.getPreferences() == null) plugin.removeMaster(masterUUID);
				}
			}
		}
		// If a master UUID was specified, only free this slave from that master.
		else{
			Master master = plugin.masterList.get(masterUUID);
			if(master == null) return;
			
			master.removeServant(servantUUID);
			if(master.hasServants()){
				if(master.getPreferences() == null) plugin.removeMaster(masterUUID);
			}
		}
	}
	
	public static void freeAllServantsFromMaster(UUID masterUUID){
		Master master = plugin.masterList.get(masterUUID);
		if(master == null) return;
		
		for(UUID servant : master.getServantUUIDs()) master.removeServant(servant);
//		saveEmptyMasterPreferences(masterUUID, master.getPreferences());
		if(master.getPreferences() == null) plugin.removeMaster(masterUUID);
	}
	
	public static boolean checkIfServantHasPermission(UUID servantUUID, Ability action){
		if(isServant(servantUUID) == false || getServant(servantUUID).hasAbility(action)) return true;
		Player p = plugin.getServer().getPlayer(servantUUID);
		
		if(p == null)return false; //EDIT: TODO: Fix this to cast an exception instead
		
		return (p.hasPermission("influence.servant.override.*") ||
				p.hasPermission("influence.servant.override."+action.name().toLowerCase().replace("_", "")));
	}
	
	public static boolean checkIfServantHasPermission(Player servant, Ability action){
		return (isServant(servant.getUniqueId()) == false ||
				getServant(servant.getUniqueId()).hasAbility(action) ||
				servant.hasPermission("influence.servant.override.*") ||
				servant.hasPermission("influence.servant.override."+action.name().toLowerCase().replace("_", "")));
	}
	
	private static Set<Servant> getAllServantsBelow(Master master){
		Set<Servant> servants = new HashSet<Servant>();
		
		for(UUID servant : master.getServantUUIDs()){
			if(servants.contains(servant)) continue;
			servants.add(master.getServant(servant));
			
			if(plugin.masterList.containsKey(servant)){
				servants.addAll(getAllServantsBelow(plugin.masterList.get(servant)));
			}
		}
		return servants;
	}
	
	public static Set<Servant> getAllServants(){
		Set<Servant> servants = new HashSet<Servant>();
		for(Master master : plugin.masterList.values()) servants.addAll(master.getServants());
		return servants;
	}
	
	public static Set<UUID> getAllServantUUIDs(){
		Set<UUID> servants = new HashSet<UUID>();
		for(Master master : plugin.masterList.values()) servants.addAll(master.getServantUUIDs());
		return servants;
	}
	
	public static Set<Master> getAllMasters(){
		Set<Master> masters = new HashSet<Master>();
		masters.addAll(plugin.masterList.values());
		return masters;
	}
	
	public static Set<UUID> getAllMasterUUIDs(){
		Set<UUID> masters = new HashSet<UUID>();
		masters.addAll(plugin.masterList.keySet());
		return masters;
	}
}