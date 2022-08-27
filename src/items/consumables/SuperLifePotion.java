package items.consumables;

import java.util.ArrayList;

import map.Map;
import map.Room;
import util.RandUtil;
import util.Tuple;
import characters.Character;
import characters.active.ActiveCharacter;

public class SuperLifePotion extends Consumable {
	
	int lifeEffect = 150;
	ArrayList<String> attributes = new ArrayList<String>();
	private int minimumPrice = 350;

	public SuperLifePotion(Character character, Map map, Room room,
			Tuple<Integer, Integer> position, int price) {
		super("potion", null, null, 5, 1, "Cures the user",
				character, map, room, position, price);
		this.setAdjectives(this.getLifePotionAttributes());
	}
	
	public ArrayList<String> getLifePotionAttributes(){
		attributes.add("life");
		return attributes;
	}

	@Override
	public void consume(ActiveCharacter character) {
		if (character.getLife() < character.getTotalLife()){
			if ((character.getLife() + this.lifeEffect) > character.getTotalLife()){
				character.setLife(character.getTotalLife());
			} else {
				character.setLife(character.getLife() + lifeEffect);
			}
		}
	}

	@Override
	public void setRandomPrice() {
		this.setPrice(RandUtil.RandomNumber(minimumPrice, (int)Math.ceil(minimumPrice*1.3)));
	}

}
