package characters.active;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import net.slashie.libjcsi.wswing.WSwingConsoleInterface;
import net.slashie.util.Pair;
import net.slashie.util.Util;
import map.Map;
import map.Room;
import characters.Character;
import characters.active.enemies.FollowingMove;
import characters.active.enemies.FollowingMoveDumb;
import characters.active.enemies.Movement;
import characters.active.enemies.RandomMove;
import grammars.grammars.GrammarIndividual;
import grammars.grammars.GrammarSelectorNP;
import grammars.grammars.GrammarSelectorS;
import grammars.grammars.GrammarsGeneral;
import grammars.grammars.PrintableObject;
import grammars.parsing.JSONParsing;
import util.RandUtil;
import util.Tuple;
import magic.ConfuseRay;
import magic.FireRing;
import magic.Fireball;
import magic.Spell;
import items.consumables.Consumable;
import items.wereables.WereableWeapon;
import items.wereables.Axe;
import items.wereables.LongSword;
import items.wereables.NormalArmor;
import items.wereables.NormalGloves;
import items.wereables.NormalHelmet;
import items.wereables.NormalPants;
import items.wereables.ShortSword;
import items.wereables.SmallShield;
import items.wereables.WereableArmor;
import items.Item;
import items.ItemEnumerate;
import items.ItemEnumerate.ArmorType;
import items.ItemEnumerate.WeaponType;
import main.Main;

/**
 * TODO RELEVANT:
 * - Change the attack damage depending on the weapon, defense o the character, etc.
 * 
 * TODO ADDITIONAL: 
 * - Luck depending on the used weapon.
 * - Introduce criticals, so depending on the draw we could deal more damage
 *
 */

public abstract class ActiveCharacter extends Character {
	private int maxNumberSpells = 2;
	private int damage;
	private int defense;
	private int totalLife; // TotalLife
	private int speed;
	private int life;
	private int magic;
	private int totalMagic;
	private int luck;
	private int inventorySpace;
	private int actualInventorySpace;
	private int evasion;
	private int vision;
	private int limLifeLevel;
	private int limManaLevel;
	private Movement movementType;
	private boolean isDead;
	private boolean isFirstTimeDead;
	private boolean hasAttackedHeroe;
	private int maximumItemsInventory;
	private ArrayList<WereableWeapon> weaponsEquipped;
	private ArrayList<WereableArmor> armorsEquipped;
	private ArrayList<Tuple<Integer, Integer>> visiblePositions = new ArrayList<Tuple<Integer, Integer>>();
	private ArrayList<Spell> spells = new ArrayList<Spell>();
	private int tirenessTotal = 0;
	private int tirenessCurrent = 0;
	private int level = 1;
	private int experience = 0;
	private int nextLevelExperience = 0;
	private int experienceGiven = 0;
	private int confusionTurns = 0;
	private boolean hasBeenAttackedByHeroe = false;
	private int money = 0;

	public ActiveCharacter(String name, String description,
			Map map, Room room, Tuple<Integer, Integer> position, int damage,
			int defense, int speed, int life, int luck, int weight, int length, Mood mood,
			ArrayList<WereableWeapon> weaponsEquipped,
			ArrayList<WereableArmor> armorsEquipped, int inventorySpace, int carryWeight,
			int actualCarryWeight, ArrayList<Item> inventory, int actualInventorySpace, int evasion,
			int totalLife, int magic, int totalMagic, String symbolRepresentation, int vision, Movement movementType,
			ArrayList<String> adjectives, int level, int money) {
		super(name, description, map, room, position, weight, length, carryWeight, actualCarryWeight, 
				inventory, symbolRepresentation, mood, adjectives);
		if (("CONFUSED").equals(this.getMood().toString()))
			this.setConfusionTurns(RandUtil.RandomNumber(1, 4));
		this.damage = damage;
		this.totalMagic = totalMagic;
		this.magic = magic;
		this.defense = defense;
		this.life = life;
		this.limLifeLevel = life;
		this.limManaLevel = magic;
		this.luck = luck; // number between 0 and 100
		this.weaponsEquipped = weaponsEquipped;
		this.armorsEquipped = armorsEquipped;
		this.inventorySpace = inventorySpace;
		this.actualInventorySpace = actualInventorySpace;
		this.evasion = evasion; // number between 0 and 100
		this.totalLife = totalLife;
		this.vision = vision;
		this.isDead = false;
		this.isFirstTimeDead = true;
		this.movementType = movementType;
		this.spells = new ArrayList<Spell>();
		this.maximumItemsInventory = 6;
		this.level = level;
		//every 5 units of weight subtracts 1 unit of speed
		//minimum speed = 2
		this.speed = speed;
		this.money = money;
	}
	
	public void setRandomSpells() {
		int randNum = RandUtil.RandomNumber(0, 9);
		switch(randNum) {
		case 0:
			this.addSpell(new FireRing());
			this.addSpell(new Fireball());
			break;
		case 1:
		case 2:
			this.addSpell(new FireRing());
			break;
		case 3:
			this.addSpell(new ConfuseRay());
			break;
		case 4:
			this.addSpell(new Fireball());
			this.addSpell(new ConfuseRay());
			break;
		case 5:
			this.addSpell(new Fireball());
			this.addSpell(new ConfuseRay());
			this.addSpell(new FireRing());
			break;
		default:
			this.addSpell(new Fireball());
			break;
		}
	}
	
	public void setSpeedWeight() {
		int weight = this.getActualCarryWeight();
		int res = this.getSpeed();
		System.out.println("PESO ACTUAL: " + this.getActualCarryWeight());
		
		while (weight - 5 > 0) {
			res--;
			weight -= 5;
		}
		
		if (res < 2)
			res = 2;
		
		this.setSpeed(res);
	}
	
	public void setVisiblePositions(){
		this.visiblePositions = new ArrayList<Tuple<Integer, Integer>>();
		Tuple<Integer, Integer> position = this.getPosition();
		int minMap_x = position.x - this.getVision(); 
		if (minMap_x < this.getMap().global_init().x) minMap_x = this.getMap().global_init().x;
		
		int minMap_y = position.y - this.getVision(); 
		if (minMap_y < this.getMap().global_init().y) minMap_y = this.getMap().global_init().y;
		
		int maxMap_x = position.x + this.getVision(); 
		if (maxMap_x > this.getMap().global_fin().x) maxMap_x = this.getMap().global_fin().x;
		
		int maxMap_y = position.y + this.getVision();
		if (maxMap_y > this.getMap().global_fin().y) maxMap_y = this.getMap().global_fin().y;

		for (int i = minMap_x; i <= maxMap_x; i++){
			for (int j = minMap_y; j <= maxMap_y; j++){
				this.visiblePositions.add(new Tuple<Integer, Integer>(i, j));
			}
		}
	}
	
	public boolean itemContainInventory(Item item) {
		for (Item i : this.getInventory()) {
			if (i.getName().equals(item.getName()))
				return true;
		}
		
		return false;
	}
	
	public boolean itemContainEquipment(Item item) {
		for (WereableArmor i : this.getArmorsEquipped()) {
			if (i.getName().equals(item.getName()))
				return true;
		}
		
		return false;
	}
	
	public void putRandomItemInventory() {
		//enemies can drop stuff if they are killed
		int itemRandom = RandUtil.RandomNumber(0, 12);
		int itemLevel = RandUtil.RandomNumber(this.getLevel(), this.getLevel() + 3);
		boolean isMagic = RandUtil.RandomNumber(0, 2) > 0 ? true : false; 
		Item item = null;
		switch(itemRandom) {
			case 0:
				item = new LongSword(this, null, null, null, itemLevel, isMagic, 0);
			break;
			case 1:
				item = new ShortSword(this, null, null, null, itemLevel, isMagic, 0);
			break;
			case 2:
				item = new NormalArmor(this, null, null, null, itemLevel, isMagic, 0);
			break;
			case 3:
				item = new NormalGloves(this, null, null, null, itemLevel, isMagic, 0);
			break;
			case 4:
				item = new NormalHelmet(this, null, null, null, itemLevel, isMagic, 0);
			break;
			case 5:
				item = new SmallShield(this, null, null, null, itemLevel, isMagic, 0);
			break;
			case 6:
				item = new NormalPants(this, null, null, null, itemLevel, isMagic, 0);
			break;
		}
		
		if (item != null && !this.itemContainInventory(item)) {
			item.setRandomPrice();
			item.setSellPriceInit();
			this.getInventory().add(item);
		}
	}
	
	public ArrayList<Tuple<Integer, Integer>> getImmediateReachablePositions() {
		ArrayList<Tuple<Integer, Integer>> allWalkablePositions = new ArrayList<Tuple<Integer, Integer>>();
		ArrayList<Tuple<Integer, Integer>> walkablePositions = new ArrayList<Tuple<Integer, Integer>>();
		allWalkablePositions.add(new Tuple<Integer, Integer>(this.getPosition().x - 1, this.getPosition().y));
		allWalkablePositions.add(new Tuple<Integer, Integer>(this.getPosition().x + 1, this.getPosition().y));
		allWalkablePositions.add(new Tuple<Integer, Integer>(this.getPosition().x, this.getPosition().y - 1));
		allWalkablePositions.add(new Tuple<Integer, Integer>(this.getPosition().x, this.getPosition().y + 1));
		for (Tuple<Integer, Integer> pos : allWalkablePositions) {
			if (this.canMove(pos)) {
				walkablePositions.add(pos);
			}
		}
		return walkablePositions;
	}
	
	public Movement getMovementTypeFromMood() {
		//gives to enemies different behaviors depending on their mood
		Movement move;
		switch(this.getMood()) {
		case SLEEPY:
		case TERRIFIED:
			move = new RandomMove();
			break;
		case ANGRY:
		case ENCOURAGED:
			move = new FollowingMove();
			break;
		default:
			move = new FollowingMoveDumb();
			break;
		}
		return move;
	}

	public Tuple<Integer,Integer> getDamageLuckMood(ActiveCharacter character, int damage) {
		int luck = character.getLuck();
		Mood actual = this.getMood();
		if (actual.name().equals(Mood.ANGRY.name())) {
			damage += (int)(Math.ceil(damage*0.2));
			luck -= (int)(Math.ceil(luck*0.25));
		} else if (actual.name().equals(Mood.TERRIFIED.name())) {
			damage -= (int)(Math.ceil(damage*0.25));
			luck -= (int)(Math.ceil(luck*0.3));
		} else if (actual.name().equals(Mood.CONFUSED.name())) {
			damage += (int)(Math.ceil(damage*0.2));
			luck -= (int)(Math.ceil(luck*0.2));
		} else if (actual.name().equals(Mood.SLEEPY.name())) {
			damage -= (int)(Math.ceil(damage*0.1));
			luck -= (int)(Math.ceil(luck*0.35));
		} else if (actual.name().equals(Mood.ENCOURAGED.name())) {
			damage += (int)(Math.ceil(damage*0.1));
			luck += (int)(Math.ceil(luck*0.1));
		}	
		if (luck > 100)
			luck = 100;
		else if (luck < 0)
			luck = 5;
		return new Tuple<Integer,Integer> (luck, damage);
	}
	
	public int getDefenseMood(ActiveCharacter character) {
		int defense = character.getDefense();
		Mood actual = this.getMood();
		if (actual.name().equals(Mood.ANGRY.name())) {
			return -(defense - (int)(Math.ceil(defense*0.1)));
		} else if (actual.name().equals(Mood.TERRIFIED.name())) {
			return -(defense - (int)(Math.ceil(defense*0.25)));
		} else if (actual.name().equals(Mood.SLEEPY.name())) {
			return -(defense - (int)(Math.ceil(defense*0.2)));
		} else if (actual.name().equals(Mood.ENCOURAGED.name())) {
			return defense + (int)(Math.ceil(defense*0.1));
		}	
		return 0;
	}
	
	public int getAttackFromWeapons(ActiveCharacter character){
		int damage = 0;
		for (WereableWeapon w: character.getWeaponsEquipped()){
			if (w.getDurability() > 0){
				Tuple<Integer,Integer> statsMood = getDamageLuckMood(character, w.getAttack());
				int randNumber = RandUtil.RandomNumber(0, 100);
				if (statsMood.x <= randNumber){
					w.setDurability(w.getDurability() - w.getErosion());
				}
				damage += statsMood.y;
			}
		}
		return damage;
	}

	public int getDefenseFromArmor(ActiveCharacter character){
		int defense = 0;
		for (WereableArmor w: character.getArmorsEquipped()){
			if (w.getDurability() > 0){
				int randNumber = RandUtil.RandomNumber(0, 100);
				if (character.getLuck() <= randNumber){
					w.setDurability(w.getDurability() - w.getErosion());
				}
				defense += w.getDefense();
			}
		}
		return defense;
	}
	
	public int getDefenseFromShields(ActiveCharacter character){
		int defense = 0;
		for (WereableWeapon w: character.getWeaponsEquipped()){
			if (w.getDurability() > 0){
				int randNumber = RandUtil.RandomNumber(0, 100);
				if (character.getLuck() <= randNumber){
					w.setDurability(w.getDurability() - w.getErosion());
				}
				defense += w.getDefense();
			}
		}
		return defense;
	}

	public int getFullAttackNumbers(ActiveCharacter attacker, ActiveCharacter defender){
		int randNumber = RandUtil.RandomNumber(0, 100);
		int damage = 0;
		System.out.println("STARTING ATTACK: defender evasion -> " + defender.evasion + " vs " + randNumber);
		System.out.println("attacker luck -> " + attacker.getLuck() + " vs " + randNumber);
		if (attacker.equals(defender)) {
			System.out.println("SELFATTACK");
			System.out.println("MAX DAMAGE: " + this.getAttackFromWeapons(attacker));
			System.out.println("DEFENSE MOOD: " + this.getDefenseMood(defender));
			damage = this.getAttackFromWeapons(attacker) - this.getDefenseMood(defender);
		} else if (attacker.getLuck() >= randNumber && defender.evasion <= randNumber){
				System.out.println("ATTACKING ENEMY");
				System.out.println("MAX DAMAGE: " + this.getAttackFromWeapons(attacker));
				System.out.println("DEFENSE ARMOR: " + this.getDefenseFromArmor(defender));
				System.out.println("DEFENSE SHIELD: " + this.getDefenseFromShields(defender));
				System.out.println("DEFENSE MOOD: " + this.getDefenseMood(defender));
				damage = this.getAttackFromWeapons(attacker) - this.getDefenseFromArmor(defender)
						- this.getDefenseFromShields(defender) - this.getDefenseMood(defender);
				if (damage < 1)
					damage = 1;
		} else {
			if (Main.isSoundActivated)
				Main.avoidSound.play();
		}
		if (damage > 0){
			return damage;
		}
		return 0;
	}
	
	public void dropAllItems(){
		for(Item item : this.getInventory()){
			if (Main.debug){
				System.out.println("Im dropping: " + item.getName());
			}
			item.setAttributesFromCharacter(this);
			this.getMap().putItemRoom(item);
		}
		this.setInventory(new ArrayList<Item>());
	}
	
	public abstract void setCharacterDead();

	//tuple of 2 booleans, the first one returns true if the user has inflicted damage on the foe
	//and the second value returns true if the user has damaged itself
	private Tuple<Boolean, Boolean> attack(ActiveCharacter defender){
		if (this.getMood().name().equals(Mood.CONFUSED.name())) {
			int randLuck = Util.rand(0, 100);
			int luck = this.getLuck();
			luck -= (int)(Math.ceil(luck*0.2));
			System.out.println("Self attack? luck: " + luck + "rand: " + randLuck);
			if (luck <= randLuck) {
				int selfDamage = (int)(Math.ceil(this.getFullAttackNumbers(this, this)*0.5));
				int attackerLife = this.getLife() - selfDamage;
				attackerLife = attackerLife < 0 ? 0 : attackerLife;
				this.setLife(attackerLife);
				this.setCharacterDead();
				System.out.println("selfDamage:" + selfDamage);
				return new Tuple<Boolean, Boolean> (false, true);
			}
		}
		int damageDone = this.getFullAttackNumbers(this, defender);
		System.out.println("Attack Done: " + damageDone);
		if (Main.debug){
			System.out.println("Attack Done: " + damageDone);
		}
		if (damageDone <= 0) return new Tuple<Boolean, Boolean> (false, false);
		int defenderLife = defender.getLife() - damageDone;
		defenderLife = defenderLife < 0 ? 0 : defenderLife;
		defender.setLife(defenderLife);
		defender.setCharacterDead();
		return new Tuple<Boolean, Boolean> (true, false);
	}
	
	public Pair<Tuple<Boolean, Boolean>, ActiveCharacter> weaponAttack() {
		Map map = this.getMap();
		ActiveCharacter monster = map.getMonstersPosition(this).get(0);
		for (int i = 0; i < map.getMonstersPosition(this).size(); i++) {
			monster = map.getMonstersPosition(this).get(i);
			if (!monster.isDead()) {
				monster = map.getMonstersPosition(this).get(i);
				break;
			}
		}
		Pair<Tuple<Boolean, Boolean>, ActiveCharacter> returnValue = new Pair<Tuple<Boolean,Boolean>, ActiveCharacter>(this.attack(monster), monster);
		return returnValue;
	}
	
	private void attackWithSpell(ActiveCharacter defender, Spell spell) {
		if (Main.debug){
			System.out.println("Spell attack Done: " + spell.getDamage());
		}
		int defenderLife = defender.getLife() - (this.getDamageLuckMood(this, spell.getDamage() + this.getDamage()).y - getDefenseMood(defender));
		defenderLife = defenderLife < 0 ? 0 : defenderLife;
		defender.setLife(defenderLife);
		defender.setCharacterDead();
	}
	
	public Tuple<Boolean, ArrayList<ActiveCharacter>> attackSpell(int itemNumber, ActiveCharacter user) {
		//if selfhurt, the boolean value of the tuple returns true
		ArrayList<ActiveCharacter> hurtCharacters = new ArrayList<ActiveCharacter>();
		if (this.getMood().name().equals(Mood.CONFUSED.name())) {
			Spell spell = this.getSpells().get(itemNumber);
			int randLuck = Util.rand(0, 100);
			int luck = this.getLuck();
			luck -= (int)(Math.ceil(luck*1));
			System.out.println("Self attack? luck: " + luck + "rand: " + randLuck);
			if (luck <= randLuck && this.generateSpell(spell)) {
				int selfDamage = (int)(Math.ceil(this.getDamageLuckMood(this, spell.getDamage() + this.getDamage()).y*0.5));
				int attackerLife = this.getLife() - selfDamage;
				attackerLife = attackerLife < 0 ? 0 : attackerLife;
				this.setLife(attackerLife);
				this.setCharacterDead();
				System.out.println("selfDamage:" + selfDamage);
				hurtCharacters.add(this);
				return new Tuple<Boolean, ArrayList<ActiveCharacter>> (true, hurtCharacters);
			}
		}
		if (this.getSpells().size() > itemNumber) {
			Spell spell = this.getSpells().get(itemNumber);
			Room room = this.getRoom();
			if (this.generateSpell(spell)) {
				ArrayList<Tuple<Integer, Integer>> spellDamagedPositions = spell.getDamagedPositions(this);
				ArrayList<Tuple<Integer, Integer>> charactersPositions = room.getPositionsOfMonsters();
				charactersPositions.add(user.getPosition());
				if (spellDamagedPositions.size() > 0) {
					for (Tuple<Integer, Integer> pos : spellDamagedPositions) {
						if (RandUtil.sameTuple(pos, user.getPosition()) && this != user) {
							hurtCharacters.add(user);
							this.attackWithSpell(user, spell);
						} else {
							if (Main.isSoundActivated)
								Main.avoidSound.play();
						}
						if (RandUtil.containsTuple(pos, charactersPositions)) {
							for (ActiveCharacter monsterDamaged : room.getMonstersPosition(pos)) {
								hurtCharacters.add(monsterDamaged);
								this.attackWithSpell(monsterDamaged, spell);
							}
						} else {
							if (Main.isSoundActivated)
								Main.avoidSound.play();
						}
					}
				}
			}
		}
		return new Tuple<Boolean, ArrayList<ActiveCharacter>> (false, hurtCharacters);
	}
	
	private boolean generateSpell(Spell spell) {
		if (this.getMagic() < spell.getManaCost()) {
			if (Main.debug){
				System.out.println("Not enough mana: " + this.getMagic() + " for the spell: " + spell.getManaCost());
			}
			return false;
		}
		this.setMagic(this.getMagic() - spell.getManaCost());
		return true;
	}
	
	public ArrayList<ItemEnumerate.WeaponType> getFreeWeaponSlots(){
		
		ArrayList<ItemEnumerate.WeaponType> availableSlots = new ArrayList<ItemEnumerate.WeaponType>();
		
		for (ItemEnumerate.WeaponType weaponType : ItemEnumerate.WeaponType.values()){
			availableSlots.add(weaponType);
		}
		
		for (WereableWeapon w: weaponsEquipped){
			for (WeaponType a: w.getWeaponType()){
				availableSlots.remove(a);
			}
		}
		return availableSlots;
	}
	
	public ArrayList<ItemEnumerate.ArmorType> getFreeArmorSlots(){
		ArrayList<ItemEnumerate.ArmorType> availableSlots = new ArrayList<ItemEnumerate.ArmorType>();
		
		for (ItemEnumerate.ArmorType armorType : ItemEnumerate.ArmorType.values()){
			availableSlots.add(armorType);
		}
		for (WereableArmor a: this.getArmorsEquipped()){
			for (ArmorType i: a.getArmorType()){
				availableSlots.remove(i);
			}
		}
		
		return availableSlots;
	}
	
	public Item getWearHelmet() {
		for (WereableArmor armorEquiped : this.getArmorsEquipped()) {
			if (armorEquiped.getArmorType().get(0).equals(ItemEnumerate.ArmorType.HEAD)){
				return armorEquiped;
			}
		}
		return null;
	}
	
	public Item getWearChest() {
		for (WereableArmor armorEquiped : this.getArmorsEquipped()) {
			if (armorEquiped.getArmorType().get(0).equals(ItemEnumerate.ArmorType.CHEST)){
				return armorEquiped;
			}
		}
		return null;
	}
	
	public Item getWearPants() {
		for (WereableArmor armorEquiped : this.getArmorsEquipped()) {
			if (armorEquiped.getArmorType().get(0).equals(ItemEnumerate.ArmorType.PANTS)){
				return armorEquiped;
			}
		}
		return null;
	}
	
	public Item getWearGloves() {
		for (WereableArmor armorEquiped : this.getArmorsEquipped()) {
			if (armorEquiped.getArmorType().get(0).equals(ItemEnumerate.ArmorType.HANDS)){
				return armorEquiped;
			}
		}
		return null;
	}
	
	public ArrayList<Item> getWearHandsDefense() {
		ArrayList<Item> handsWereable = new ArrayList<Item>(); 
		for (WereableArmor armorEquiped : this.getArmorsEquipped()) {
			if (armorEquiped.getArmorType().get(0).equals(ItemEnumerate.ArmorType.HANDS)){
				handsWereable.add(armorEquiped);
			}
		}
		return handsWereable;
	}
	
	public ArrayList<Item> getWearHandsAttack() {
		ArrayList<Item> handsWereable = new ArrayList<Item>(); 
		for (WereableWeapon weaponEquiped : this.getWeaponsEquipped()) {
			if (weaponEquiped.getWeaponType().get(0).equals(ItemEnumerate.WeaponType.LEFTHAND) ||
					weaponEquiped.getWeaponType().get(0).equals(ItemEnumerate.WeaponType.RIGHTHAND)){
				handsWereable.add(weaponEquiped);
			}
		}
		return handsWereable;
	}
	
	public boolean equipWeapon(WereableWeapon weapon){
		ArrayList<WeaponType> freeSlots = new ArrayList<WeaponType>(this.getFreeWeaponSlots());
		ArrayList<WeaponType> weaponType = weapon.getWeaponType();
		if (freeSlots.containsAll(weaponType) || (weapon.getIsSingleHand() && !freeSlots.isEmpty())){
			if (this.getInventory().contains(weapon)){
				if (weapon.getIsSingleHand()){
					ArrayList<WeaponType> type = new ArrayList<WeaponType>();
					if (freeSlots.size() > 0) {
						type.add(freeSlots.get(0));
						weapon.setWeaponType(type);
					}
				}
				this.getWeaponsEquipped().add(weapon);
				weapon.setCharacter(this);
				this.getInventory().remove(weapon);
				return true;
			}
		}
		return false;
	}
	
	public boolean equipArmor(WereableArmor armor){
		ArrayList<ArmorType> freeSlots = new ArrayList<ArmorType>(this.getFreeArmorSlots());
		ArrayList<ArmorType> armorType = armor.getArmorType();
		if (freeSlots.containsAll(armorType)){
			if (this.getInventory().contains(armor)){
				this.armorsEquipped.add(armor);
				armor.setCharacter(this);
				this.getInventory().remove(armor);
				return true;
			}
		}
		return false;
	}
	
	public void printInventory(ArrayList<Item> inventory, WSwingConsoleInterface j, int initPos_i, int initPos_j){
		for (int i = 0; i < inventory.size(); i++){
			String name = i + 1 + " - " + inventory.get(i).getName();
			j.print(initPos_j, initPos_i + i, name);
		}
	}
	
	public void _printInventory(WSwingConsoleInterface j, JsonObject rootObjGrammar, JsonObject rootObjWords){
		JsonObject rootObjNames = null;
		rootObjNames = JSONParsing.getElement(rootObjGrammar, "GENERAL").getAsJsonObject();
		
		for (int i = 0; i < this.getInventory().size(); i++){
			if (this.getInventory().get(i).getPrintableSentence().length() <= 0) {
				GrammarsGeneral grammarGeneral = new GrammarsGeneral(rootObjNames);
				GrammarSelectorNP grammarIndividual = new GrammarSelectorNP(grammarGeneral.getRandomGrammar(), rootObjWords, this.getInventory().get(i), "GENERAL");
				this.getInventory().get(i).setPrintableSentence(grammarIndividual.getRandomSentenceTranslated());
			}
			j.print(0, this.getMap().global_fin().x + 1 + i, i + 1 + " - " + this.getInventory().get(i).getPrintableSentence());
		}
	}
	
	public void _printLife(JsonObject rootObjWords, WSwingConsoleInterface j, int initPos_i, int initPos_j){
		String translation = JSONParsing.getTranslationWord("life", "N", rootObjWords);
		String life = translation + ": " + this.getLife() + "/" + this.getTotalLife();
		j.print(initPos_j, initPos_i, life);
	}
	
	public void _printMana(JsonObject rootObjWords, WSwingConsoleInterface j, int initPos_i, int initPos_j){
		String translation = JSONParsing.getTranslationWord("mana", "N", rootObjWords);
		String magic = translation + ": " + this.getMagic() + "/" + this.getTotalMagic();
		j.print(initPos_j, initPos_i, magic);
	}
	
	public void _printSpeed(JsonObject rootObjWords, WSwingConsoleInterface j, int initPos_i, int initPos_j){
		String translation = JSONParsing.getTranslationWord("speed", "N", rootObjWords);
		String speed = translation + ": " + this.getSpeed();
		j.print(initPos_j, initPos_i, speed);
	}
	
	public void _printMood(JsonObject rootObjWords, WSwingConsoleInterface j, int initPos_i, int initPos_j){
		String translation = JSONParsing.getTranslationWord("mood", "N", rootObjWords);
		String mood = translation + ": " + JSONParsing.getTranslationWord(this.getMood().name().toLowerCase(), "ADJ", rootObjWords);
		j.print(initPos_j, initPos_i, mood);
	}
	
	public void _printName(WSwingConsoleInterface j, int initPos_i, int initPos_j){
		String name = this.getName();
		j.print(initPos_j, initPos_i, name);
	}
	
	public void printMonstersInformation(JsonObject rootObjWords, WSwingConsoleInterface j, int initPos_i, int initPos_j){
		_printName(j, initPos_j, initPos_i);
		Main.countElements++;
		_printLife(rootObjWords, j, initPos_j + 1, initPos_i + 2);
		Main.countElements++;
		_printSpeed(rootObjWords, j, initPos_j + 2, initPos_i + 2);
		Main.countElements++;
		_printMood(rootObjWords, j, initPos_j + 3, initPos_i + 2);
	}
	
	public void _printGroundObjects(WSwingConsoleInterface j, JsonObject rootObjWords){
		if (this.getRoom().getItemsPosition(this.getPosition()).size() > 0) {
			main.Main.countElements += 2;
			j.print(this.getMap().global_fin().y + 3, main.Main.countElements+2, JSONParsing.getTranslationWord("items", "N", rootObjWords) + ": ");
		}
		for (Item item : this.getRoom().getItemsPosition(this.getPosition())) {
			main.Main.countElements += 1;
			item.printItemsInformation(j, this.getMap().global_fin().y + 3, main.Main.countElements+2);
		}
	}
	
	public boolean unequipItem(Item item) {
		if (item != null && this.getInventory().size() < this.getMaximumItemsInventory()) {
			if (item instanceof WereableArmor) {
				return this.unEquipArmor((WereableArmor)item);
			} else if (item instanceof WereableWeapon) {
				return this.unEquipWeapon((WereableWeapon)item);
			}
		}
		return false;
	}
	
	/**
	 * The armor will go to the inventory if there's enough space
	 * @param armor
	 * @return
	 */
	
	public boolean unEquipArmor(WereableArmor armor){
		if ((this.getInventorySpace() >= this.getActualInventorySpace() + armor.getSpace()) && this.getArmorsEquipped().contains(armor)){
			this.getArmorsEquipped().remove(armor);
			this.getInventory().add(armor);
			this.setActualInventorySpace(this.getActualInventorySpace() + armor.getSpace());
			return true;
		}
		
		return false;
	}
	
	public boolean unEquipWeapon(WereableWeapon weapon){
		if ((this.getInventorySpace() >= this.getActualInventorySpace() + weapon.getSpace()) && this.getWeaponsEquipped().contains(weapon)){
			if (weapon.getIsSingleHand()){
				weapon.setWeaponType(new ArrayList<ItemEnumerate.WeaponType>());
			}
			this.getWeaponsEquipped().remove(weapon);
			this.getInventory().add(weapon);
			this.setActualInventorySpace(this.getActualInventorySpace() + weapon.getSpace());
			return true;
		}
		
		return false;
	}
	
	public boolean throwItem(Item item){
		if (item.getCharacter().equals(this)){
			item.setCharacter(null);
			item.setMap(this.getMap());
			item.setRoom(this.getRoom());
			item.setPosition(this.getPosition());
			item.getRoom().getItemsRoom().add(item);
			if (this.getWeaponsEquipped().contains(item)){
				this.getWeaponsEquipped().remove(item);
			} else if (this.getArmorsEquipped().contains(item)){
				this.getArmorsEquipped().remove(item);
			} else if (this.getInventory().contains(item)){
				this.getInventory().remove(item);
			}
			this.setActualCarryWeight(this.getActualCarryWeight() - item.getWeight());
			this.setActualInventorySpace(this.getActualInventorySpace() - item.getSpace());
			this.setSpeedWeight();
			
			return true;
		}
		return false;
	}
	
	/**
	 * Needs its own method, since the weapon's type needs to be set to
	 * empty in case it doesn't 2 hands
	 * @return
	 */
	public boolean throwWeapon(WereableWeapon weapon){
		if (weapon.getIsSingleHand() && weapon.getCharacter().equals(this)){
			weapon.setWeaponType(new ArrayList<WeaponType>());
		}
		return this.throwItem(weapon);
		
	}
	
	public boolean putItemInventory(Item item){
		if (this.getActualCarryWeight() + item.getWeight() <= this.getWeight() && this.getInventory().size() < this.getMaximumItemsInventory()){
			if (this.getActualInventorySpace() + item.getSpace() <= this.getInventorySpace()){
				this.setActualCarryWeight(this.getActualCarryWeight() + item.getWeight());
				this.setActualInventorySpace(this.getActualInventorySpace() + item.getSpace());
				this.getInventory().add(item);
				item.setCharacter(this);
				return true;
			}
		}
		return false;
	}
	
	public Item pickItem(Tuple<Integer, Integer> pos, Room room){
		if (room.isMapPositionHere(pos) && this.getInventory().size() < this.getMaximumItemsInventory()){
			for (Item item : room.getItemsRoom()){
				if (pos.x == item.getPosition().x && pos.y == item.getPosition().y){
					if (this.putItemInventory(item)){
						this.setSpeedWeight();
						room.getItemsRoom().remove(item);
						
						return item;
					}
				}
			}
		}
		return null;
	}
	
	// TODO: Change this so we can use another inventory
	public boolean useConsumable(Consumable consumable){
		if (this.getInventory().contains(consumable)){
			if (consumable.getCharacter().equals(this)){
				consumable.consume(this);
				this.getInventory().remove(consumable);
				consumable.setCharacter(null);
				return true;
			} else {
				if (Main.debug){
					System.out.println("The item is not associated to the Character");
				}
			}
		}
		if (Main.debug){
			System.out.println("The item is not in the inventory");
		}
		return false;
	}
	
	public boolean move(Tuple<Integer, Integer> position){
		Room room = this.getMap().obtainRoomByPosition(position); 
		if (this.canMove(position)) {
			this.setPosition(position);
			this.setRoom(room);
			return true;
		}
		return false;
	}
	
	public boolean canMove(Tuple<Integer, Integer> position) {
		Room room = this.getMap().obtainRoomByPosition(position); 
		if (room != null && !RandUtil.containsTuple(position, room.getInsidecolumns()) 
				&& (room.isInside(position) || room.isADoor(position))){
			return true;
		}
		return false;
	}
	
	public boolean useItem(Item item){
		if (item.isWereableItem()){
			if (item.isWereableArmor()){
				return this.equipArmor((WereableArmor)item);
			} else {
				if (item.isWereableWeapon()){
					return this.equipWeapon((WereableWeapon)item);
				}
			}
		} else {
			if (item.isConsumableItem()){
				return this.useConsumable((Consumable)item);
			}
		}
		
		return false;
	}
	
	public Pair<Tuple<Boolean, Boolean>, String> spellAttack(ActiveCharacter user, GrammarIndividual grammarAttack, JsonObject rootObjWords, ArrayList<PrintableObject> names){
		int nspells = this.getSpells().size();
		System.out.println("nspells: " + nspells);
		if (nspells > 0) {
			int getSpell;
			if (nspells == 1)
				getSpell = 0;
			else
				getSpell = RandUtil.RandomNumber(0, nspells-1);
			Spell spell = this.getSpells().get(getSpell);
			if (RandUtil.containsTuple(user.getPosition(), spell.getDamagedPositions(this))
					&& spell.getManaCost() <= this.getMagic()) {
				ArrayList<String> prep = new ArrayList<>();
				prep.add("against");
				user.setPrepositions(prep);
				names.add(spell);
				names.add(user);
				
				GrammarSelectorS selector = null;
				try {
					selector = new GrammarSelectorS(grammarAttack, rootObjWords, names, "SPELLS", "SPELLS");
				} catch (JsonIOException | JsonSyntaxException | FileNotFoundException | InstantiationException
						| IllegalAccessException e) {
					e.printStackTrace();
				}
				boolean hasWorked = false;
				Tuple<Boolean, ArrayList<ActiveCharacter>> spellAt = this.attackSpell(getSpell, user);
				if (spellAt.y.size() > 0) hasWorked = true;
				if (hasWorked && spell.checkEffect(user)) {
					
				}
				String message = selector.getRandomSentence();
				if (spell.isHasBeenUsed() && RandUtil.RandomNumber(0, 2) == 1) {
					message += ", " + JSONParsing.getRandomWord("OTHERS", "again", rootObjWords);
				} else {
					spell.setHasBeenUsed(true);
				}
				Pair<Tuple<Boolean, Boolean>, String> returnValue = new Pair<Tuple<Boolean, Boolean>, String>(new Tuple<Boolean,Boolean>(hasWorked, spellAt.x), message);
				return returnValue;
			}
		}
		return new Pair<Tuple<Boolean, Boolean>, String>(new Tuple<Boolean, Boolean>(false,false),"");
		
	}
	
	public Pair<Tuple<Boolean, Boolean>, String> weaponAttack(ActiveCharacter user, GrammarIndividual grammarAttack, JsonObject rootObjWords, ArrayList<PrintableObject> names){
		names.add(user);
		names.add(this.getWeaponsEquipped().get(0));
		GrammarSelectorS selector = null;
		try {
			selector = new GrammarSelectorS(grammarAttack, rootObjWords, names, "ATTACK", "ATTACK");
		} catch (JsonIOException | JsonSyntaxException | FileNotFoundException | InstantiationException
				| IllegalAccessException e) {
			e.printStackTrace();
		}
		String message = selector.getRandomSentence();
		if (hasAttackedHeroe && RandUtil.RandomNumber(0, 2) == 1) {
			message += ", " + JSONParsing.getRandomWord("OTHERS", "again", rootObjWords);
		} else {
			hasAttackedHeroe = true;
		}
		Pair<Tuple<Boolean, Boolean>, String> returnValue = new Pair<Tuple<Boolean, Boolean>, String>(this.attack(user), message);
		return returnValue;
	}
	
	public void makeMoves(ActiveCharacter user) {
		if (this.tirenessTotal <= 0 || this.tirenessCurrent != this.tirenessTotal) {
			Tuple<Integer, Integer> pos = this.movementType.moveCharacter(this, user);
			if (pos != null) {
				this.move(pos);
				this.tirenessCurrent++;
			}
		} else {
			this.tirenessCurrent = 0;
		}
	}
	
	public Pair<Pair<Tuple<Boolean, Boolean>, String>, ActiveCharacter> doTurn(ActiveCharacter user, GrammarIndividual grammarAttack, JsonObject rootObjWords){
		if (this.getRoom().equals(user.getRoom()) && !this.isDead()){
			boolean soundOn = Main.isSoundActivated;
			System.out.println("MONSTRO: " + this.getName() + " ANIMO: " + this.getMood().name());
			Pair<Tuple<Boolean, Boolean>, String> result;
			ArrayList<PrintableObject> names = new ArrayList<PrintableObject>();
			//checks monster's mood: current status, the number of turns remaining in the current status, etc
			//this.checkMood();
			boolean spellAt = false;
			boolean weaponAt = false;
			boolean move = false;
			names.add(this);
			//monster's behavior depends on its mood
			switch(this.getMood()) {
			case TERRIFIED:
				move = true;
				break;
			case ANGRY:
				weaponAt = true;
				move = true;
				break;
			default:
				spellAt = true;
				weaponAt = true;
				move = true;
				break;
			}
			System.out.println("ESTE BICHO PUEDE HACER: " + this.getName());
			System.out.println("MAGIAS: " + this.getSpells().size());
			if (this.getSpells().size() > 0 && spellAt) {
				//first boolean defines if it has dealt damage
				//the second one defines if it has hit itself
				//the ActiveCharacter variable returns the instance of the enemy if it has damaged itself bc of confusion
				result = spellAttack(user, grammarAttack, rootObjWords, names);
				System.out.println("HACE MAGIA");
				if (result.getA().y)
					//if it hits itself
					return new Pair<Pair<Tuple<Boolean, Boolean>, String>, ActiveCharacter>(result, this);
				else if (result.getA().x) {
					//if not
					if (soundOn)
						Main.heroHitSound.play();
					return new Pair<Pair<Tuple<Boolean, Boolean>, String>, ActiveCharacter>(result, null);
				}
			}
			if (this.getWeaponsEquipped().size() > 0 && RandUtil.sameTuple(this.getPosition(), user.getPosition()) && weaponAt) {
				result = weaponAttack(user, grammarAttack, rootObjWords, names);
				System.out.println("ATACA");
				if (result.getA().y)
					return new Pair<Pair<Tuple<Boolean, Boolean>, String>, ActiveCharacter>(result, this);
				else {
					if (soundOn)
						Main.heroHitSound.play();
					return new Pair<Pair<Tuple<Boolean, Boolean>, String>, ActiveCharacter>(result, null);
				} 
			} else if (move) {
				System.out.println("SE MUEVE");
				makeMoves(user);
			}
		}
		return new Pair<Pair<Tuple<Boolean, Boolean>, String>, ActiveCharacter>
		(new Pair<Tuple<Boolean, Boolean>, String>(new Tuple<Boolean, Boolean> (false, false), ""),null);	
	}
	
	public String getLifeAdjective() {
		if (this.getLife() > 75 && this.getLife() <= 100) {
			return "a lot of";
		} else if (this.getLife() > 40 && this.getLife() <= 75) {
				return "some";
		} else {
			return "a little";
		}
	}
	
	public String getManaAdjective() {
		if (this.getMagic() > 75 && this.getMagic() <= 100) {
			return "a lot of";
		} else if (this.getMagic() > 40 && this.getMagic() <= 75) {
				return "some";
		} else {
			return "a little";
		}
	}
	
	public void setNextLevelExperience() {
		int nextExperienceLevel = this.getLevel() * 150;
		this.setNextLevelExperience(nextExperienceLevel); 
	}
	
	public void setNewLevel(int newLevel) {
		this.setLevel(newLevel);
		this.setNextLevelExperience();
	}
	
	public void setLimLife(int newLimLife) {
		this.limLifeLevel = newLimLife;
	}
	
	public void setLimMana(int newLimMana) {
		this.limManaLevel = newLimMana;
	}
	
	public void setNewLimLife(int newLimLife) {
		this.setLimLife(newLimLife);
		this.setLife(newLimLife);
	}
	
	public void setNewLimMana(int newLimMana) {
		this.setLimMana(newLimMana);
		this.setMagic(newLimMana);
	}
	
	public abstract void addNewExperience(int addExperience);
	
	public void getRandomEquip() {
		WereableWeapon oneHandSword;
		//gives different weapons and armors depending on the foe's level
		int number;
		int numberArmor = RandUtil.RandomNumber(0, this.getLevel()*3);;
		if (this.getLevel() < 3) {
			number = RandUtil.RandomNumber(0, 7);
			if (number == 0) {
				oneHandSword = new LongSword(this, null, null, null, level, false, 0);
			} else
				oneHandSword = new ShortSword(this, null, null, null, level, false, 0);
		} else if (this.getLevel() < 6) {
			if (RandUtil.RandomNumber(0, 5) == 0) {
				oneHandSword = new LongSword(this, null, null, null, level, false, 0);
			} else
				oneHandSword = new ShortSword(this, null, null, null, level, false, 0);
		} else {
			int possibility = RandUtil.RandomNumber(0, 8);
			if (possibility == 0) {
				oneHandSword = new Axe(this, null, null, null, level, false, 0);
			} else if (possibility < 4)
				oneHandSword = new LongSword(this, null, null, null, level, false, 0);
			else
				oneHandSword = new ShortSword(this, null, null, null, level, false, 0);
		}
		oneHandSword.setRandomPrice();
		oneHandSword.setSellPriceInit();
		this.putItemInventory(oneHandSword);
		this.equipWeapon(oneHandSword);
		if (this.getLevel() > 3) {
			while (numberArmor > 2) {
				int randNum = 20 - this.getLevel();
				if (randNum < 7)
					randNum = 7;
				int n = RandUtil.RandomNumber(0, randNum);
				int numberMagic = RandUtil.RandomNumber(0, 6);
				boolean isMagic = (numberMagic < 2);
				WereableArmor armor = null;
				switch(n) {
				//choose armor
				case 0:
					armor = new NormalArmor(this, null, null, null, this.getLevel(), isMagic, 0);
					break;
				//choose gloves
				case 1:
				case 2:
				case 3:
					armor = new NormalGloves(this, null, null, null, this.getLevel(), isMagic, 0);
					break;
				//choose helmet
				case 4:
				case 5:
					armor = new NormalHelmet(this, null, null, null, this.getLevel(), isMagic, 0);
					break;
				//choose pants
				case 6:
					armor = new NormalPants(this, null, null, null, this.getLevel(), isMagic, 0);
					break;
				default:
					break;
				}
				
				if (armor != null && !this.itemContainEquipment(armor)) {
					armor.setRandomPrice();
					armor.setSellPriceInit();
					this.putItemInventory(armor);
					this.equipArmor(armor);
					numberArmor -= 3;
				} else
					numberArmor--;
			}
		}
	}
	
	public int getDamage() {
		return damage;
	}
	
	public void setDamage(int damage) {
		this.damage = damage;
	}
	
	public int getDefense() {
		return defense;
	}
	
	public void setDefense(int defense) {
		this.defense = defense;
	}
	
	public int getLife() {
		return life;
	}
	
	public void setLife(int life) {
		this.life = life;
	}
	
	public int getLuck() {
		return luck;
	}
	
	public void setLuck(int luck) {
		this.luck = luck;
	}
	
	public ArrayList<WereableWeapon> getWeaponsEquipped(){
		return weaponsEquipped;
	}

	public int getInventorySpace() {
		return inventorySpace;
	}

	public void setInventorySpace(int inventorySpace) {
		this.inventorySpace = inventorySpace;
	}

	public ArrayList<WereableArmor> getArmorsEquipped() {
		return armorsEquipped;
	}

	public void setArmorsEquipped(ArrayList<WereableArmor> armorsEquipped) {
		this.armorsEquipped = armorsEquipped;
	}

	public void setWeaponsEquipped(ArrayList<WereableWeapon> weaponsEquipped) {
		this.weaponsEquipped = weaponsEquipped;
	}
	
	public int getActualInventorySpace() {
		return actualInventorySpace;
	}

	public void setActualInventorySpace(int actualInventorySpace) {
		this.actualInventorySpace = actualInventorySpace;
	}

	public int getEvasion(){
		return this.evasion;
	}

	public void setEvasion(int evasion){
		this.evasion = evasion;
	}
	
	public Integer getSpeed(){
		return this.speed;
	}

	public void setSpeed(int speed){
		this.speed = speed;
	}

	public int getTotalLife() {
		return limLifeLevel;
	}

	public void setTotalLife(int totalLife) {
		this.totalLife = totalLife;
	}
	
	public int getTotalMagic() {
		return limManaLevel;
	}

	public void setTotalMagic(int totalMagic) {
		this.totalMagic = totalMagic;
	}
	
	public int getMagic() {
		return magic;
	}

	public void setMagic(int magic) {
		this.magic = magic;
	}
	
	public ArrayList<Tuple<Integer, Integer>> getVisiblePositions(){
		return this.visiblePositions;
	}

	public int getVision() {
		return vision;
	}

	public void setVision(int vision) {
		this.vision = vision;
	}

	public boolean isDead() {
		return isDead;
	}

	public void setDead(boolean isDead) {
		this.isDead = isDead;
	}

	public Movement getMovementType() {
		return movementType;
	}

	public void setMovementType(Movement movementType) {
		this.movementType = movementType;
	}

	public boolean isFirstTimeDead() {
		return isFirstTimeDead;
	}

	public void setFirstTimeDead(boolean isFirstTimeDead) {
		this.isFirstTimeDead = isFirstTimeDead;
	}
	
	public int getMaxNumberSpells() {
		return maxNumberSpells;
	}

	public void setMaxNumberSpells(int maxNumberSpells) {
		this.maxNumberSpells = maxNumberSpells;
	}

	public boolean addSpell(Spell spell) {
		if (this.getSpells().size() <= this.getMaxNumberSpells()) {
			ArrayList<Spell> newSpells = new ArrayList<Spell>();
			for (Spell oldSpell: this.getSpells()) {
				newSpells.add(oldSpell);
			}
			newSpells.add(spell);
			this.setSpells(newSpells);
			return true;
		} else {
			return false;
		}
	}
	
	public ArrayList<String> getAdjectivesIndividual() {
		ArrayList<String> adjectives = new ArrayList<String>();
		adjectives.add("good");
		return adjectives;
	}
	
	public void setAdjectivesMonster(ActiveCharacter user) {
		ArrayList<String> adjectives = this.getAdjectivesIndividual();
		if (user.getLife() >= 70 && this.getLife() <= 20) {
			adjectives.add("small");
			adjectives.add("scared");
		} else if (user.getLife() <= 30 && this.getLife() >= 50){
			adjectives.add("scary");
			adjectives.add("big");
		}
		this.setAdjectives(adjectives);
	}
	
	public void setAdjectivesUser() {
		ArrayList<String> adjectivesUser = this.getAdjectivesIndividual();
		if (this.getLife() >= 70) {
			adjectivesUser.add("big");
			adjectivesUser.add("brave");
			adjectivesUser.add("glorious");
		} else if (this.getLife() <= 30){
			adjectivesUser.add("small");
			adjectivesUser.add("scared");
		} else {
			adjectivesUser.add("average");
		}
		this.setAdjectives(adjectivesUser);
	}
	

	public ArrayList<Spell> getSpells() {
		return spells;
	}

	public void setSpells(ArrayList<Spell> spells) {
		this.spells = spells;
	}

	public int getMaximumItemsInventory() {
		return maximumItemsInventory;
	}

	public void setMaximumItemsInventory(int maximumItemsInventory) {
		this.maximumItemsInventory = maximumItemsInventory;
	}

	public void setVisiblePositions(ArrayList<Tuple<Integer, Integer>> visiblePositions) {
		this.visiblePositions = visiblePositions;
	}

	public int getTirenessTotal() {
		return tirenessTotal;
	}

	public void setTirenessTotal(int tirenessTotal) {
		this.tirenessTotal = tirenessTotal;
	}

	public int getTirenessCurrent() {
		return tirenessCurrent;
	}

	public void setTirenessCurrent(int tirenessCurrent) {
		this.tirenessCurrent = tirenessCurrent;
	}

	public int getExperienceGiven() {
		return experienceGiven;
	}

	public void setExperienceGiven(int experienceGiven) {
		this.experienceGiven = experienceGiven;
	}

	public int getExperience() {
		return experience;
	}

	public void setExperience(int experience) {
		this.experience = experience;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}
	
	public int getConfusionTurns() {
		return confusionTurns;
	}

	public void setConfusionTurns(int confusionTurns) {
		this.confusionTurns = confusionTurns;
	}

	public int getNextLevelExperience() {
		return nextLevelExperience;
	}

	public void setNextLevelExperience(int nextLevelExperience) {
		this.nextLevelExperience = nextLevelExperience;
	}
	
	public boolean isHasAttackedHeroe() {
		return hasAttackedHeroe;
	}

	public void setHasAttackedHeroe(boolean hasAttackedHeroe) {
		this.hasAttackedHeroe = hasAttackedHeroe;
	}

	public boolean isHasBeenAttackedByHeroe() {
		return hasBeenAttackedByHeroe;
	}

	public void setHasBeenAttackedByHeroe(boolean hasBeenAttackedByHeroe) {
		this.hasBeenAttackedByHeroe = hasBeenAttackedByHeroe;
	}
	
	public int getMoney() {
		return money;
	}
	
	public void setMoney(int money) {
		this.money = money;
	}

}
