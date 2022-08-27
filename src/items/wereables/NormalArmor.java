package items.wereables;

import items.ItemEnumerate.ArmorType;

import java.util.ArrayList;

import map.Map;
import map.Room;
import util.RandUtil;
import util.Tuple;
import characters.Character;

public class NormalArmor extends WereableArmor {
	
	ArrayList<String> attributes = new ArrayList<String>();
	
	public NormalArmor (Character character, Map map, Room room, Tuple<Integer, Integer> position,
			int level, boolean isMagic, int price) {
		super("armor", null, "", "m", 
				10, 3, new ArrayList<ArmorType>(), 100, character,
				4 + level, map, room, position, 0, level, isMagic, price, 600);
		ArrayList<ArmorType> armorType = new ArrayList<ArmorType>();
		armorType.add(ArmorType.CHEST);
		this.setArmorType(armorType);
		this.setAdjectives(this.getNormalChestAttributes());
		if (isMagic){
			attributes = this.getAdjectives();
			attributes.add("magic");
			this.setAdjectives(attributes);
		}
		this.setAttributes(level);
		setRandomPrice();
		
	}
	
	public ArrayList<String> getNormalChestAttributes(){
		attributes.add("normal");
		attributes.add("average");
		return attributes;
	}
}
