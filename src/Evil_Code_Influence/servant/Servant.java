package Evil_Code_Influence.servant;

import java.util.UUID;

import Evil_Code_Influence.servant.AbilityConfig.Ability;

public class Servant {
	private UUID servantUUID;
	private UUID ownerUUID;
	private AbilityConfig abilityConfig;
	private double wage;
	
	public Servant(UUID servant, UUID master, AbilityConfig abilities, double wage){
		servantUUID = servant;
		ownerUUID = master;
		abilityConfig = abilities == null ? new AbilityConfig(true) : abilities;
		this.wage = wage;
	}
	
	public UUID getOwner(){return ownerUUID;}
	public UUID getPlayerUUID(){return servantUUID;}
	
	public AbilityConfig getAbilityConfig(){return abilityConfig;}
//	public void setAbilityConfig(AbilityConfig newAbilities){abilityConfig = newAbilities;}
	
	public boolean hasAbility(Ability ability){
		return abilityConfig.hasAbility(ability);
	}
	
	public void setAbility(Ability ability, Boolean value){
		abilityConfig.setAbility(ability, value);
	}
	
	public double getWage(){
		return wage;
	}
	
	public void setWage(double newWage){
		wage = newWage;
	}
}
