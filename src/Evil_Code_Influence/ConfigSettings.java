package Evil_Code_Influence;
import Evil_Code_Influence.commands.CommandCollectServant.CollectType;

public class ConfigSettings {
	CollectType[] collectBlackList;
	
	public ConfigSettings(Influence plugin){
		//initialize self with pluing.getConfig();
		collectBlackList = new CollectType[]{CollectType.XP};
	}
	
	public boolean isCollectTypeBlackListed(CollectType type){
		for(CollectType blackListed : collectBlackList){
			if(type == blackListed){
				return true;
			}
		}
		return false;
	}
}
