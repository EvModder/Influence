package Evil_Code_Influence.servant;

import java.util.UUID;

import Evil_Code_Influence.servant.AbilityConfig.Ability;

public class Servant {
	private UUID servantUUID;
	private UUID ownerUUID;
	private AbilityConfig abilityConfig;
	
	public Servant(UUID servant, UUID master, AbilityConfig abilities){
		servantUUID = servant;
		ownerUUID = master;
		abilityConfig = abilities;
	}
	
	public UUID getOwner(){return ownerUUID;}
	public UUID getPlayerUUID(){return servantUUID;}
	
	public AbilityConfig getAbilityConfig(){return abilityConfig;}
	
	public boolean hasAbility(Ability ability){
		return abilityConfig.hasAbility(ability);
	}
	
	public void setAbility(Ability ability, Boolean value){
		abilityConfig.setAbility(ability, value);
	}
}
