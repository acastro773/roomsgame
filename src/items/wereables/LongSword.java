package items.wereables;

import items.ItemEnumerate.WeaponType;

import java.util.ArrayList;

import map.Map;
import map.Room;
import util.RandUtil;
import util.Tuple;
import characters.Character;

public class LongSword extends WereableWeapon {
	
	ArrayList<String> attributes = new ArrayList<String>();
	
	public LongSword (Character character, Map map, Room room, Tuple<Integer, Integer> position,
			int level, boolean isMagic, int price) {
		super("sword", null, "", "m", 
				5, 5, 100, character, 
				new ArrayList<WeaponType>(),
				map, room, position, 10 + level, 0, true, 0, level, isMagic, price, 300);
		this.setAdjectives(this.getTwoHandSwordAttributes());
		ArrayList<WeaponType> weaponType = new ArrayList<WeaponType>();
		weaponType.add(WeaponType.LEFTHAND);
		weaponType.add(WeaponType.RIGHTHAND);
		this.setWeaponType(weaponType);
		if (isMagic){
			attributes = this.getAdjectives();
			attributes.add("magic");
			this.setAdjectives(attributes);
		}
		this.setAttributes(this.getLevel(), false);
		setRandomPrice();
	}
	
	public ArrayList<String> getTwoHandSwordAttributes(){
		attributes.add("long");
		return attributes;
	}
}
