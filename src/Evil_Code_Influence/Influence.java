package Evil_Code_Influence;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import Evil_Code_Influence.commands.CommandManager;
import Evil_Code_Influence.commands.CommandUtils;
import Evil_Code_Influence.listeners.Listener_PlayerAction;
import Evil_Code_Influence.listeners.Listener_PlayerChat;
import Evil_Code_Influence.master.Master;
import Evil_Code_Influence.servant.AbilityConfig;
import Evil_Code_Influence.servant.Servant;
import Evil_Code_Influence.servant.AbilityConfig.Ability;

public final class Influence extends JavaPlugin{
	private static Influence plugin;
	public static Influence getPlugin(){return plugin;}
	private FileConfiguration config;
	@Override public FileConfiguration getConfig(){return config;}//TODO: use plugin base
	@Override public void saveConfig(){
		try{config.save(new File("./plugins/EvFolder/config-influence.yml"));}
		catch(IOException e){e.printStackTrace();}
	}
	
	private static AbilityConfig defaultPerms;
	public static AbilityConfig getDefaultAbilities(){return defaultPerms;}
	
//	public final static String prefix = "�8[�2Ifl�8]�f ";
	protected double MIN_WAGE;
	protected Map<UUID, Master> masterList;//TODO: only load masters/servants who are online
	
	@Override public void onEnable(){
		//projectID= ???; //Need to find this out when uploaded to bukkit.com!
		//this= plugin instance, projectID= bukkit id, getFile()= file to replace/update, UpdateType= type, boolean= log progress %s
		//TODO: upload to bukkit and then uncomment this
//		new Updater(this, projectID, getFile(), Updater.UpdateType., true);
		plugin = this;
		config = FileIO.loadConfig(this, "config-influence.yml", getClass().getResourceAsStream("/config.yml"));
		
		MIN_WAGE = config.getDouble("min-daily-wage");
		
		//Load default servant permissions
		Set<Ability> abilities = new HashSet<Ability>();
		ConfigurationSection defaultPermSettings = config.getConfigurationSection("default-servant-permissions");
		for(String setting : defaultPermSettings.getKeys(false))
			if(defaultPermSettings.getBoolean(setting)) abilities.add(Ability.valueOf(setting.toUpperCase()));
		defaultPerms = new AbilityConfig(abilities);
		
		loadMasterList();
		new InfluenceAPI();
		new VaultHook(this);
		registerCommands();
		if(!masterList.isEmpty()) registerEvents();
	}
	@Override public void onDisable(){
		saveMasterList();
	}
	
	private void registerCommands(){
		new CommandUtils();
		new CommandManager();
	}
	private void registerEvents(){
		new Listener_PlayerAction();
		new Listener_PlayerChat();
	}
	private void unregisterEvents(){
		HandlerList.unregisterAll(this);
	}
	
	private void loadMasterList(){
		//Load masters & servants file
		masterList = new HashMap<UUID, Master>();
		String file = FileIO.loadFile("masters-servants.txt", "");
		
		Master master = null;
		UUID mUUID=null, sUUID=null;
		if(!file.isEmpty()) try{
			for(String line : file.split("\n")){
				String[] lineData = line.replace(" ", "").toLowerCase().split("\\|");
				
				//if master
				if(line.startsWith("m|")){
					if(master != null) masterList.put(mUUID, master);//add previous master
					
					mUUID = UUID.fromString(lineData[1]);
					
					// If they have custom AbilityConfig preferences and/or starting wage for their servants,
					// load it & add them with it
					AbilityConfig preferences = null;
					double wage = MIN_WAGE;
					
					for(int i=3; i<lineData.length; ++i){
						String data = lineData[i];
						if(data.startsWith("perms{")){
							preferences = new AbilityConfig(false);
							for(String ability : data.substring(data.indexOf("{")+1, data.indexOf("}")).split(",")){
								preferences.setAbility(Ability.valueOf(ability.toUpperCase()), true);
							}
						}
						else if(data.startsWith("wage{")){
							try{wage = Double.parseDouble(data.substring(data.indexOf('{')+1, data.indexOf('}')));}
							catch(NumberFormatException ex){
								getLogger().warning("Unable to load number value for WAGE in masterlist");
							}
						}
					}
					master = new Master(mUUID, preferences, wage);
				}
				//if servant
				else if(master != null && line.startsWith("s|")){
					sUUID = UUID.fromString(lineData[1]);
					
					if(lineData.length >= 3){
						Servant servant = new Servant(sUUID, mUUID, master.getPreferences(), MIN_WAGE);
						
						for(int i=3; i<lineData.length; ++i){
							String data = lineData[i];
							
							if(data.startsWith("perms{")){
								data = ','+data.toLowerCase().substring(data.indexOf("{")+1, data.indexOf("}"))+',';
								
								for(Ability ability : AbilityConfig.Ability.values()){
									servant.setAbility(ability, data.contains(','+ability.name()+','));
								}
							}
							else if(data.startsWith("wage{") && data.endsWith("}")){
								try{servant.setWage(Double.parseDouble(data.substring(data.indexOf('{')+1, data.indexOf('}'))));}
								catch(NumberFormatException ex){
									getLogger().warning("Unable to load number value for WAGE in masterlist");
								}
							}
						}
						master.addServant(servant.getPlayerUUID(), true);//force=true to skip rank checking
					}
					else master.addServant(sUUID, true);//force=true to skip rank checking
				}//if loading servant line
			}//for(line in file)
			if(master != null) masterList.put(mUUID, master);//add last master
		}//if file is not empty
		catch(IllegalArgumentException ex1){
			getLogger().warning("ERROR: Could not load masterlist! Please check the file for errors (UUIDs)");
		}
		catch(ArrayIndexOutOfBoundsException ex2){
			getLogger().warning("ERROR: Could not load masterlist! Please check the file for errors (missing values)");
		}
		getLogger().info("Enable complete! > ["+masterList.size()+"] master profiles loaded");
	}
	
	private void saveMasterList(){
		/** Example File:
		 * 
		 * MasterList:
		 *   m|134534-143f1-134rf134|Setteal:
		 *     s|134134-13414-134143-134143|FoofPuss
		 *     s|q34134-13413-134543-3434f4|DiamondBlocks|perms{break_blocks,sleep,eat,attack}|wage{50}
		 *     s|345345-asdgg-34f43f-ah34t3|pwu1|wage{25.50}
		 *   
		 *   m|asgg5g-5234v-34t53535|lekrosa|perms{place_blocks,break_blocks}:
		 *     s|344334-34f4f-13g5hh-5234f4|Evil_Witchdoctor
		 * 
		*/
		StringBuilder file = new StringBuilder("Master List: \n");
/*		List<Master> masters = new ArrayList<Master>(); masters.addAll(masterList.values());
		
		masters.sort(new Comparator<Master>() {
			@Override
			public int compare(final Master m1, final Master m2) {
				// Alphabetical sorting by username
				return (getServer().getOfflinePlayer(m1.getPlayerUUID()).getName()
						.compareTo(getServer().getOfflinePlayer(m2.getPlayerUUID()).getName()));
			}
		});*/
		
		for(Master master : masterList.values()){
			//Write master
			file.append("  m|").append(master.getPlayerUUID().toString())
				.append('|').append(getServer().getOfflinePlayer(master.getPlayerUUID()).getName());
			if(master.getPreferences() != null){
//				// the uuid of the master and the abilities he/she allows servants to use by default
//				 *  
//				 *  134sdf-34f32-3fec3rc-fl35b{break_blocks,eat,sleep,teleport,attack_monsters,attack_animals}
				file.append("|perms{");
				boolean notFirst = false;
				for(Ability ability : master.getPreferences().getAbilities()){
					if(master.getPreferences().hasAbility(ability)){
						if(notFirst) file.append(',');
						else notFirst = true;
						file.append(ability.name());
					}
				}
				file.append('}');
			}
			if(master.getStartingWage() != MIN_WAGE){
				file.append("|wage{").append(String.valueOf(master.getStartingWage())).append('}');
			}
			
			file.append(":\n");
			
			for(Servant servant : master.getServants()){
				// Write servant
				file.append("    s|").append(servant.getPlayerUUID().toString())
					.append('|').append(getServer().getOfflinePlayer(servant.getPlayerUUID()).getName());
				
				boolean customPerms = master.getPreferences() == null ? !servant.getAbilityConfig().equals(defaultPerms) :
					!servant.getAbilityConfig().equals(master.getPreferences());
				if(customPerms){
					file.append("|perms{");
					boolean notFirst = false;
					for(Ability ability : Ability.values()){
						if(servant.getAbilityConfig().hasAbility(ability)){
							if(notFirst) file.append(',');
							else notFirst = true;
							file.append(ability.name());
						}
					}
					file.append('}');
				}
				if(servant.getWage() != master.getStartingWage()){
					file.append("|wage{").append(String.valueOf(servant.getWage())).append('}');
				}
				file.append('\n');
			}
			file.append('\n');
		}
		FileIO.saveFile("masters-servants.txt", file.toString());
	}
	
	// Methods called by InfluenceAPI
	protected void addMaster(UUID masterUUID, Master master){
		if(masterList.isEmpty()) registerEvents();
		masterList.put(masterUUID, master);
	}
	
	protected void removeMaster(UUID masterUUID){
		if(masterList.remove(masterUUID) != null && masterList.isEmpty()) unregisterEvents();
	}
}
