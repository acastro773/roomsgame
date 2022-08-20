package items.wereables;

import items.ItemEnumerate.WeaponType;

import java.util.ArrayList;

import map.Map;
import map.Room;
import util.Tuple;
import characters.Character;

public class Axe extends WereableWeapon {
	
	ArrayList<String> attributes = new ArrayList<String>();
	
	public Axe (Character character, Map map, Room room, Tuple<Integer, Integer> position,
			int level, boolean isMagic) {
		super("axe", null, "", "m", 
				8, 5, 100, character, 
				new ArrayList<WeaponType>(),
				map, room, position, 15 + level, 0, true, 0, level, isMagic);
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
		
	}
	
	public ArrayList<String> getTwoHandSwordAttributes(){
		attributes.add("big");
		return attributes;
	}

}
