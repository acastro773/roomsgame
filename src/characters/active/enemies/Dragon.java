package characters.active.enemies;

import items.Item;
import items.wereables.ShortSword;
import items.wereables.WereableArmor;
import items.wereables.WereableWeapon;
import magic.FireRing;
import magic.Fireball;

import java.util.ArrayList;

import characters.Character;
import characters.active.ActiveCharacter;
import map.Map;
import map.Room;
import util.RandUtil;
import util.Tuple;

public class Dragon extends ActiveCharacter {
	
	public Dragon(Map map, Room room, Tuple<Integer, Integer> position, ArrayList<String> adjectives, int level) {
		super("dragon", "", map, room, position, 8+level, 3+level, 15+(3*level), 80+(level*20),
				100, 100, 100, getRandomMood(), new ArrayList<WereableWeapon>(), new ArrayList<WereableArmor>(), 60,
				70, 0, new ArrayList<Item>(), 0, 0, 100, 50, 0, "D", 2, null, adjectives, level);
		this.setMovementType(getMovementTypeFromMood());
		this.setTirenessTotal(7);
		this.setExperienceGiven(100+level*10);
		this.setRandomSpells();
		this.getRandomEquip();
		int randNum = 50 - this.getLevel()*3;
		if (randNum < 2)
			randNum = 2;
		if (RandUtil.RandomNumber(0, randNum) == 0) {
			this.putRandomItemInventory();
		}
		//this.setSpeed(getSpeedWeight(this.getSpeed()));
	}
	
	public ArrayList<String> getAdjectivesIndividual() {
		ArrayList<String> adjectives = new ArrayList<String>();
		adjectives.add("old");
		return adjectives;
	}

	@Override
	public void setCharacterDead(ActiveCharacter character) {
		if (character.getLife() <= 0){
			character.setDead(true);
			this.dropAllItems(character);
			character.getRoom().removeTurnDead(character);
		}
	}

	@Override
	public void addNewExperience(int addExperience) {
		// TODO Auto-generated method stub
		
	}
}
