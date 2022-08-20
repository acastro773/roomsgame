package main;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.Timer;
import javax.swing.text.DefaultCaret;

import com.google.gson.JsonArray;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import characters.Character.Mood;
import characters.active.ActiveCharacter;
import config.ChangeKeyBinding;
import grammars.grammars.GrammarIndividual;
import grammars.grammars.GrammarSelectorNP;
import grammars.grammars.GrammarSelectorS;
import grammars.grammars.GrammarsGeneral;
import grammars.grammars.PrintableObject;
import grammars.parsing.JSONParsing;
import items.Item;
import items.wereables.NormalArmor;
import items.wereables.NormalGloves;
import items.wereables.NormalHelmet;
import items.wereables.NormalPants;
import items.wereables.ShortSword;
import items.wereables.WereableArmor;
import items.wereables.WereableWeapon;
import magic.FireRing;
import map.Map;
import map.Room;
import net.slashie.libjcsi.wswing.WSwingConsoleInterface;
import net.slashie.util.Pair;
import net.slashie.util.Util;
import util.ActionHandler;
import util.JTextAreaWithListener;
import util.MessageDescriptionsUtil;
import util.RandUtil;
import util.SoundReproduction;
import util.Tuple;


public class Main {
	public static String language = new String("EN");
	public static ResourceBundle messagesWereables, keyBinding;
	public static int countElements;
	public static HashMap<String, Integer> keysMap;
	public static boolean debug = false;
	public static boolean testMode = false;
	public static boolean canUsePronoun = false;
	public static char[] usedSymbols = {'.', 'P', 'G', 'A'};
	static Tuple<Integer, Integer> initial_point = new Tuple<Integer, Integer>(0, 0);
	static Tuple<Integer, Integer> final_point = new Tuple<Integer, Integer>(35, 35);
	static ArrayList<Tuple<Integer, Integer>> portals = new ArrayList<Tuple<Integer, Integer>>(); 
	static Integer[] movementInput;
	static Integer[] inventoryInput;
	static Integer[] pickItemInput;
	static Integer[] attackInput;
	public static Integer[] spellInput;
	public static Integer[] descriptionSpellInput;
	static Integer[] descriptionInput;
	static Integer[] descriptionWereableInput;
	public static Integer[] throwItemInput;
	public static Integer[] unequipItemInput;
	static Integer[] changeNumericDescInput;
	static Integer[] changeColorsInput;
	static Integer[] rebindKeysInput;
	static Integer[] activateSoundInput;
	static Integer[] increaseFontInput;
	static Integer[] decreaseFontInput;
	static Integer[] arrayColors1 = new Integer[]{12,2,3,11,5,15,7};
	static Integer[] arrayColors2 = new Integer[]{8,4,3,11,15,6,14};
	public static Integer[][] arrayColors = {arrayColors1, arrayColors2};
	public static int selectedColor = 0;
	static Map map;
	static Tuple<Integer, Integer> pos = new Tuple<Integer, Integer>(1,1);
	static Room roomEnemy;
	static Room roomCharacter;
	static DefaultCaret caret;
	public static WSwingConsoleInterface j = new WSwingConsoleInterface("The Accessible Dungeon");
	public static ActiveCharacter user = null;
	static boolean firstTime = true;
	public static boolean hasChanged = false;
	static boolean hasMoved = false;
	static char previousPositionChar = '.';
	static char previousPositionChar2 = '.';
	static char deepnessScore = 0;
	static boolean isNumericDescription = false;
	static boolean hasUsedPortal = false;
	public static boolean hasEquipedItem = false;
	public static boolean hasThrownItem = false;
	static boolean hasUnequipedItem = false;
	public static boolean hasPickedItem = false;
	static boolean doMonstersTurn = false;
	public static boolean bindingFinished = false;
	public static boolean unequipPressed = false;
	public static boolean spellsPressed = false;
	public static boolean throwPressed = false;
	public static boolean isSoundActivated = true;
	public static boolean hasIncreasedFontSize = false;
	static JsonParser parser = new JsonParser();
	static JsonObject rootObj;
	public static JTextAreaWithListener messageLabel = new JTextAreaWithListener(j);
	static int caretPosition = 0;
	static JScrollPane jScrollPane;
	static JFrame window;
	static boolean newMatch = true;
	public static JsonObject rootObjWords;
	public static JsonObject rootObjGrammar;
	public static JsonObject rootApparitionRate;
	static ActionHandler actionHandler;
	static GrammarsGeneral grammarAttack;
	static GrammarsGeneral grammarPickItem;
	static GrammarsGeneral grammarUnvalidDescription;
	static GrammarsGeneral grammarAdjectiveDescription;
	static GrammarsGeneral grammarMissDescription;
	static GrammarsGeneral grammarGeneralDescription;
	static GrammarsGeneral grammarSimpleVerb;
	static GrammarsGeneral grammarGeneralObj;
	static GrammarsGeneral grammarSelfharmDescription;
	static SoundReproduction walkSound;
	static SoundReproduction beepSound;
	static SoundReproduction hitSwordSound;
	static SoundReproduction heroHitSound;
	static SoundReproduction deathSound;
	static SoundReproduction deathEnemySound;
	static SoundReproduction collisionSound;
	static SoundReproduction waterdropSound;
	static JsonObject rateAppar1;
	public static ArrayList<Integer> enemiesList1Rate = new ArrayList<Integer>();
	public static ArrayList<Integer> enemiesList1Danger = new ArrayList<Integer>();
	public static ArrayList<String> enemiesList1Name = new ArrayList<String>();
	static JsonObject rateAppar2;
	public static ArrayList<Integer> enemiesList2Rate = new ArrayList<Integer>();
	public static ArrayList<Integer> enemiesList2Danger = new ArrayList<Integer>();
	public static ArrayList<String> enemiesList2Name = new ArrayList<String>();
	static JsonObject rateAppar3;
	public static ArrayList<Integer> enemiesList3Rate = new ArrayList<Integer>();
	public static ArrayList<Integer> enemiesList3Danger = new ArrayList<Integer>();
	public static ArrayList<String> enemiesList3Name = new ArrayList<String>();
	static ArrayList<JsonArray> enemiesJson1 = new ArrayList<JsonArray>();
	static ArrayList<JsonArray> enemiesJson2 = new ArrayList<JsonArray>();
	static ArrayList<JsonArray> enemiesJson3 = new ArrayList<JsonArray>();
	static int countAction = 0;
	public static int possibleCry = 0;
	public static double lastTime = 0;
	public static double cooldPressKey = 50;
	public static double lastTimeEnemy = 0;
	public static double cooldTurnEnemy = 50;
	public static Pair<Boolean, ActiveCharacter> monsterTurn = new Pair<Boolean, ActiveCharacter>(false, null);
	
	
	public static boolean isInputType(Integer[] type, int key) {
		return Arrays.asList(type).contains(key);
	}
	
	public static boolean isTwoKeysInput(int key){
		if (isInputType(unequipItemInput, key) || isInputType(throwItemInput, key) || isInputType(spellInput, key)) {
			return true;
		}
		return false;
	}
	
	public static boolean usePronoun() {
		if (canUsePronoun) {
			if (RandUtil.RandomNumber(0, 2) > 0) {
				return true;
			}
		}
		return false;
	}
	
	public static void _setKeyMap() {
		ResourceBundle.clearCache();
		keyBinding = ResourceBundle.getBundle("config.keys");
		Enumeration <String> keys = keyBinding.getKeys();
		keysMap = new HashMap<String, Integer>();
		while (keys.hasMoreElements()) {
			String key = keys.nextElement();
			String value = keyBinding.getString(key);
			keysMap.put(key, Integer.parseInt(value));
		}
		_bindKeys();
	}
	
	public static void _setLanguage() {
		FileInputStream in;
		Properties languageProperties = new Properties();
		try {
			in = new FileInputStream("src/config/language.properties");
			languageProperties.load(in);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		for(Object value: languageProperties.values()) {
			language = (String)value;
		}
	}
	
	public static void printMessage(String message){
		String previousMessage = messageLabel.getText();
		messageLabel.setText(message + "\n" + previousMessage);
		messageLabel.moveCaretPosition(0);
		messageLabel.requestFocus();
	}
	
	public static void _bindKeys() {
		movementInput = new Integer[] {keysMap.get("left"), keysMap.get("right"), keysMap.get("down"), keysMap.get("up")};
		inventoryInput = new Integer[] {keysMap.get("item1"), keysMap.get("item2"), keysMap.get("item3"), keysMap.get("item4"),
				keysMap.get("item5"), keysMap.get("item6")};
		pickItemInput = new Integer[] {keysMap.get("pickItem")};
		attackInput = new Integer[] {keysMap.get("attack")};
		spellInput = new Integer[] {keysMap.get("spell")};
		descriptionSpellInput = new Integer[] {keysMap.get("descSpell")};
		descriptionInput = new Integer[] {keysMap.get("descInv"), keysMap.get("descStats"), keysMap.get("descMana"), 
				keysMap.get("descMonster"), keysMap.get("descEnv"), keysMap.get("descWalkablePositions")};
		descriptionWereableInput = new Integer[] {keysMap.get("descHead"), keysMap.get("descHands"), keysMap.get("descChest"),
				keysMap.get("descPants"), keysMap.get("descGloves"), keysMap.get("descWereableItems")};
		throwItemInput = new Integer[] {keysMap.get("throwItem")};
		unequipItemInput = new Integer[] {keysMap.get("unequipItem")};
		changeNumericDescInput = new Integer[] {keysMap.get("changeNumericDesc")};
		changeColorsInput = new Integer[] {keysMap.get("changeColors")};
		rebindKeysInput = new Integer[] {keysMap.get("rebindKeys")};
		activateSoundInput = new Integer[] {keysMap.get("activateSound")};
		increaseFontInput = new Integer[] {keysMap.get("increaseFont")};
		decreaseFontInput = new Integer[] {keysMap.get("decreaseFont")};
	}
	
	private static void printUserInformation() {
		user._printInventory(j, rootObjGrammar, rootObjWords);
		user._printLife(rootObjWords, j, 0, map.global_fin().y + 1);
		user._printMana(rootObjWords, j, 1, map.global_fin().y + 1);
		user._printSpeed(rootObjWords, j, 2, map.global_fin().y + 1);
		user._printMood(rootObjWords, j, 3, map.global_fin().y + 1);
		j.print(map.global_fin().y + 1, 4, JSONParsing.getTranslationWord("score", "N", rootObjWords) + ": " + 
				Integer.toString(deepnessScore));
		j.print(map.global_fin().y + 1, 5, JSONParsing.getTranslationWord("level", "N", rootObjWords) + ": " + 
				Integer.toString(user.getLevel()));
		j.print(map.global_fin().y + 1, 6, "exp" + ": " + 
				Integer.toString(user.getExperience()) + "/" + user.getNextLevelExperience());
	}
	
	public static void printEverything(boolean needsToPrintGroundObjects){
		j.cls();
		countElements = 4;
		map.printBorders(j, user);
		map.printInside(j, user);
		map.printItems(j, user);
		map.printMonsters(j, user);
		//if (user.getRoom().turnMes)
			//map.printInformationTurns(j, user.getRoom(), user.getMap().global_fin.x-1, user.getMap().global_fin.y+1);
		printUserInformation();
		map._printInformationMonsters(j, user, rootObjWords);
		if (needsToPrintGroundObjects) {
			user._printGroundObjects(j, rootObjWords);
		}
		j.print(user.getPosition().y, user.getPosition().x, user.getSymbolRepresentation(), arrayColors[selectedColor][0]);
		j.refresh();
	}
	
	public static void _moveCharacterAction(int i) throws JsonIOException, JsonSyntaxException, InstantiationException, IllegalAccessException{
		Tuple<Integer, Integer> previousPosition = user.getPosition();
        Tuple<Integer, Integer> newPosition = RandUtil.inputMoveInterpretation(i, Arrays.asList(movementInput), user);
		if (user.move(newPosition)){
			hasMoved = true;
        	user.setVisiblePositions();
        	printEverything(true);
        	previousPositionChar = previousPositionChar2;
        	previousPositionChar2 = j.peekChar(newPosition.y, newPosition.x);
        	if (RandUtil.containsString(usedSymbols, j.peekChar(newPosition.y, newPosition.x))){
            	if (hasChanged){
            		if (debug) {
            			System.out.println(map.getSymbolPosition(previousPosition));
            		}
	            	hasChanged = false;
            	}
        	}
        	if (isSoundActivated) {
        		walkSound.reproduce();
        	}
        } else {
        	_messageUnvalid();
        	printEverything(true);
        	hasMoved = false;
        	if (isSoundActivated) {
        		collisionSound.reproduce();
        	}
        }
		if (user.getRoom().isPortal(user.getPosition())) {
			hasUsedPortal = true;
			newMatch = false;
			gameFlow();
		}
	}
	
	public static void _initialize(){
		if (user == null || newMatch) {
			ArrayList<String> adjectives = new ArrayList<String>();
			adjectives.add("big");
			adjectives.add("brave");
			adjectives.add("glorious");
			user = new ActiveCharacter("hero", "", null, null, null, 
					40, 2, 20, 200000, 100, 100, 100, Mood.NEUTRAL, new ArrayList<WereableWeapon>(),
					new ArrayList<WereableArmor>(), 100, 100, 0,
					new ArrayList<Item>(), 0, 0, 100, 100, 100, "@", 4, null, adjectives, 20);
			user.setNextLevelExperience();
			WereableWeapon oneHandSword = new ShortSword(user, null, null, null, user.getLevel(), true);
			user.putItemInventory(oneHandSword);
			FireRing fireRing = new FireRing();
			user.addSpell(fireRing);
		}
		walkSound = new SoundReproduction(JSONParsing.getSoundSource(Map.sndObj, user, "STEP"));
		collisionSound = new SoundReproduction(JSONParsing.getSoundSource(Map.sndObj, user, "COLLISION"));
		deathSound = new SoundReproduction(JSONParsing.getSoundSource(Map.sndObj, user, "DEAD"));
		heroHitSound = new SoundReproduction(JSONParsing.getSoundSource(Map.sndObj, user, "HURT"));
		newMatch = false;
		_initializeMap();
		_setKeyMap();
		j.print(user.getPosition().y, user.getPosition().x, user.getSymbolRepresentation(), arrayColors[selectedColor][0]);
		actionHandler = new ActionHandler(keysMap, user, rootObj, rootObjWords);
	}
	
	public static void _initializeMap() {
		int min_rooms = 5;
		map = new Map(initial_point, final_point);
		while (map.getRooms().size() < min_rooms || !map.hasPortals()) {
			map = new Map(initial_point, final_point);
		}
		map.initialize(user);
		printEverything(true);
		j.print(user.getPosition().y, user.getPosition().x, user.getSymbolRepresentation(), arrayColors[selectedColor][0]);
		j.refresh();
	}
	
	public static String _getMessage(GrammarIndividual grammarIndividual, ArrayList<PrintableObject> names, String type, String verbType, boolean usePronoun
			, boolean useAnd, boolean isConfused) {
		GrammarSelectorS selector = null;
		try {
			selector = new GrammarSelectorS(grammarIndividual, rootObjWords, names, type, verbType);
		} catch (JsonIOException | JsonSyntaxException | FileNotFoundException | InstantiationException
				| IllegalAccessException e) {
			e.printStackTrace();
		}
		if (selector != null) {
			return selector.getRandomSentence(usePronoun, useAnd, isConfused);
		}
		
		return "";
	}
	
	public static void generatePrintMessage(ArrayList<PrintableObject> names, GrammarsGeneral grammar, String type, String verbType, 
			boolean usePronoun, boolean useAnd, boolean isConfused) {
		GrammarIndividual grammarIndividual = grammar.getRandomGrammar();
		printMessage(_getMessage(grammarIndividual, names, type, verbType, usePronoun, useAnd, isConfused));
	}
	
	public static void useAndWithItem(Item item) {
		String message = JSONParsing.getTranslationWord("and", "OTHERS", rootObjWords);
		GrammarIndividual grammarIndividual = grammarGeneralObj.getRandomGrammar();
		GrammarSelectorNP selector = new GrammarSelectorNP(grammarIndividual, rootObjWords, item, "GENERAL");
		printMessage(message + " " + selector.getRandomSentenceTranslated());
	}
	
	public static void _messageUnvalid() {
		String message = "";
		PrintableObject that = new PrintableObject("that", "", null, null);
		ArrayList<PrintableObject> names = new ArrayList<PrintableObject>();
		names.add(user);
		names.add(that);
		GrammarIndividual grammarIndividual = grammarUnvalidDescription.getRandomGrammar();
		message += _getMessage(grammarIndividual, names, "DESCUNVALID", "DESCUNVALID", usePronoun(), false, false);
		if (!message.isEmpty()) {
			printMessage(message);
		}
	}
	
	public static void _unequipItem(Item item){
		if (user.unequipItem(item)) {
			printEverything(true);
			ArrayList<PrintableObject> names = new ArrayList<PrintableObject>();
			names.add(user);
			names.add(item);
			if (hasUnequipedItem) {
				useAndWithItem(item);
			} else {
				generatePrintMessage(names, grammarPickItem, "UNEQUIP", "UNEQUIP", usePronoun(), false, false);
				hasUnequipedItem = true;
			}
			hasChanged = false;
		} else {
			_messageUnvalid();
		}
		printEverything(true);	
		j.print(user.getPosition().y, user.getPosition().x, user.getSymbolRepresentation(), arrayColors[selectedColor][0]);
	}
	
	public static void unequipItemAction(int itemCode) {
		hasEquipedItem = false;
    	if (isInputType(descriptionWereableInput, itemCode)) {
    		if (itemCode == keysMap.get("descHead")) {
    			Item helmet = user.getWearHelmet();
    			if (helmet != null) {
    				_unequipItem(helmet);
    			}
    		}
    		if (itemCode == keysMap.get("descChest")) {
    			Item chest = user.getWearChest();
    			if (chest != null) {
    				_unequipItem(chest);
    			}
    		}
    		if (itemCode == keysMap.get("descPants")) {
    			Item pants = user.getWearPants();
    			if (pants != null) {
    				_unequipItem(pants);
    			}
    		}
    		if (itemCode == keysMap.get("descGloves")) {
    			Item gloves = user.getWearGloves();
    			if (gloves != null) {
    				_unequipItem(gloves);
    			}
    		}
    		if (itemCode == keysMap.get("descHands")) {
    			if (user.getWeaponsEquipped().size() > 0) {
    				_unequipItem(user.getWeaponsEquipped().get(0));
    			}
    		}
    		printEverything(false);
    		canUsePronoun = true;
    	}
	}
	
	public static void spellAction(int itemCode) {
		doMonstersTurn = true;
    	if (isInputType(inventoryInput, itemCode)) {
    		actionHandler._spellAction(itemCode, usePronoun());
    		canUsePronoun = true;
    		printEverything(true);
    		setFlagsToFalse();
    	}
    	canUsePronoun = true;
    	printEverything(true);
	}
	
	public static void throwAction(int itemCode) {
		if (isInputType(inventoryInput, itemCode)) {
    		actionHandler._throwItem(itemCode, usePronoun());
    		canUsePronoun = true;
    		printEverything(true);
    		hasEquipedItem = false;
    		hasUnequipedItem = false;
    		hasPickedItem = false;
    	}
	}
	
	private static void setFlagsToFalse() {
		hasEquipedItem = false;
		hasUnequipedItem = false;
		hasThrownItem = false;
		hasPickedItem = false;
	}
	
	public static void makeMovement(int i) throws JsonIOException, JsonSyntaxException, InstantiationException, IllegalAccessException {
		boolean actionDone = false;
		boolean attackDone = false;
		double now = System.currentTimeMillis();
		
			if(now - lastTime > cooldPressKey && isInputType(movementInput, i)){
				attackDone = true;
	        	actionDone = true;
	        	_moveCharacterAction(i);
	        	setFlagsToFalse();
	        	if (countAction > 0) {
	        		countAction--;
	        		System.out.println("countAction: " + countAction);
	        	}
	        }
	        else if (now - lastTime > cooldPressKey && isInputType(inventoryInput, i)) {
	        	attackDone = true;
	        	actionDone = true;
	        	actionHandler._inventoryAction(i, usePronoun());
	        	canUsePronoun = true;
	        	printEverything(false);
	        	hasUnequipedItem = false;
	        	hasThrownItem = false;
	        	hasPickedItem = false;
	        }
	        else if (now - lastTime > cooldPressKey && isInputType(pickItemInput, i)) {
	        	actionDone = true;
	        	actionHandler._pickItemAction(usePronoun(), hasPickedItem);
	        	canUsePronoun = true;
	        	printEverything(true);
	        	hasEquipedItem = false;
	        	hasUnequipedItem = false;
	        	hasThrownItem = false;
	        }
	        else if (now - lastTime > cooldPressKey && isInputType(attackInput, i)) {
	        	actionDone = true;
	        	attackDone = true;
	        	actionHandler._attackAction(usePronoun());
	        	canUsePronoun = true;
	        	printEverything(true);
	        	setFlagsToFalse();
	        } 
	        else if (now - lastTime > cooldPressKey && isInputType(spellInput, i)) {
	        	actionDone = true;
	        	attackDone = true;
	        	spellsPressed = true;
	        	setFlagsToFalse();
	        	messageLabel.requestFocus();
	        } else if (now - lastTime > cooldPressKey && (isInputType(descriptionInput, i) || isInputType(descriptionWereableInput, i))) {
	        	actionDone = true;
	        	setFlagsToFalse();
	        	actionHandler._descriptionAction(i, usePronoun(), isNumericDescription);
	        	canUsePronoun = true;
	        	printEverything(false);
	        } else if (now - lastTime > cooldPressKey && isInputType(throwItemInput, i)) {
	        	actionDone = true;
	        	hasUnequipedItem = false;
	        	hasEquipedItem = false;
	        	hasPickedItem = false;
	        	throwPressed = true;
	        	messageLabel.requestFocus();
	        } else if (now - lastTime > cooldPressKey && isInputType(changeNumericDescInput, i)) {
	        	actionDone = true;
	        	isNumericDescription = !isNumericDescription;
	        } else if (now - lastTime > cooldPressKey && isInputType(changeColorsInput, i)) {
	        	actionDone = true;
	        	if (selectedColor == arrayColors.length - 1) {
	        		selectedColor = 0;
	        	} else {
	        		selectedColor++;
	        	}
	        	printEverything(true);
	        } else if (now - lastTime > cooldPressKey && isInputType(unequipItemInput, i)) {
	        	actionDone = true;
	        	hasEquipedItem = false;
	        	hasPickedItem = false;
	        	hasThrownItem = false;
	        	unequipPressed = true;
	        	messageLabel.requestFocus();
	        } else if (now - lastTime > cooldPressKey && isInputType(rebindKeysInput, i)) {
	        	actionDone = true;
	        	rebindKeys();
	        } else if (now - lastTime > cooldPressKey && isInputType(descriptionSpellInput, i)) {
	        	actionDone = true;
	        	MessageDescriptionsUtil.describeSpells(user, rootObjWords, grammarSimpleVerb);
	        } else if (now - lastTime > cooldPressKey && isInputType(activateSoundInput, i)) {
	        	actionDone = true;
	        	activateDeactivateSound();
	        } else if (now - lastTime > cooldPressKey && isInputType(increaseFontInput, i)) {
	        	actionDone = true;
	        	increaseFontSize();
	        } else if (now - lastTime > cooldPressKey && isInputType(decreaseFontInput, i)) {
	        	actionDone = true;
	        	decreaseFontSize();
	        }
			if (actionDone) {
				if (attackDone) {
					if (user.getRoom().getTurnsList().size() > 0)
						user.getRoom().removeCurrentTurn();
				} else {
					if (isSoundActivated) {
		        		beepSound.reproduce();
		        	}
				}
				lastTime = System.currentTimeMillis();
			}

	}
	
	public static void enemyTurn(ActiveCharacter thing, GrammarIndividual grammarIndividual) {
		double now = System.currentTimeMillis();
		if (now - lastTimeEnemy > cooldTurnEnemy ) {
			lastTimeEnemy = System.currentTimeMillis();
			Pair<Pair<Tuple<Boolean, Boolean>, String>, ActiveCharacter> message = user.getRoom().monsterTurn(user, thing, grammarIndividual, rootObjWords);
	    	printEverything(true);
			//confusion message
			if (message.getA().getA().y && message.getB() != null) {
				GrammarIndividual grammarIndividualSh = grammarSelfharmDescription.getRandomGrammar();
				ArrayList<PrintableObject> namesSh = new ArrayList<PrintableObject>();
				ArrayList<String> prepositionUser = new ArrayList<String>();
				ArrayList<String> prepositionConf = new ArrayList<String>();
				PrintableObject confusion = new PrintableObject("confusion", "", null, null);
				prepositionUser.add("but");
				prepositionConf.add("in");
				message.getB().setPrepositions(prepositionUser);
				confusion.setPrepositions(prepositionConf);
				namesSh.add(message.getB());
				namesSh.add(message.getB());
				namesSh.add(confusion);
				String messageMiss = ", " + main.Main._getMessage(grammarIndividualSh, namesSh, "SELFHARM", "SELFHARM", true, false, true);
				main.Main.printMessage(message.getA().getB() + messageMiss);
			} else if (message.getA().getA().x && !message.getA().getB().isEmpty()) {
				//if it has dealt damage and receives the "x attacks y with z weapon" prints the message
				printMessage(message.getA().getB());
			} else if (!message.getA().getB().isEmpty()){
				GrammarIndividual grammarIndividualMiss = grammarMissDescription.getRandomGrammar();
				ArrayList<PrintableObject> namesMiss = new ArrayList<PrintableObject>();
				ArrayList<String> preposition = new ArrayList<String>();
				ArrayList<String> prepositionBefore = user.getPrepositions();
				preposition.add("but");
				user.setPrepositions(preposition);
				namesMiss.add(user);
				String messageMiss = ", " + _getMessage(grammarIndividualMiss, namesMiss, "MISS", "MISS", true, false, false);
				user.setPrepositions(prepositionBefore);
				String[] words = messageMiss.split("\\s+");
				messageMiss = messageMiss.replaceFirst(words[2] + " ", "");
				printMessage(message.getA().getB() + messageMiss);
			}
			user.getRoom().removeCurrentTurn();
			monsterTurn = new Pair<Boolean, ActiveCharacter>(false, null);
		}
	}
	
	public static void playerTurn() throws JsonIOException, JsonSyntaxException, InstantiationException, IllegalAccessException {
    	int i = j.inkey().code;
		
		System.out.println("Code" + i);
		
		makeMovement(i);
	}
	
	public static void turnLoop() throws JsonIOException, JsonSyntaxException, InstantiationException, IllegalAccessException {
		for (;;) {
			for (ActiveCharacter monster : user.getRoom().getMonsters()) {
				monster.setAdjectivesMonster(user);
			}
			user.setAdjectivesUser();
			if (user.getLife() > 0) {
				GrammarIndividual grammarIndividual = grammarAttack.getRandomGrammar();
				if (monsterTurn.getA())
					enemyTurn(monsterTurn.getB(), grammarIndividual);
				else {
					if (user.getRoom().getTurnsList().size() > 0) {
						ActiveCharacter thing = user.getRoom().getTurnsList().get(0);
						if (thing != null && !thing.isDead())
							System.out.println("***** CURRENT TURN ***** -> " + thing.getName());
						if (thing != user && !thing.isDead()) {
							monsterTurn = new Pair<Boolean, ActiveCharacter>(true, thing);
						} else if (thing == user && !thing.isDead()) {
							System.out.println("HAY ENEMIGOS Y TURNO USER");		
							playerTurn();	
						}
						if (user.getRoom().getTurnsList().size() < 1 && !user.getMap().allEnemiesRoomDead(user.getRoom())) {
							user.getRoom().setListOfTurns(user);
						}
					} else if (user.getRoom().getTurnsList().size() < 1 && !user.getMap().allEnemiesRoomDead(user.getRoom())) {
						user.getRoom().setListOfTurns(user);
					} else if (user.getRoom().getMonsters().size() < 1 || user.getMap().allEnemiesRoomDead(user.getRoom())){
						System.out.println("SIN MONSTROS");
						playerTurn();
					}
				}
				if (countAction == 0) {
					if (isSoundActivated)
						waterdropSound.reproduce();
					countAction = Util.rand(25, 50);
					System.out.println("countAction set: " + countAction);
				}
			}
			else {
				if (isSoundActivated)
					deathSound.reproduce();
				MessageDescriptionsUtil._messageDescriptionDead(user, true, grammarAdjectiveDescription);
				try {
					deepnessScore = 0;
					newMatch = true;
					messageLabel.setText("");
					main(null);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public static void gameFlow() throws JsonIOException, JsonSyntaxException, InstantiationException, IllegalAccessException {
		messagesWereables = ResourceBundle.getBundle("translations.files.MessagesWereable");
		if (deepnessScore == 0){
			_initialize();
		} else {
			_initializeMap();
		}
		if (hasUsedPortal) {
			printEverything(true);
			PrintableObject portal = new PrintableObject("portal", "", null, null);
			ArrayList<PrintableObject> names = new ArrayList<PrintableObject>();
			names.add(user);
			names.add(portal);
			GrammarIndividual grammarIndividual = grammarGeneralDescription.getRandomGrammar();
			printMessage(_getMessage(grammarIndividual, names, "DESCGOESTHROUGH", "DESCGOESTHROUGH", false, false, false));
			deepnessScore++;
			hasUsedPortal = false;
		}
		
		turnLoop();
		
	}
	
	public static void increaseFontSize() {
		float sizeIncrease = 5.0f;
		Font font = messageLabel.getFont();
		float size = font.getSize() + sizeIncrease;
		messageLabel.setEditable(false);
		messageLabel.setFont(font.deriveFont(size));
	}
	
	public static void decreaseFontSize() {
		float sizeDecrease = 5.0f;
		Font font = messageLabel.getFont();
		float size = font.getSize() - sizeDecrease;
		messageLabel.setEditable(false);
		messageLabel.setFont(font.deriveFont(size));
	}
	
	public static void configureTextArea() {
		if (!hasIncreasedFontSize) {
			increaseFontSize();
		}
		window = new JFrame();
		caret = (DefaultCaret)messageLabel.getCaret();
		jScrollPane = new JScrollPane(messageLabel);
		window.add(jScrollPane);
		window.setVisible(true);
		window.setBounds(0, 0, 600, 350);
		hasIncreasedFontSize = true;
	}
	
	public static void restartMessage() {
		JLabel message = new JLabel();
		message.setText(JSONParsing.getTranslationWord("restart", "OTHERS", rootObjWords));
		message.requestFocusInWindow();
		JOptionPane.showMessageDialog(null, message, "", JOptionPane.PLAIN_MESSAGE);
		System.exit(1);
	}
	
	public static void rebindKeys() {
		try {
			new ChangeKeyBinding(j);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public static void activateDeactivateSound() {
		isSoundActivated = !isSoundActivated;
	}
	
	public static void configureApparitionRate() {
		//getting base apparition rate, base danger level and the list of available enemies from json file
		rateAppar1 = JSONParsing.getElement(Main.rootApparitionRate, "ENEMY1").getAsJsonObject();
		enemiesJson1.add(JSONParsing.getElement(rateAppar1, "GOBLIN").getAsJsonArray());
		enemiesJson1.add(JSONParsing.getElement(rateAppar1, "RAT").getAsJsonArray());
		rateAppar2 = JSONParsing.getElement(Main.rootApparitionRate, "ENEMY2").getAsJsonObject();
		enemiesJson2.add(JSONParsing.getElement(rateAppar2, "GOBLIN").getAsJsonArray());
		enemiesJson2.add(JSONParsing.getElement(rateAppar2, "LITTLESLIME").getAsJsonArray());
		enemiesJson2.add(JSONParsing.getElement(rateAppar2, "RAT").getAsJsonArray());
		rateAppar3 = JSONParsing.getElement(Main.rootApparitionRate, "ENEMY3").getAsJsonObject();
		enemiesJson3.add(JSONParsing.getElement(rateAppar3, "SLIME").getAsJsonArray());
		enemiesJson3.add(JSONParsing.getElement(rateAppar3, "DRAGON").getAsJsonArray());
		enemiesJson3.add(JSONParsing.getElement(rateAppar3, "GOBLIN").getAsJsonArray());
		enemiesJson3.add(JSONParsing.getElement(rateAppar3, "LITTLESLIME").getAsJsonArray());
		enemiesJson3.add(JSONParsing.getElement(rateAppar3, "RAT").getAsJsonArray());
		
		//storing values on each ArrayList so it can be available since the beginning
		for (JsonArray enemy : enemiesJson1) {
			enemiesList1Rate.add(Integer.valueOf(JSONParsing.getElement(enemy, "RATE").toString()));
			enemiesList1Danger.add(Integer.valueOf(JSONParsing.getElement(enemy, "BASEDANGER").toString()));
			enemiesList1Name.add(JSONParsing.getElement(enemy, "NAME").toString());
		}
		
		for (JsonArray enemy : enemiesJson2) {
			enemiesList2Rate.add(Integer.valueOf(JSONParsing.getElement(enemy, "RATE").toString()));
			enemiesList2Danger.add(Integer.valueOf(JSONParsing.getElement(enemy, "BASEDANGER").toString()));
			enemiesList2Name.add(JSONParsing.getElement(enemy, "NAME").toString());
		}
		
		for (JsonArray enemy : enemiesJson3) {
			enemiesList3Rate.add(Integer.valueOf(JSONParsing.getElement(enemy, "RATE").toString()));
			enemiesList3Danger.add(Integer.valueOf(JSONParsing.getElement(enemy, "BASEDANGER").toString()));
			enemiesList3Name.add(JSONParsing.getElement(enemy, "NAME").toString());
		}
	}

	public static void main(String[] args) throws IOException, JsonIOException, JsonSyntaxException, InstantiationException, IllegalAccessException {
		_setLanguage();
		configureTextArea();
		j.getTargetFrame().requestFocus();
		rootObj = parser.parse(new FileReader("./src/grammars/languages/sentenceGrammar" + language + ".json")).getAsJsonObject();
		rootApparitionRate = parser.parse(new FileReader("./src/characters/active/enemies/apparitionRate.json")).getAsJsonObject();
		rootObjWords = parser.parse(new FileReader("./src/grammars/languages/words" + language + ".json")).getAsJsonObject();
		rootObjGrammar = parser.parse(new FileReader("./src/grammars/languages/objectGrammar" + language + ".json")).getAsJsonObject();
		grammarAttack = new GrammarsGeneral(JSONParsing.getElement(rootObj, "ATTACK").getAsJsonObject());
		grammarPickItem = new GrammarsGeneral(JSONParsing.getElement(rootObj, "PICK").getAsJsonObject());
		grammarGeneralDescription = new GrammarsGeneral(JSONParsing.getElement(rootObj, "GENERAL").getAsJsonObject());
		grammarUnvalidDescription = new GrammarsGeneral(JSONParsing.getElement(rootObj, "DESCUNVALID").getAsJsonObject());
		grammarAdjectiveDescription = new GrammarsGeneral(JSONParsing.getElement(rootObj, "DESCRIPTIONADJECTIVE").getAsJsonObject());
		grammarMissDescription = new GrammarsGeneral(JSONParsing.getElement(rootObj, "ATTACKMISS").getAsJsonObject());
		grammarSimpleVerb = new GrammarsGeneral(JSONParsing.getElement(rootObj, "SIMPLEVERB").getAsJsonObject());
		grammarGeneralObj = new GrammarsGeneral(JSONParsing.getElement(rootObjGrammar, "GENERAL").getAsJsonObject());
		grammarSelfharmDescription = new GrammarsGeneral(JSONParsing.getElement(rootObj, "SELFHARM").getAsJsonObject());
		Map.sndObj = parser.parse(new FileReader("./src/sounds/soundsLoc.json")).getAsJsonObject();
		beepSound = new SoundReproduction(JSONParsing.getSoundSource(Map.sndObj, null, "BEEP"));
		waterdropSound = new SoundReproduction(JSONParsing.getSoundSource(Map.sndObj, null, "WATERDROP"));
		configureApparitionRate();
		countAction = Util.rand(12, 25);
		possibleCry = Util.rand(3, 10);
		System.out.println("countAction set: " + countAction);
		if (!testMode){
			gameFlow();
		}
	}
}