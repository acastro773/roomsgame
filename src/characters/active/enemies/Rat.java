package characters.active.enemies;

import items.Item;
import items.wereables.ShortSword;
import items.wereables.WereableArmor;
import items.wereables.WereableWeapon;
import main.Main;

import java.util.ArrayList;

import characters.active.ActiveCharacter;
import map.Map;
import map.Room;
import util.RandUtil;
import util.Tuple;

public class Rat extends ActiveCharacter {
	
	public Rat(Map map, Room room, Tuple<Integer, Integer> position, ArrayList<String> adjectives, int level) {
		super("rat", "", map, room, position, 2+level, 1+(int)Math.ceil(level*0.5), 8+(3*level), 30+(level*5),
				70, 100, 100, getRandomMood(), new ArrayList<WereableWeapon>(), new ArrayList<WereableArmor>(), 60,
				70, 0, new ArrayList<Item>(), 0, 0, 100, 50, 0, "R", 3, null, adjectives, level, 0);
		this.setMovementType(getMovementTypeFromMood());
		this.setExperienceGiven(20+level*10);
		this.getRandomEquip();
		int randNum = 50 - this.getLevel()*3;
		if (randNum < 2)
			randNum = 2;
		if (RandUtil.RandomNumber(0, randNum) == 0) {
			this.putRandomItemInventory();
		}
		setMoney(RandUtil.RandomNumber(8, 8*this.getLevel() + 1));
	}
	
	public ArrayList<String> getAdjectivesIndividual() {
		ArrayList<String> adjectives = this.getAdjectivesMood();
		adjectives.add("small");
		return adjectives;
	}

	@Override
	public void setCharacterDead() {
		if (this.getLife() <= 0) {
			this.setDead(true);
			this.dropAllItems();
			this.getRoom().removeTurnDead(this);
			Main.user.setMoney(Main.user.getMoney()+this.getMoney());
		}	
	}

	@Override
	public void addNewExperience(int addExperience) {
		// TODO Auto-generated method stub
		
	}
}