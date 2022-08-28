package characters.active.playerclass;

import java.util.ArrayList;

import characters.Character.Mood;
import characters.active.ActiveCharacter;
import grammars.grammars.GrammarIndividual;
import grammars.grammars.PrintableObject;
import items.Item;
import items.wereables.WereableArmor;
import items.wereables.WereableWeapon;
import magic.ConfuseRay;
import magic.FireRing;
import magic.Fireball;
import main.Main;
import map.Map;
import map.Room;
import util.RandUtil;
import util.Tuple;

public class Mage extends ActiveCharacter {

	public Mage(Map map, Room room, Tuple<Integer, Integer> position, ArrayList<String> adjectives, int level, int damage, int defense,
			int speed, int life, int luck, int weight, int evasion, int magic) {
		super("mage", "", map, room, position, 
				damage, defense, speed, life, luck, weight, 100, Mood.NEUTRAL, new ArrayList<WereableWeapon>(),
				new ArrayList<WereableArmor>(), 100, 100, 0,
				new ArrayList<Item>(), 0, evasion, 100, magic, 100, "@", 4, null, adjectives, level, 0);	
	}
	@Override
	public void addNewExperience(int addExperience) {
		if (this.getExperience() + addExperience >= this.getNextLevelExperience()) {
			int experienceToNextLevel = this.getNextLevelExperience() - this.getExperience();
			this.setExperience(addExperience - experienceToNextLevel);
			this.setSpeed(this.getSpeed()+3);
			this.setDamage(this.getDamage()+2);
			this.setDefense(this.getDefense()+1);
			this.setNewLevel(this.getLevel() + 1);
			this.setNewLimLife(this.getTotalLife() + 5);
			this.setNewLimMana(this.getTotalMagic() + 60);
			if (this.getLevel() == 4) {
				FireRing fireRing = new FireRing();
				this.addSpell(fireRing);
				GrammarIndividual grammarIndividual = Main.grammarGeneralDescription.getRandomGrammar();
				ArrayList<PrintableObject> names = new ArrayList<PrintableObject>();
				names.add(this);
				names.add(fireRing);
				String message = main.Main._getMessage(grammarIndividual, names, "LEARN", "LEARN", false, false, false);
				Main.printMessage(message);
			} else if (this.getLevel() == 7) {
				ConfuseRay confuseRay = new ConfuseRay();
				this.addSpell(confuseRay);
				GrammarIndividual grammarIndividual = Main.grammarGeneralDescription.getRandomGrammar();
				ArrayList<PrintableObject> names = new ArrayList<PrintableObject>();
				names.add(this);
				names.add(confuseRay);
				String message = main.Main._getMessage(grammarIndividual, names, "LEARN", "LEARN", false, false, false);
				Main.printMessage(message);
			}
		} else {
			this.setExperience(this.getExperience() + addExperience);
		}
	}
	@Override
	public void setCharacterDead() {
		if (this.getLife() <= 0){
			this.setDead(true);
			this.dropAllItems();
			this.getRoom().removeTurnDead(this);
		}
		
	}
}

