package items.consumables;

import java.util.ArrayList;

import map.Map;
import map.Room;
import util.RandUtil;
import util.Tuple;
import characters.Character;
import characters.active.ActiveCharacter;

public class SuperMagicPotion extends Consumable {
	
	int magicEffect = 150;
	ArrayList<String> attributes = new ArrayList<String>();
	private int minimumPrice = 350;

	public SuperMagicPotion(Character character, Map map, Room room,
			Tuple<Integer, Integer> position, int price) {
		super("potion", null, null, 5, 1, "",
				character, map, room, position, price);
		this.setAdjectives(this.getMagicPotionAttributes());
	}
	
	public ArrayList<String> getMagicPotionAttributes(){
		attributes.add("magic");
		return attributes;
	}

	@Override
	public void consume(ActiveCharacter character) {
		if (character.getMagic() < character.getTotalMagic()){
			if ((character.getMagic() + this.magicEffect) > character.getTotalMagic()){
				character.setMagic(character.getTotalMagic());
			} else {
				character.setMagic(character.getMagic() + magicEffect);
			}
		}
	}
	
	@Override
	public void setRandomPrice() {
		this.setPrice(RandUtil.RandomNumber(minimumPrice, (int)Math.ceil(minimumPrice*1.3)));
	}

}
