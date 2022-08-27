package items.consumables;

import java.util.ArrayList;

import map.Map;
import map.Room;
import util.Tuple;
import characters.Character;
import characters.active.ActiveCharacter;

public class LifeExtendedPotion extends Consumable {
	
	int lifeExtendedEffect;
	ArrayList<String> attributes = new ArrayList<String>();

	public LifeExtendedPotion(int weight, int space, String effectDescription,
			Character character, Map map, Room room,
			Tuple<Integer, Integer> position, int lifeExtendedEffect, int price) {
		super("potion", null, null, weight, space, effectDescription,
				character, map, room, position, price);
		this.lifeExtendedEffect = lifeExtendedEffect;
		this.setAdjectives(this.getLifeExtendedPotionAttributes());
	}
	
	public ArrayList<String> getLifeExtendedPotionAttributes(){
		attributes.add("life");
		attributes.add("extended");
		return attributes;
	}
	
	public int getLifeExtendedEffect(){
		return this.lifeExtendedEffect;
	}

	@Override
	public void consume(ActiveCharacter character) {
		character.setTotalLife(character.getTotalLife() + this.getLifeExtendedEffect());
		character.setLife(character.getLife() + this.getLifeExtendedEffect());
	}

	@Override
	public void setRandomPrice() {
		// TODO Auto-generated method stub
		
	}

}
