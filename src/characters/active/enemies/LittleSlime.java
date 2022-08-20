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

public class LittleSlime extends ActiveCharacter {
	
	public LittleSlime(Map map, Room room, Tuple<Integer, Integer> position, ArrayList<String> adjectives, int level) {
		super("slimey", "", map, room, position, 2+level, 1+(int)Math.ceil(level*0.5), 10+(2*level), 60+(level*5),
				70, 100, 100, getRandomMood(), new ArrayList<WereableWeapon>(), new ArrayList<WereableArmor>(), 60,
				70, 0, new ArrayList<Item>(), 0, 0, 100, 50, 0, "s", 3, null, adjectives, level);
		this.setMovementType(getMovementTypeFromMood());
		this.setExperienceGiven(25+level*10);
		this.getRandomEquip();
		int randNum = 50 - this.getLevel()*3;
		if (randNum < 2)
			randNum = 2;
		if (RandUtil.RandomNumber(0, randNum) == 0) {
			this.putRandomItemInventory();
		}
	}
	
	public ArrayList<String> getAdjectivesIndividual() {
		ArrayList<String> adjectives = new ArrayList<String>();
		adjectives.add("small");
		return adjectives;
	}
}