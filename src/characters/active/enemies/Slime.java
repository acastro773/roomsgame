package characters.active.enemies;

import items.Item;
import items.wereables.ShortSword;
import items.wereables.WereableArmor;
import items.wereables.WereableWeapon;

import java.util.ArrayList;

import characters.active.ActiveCharacter;
import map.Map;
import map.Room;
import util.RandUtil;
import util.Tuple;

public class Slime extends ActiveCharacter {
	
	public Slime(Map map, Room room, Tuple<Integer, Integer> position, ArrayList<String> adjectives, int level) {
		super("slime", "", map, room, position, 2+(level*2), 2+level, 12+level, 100+(level*20),
				70, 100, 100, getRandomMood(), new ArrayList<WereableWeapon>(), new ArrayList<WereableArmor>(), 60,
				70, 0, new ArrayList<Item>(), 0, 0, 100, 50, 0, "S", 3, null, adjectives, level);
		this.setMovementType(getMovementTypeFromMood());
		this.setExperienceGiven(40+level*10);
		this.getRandomEquip();
		int randNum = 50 - this.getLevel()*3;
		if (randNum < 2)
			randNum = 2;
		if (RandUtil.RandomNumber(0, randNum) == 0) {
			this.putRandomItemInventory();
		}
	}
	
	@Override
	public void setCharacterDead(ActiveCharacter character) {
		if (character.getLife() <= 0) {
			System.out.println("SE CREAN SLIMIES");
			int characLvl = character.getLife();
			character.setDead(true);
			Tuple<Integer, Integer> position = character.getPosition();
			LittleSlime lilSlime1 = new LittleSlime(character.getMap(), character.getRoom(), position, new ArrayList<String>(), characLvl);
			character.getRoom().getMonsters().add(lilSlime1);
			LittleSlime lilSlime2 = new LittleSlime(character.getMap(), character.getRoom(), position, new ArrayList<String>(), characLvl);
			character.getRoom().getMonsters().add(lilSlime2);	
			character.getRoom().removeTurnDead(character);
		}
	}
	
	public ArrayList<String> getAdjectivesIndividual() {
		ArrayList<String> adjectives = new ArrayList<String>();
		adjectives.add("big");
		return adjectives;
	}
}