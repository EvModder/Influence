package Evil_Code_Influence.servant;

import java.util.HashMap;
import java.util.Map;

public class AbilityConfig {
	//TODO: Load default Nodes from a config somewhere
	public enum Ability{
		BREAK_BLOCKS, PLACE_BLOCKS, EMPTY_BUCKET,
		ENTER_BED, INTERACT_ENTITY, INTERACT_BLOCK, RIDE_MOB,
		ATTACK, ATTACK_PLAYER, ATTACK_ANIMAL, ATTACK_MONSTER,
		ATTACK_MASTER, COMMANDS, OWN_SERVANTS, TELEPORT, USE_CHESTS
	};
	
	final static Map<Ability, Boolean> defaults = new HashMap<Ability, Boolean>();
	static{
		for(Ability node : Ability.values()) defaults.put(node, true);
//		defaults.put(Ability.BREAK_BLOCKS, true);
//		defaults.put(Ability.PLACE_BLOCKS, true);
//		defaults.put(Ability.EMPTY_BUCKET, true);
//		defaults.put(Ability.ENTER_BED, true);
//		defaults.put(Ability.INTERACT_ENTITY, true);
//		defaults.put(Ability.RIDE_MOB, true);
//		defaults.put(Ability.INTERACT_BLOCK, true);
//		defaults.put(Ability.ATTACK, true);
//		defaults.put(Ability.ATTACK_PLAYER, true);
//		defaults.put(Ability.ATTACK_ANIMAL, true);
		defaults.put(Ability.ATTACK_MASTER, false);
		defaults.put(Ability.OWN_SERVANTS, false);
//		defaults.put(Ability.ATTACK_MONSTER, true);
//		defaults.put(Ability.USE_CHESTS, true);
	};
	
	private Map<Ability, Boolean> abilities = new HashMap<Ability, Boolean>();
	
	public AbilityConfig(Map<Ability, Boolean> abilityList){
		abilities.putAll(defaults);
		abilities.putAll(abilityList);
	}
	
	public AbilityConfig(boolean useDefaults){
		if(useDefaults) abilities.putAll(defaults);
		else{
			for(Ability node : Ability.values()) abilities.put(node, false);
		}
	}
	
	public void setAbility(Ability ability, boolean value){
		abilities.put(ability, value);
	}
	
	public boolean hasAbility(Ability ability){
		return abilities.containsKey(ability);
	}
	
	public static Map<Ability, Boolean> getDefaultAbilities(){
		return defaults;
	}
}
