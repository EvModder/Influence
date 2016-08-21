package Evil_Code_Influence.servant;

import java.util.HashSet;
import java.util.Set;

import Evil_Code_Influence.Influence;

public class AbilityConfig {
	public enum Ability{
		BREAK_BLOCKS, PLACE_BLOCKS, EMPTY_BUCKET,
		ENTER_BED, INTERACT_ENTITY, INTERACT_BLOCK, RIDE_MOB,
		ATTACK, ATTACK_PLAYER, ATTACK_ANIMAL, ATTACK_MONSTER,
		ATTACK_MASTER, COMMANDS, OWN_SERVANTS, TELEPORT, USE_CHESTS
	};
	
	private Set<Ability> abilities = new HashSet<Ability>();
	
	public AbilityConfig(boolean useDefaults){
		if(useDefaults) abilities.addAll(Influence.getDefaultAbilities().abilities);
	}
	
	public AbilityConfig(Set<Ability> abilityList){
		abilities.addAll(abilityList);
	}
	
	public void setAbility(Ability ability, boolean value){
		if(value) abilities.add(ability);
		else abilities.remove(ability);
	}
	
	public boolean hasAbility(Ability ability){
		return abilities.contains(ability);
	}
	
	@Override
	public boolean equals(Object obj){
		return (obj instanceof AbilityConfig && ((AbilityConfig)obj).abilities.equals(abilities));
	}
	
	public Set<Ability> getAbilities(){return abilities;}
}
