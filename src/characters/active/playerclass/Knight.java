package characters.active.playerclass;

import java.util.ArrayList;

import characters.Character.Mood;
import characters.active.ActiveCharacter;
import items.Item;
import items.wereables.WereableArmor;
import items.wereables.WereableWeapon;
import map.Map;
import map.Room;
import util.RandUtil;
import util.Tuple;

public class Knight extends ActiveCharacter {

	public Knight(Map map, Room room, Tuple<Integer, Integer> position, ArrayList<String> adjectives, int level, int damage, int defense,
			int speed, int life, int luck, int weight, int evasion, int magic) {
		super("knight", "", map, room, position, 
				damage, defense, speed, life, luck, weight, 100, Mood.NEUTRAL, new ArrayList<WereableWeapon>(),
				new ArrayList<WereableArmor>(), 100, 100, 0,
				new ArrayList<Item>(), 0, evasion, 100, magic, 100, "@", 4, null, adjectives, level);	
	}
	@Override
	public void addNewExperience(int addExperience) {
		if (this.getExperience() + addExperience >= this.getNextLevelExperience()) {
			int experienceToNextLevel = this.getNextLevelExperience() - this.getExperience();
			this.setExperience(addExperience - experienceToNextLevel);
			this.setSpeed(this.getSpeed()+1);
			this.setDamage(this.getDamage()+2);
			this.setDefense(this.getDefense()+3);
			this.setNewLevel(this.getLevel() + 1);
			this.setNewLimLife(this.getTotalLife() + 20);
		} else {
			this.setExperience(this.getExperience() + addExperience);
		}
	}
	@Override
	public void setCharacterDead(ActiveCharacter character) {
		if (character.getLife() <= 0){
			character.setDead(true);
			this.dropAllItems(character);
			character.getRoom().removeTurnDead(character);
		}
		
	}
}

