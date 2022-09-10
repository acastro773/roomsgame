package characters.active.enemies;

import items.Item;
import items.wereables.ShortSword;
import items.wereables.WereableArmor;
import items.wereables.WereableWeapon;
import magic.Fireball;
import main.Main;

import java.util.ArrayList;

import characters.active.ActiveCharacter;
import map.Map;
import map.Room;
import net.slashie.util.Util;
import util.RandUtil;
import util.Tuple;

public class Goblin extends ActiveCharacter {

	public Goblin(Map map, Room room, Tuple<Integer, Integer> position, ArrayList<String> adjectives, int level) {
		super("goblin", "", map, room, position, 2+(2*level), 1+level, 7+(2*level), 80+(level*10),
				80, 100, 100, getRandomMood(), new ArrayList<WereableWeapon>(), new ArrayList<WereableArmor>(), 60,
				70, 0, new ArrayList<Item>(), 0, 0, 100, 50, 0, "G", 3, null, adjectives, level, 0);	
		this.setMovementType(getMovementTypeFromMood());
		this.setTirenessTotal(8+level);
		this.setExperienceGiven(35+level*10);
		this.setRandomSpells();
		this.getRandomEquip();
		int randNum = 50 - this.getLevel()*3;
		if (randNum < 2)
			randNum = 2;
		if (RandUtil.RandomNumber(0, randNum) == 0) {
			this.putRandomItemInventory();
		}
		setMoney(RandUtil.RandomNumber(15, 15*this.getLevel() + 1));
	}
	
	public ArrayList<String> getAdjectivesIndividual() {
		ArrayList<String> adjectives = this.getAdjectivesMood();
		adjectives.add("grey");
		return adjectives;
	}

	@Override
	public void addNewExperience(int addExperience) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setCharacterDead() {
		if (this.getLife() <= 0){
			this.setDead(true);
			this.dropAllItems();
			this.getRoom().removeTurnDead(this);
			Main.user.setMoney(Main.user.getMoney()+this.getMoney());
		}
	}

}
