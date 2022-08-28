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

public class Slime extends ActiveCharacter {
	
	public Slime(Map map, Room room, Tuple<Integer, Integer> position, ArrayList<String> adjectives, int level) {
		super("slime", "", map, room, position, 2+(level*2), 2+level, 12+level, 100+(level*20),
				70, 100, 100, getRandomMood(), new ArrayList<WereableWeapon>(), new ArrayList<WereableArmor>(), 60,
				70, 0, new ArrayList<Item>(), 0, 0, 100, 50, 0, "S", 3, null, adjectives, level, 0);
		this.setMovementType(getMovementTypeFromMood());
		this.setExperienceGiven(40+level*10);
		this.getRandomEquip();
		int randNum = 50 - this.getLevel()*3;
		if (randNum < 2)
			randNum = 2;
		if (RandUtil.RandomNumber(0, randNum) == 0) {
			this.putRandomItemInventory();
		}
		setMoney(RandUtil.RandomNumber(50, 50*this.getLevel() + 1));
	}
	
	@Override
	public void setCharacterDead() {
		if (this.getLife() <= 0) {
			int characLvl = this.getLife();
			this.setDead(true);
			Tuple<Integer, Integer> position = this.getPosition();
			ArrayList<ActiveCharacter> monsters = this.getRoom().getMonsters();
			LittleSlime lilSlime1 = new LittleSlime(this.getMap(), this.getRoom(), position, new ArrayList<String>(), characLvl);
			monsters.add(lilSlime1);
			LittleSlime lilSlime2 = new LittleSlime(this.getMap(), this.getRoom(), position, new ArrayList<String>(), characLvl);
			monsters.add(lilSlime2);
			this.getRoom().setMonsters(monsters);
			this.getRoom().removeTurnDead(this);
			Main.user.setMoney(Main.user.getMoney()+this.getMoney());
		}
	}
	
	public ArrayList<String> getAdjectivesIndividual() {
		ArrayList<String> adjectives = new ArrayList<String>();
		adjectives.add("big");
		return adjectives;
	}

	@Override
	public void addNewExperience(int addExperience) {
		// TODO Auto-generated method stub
		
	}
}