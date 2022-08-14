package characters.active.enemies;

import items.Item;
import items.wereables.ShortSword;
import items.wereables.WereableArmor;
import items.wereables.WereableWeapon;
import magic.Fireball;

import java.util.ArrayList;

import characters.active.ActiveCharacter;
import map.Map;
import map.Room;
import net.slashie.util.Util;
import util.RandUtil;
import util.Tuple;

public class Goblin extends ActiveCharacter {

	public Goblin(Map map, Room room, Tuple<Integer, Integer> position, ArrayList<String> adjectives, int level) {
		super("goblin", "", map, room, position, 2+(2*level), 1+level, 7+(2*level), 80,
				80, 100, 100, getRandomMood(), new ArrayList<WereableWeapon>(), new ArrayList<WereableArmor>(), 60,
				70, 0, new ArrayList<Item>(), 0, 0, 100, 50, 0, "G", 3, null, adjectives, level);	
		this.setMovementType(getMovementTypeFromMood());
		this.setTirenessTotal(8+level);
		this.setExperienceGiven(35+level*10);
		this.addSpell(new Fireball());
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
		adjectives.add("grey");
		return adjectives;
	}

}
