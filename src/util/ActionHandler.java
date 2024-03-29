package util;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.gson.JsonObject;

import characters.active.ActiveCharacter;
import grammars.grammars.GrammarIndividual;
import grammars.grammars.GrammarsGeneral;
import grammars.grammars.PrintableObject;
import grammars.grammars.WordsGrammar;
import grammars.parsing.JSONParsing;
import items.Item;
import main.Main;
import net.slashie.util.Pair;

public class ActionHandler {
	
	private HashMap<String, Integer> keysMap;
	private GrammarsGeneral grammarUseItem;
	private GrammarsGeneral grammarPickItem;
	private GrammarsGeneral grammarMissDescription;
	private GrammarsGeneral grammarSelfharmDescription;
	private GrammarsGeneral grammarAdjectiveDescription;
	private GrammarsGeneral grammarGeneralDescription;
	private GrammarsGeneral grammarAttack;
	private GrammarsGeneral grammarDescribeEnvironment;
	private GrammarsGeneral grammarDescribeCharacterWears;
	private GrammarsGeneral grammarDescribeEnvironmentSimple;
	private GrammarsGeneral grammarDescribePersonal;
	private GrammarsGeneral grammarDescribeItem;
	private GrammarsGeneral grammarDescribeSimple;
	private JsonObject rootObjWords;
	private ActiveCharacter user;
	
	public ActionHandler(HashMap<String, Integer> keysMap, ActiveCharacter user, JsonObject rootObj, JsonObject rootObjWords) {
		//gets the all the different sentence structures to satisfy the description we want to give
		grammarAttack = new GrammarsGeneral(JSONParsing.getElement(rootObj, "ATTACK").getAsJsonObject());
		grammarPickItem = new GrammarsGeneral(JSONParsing.getElement(rootObj, "PICK").getAsJsonObject());
		grammarUseItem = new GrammarsGeneral(JSONParsing.getElement(rootObj, "USE").getAsJsonObject());
		grammarDescribeItem = new GrammarsGeneral(JSONParsing.getElement(rootObj, "DESCITEM").getAsJsonObject());
		grammarDescribeSimple = new GrammarsGeneral(JSONParsing.getElement(rootObj, "DESCSIMPLE").getAsJsonObject());
		grammarDescribePersonal = new GrammarsGeneral(JSONParsing.getElement(rootObj, "DESCPERSONAL").getAsJsonObject());
		grammarDescribeEnvironment = new GrammarsGeneral(JSONParsing.getElement(rootObj, "DESCENV").getAsJsonObject());
		grammarDescribeEnvironmentSimple = new GrammarsGeneral(JSONParsing.getElement(rootObj, "DESCENVSIMPLE").getAsJsonObject());
		grammarDescribeCharacterWears = new GrammarsGeneral(JSONParsing.getElement(rootObj, "DESCCHAWEARS").getAsJsonObject());
		grammarAdjectiveDescription = new GrammarsGeneral(JSONParsing.getElement(rootObj, "DESCRIPTIONADJECTIVE").getAsJsonObject());
		grammarMissDescription = new GrammarsGeneral(JSONParsing.getElement(rootObj, "ATTACKMISS").getAsJsonObject());
		grammarSelfharmDescription = new GrammarsGeneral(JSONParsing.getElement(rootObj, "SELFHARM").getAsJsonObject());
		grammarGeneralDescription = new GrammarsGeneral(JSONParsing.getElement(rootObj, "GENERAL").getAsJsonObject());
		this.keysMap = keysMap;
		this.rootObjWords = rootObjWords;
		this.user = user;
	}

	public void _pickItemAction(boolean usePronoun, boolean hasPickedItem){
		Item item = user.pickItem(user.getPosition(), user.getRoom());
		if (user.getInventory().size() <= user.getMaximumItemsInventory() && item != null ) {
			ArrayList<PrintableObject> names = new ArrayList<PrintableObject>();
			//e.g.: the knight picks the potion
			//knight and potion are the nouns of the phrase
			names.add(user);
			names.add(item);
			if (hasPickedItem) {
				main.Main.useAndWithItem(item);
			} else {
				main.Main.hasPickedItem = true;
				main.Main.generatePrintMessage(names, grammarPickItem, "PICK", "PICK", usePronoun, false, false);
			}
			
			main.Main.hasChanged = false;
		} else {
			main.Main._messageUnvalid();
		}
	}
	
	public void _attackAction(boolean usePronoun){
		if (user.getWeaponsEquipped().size() <= 0) {
			PrintableObject weapons = new PrintableObject("weapons", "", null, user.getPosition());
			ArrayList<PrintableObject> names = new ArrayList<PrintableObject>();
			names.add(user);
			names.add(weapons);
			main.Main.generatePrintMessage(names, grammarGeneralDescription, "NOTHAVE", "NOTHAVE", usePronoun, false, false);
		} else {
			if (Main.isSoundActivated)
				Main.attackSound.play();
			if (user.getMap().getMonstersPosition(user).size() > 0) {
				Pair<Tuple<Boolean, Boolean>, ActiveCharacter> monster = user.weaponAttack();
				ArrayList<PrintableObject> names = new ArrayList<PrintableObject>();
				//e.g.: the knight attacks the goblin with the sword
				//knight, goblin and sword are the nouns of the phrase, we add them here and then it starts forming the phrase
				names.add(user);
				names.add(monster.getB());
				names.add(user.getWeaponsEquipped().get(0));
				GrammarIndividual grammarIndividual = grammarAttack.getRandomGrammar();
				String message = main.Main._getMessage(grammarIndividual, names, "ATTACK", "ATTACK", usePronoun, false, false);
				//obtains tuple of (didDamage, selfDamage), check values and form the phrase
				if (monster.getA().x) {
					if (monster.getB().getLife() <= 0) {
						user.addNewExperience(monster.getB().getExperienceGiven());
						monster.getB().setExperienceGiven(0);
						MessageDescriptionsUtil._messageDescriptionDead(monster.getB(), false, grammarAdjectiveDescription);
						main.Main.hasChanged = true;
					} else {
						if (monster.getB().isHasBeenAttackedByHeroe() && RandUtil.RandomNumber(0, 3) == 1) {
							message += " " + JSONParsing.getRandomWord("OTHERS", "again", rootObjWords);
						} else {
							monster.getB().setHasBeenAttackedByHeroe(true);
						}
						//it only prints the message if the enemy is alive
						main.Main.printMessage(message);
					}
				} else {
					//user too confused
					if (monster.getA().y) {
						GrammarIndividual grammarIndividualSh = grammarSelfharmDescription.getRandomGrammar();
						ArrayList<PrintableObject> namesSh = new ArrayList<PrintableObject>();
						ArrayList<String> prepositionUser = new ArrayList<String>();
						ArrayList<String> prepositionConf = new ArrayList<String>();
						PrintableObject confusion = new PrintableObject("confusion", "", null, null);
						prepositionUser.add("but");
						prepositionConf.add("in");
						user.setPrepositions(prepositionUser);
						confusion.setPrepositions(prepositionConf);
						namesSh.add(user);
						namesSh.add(user);
						namesSh.add(confusion);
						String messageMiss = ", " + main.Main._getMessage(grammarIndividualSh, namesSh, "SELFHARM", "SELFHARM", true, false, true);
						main.Main.printMessage(message + messageMiss);
					} else {
						GrammarIndividual grammarIndividualMiss = grammarMissDescription.getRandomGrammar();
						ArrayList<PrintableObject> namesMiss = new ArrayList<PrintableObject>();
						ArrayList<String> preposition = new ArrayList<String>();
						preposition.add("but");
						user.setPrepositions(preposition);
						namesMiss.add(user);
						String messageAgain = "";
						String messageMiss = ", " + main.Main._getMessage(grammarIndividualMiss, namesMiss, "MISS", "MISS", true, false, false);
						if (monster.getB().isHasBeenAttackedByHeroe() && RandUtil.RandomNumber(0, 3) == 1) {
							messageAgain += ", " + JSONParsing.getRandomWord("OTHERS", "again", rootObjWords);
						} else {
							monster.getB().setHasBeenAttackedByHeroe(true);
						}
						String[] words = messageMiss.split("\\s+");
						messageMiss = messageMiss.replaceFirst(words[1] + " ", "");
						main.Main.printMessage(message + messageAgain + messageMiss);
					}
				}
	    	}
		}
	}
	
	public void _inventoryAction(int i, boolean usePronoun){
		int itemNumber = i % keysMap.get("item1");
		if (itemNumber + 1 <= user.getInventory().size()) {
			Item item = user.getInventory().get(itemNumber);
			ArrayList<PrintableObject> names = new ArrayList<PrintableObject>();
			names.add(user);
			names.add(item);
			boolean result = user.useItem(item);
			if (item.isWereableItem()) {
				if (!result) {
					main.Main._messageUnvalid();
					main.Main.hasEquipedItem = false;
				}
				if (main.Main.hasEquipedItem) {
					main.Main.useAndWithItem(item);
				} else if (result) {
					main.Main.generatePrintMessage(names, grammarUseItem, "EQUIP", "EQUIP", usePronoun, false, false);
					main.Main.hasEquipedItem = true;
				}
			} else {
				main.Main.generatePrintMessage(names, grammarUseItem, "USE", "USE", usePronoun, false, false);
			}
		}
		main.Main.hasChanged = false;
	}
	
	public void _throwItem(int keyPressed, boolean usePronoun){
		int itemNumber = keyPressed % keysMap.get("item1");
		if (itemNumber + 1 <= user.getInventory().size()) {
			Item item = user.getInventory().get(itemNumber);
			if (user.throwItem(item)) {
				ArrayList<PrintableObject> names = new ArrayList<PrintableObject>();
				names.add(user);
				names.add(item);
				if (main.Main.hasThrownItem) {
					main.Main.useAndWithItem(item);
				} else {
					main.Main.generatePrintMessage(names, grammarPickItem, "THROW", "THROW", usePronoun, false, false);
					main.Main.hasThrownItem = true;
				}
			}
			main.Main.hasChanged = false;
		}
	}
	
	public void _spellAction(int itemNumber, boolean usePronoun){
		ArrayList<PrintableObject> names = new ArrayList<PrintableObject>();
		Tuple<Boolean, ArrayList <ActiveCharacter>> spellAt = user.attackSpell(itemNumber, user);
		if (Main.isSoundActivated)
			Main.attackSound.play();
		if (spellAt.x) {
			names.add(user);
			names.add(user.getSpells().get(itemNumber));
			GrammarIndividual grammarIndividual = grammarGeneralDescription.getRandomGrammar();
			String message = main.Main._getMessage(grammarIndividual, names, "SPELLS", "SPELLS", usePronoun, false, false);
			GrammarIndividual grammarIndividualSh = grammarSelfharmDescription.getRandomGrammar();
			ArrayList<PrintableObject> namesSh = new ArrayList<PrintableObject>();
			ArrayList<String> prepositionUser = new ArrayList<String>();
			ArrayList<String> prepositionConf = new ArrayList<String>();
			PrintableObject confusion = new PrintableObject("confusion", "", null, null);
			prepositionUser.add("but");
			prepositionConf.add("in");
			user.setPrepositions(prepositionUser);
			confusion.setPrepositions(prepositionConf);
			namesSh.add(user);
			namesSh.add(user);
			namesSh.add(confusion);
			String messageMiss = ", " + main.Main._getMessage(grammarIndividualSh, namesSh, "SELFHARM", "SELFHARM", true, false, true);
			main.Main.printMessage(message + messageMiss);
		} else if (spellAt.y.size() > 0) {
			for (ActiveCharacter monsterAffected : spellAt.y) {
				names.add(user);
				names.add(user.getSpells().get(itemNumber));
				names.add(monsterAffected);
				main.Main.generatePrintMessage(names, grammarAttack, "SPELLS", "SPELLS", usePronoun, false, false);
				if (monsterAffected.isDead()) {
					user.addNewExperience(monsterAffected.getExperienceGiven());
					monsterAffected.setExperienceGiven(0);
					MessageDescriptionsUtil._messageDescriptionDead(monsterAffected, false, grammarAdjectiveDescription);
				}
			}
		} else {
			names.add(user);
			names.add(user.getSpells().get(itemNumber));
			GrammarIndividual grammarIndividual = grammarGeneralDescription.getRandomGrammar();
			String message = main.Main._getMessage(grammarIndividual, names, "SPELLS", "SPELLS", usePronoun, false, false);
			GrammarIndividual grammarIndividualMiss = grammarMissDescription.getRandomGrammar();
			ArrayList<PrintableObject> namesMiss = new ArrayList<PrintableObject>();
			ArrayList<String> preposition = new ArrayList<String>();
			preposition.add("but");
			user.setPrepositions(preposition);
			namesMiss.add(user);
			String messageMiss = ", " + main.Main._getMessage(grammarIndividualMiss, namesMiss, "MISS", "MISS", true, false, false);
			String[] words = messageMiss.split("\\s+");
			messageMiss = messageMiss.replaceFirst(words[1] + " ", "");
			main.Main.printMessage(message + messageMiss);
		}
	}
	
	public void _descriptionAction(int i, boolean usePronoun, boolean isNumericDescription){
		if (i == keysMap.get("descInv")) {
			MessageDescriptionsUtil._messageDescriptionInventory(user, grammarDescribeItem);
		}
		if (i == keysMap.get("descStats")) {
			main.Main.printMessage(MessageDescriptionsUtil._messageDescriptionStats(user, false, isNumericDescription,
					grammarDescribePersonal, rootObjWords, user, grammarDescribeSimple));
		}
		if (i == keysMap.get("descMonster")) {
			for (ActiveCharacter monster : user.getMap().getMonstersPosition(user)) {
				String message = MessageDescriptionsUtil._messageDescriptionStats(monster, true, isNumericDescription,
						grammarDescribePersonal, rootObjWords, user, grammarDescribeEnvironmentSimple);
				main.Main.printMessage(message);
			}
		}
		if (i == keysMap.get("descEnv")) {
			MessageDescriptionsUtil._messageDescriptionEnvironment(user, isNumericDescription, 
					grammarDescribeEnvironment, grammarDescribeEnvironmentSimple);
		}
		if (i == keysMap.get("descWalkablePositions")) {
			MessageDescriptionsUtil._messageDescriptionWalkablePositions(user, rootObjWords);
		}
		if (i == keysMap.get("descWereableItems")) {
			MessageDescriptionsUtil.descriptionWereables(user, usePronoun, grammarDescribeItem);
		}
		if (i == keysMap.get("descHead")) {
			Item helmet = user.getWearHelmet();
			if (helmet != null) {
				String message = MessageDescriptionsUtil._messageDescriptionCharacterWears(user, helmet, "on", "head", usePronoun, 
						grammarDescribeCharacterWears);
				if (!message.isEmpty()) {
					main.Main.printMessage(message);
				}
			}
		}
		if (i == keysMap.get("descChest")) {
			Item chest = user.getWearChest();
			if (chest != null) {
				String message = MessageDescriptionsUtil._messageDescriptionCharacterWears(user, chest, "in", "chest", usePronoun, 
						grammarDescribeCharacterWears);
				if (!message.isEmpty()) {
					main.Main.printMessage(message);
				}
			}
		}
		if (i == keysMap.get("descPants")) {
			Item pants = user.getWearPants();
			if (pants != null) {
				String message = MessageDescriptionsUtil._messageDescriptionCharacterWears(user, pants, "on", "legs", usePronoun, 
						grammarDescribeCharacterWears);
				if (!message.isEmpty()) {
					main.Main.printMessage(message);
				}
			}
		}
		if (i == keysMap.get("descGloves")) {
			Item gloves = user.getWearGloves();
			if (gloves != null) {
				String message = MessageDescriptionsUtil._messageDescriptionCharacterWears(user, gloves, "in", "hands", usePronoun, 
						grammarDescribeCharacterWears);
				if (!message.isEmpty()) {
					main.Main.printMessage(message);
				}
			}
		}
		if (i == keysMap.get("descHands")) {
			String message = MessageDescriptionsUtil._messageDescriptionCharacterWearsHands(user, grammarDescribeCharacterWears, usePronoun);
			if (message.length() > 10) {
				message += "";
				main.Main.printMessage(message);
			}
		}
		main.Main.hasChanged = false;
	}

}
