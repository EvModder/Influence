package Evil_Code_Influence;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Player;

import Evil_Code_Influence.master.Master;
import Evil_Code_Influence.servant.AbilityConfig;
import Evil_Code_Influence.servant.Servant;
import Evil_Code_Influence.servant.AbilityConfig.Ability;

public class InfluenceAPI {
	static Influence plugin;
	
	public InfluenceAPI(){
		plugin = Influence.getPlugin();
	}
	
	public static boolean isServant(UUID playerUUID){
		return plugin.isServant(playerUUID);
	}
	
	public static Servant getServant(UUID playerUUID){
		return plugin.getServant(playerUUID);
	}
	
	public static Master getMasterByUUID(UUID playerUUID){
		return plugin.masterList.get(playerUUID);
	}
	
	public static Master getMasterOf(UUID playerUUID){
		for(Master master : plugin.masterList.values()) if(master.hasServant(playerUUID)) return master;
		return null;
	}
	
	public static boolean hasServants(UUID masterUUID){
		return plugin.hasServants(masterUUID);
	}
	
	public static boolean checkIsMaster(UUID masterUUID, UUID servantUUID){
		return (plugin.masterList.containsKey(masterUUID) && plugin.masterList.get(masterUUID).hasServant(servantUUID));
	}
	
	public static boolean checkIsMasterOrAboveMaster(UUID masterUUID, UUID servantUUID){
		if(plugin.masterList.containsKey(masterUUID) == false || plugin.isServant(servantUUID) == false) return false;
		
		Master master = plugin.masterList.get(masterUUID);
		return (getAllServantsBelow(master).contains(plugin.getServant(servantUUID)));
	}
	
	public static boolean addServant(UUID masterUUID, UUID servantUUID){
		if(plugin.isServant(masterUUID) &&
				(checkIsMasterOrAboveMaster(servantUUID, masterUUID) ||
				checkIfServantHasPermission(masterUUID, Ability.OWN_SERVANTS)) == false) return false;
		
		if(plugin.masterList.containsKey(masterUUID)){
			plugin.masterList.get(masterUUID).addServant(servantUUID, false);
			return plugin.masterList.get(masterUUID).hasServant(servantUUID);
		}
		else{
			Master master = new Master(masterUUID, loadEmptyMasterPreferences(masterUUID), Influence.minWage());
			master.addServant(servantUUID, false);
			if(master.hasServant(servantUUID)){
				plugin.addMaster(masterUUID, master);
				return true;
			}
			else return false;
		}
	}
	
	public static void releaseServantFromMaster(UUID servantUUID, UUID masterUUID){
		if(plugin.isServant(servantUUID) == false) return;
		
		if(masterUUID == null){
			for(Master master : plugin.masterList.values()){
				master.removeServant(servantUUID);
				if(master.getServants().size() == 0){
					saveEmptyMasterPreferences(master.getPlayerUUID(), master.getPreferences());
					plugin.masterList.remove(master.getPlayerUUID());
				}
			}
		}
		// If a master UUID was specified, only free this slave from that master.
		else{
			Master master = plugin.masterList.get(masterUUID);
			if(master == null) return;
			
			master.removeServant(servantUUID);
			if(master.getServants().size() == 0){
				saveEmptyMasterPreferences(masterUUID, master.getPreferences());
				plugin.masterList.remove(masterUUID);
			}
		}
	}
	
	public static void freeAllServantsFromMaster(UUID masterUUID){
		Master master = plugin.masterList.get(masterUUID);
		if(master == null) return;
		
		for(Servant servant : master.getServants()) master.removeServant(servant.getPlayerUUID());
		saveEmptyMasterPreferences(masterUUID, master.getPreferences());
		plugin.removeMaster(masterUUID);
	}
	
	public static boolean checkIfServantHasPermission(UUID servantUUID, Ability action){
		if(plugin.isServant(servantUUID) == false || plugin.getServant(servantUUID).hasAbility(action)) return true;
		Player p = plugin.getServer().getPlayer(servantUUID);
		
		if(p == null)return false; //EDIT: TODO: Fix this to cast an exception instead
		
		return (p.hasPermission("influence.servant.override.*") ||
				p.hasPermission("influence.servant.override."+action.name().toLowerCase().replace("_", "")));
	}
	
	public static boolean checkIfServantHasPermission(Player servant, Ability action){
		return (plugin.isServant(servant.getUniqueId()) == false ||
				plugin.getServant(servant.getUniqueId()).hasAbility(action) ||
				servant.hasPermission("influence.servant.override.*") ||
				servant.hasPermission("influence.servant.override."+action.name().toLowerCase().replace("_", "")));
	}
	
	private static List<Servant> getAllServantsBelow(Master master){
		List<Servant> servants = new ArrayList<Servant>();
		
		for(Servant servant : master.getServants()){
			if(servants.contains(servant)) continue;
			if(plugin.masterList.containsKey(servant.getPlayerUUID())){
				servants.addAll(getAllServantsBelow(plugin.masterList.get(servant.getPlayerUUID())));
			}
		}
		return servants;
	}
	
	public static List<Servant> getAllServants(){
		List<Servant> servants = new ArrayList<Servant>();
		for(Master master : plugin.masterList.values()) servants.addAll(master.getServants());
		return servants;
	}
	
	protected static AbilityConfig loadEmptyMasterPreferences(UUID playerUUID){
//		//TODO: fill in this skeleton
//		// if FILE NOT FOUND, return null.  Otherwise return the loaded preferences for this playerUUID
//		/** Perhaps format something like this for simplicity:
//		 * 
//		 *  // the uuid of the master and the abilities he/she allows servants to use by default
//		 *  
//		 *  134sdf-34f32-3fec3rc-fl35b{break_blocks,eat,sleep,teleport,attack_monsters,attack_animals}
//		 *  
//		 */
//		return null;
		BufferedReader reader = null;
		try{reader = new BufferedReader(new FileReader("./plugins/EvFolder/master-prefs.txt"));}
		catch(FileNotFoundException e){
			//Create Directory
			File dir = new File("./plugins/EvFolder");
			if(!dir.exists()) dir.mkdir();
			return null;
		}
		
		String line = null;
		UUID mUUID = null;
		try{
			while((line = reader.readLine()) != null){
				line = line.replace(" ", "").toLowerCase();
				if(line.startsWith("m|")){
					
					try{mUUID = UUID.fromString(line.split("|")[1]);}
					
					catch(IllegalArgumentException ex1){continue;}
					catch(ArrayIndexOutOfBoundsException ex2){continue;}
					
					if(mUUID.equals(playerUUID)){
						AbilityConfig preferences = null;
						
						if(line.contains("{") && line.contains("}")){
							line = ','+line.substring(line.indexOf("{")+1, line.indexOf("}"))+',';
							
							preferences = new AbilityConfig(false);
							for(Ability ability : AbilityConfig.Ability.values()){
								preferences.setAbility(ability, line.contains(','+ability.name()+','));
							}
						}
						reader.close();
						return preferences;
					}
				}
			}
			reader.close();
		}
		catch(IOException e){}
		return null;
	}
	
	protected static void saveEmptyMasterPreferences(UUID masterUUID, AbilityConfig preferences){
		if(preferences == null) return;
		//TODO: Write the master's preferences to the file
		
		StringBuilder saveprefs = new StringBuilder('{');
		
		boolean hasAny = false;
		for(Ability ability : Ability.values()){
			if(preferences.hasAbility(ability)){
				if(hasAny) saveprefs.append(',');
				else hasAny = true;
				saveprefs.append(ability.name());
			}
		}
		saveprefs.append('}');
		
		BufferedReader reader = null;
		try{reader = new BufferedReader(new FileReader("./plugins/EvFolder/master-prefs.txt"));}
		catch(FileNotFoundException e){
			//Create Directory
			File dir = new File("./plugins/EvFolder");
			if(!dir.exists()) dir.mkdir();
			
			FileIO.saveFile("master-prefs.txt", saveprefs.toString());
			return;
		}
		
		StringBuilder newFile = new StringBuilder();
		boolean updated = false;
		String line = null;
		try{
			while((line = reader.readLine()) != null){
				if(line.startsWith("m|")){
					
					try{
						if(masterUUID.equals(UUID.fromString(line.split("|")[1]))){
							if(!updated){
								newFile.append(line.subSequence(0, line.indexOf('{')));
								newFile.append(saveprefs.toString());
								newFile.append('\n');
								continue;
							}
						}
					}
					catch(IllegalArgumentException ex1){continue;}
					catch(ArrayIndexOutOfBoundsException ex2){continue;}
				}
				// 2 is just a random choice. 0 or 10 would work just as well
				if(line.length() > 2) newFile.append(line); newFile.append('\n');
			}
			reader.close();
		}
		catch(IOException e){return;}
		FileIO.saveFile("master-prefs.txt", newFile.toString());
	}
}
