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
import characters.active.playerclass.Knight;
import characters.active.playerclass.Mage;
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
import magic.Fireball;
import map.Map;
import map.Room;
import net.slashie.libjcsi.CharKey;
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
	static Integer[] upkeyMenuInput;
	static Integer[] downkeyMenuInput;
	static Integer[] enterMenuInput;
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
	static Integer[] pauseGameInput;
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
	public static GrammarsGeneral grammarGeneralDescription;
	static GrammarsGeneral grammarSimpleVerb;
	static GrammarsGeneral grammarGeneralObj;
	static GrammarsGeneral grammarSelfharmDescription;
	public static SoundReproduction walkSound;
	public static SoundReproduction beepSound;
	public static SoundReproduction heroHitSound;
	public static SoundReproduction deathSound;
	public static SoundReproduction attackSound;
	public static SoundReproduction avoidSound;
	public static SoundReproduction deathEnemySound;
	public static SoundReproduction collisionSound;
	public static SoundReproduction waterdropSound;
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
	public static double lastTimeMenu = 0;
	public static double cooldMenu = 150;
	public static Pair<Boolean, ActiveCharacter> monsterTurn = new Pair<Boolean, ActiveCharacter>(false, null);
	public static boolean menu = true;
	public static boolean chooseOptions = false;
	public static boolean chooseControls = false;
	public static boolean gamePaused = false;
	public static boolean chooseClass = false;
	public static boolean shopMenu = false;
	public static boolean shopMenuBuy = false;
	public static boolean shopMenuSell = false;
	private static int pointerMenu = 0;
	private static ArrayList<String> eleMenu = new ArrayList<>();
	private static ArrayList<Pair<String, Integer>> defaultControls = new ArrayList<>();
	private static String userClass = "";
	
	
	
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
			defaultControls.add(new Pair<String, Integer>(key, Integer.valueOf(value)));
			Pair<String, Integer> current = defaultControls.get(defaultControls.size()-1);
			System.out.println("Cogido: " + current.getA() + " - " + current.getB());
			keysMap.put(key, Integer.parseInt(value));
		}
		_bindKeys();
		_bindKeysMenu();
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
	
	public static void _bindKeysMenu() {
		upkeyMenuInput = new Integer[] {keysMap.get("up")};
		downkeyMenuInput = new Integer[] {keysMap.get("down")};
		enterMenuInput = new Integer[] {keysMap.get("enter")};
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
		pauseGameInput = new Integer[] {keysMap.get("pause")};
	}
	
	private static void printUserInformation() {
		user._printInventory(j, rootObjGrammar, rootObjWords);
		user._printLife(rootObjWords, j, 0, map.global_fin().y + 3);
		user._printMana(rootObjWords, j, 1, map.global_fin().y + 3);
		user._printSpeed(rootObjWords, j, 2, map.global_fin().y + 3);
		user._printMood(rootObjWords, j, 3, map.global_fin().y + 3);
		j.print(map.global_fin().y + 3, 4, JSONParsing.getTranslationWord("score", "N", rootObjWords) + ": " + 
				Integer.toString(deepnessScore));
		j.print(map.global_fin().y + 3, 5, JSONParsing.getTranslationWord("level", "N", rootObjWords) + ": " + 
				Integer.toString(user.getLevel()));
		j.print(map.global_fin().y + 3, 6, "exp" + ": " + 
				Integer.toString(user.getExperience()) + "/" + user.getNextLevelExperience());
		j.print(map.global_fin().y + 3, 7, JSONParsing.getTranslationWord("money", "N", rootObjWords) + ": " + 
				user.getMoney());
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
        		walkSound.play();
        	}
        } else {
        	_messageUnvalid();
        	printEverything(true);
        	hasMoved = false;
        	if (isSoundActivated) {
        		collisionSound.play();
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
			adjectives.add("glorious");
			String knight = JSONParsing.getTranslationWord("knight", "N", rootObjWords);
			String mage = JSONParsing.getTranslationWord("mage", "N", rootObjWords);
			//depends on the user's choice in the choose-class menu	
			if (userClass.equals(knight)) {
				//sets up different adjectives depending on the chosen class
				adjectives.add("big");
				adjectives.add("brave");
				user = new Knight(null, null, null, adjectives, 1, 4, 5, 15, 100, 90, 120, 0, 40);
				user.setNextLevelExperience();
				ShortSword oneHandSword = new ShortSword(user, null, null, null, user.getLevel(), true, 0);
				oneHandSword.setRandomPrice();
				oneHandSword.setSellPriceInit();
				user.putItemInventory(oneHandSword);
			} else if (userClass.equals(mage)) {
				adjectives.add("wise");
				user = new Mage(null, null, null, adjectives, 1, 6, 2, 26, 80, 100, 90, 0, 100);
				user.setNextLevelExperience();
				Fireball fireball = new Fireball();
				user.addSpell(fireball);
			}
			
			user.setMoney(500);
		}
		walkSound = new SoundReproduction(JSONParsing.getSoundSource(Map.sndObj, user, "STEP"));
		collisionSound = new SoundReproduction(JSONParsing.getSoundSource(Map.sndObj, user, "COLLISION"));
		deathSound = new SoundReproduction(JSONParsing.getSoundSource(Map.sndObj, user, "DEAD"));
		heroHitSound = new SoundReproduction(JSONParsing.getSoundSource(Map.sndObj, user, "HURT"));
		avoidSound = new SoundReproduction(JSONParsing.getSoundSource(Map.sndObj, user, "AVOID"));
		attackSound = new SoundReproduction(JSONParsing.getSoundSource(Map.sndObj, user, "ATTACK"));
		newMatch = false;
		_initializeMap();
		j.print(user.getPosition().y, user.getPosition().x, user.getSymbolRepresentation(), arrayColors[selectedColor][0]);
		//ActionHandler class functions are called every time the user performs an action (e.g.: attacking an enemy)
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
		//using refresh() function to update the game window 
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
		//gets the sentence structure depending on the type of phrase we want to formulate
		GrammarIndividual grammarIndividual = grammar.getRandomGrammar();
		//prints message
		printMessage(_getMessage(grammarIndividual, names, type, verbType, usePronoun, useAnd, isConfused));
	}
	
	public static void useAndWithItem(Item item) {
		//to avoid repeating the same type of phrase
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
    	if (isInputType(inventoryInput, itemCode)) {
    		//calls the Action Handler function for casting spells 
    		//so it generates the intended message
    		actionHandler._spellAction(itemCode, usePronoun());
    		canUsePronoun = true;
    		setFlagsToFalse();
    	}
    	canUsePronoun = true;
    	//refresh game's window
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
	
	public static void changeColors() {
		if (selectedColor == arrayColors.length - 1) {
    		selectedColor = 0;
    	} else {
    		selectedColor++;
    	}
	}
	
	public static void makeMovement(int i) throws JsonIOException, JsonSyntaxException, InstantiationException, IllegalAccessException {
		//player's turn input check function
		boolean actionDone = false;
		boolean attackDone = false;
		double now = System.currentTimeMillis();
		//game pause and shop
		if (now - lastTimeMenu > cooldMenu && isInputType(pauseGameInput, i) && !gamePaused) {
			gamePaused = true;
			lastTimeMenu = now;
			return;
		}
		if (now - lastTimeMenu > cooldMenu && user.getRoom().hasShop 
				&& RandUtil.containsTuple(user.getRoom().getShop().getPosition(), user.getVisiblePositions()) 
				&& isInputType(attackInput, i) && !shopMenu) {
			shopMenu = true;
			lastTimeMenu = now;
			return;
		}
		//
		if(now - lastTime > cooldPressKey && isInputType(movementInput, i)){
			attackDone = true;
        	actionDone = true;
        	_moveCharacterAction(i);
        	setFlagsToFalse();
        	//every time the player moves, decreases the counter for playing the water drop sound
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
        	i = j.inkey().code;
        	spellAction(i);
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
        	changeColors();
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
				//this checks if the user is confused, and decreases by 1 the number of confusion turns left
				if (("CONFUSED").equals(user.getMood().toString())) {
					if (user.getConfusionTurns() > 0) {
						System.out.println("CONFUSION TURNS: " + user.getConfusionTurns());
						if (user.getConfusionTurns() == 1) {
							user.setConfusionTurns(0);
							user.setMood(Mood.NEUTRAL);
						} else
							user.setConfusionTurns(user.getConfusionTurns()-1);
					}
				}
					
				if (user.getRoom().getTurnsList().size() > 0)
					user.getRoom().removeCurrentTurn();
			} else {
				//if a not attacking action is performed, plays a beep sound
				if (isSoundActivated) {
	        		beepSound.play();
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
				if (("CONFUSED").equals(user.getMood().toString())) {
					if (user.getConfusionTurns() > 0) {
						System.out.println("CONFUSION TURNS: " + user.getConfusionTurns());
						if (user.getConfusionTurns() == 1) {
							user.setConfusionTurns(0);
							user.setMood(Mood.NEUTRAL);
						} else
							user.setConfusionTurns(user.getConfusionTurns()-1);
					}
				}
			} else if (message.getA().getA().x && !message.getA().getB().isEmpty()) {
				//if it has dealt damage and receives the "x attacks y with z weapon" prints the message
				printMessage(message.getA().getB());
				if (("CONFUSED").equals(user.getMood().toString())) {
					if (user.getConfusionTurns() > 0) {
						System.out.println("CONFUSION TURNS: " + user.getConfusionTurns());
						if (user.getConfusionTurns() == 1) {
							user.setConfusionTurns(0);
							user.setMood(Mood.NEUTRAL);
						} else
							user.setConfusionTurns(user.getConfusionTurns()-1);
					}
				}
			} else if (!message.getA().getB().isEmpty()){
				//if it hasn't dealt damage
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
				if (("CONFUSED").equals(user.getMood().toString())) {
					if (user.getConfusionTurns() > 0) {
						System.out.println("CONFUSION TURNS: " + user.getConfusionTurns());
						if (user.getConfusionTurns() == 1) {
							user.setConfusionTurns(0);
							user.setMood(Mood.NEUTRAL);
						} else
							user.setConfusionTurns(user.getConfusionTurns()-1);
					}
				}
			}
			user.getRoom().removeCurrentTurn();
			monsterTurn = new Pair<Boolean, ActiveCharacter>(false, null);
			if (thing.getConfusionTurns() > 0) {
				if (thing.getConfusionTurns() == 1) {
					thing.setConfusionTurns(0);
					thing.setMood(Mood.NEUTRAL);
				} else
					thing.setConfusionTurns(thing.getConfusionTurns()-1);
			}
		}
	}
	
	public static void playerTurn() throws JsonIOException, JsonSyntaxException, InstantiationException, IllegalAccessException {
    	int i = j.inkey().code;
		
		System.out.println("Code" + i);
		
		makeMovement(i);
	}
	
	public static void turnLoop() throws JsonIOException, JsonSyntaxException, InstantiationException, IllegalAccessException {
		for (;;) {
			//if a menu is opened, the game stops the turn thing until the player closes the menu
			if (!gamePaused && !shopMenu && !shopMenuBuy && !shopMenuSell) {
				for (ActiveCharacter monster : user.getRoom().getMonsters()) {
					monster.setAdjectivesMonster(user);
				}
				user.setAdjectivesUser();
				if (user.getLife() > 0) {
					//sets up the structure of the sentence if a character attacks another
					GrammarIndividual grammarIndividual = grammarAttack.getRandomGrammar();
					//getA() returns true if it's monster's turn, getB() returns the monster ID
					if (monsterTurn.getA())
						enemyTurn(monsterTurn.getB(), grammarIndividual);
					else {
						//if it doesn't know whose turn it is, checks if the current turn hasn't ended yet
						if (user.getRoom().getTurnsList().size() > 0) {
							//if not, gets the character
							ActiveCharacter thing = user.getRoom().getTurnsList().get(0);
							//enemy's turn
							if (thing != user && !thing.isDead()) {
								monsterTurn = new Pair<Boolean, ActiveCharacter>(true, thing);
							} else if (thing == user && !thing.isDead()) {
								//user turn		
								playerTurn();	
							}
							//if current turn has ended and there are still enemies, calculates next turn
							if (user.getRoom().getTurnsList().size() < 1 && !user.getMap().allEnemiesRoomDead(user.getRoom())) {
								user.getRoom().setListOfTurns(user);
							}
						} else if (user.getRoom().getTurnsList().size() < 1 && !user.getMap().allEnemiesRoomDead(user.getRoom())) {
							user.getRoom().setListOfTurns(user);
						} else if (user.getRoom().getMonsters().size() < 1 || user.getMap().allEnemiesRoomDead(user.getRoom())){
							//if all enemies are dead or there are no enemies, it's always player's turn
							playerTurn();
						}
					}
					if (countAction == 0) {
						//water drop sound plays (for immersion purposes)
						if (isSoundActivated)
							waterdropSound.play();
						countAction = Util.rand(25, 50);
					}
				}
				else {
					//if the player is dead, restart game
					if (isSoundActivated)
						deathSound.play();
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
			} else {
				//menu loops for pausing and shopping
				if (gamePaused) {
					configurePauseMenu();
					while(gamePaused) {
						printMenu();
						int n = j.inkey().code;
						menuLoopInputs(n);
					}
				} else if (shopMenu) {
					configureShopMenu();
					while(shopMenu) {
						printMenu();
						int n = j.inkey().code;
						menuLoopInputs(n);
					}
				} else if (shopMenuBuy) {
					configureBuyMenu();
					while(shopMenuBuy) {
						printMenu();
						int n = j.inkey().code;
						menuLoopInputs(n);
					}
				} else if (shopMenuSell) {
					configureSellMenu();
					while(shopMenuSell) {
						printMenu();
						int n = j.inkey().code;
						menuLoopInputs(n);
					}
				}
				
			}
		}
	}
	
	public static void gameFlow() throws JsonIOException, JsonSyntaxException, InstantiationException, IllegalAccessException {
		messagesWereables = ResourceBundle.getBundle("translations.files.MessagesWereable");
		if (deepnessScore == 0){
			//if deepness equals 0, then the game is starting
			_initialize();
		} else {
			_initializeMap();
		}
		if (hasUsedPortal) {
			printEverything(true);
			PrintableObject portal = new PrintableObject("portal", "", null, null);
			ArrayList<PrintableObject> names = new ArrayList<PrintableObject>();
			//gets the nouns of the sentence that will be formed
			names.add(user);
			names.add(portal);
			//gets the structure of the sentence (check sentenceGrammar's files)
			GrammarIndividual grammarIndividual = grammarGeneralDescription.getRandomGrammar();
			printMessage(_getMessage(grammarIndividual, names, "DESCGOESTHROUGH", "DESCGOESTHROUGH", false, false, false));
			//using a portal = new Map object = new level
			deepnessScore++;
			hasUsedPortal = false;
		}
		//function that checks current ActiveCharacter turn
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
	
	public static void configureMenu() {
		eleMenu.clear();
		String gamestart = JSONParsing.getTranslationWord("start game", "OTHERS", rootObjWords);
		eleMenu.add(gamestart);
		gamestart = JSONParsing.getTranslationWord("options", "OTHERS", rootObjWords);
		eleMenu.add(gamestart);
		gamestart = JSONParsing.getTranslationWord("exit game", "OTHERS", rootObjWords);
		eleMenu.add(gamestart);
		pointerMenu = 0;
	}
	
	public static void configurePauseMenu() {
		eleMenu.clear();
		String gamestart = JSONParsing.getTranslationWord("resume", "OTHERS", rootObjWords);
		eleMenu.add(gamestart);
		gamestart = JSONParsing.getTranslationWord("options", "OTHERS", rootObjWords);
		eleMenu.add(gamestart);
		gamestart = JSONParsing.getTranslationWord("quit", "OTHERS", rootObjWords);
		eleMenu.add(gamestart);
		pointerMenu = 0;
	}
	
	public static void configureOptions() {
		eleMenu.clear();
		String color = JSONParsing.getTranslationWord("color palette", "OTHERS", rootObjWords);
		String volume = JSONParsing.getTranslationWord("volume", "OTHERS", rootObjWords);
		String controls = JSONParsing.getTranslationWord("controls", "OTHERS", rootObjWords);
		String back = JSONParsing.getTranslationWord("back", "OTHERS", rootObjWords);
		eleMenu.add(color);
		eleMenu.add(volume);
		eleMenu.add(controls);
		eleMenu.add(back);
		pointerMenu = 0;
	}
	
	public static void configureControls() {
		eleMenu.clear();
		String rebind = JSONParsing.getTranslationWord("remap keys", "OTHERS", rootObjWords);
		String reset = JSONParsing.getTranslationWord("reset controls", "OTHERS", rootObjWords);
		String back = JSONParsing.getTranslationWord("back", "OTHERS", rootObjWords);
		eleMenu.add(rebind);
		eleMenu.add(reset);
		eleMenu.add(back);
		pointerMenu = 0;
	}
	
	public static void configureChooseClass() {
		eleMenu.clear();
		String knight = JSONParsing.getTranslationWord("knight", "N", rootObjWords);
		String mage = JSONParsing.getTranslationWord("mage", "N", rootObjWords);
		String back = JSONParsing.getTranslationWord("back", "OTHERS", rootObjWords);
		eleMenu.add(knight.toUpperCase());
		eleMenu.add(mage.toUpperCase());
		eleMenu.add(back);
		pointerMenu = 0;
	}
	
	public static void configureShopMenu() {
		eleMenu.clear();
		String buy = JSONParsing.getTranslationWord("buy items", "OTHERS", rootObjWords);
		String sell = JSONParsing.getTranslationWord("sell items", "OTHERS", rootObjWords);
		String back = JSONParsing.getTranslationWord("back", "OTHERS", rootObjWords);
		eleMenu.add(buy);
		eleMenu.add(sell);
		eleMenu.add(back);
		pointerMenu = 0;
	}
	
	public static void configureBuyMenu() {
		eleMenu.clear();
		ArrayList<String> itemList = new ArrayList<>();
		for (Item item : user.getRoom().getShop().getStoredItems()) {
			itemList.add(item.getPrintableName() + ": " + item.getPrice());
		}
		String back = JSONParsing.getTranslationWord("back", "OTHERS", rootObjWords);
		itemList.add(back);
		eleMenu = itemList;
		pointerMenu = 0;
	}
	
	public static void configureSellMenu() {
		eleMenu.clear();
		ArrayList<String> itemList = new ArrayList<>();
		for (Item item : user.getInventory()) {
			itemList.add(item.getPrintableName() + ": " + item.getSellPrice());
		}
		String back = JSONParsing.getTranslationWord("back", "OTHERS", rootObjWords);
		itemList.add(back);
		eleMenu = itemList;
		pointerMenu = 0;
	}
	
	public static void printTitleMenu() {
		int xstart = (((j.xdim + 1)/2)/2)/2;
		int xmed = (j.xdim+1)/2;
		
		String title = JSONParsing.getTranslationWord("roomsgame", "OTHERS", rootObjWords);
		
		for (int i = 0; i < 7; i++) {
			j.print(xmed-xstart+(i*2), 6, "H", arrayColors[selectedColor][i]);
			j.print(xmed-xstart+(i*2)+1, 6, "H", arrayColors[selectedColor][i]);
			j.print(xmed-xstart+(i*2), 10, "H", arrayColors[selectedColor][i]);
			j.print(xmed-xstart+(i*2)+1, 10, "H", arrayColors[selectedColor][i]);
		}
		int phrase_size = (int)Math.floor(title.length() * 0.5);
		int startX = xmed - phrase_size;
		j.print(startX, 8, title);
	}
	
	public static void printMenu() {
		//each time this function is called it will cleanse and refresh the game window
		//updating the cursor position
		j.cls();
		int xtam = (int)Math.floor((j.xdim + 1) * 0.5);
		ArrayList<Integer> listX = new ArrayList<>();
		for (int i = 0; i < eleMenu.size(); i++) {
			int phrase_size = (int)Math.floor(eleMenu.get(i).length() * 0.5);
			int startX = xtam - phrase_size;
			listX.add(startX);
			j.print(startX, 12+i, eleMenu.get(i));
		}
		int pointerStartX = Collections.min(listX);
		j.print(pointerStartX-2, 12 + pointerMenu, ">");
		printTitleMenu();
		if (shopMenu || shopMenuBuy || shopMenuSell)
			j.print(map.global_fin().y + 3, 7, JSONParsing.getTranslationWord("money", "N", rootObjWords) + ": " + 
					user.getMoney());
		j.refresh();
	}
	
	public static void menuLoopInputs(int i) {
		//using currentTimeMillis and a cooldown type variable
		//it avoids the possibility of getting multiple inputs at once
		Long now = System.currentTimeMillis();
		if (now - lastTimeMenu > cooldMenu && isInputType(enterMenuInput,i)) {
			String chosen = eleMenu.get(pointerMenu);
			System.out.println("Escogido: " + chosen);
			if (chooseOptions) {
				String color = JSONParsing.getTranslationWord("color palette", "OTHERS", rootObjWords);
				String volume = JSONParsing.getTranslationWord("volume", "OTHERS", rootObjWords);
				String controls = JSONParsing.getTranslationWord("controls", "OTHERS", rootObjWords);
				String back = JSONParsing.getTranslationWord("back", "OTHERS", rootObjWords);
				
				if (chosen.equals(color)) {
					changeColors();
				} else if (chosen.equals(volume)) {
					activateDeactivateSound();
				} else if (chosen.equals(controls)) {
					chooseControls = true;
					chooseOptions = false;
					configureControls();
				} else if (chosen.equals(back)) {
					chooseOptions = false;
					if (gamePaused)
						configurePauseMenu();
					else
						configureMenu();
				}
			} else if (chooseControls){
				String rebind = JSONParsing.getTranslationWord("remap keys", "OTHERS", rootObjWords);
				String reset = JSONParsing.getTranslationWord("reset controls", "OTHERS", rootObjWords);
				String back = JSONParsing.getTranslationWord("back", "OTHERS", rootObjWords);
				
				if (chosen.equals(rebind)) {
					rebindKeys();
				} else if (chosen.equals(reset)) {
					keysMap.clear();
					for (Pair<String, Integer> original : defaultControls) {
						keysMap.put(original.getA(), original.getB());
					}
					_bindKeys();
					_bindKeysMenu();
				} else if (chosen.equals(back)) {
					chooseControls = false;
					chooseOptions = true;
					configureOptions();
				}
			} else if (gamePaused){
				String resume = JSONParsing.getTranslationWord("resume", "OTHERS", rootObjWords);
				String options = JSONParsing.getTranslationWord("options", "OTHERS", rootObjWords);
				String quit = JSONParsing.getTranslationWord("quit", "OTHERS", rootObjWords);
				if (chosen.equals(resume)) {
					gamePaused = false;
					printEverything(true);
				} else if (chosen.equals(options)) {
					chooseOptions = true;
					configureOptions();
				} else if (chosen.equals(quit)) {
					gamePaused = false;
					menu = true;
					try {
						main(null);
					} catch (JsonIOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (JsonSyntaxException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InstantiationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			} else if (chooseClass) {
				String knight = JSONParsing.getTranslationWord("knight", "N", rootObjWords);
				String mage = JSONParsing.getTranslationWord("mage", "N", rootObjWords);
				String back = JSONParsing.getTranslationWord("back", "OTHERS", rootObjWords);
				if (chosen.equals(knight.toUpperCase())) {
					userClass = knight;
					menu = false;
					chooseClass = false;
				} else if (chosen.equals(mage.toUpperCase())) {
					userClass = mage;
					menu = false;
					chooseClass = false;
				} else if (chosen.equals(back)) {
					chooseClass = false;
					configureMenu();
				}
			} else if (shopMenu) {
				String buy = JSONParsing.getTranslationWord("buy items", "OTHERS", rootObjWords);
				String sell = JSONParsing.getTranslationWord("sell items", "OTHERS", rootObjWords);
				String back = JSONParsing.getTranslationWord("back", "OTHERS", rootObjWords);
				if (chosen.equals(buy)) {
					shopMenuBuy = true;
					shopMenu = false;
					configureBuyMenu();
				} else if (chosen.equals(sell)) {
					shopMenuSell = true;
					shopMenu = false;
					configureSellMenu();
				} else if (chosen.equals(back)) {
					shopMenu = false;
					printEverything(true);
				}
			} else if (shopMenuBuy) {
				ArrayList<String> itemList = new ArrayList<>();
				for (Item item : user.getRoom().getShop().getStoredItems()) {
					itemList.add(item.getPrintableName() + ": " + item.getPrice());
				}
				String back = JSONParsing.getTranslationWord("back", "OTHERS", rootObjWords);
				boolean done = false;
				for (int n = 0; n < itemList.size(); n++) {
					String item = itemList.get(n);
					if (chosen.equals(item) && !done) {
						if (user.getRoom().getShop().buyItem(n)) {
							configureBuyMenu();
							done = true;
						}
					}
				}
				if (chosen.equals(back)) {
					shopMenu = true;
					shopMenuBuy = false;
					configureShopMenu();
				}
			} else if (shopMenuSell) {
				ArrayList<String> itemList = new ArrayList<>();
				for (Item item : user.getInventory()) {
					itemList.add(item.getPrintableName() + ": " + item.getSellPrice());
				}
				String back = JSONParsing.getTranslationWord("back", "OTHERS", rootObjWords);
				boolean done = false;
				for (int n = 0; n < itemList.size(); n++) {
					String item = itemList.get(n);
					if (chosen.equals(item) && !done) {
						user.getRoom().getShop().sellItem(n);
						configureSellMenu();
						done = true;
						break;
					}
				}
				if (chosen.equals(back)) {
					shopMenu = true;
					shopMenuSell = false;
					configureShopMenu();
				}
			} else {
				String start = JSONParsing.getTranslationWord("start game", "OTHERS", rootObjWords);
				String options = JSONParsing.getTranslationWord("options", "OTHERS", rootObjWords);
				String exit = JSONParsing.getTranslationWord("exit game", "OTHERS", rootObjWords);
				if (chosen.equals(start)) {
					chooseClass = true;
					configureChooseClass();
				} else if (chosen.equals(options)) {
					chooseOptions = true;
					configureOptions();
				} else if (chosen.equals(exit)) {
					System.exit(0);
				}
			}
			lastTimeMenu = now;
			if (isSoundActivated)
				beepSound.play();
			System.out.println("pointer: " + pointerMenu);
		}
		//pointer values are limited by the current eleMenu's size to avoid getting errors
		if (now - lastTimeMenu > cooldMenu && isInputType(downkeyMenuInput,i)) {
			if (pointerMenu >= eleMenu.size()-1)
				pointerMenu = 0;
			else
				pointerMenu++;
			lastTimeMenu = now;
			if (isSoundActivated)
				beepSound.play();
			System.out.println("pointer: " + pointerMenu);
		}
		if (now - lastTimeMenu > cooldMenu && isInputType(upkeyMenuInput,i)) {
			if (pointerMenu <= 0)
				pointerMenu = eleMenu.size()-1;
			else
				pointerMenu--;
			lastTimeMenu = now;
			if (isSoundActivated)
				beepSound.play();
			System.out.println("pointer: " + pointerMenu);
		}
	}
	
	public static void menuLoop() {
		//the configuration functions set up the different menu's options to choose from
		//and saves it in the variable called "eleMenu"
		//it also puts the pointer variable to 0
		configureMenu();
		while(menu) {
			//this function prints the contents of the eleMenu variable
			printMenu();
			//waits for the user to press a key
			CharKey e = j.inkey();
			int i = e.code;
			//depending on the type of menu (options menu, choose-class menu, shop menu, etc)
			//and the position of the pointer variable
			//it changes different variables' values
			menuLoopInputs(i);
		}
	}

	public static void main(String[] args) throws IOException, JsonIOException, JsonSyntaxException, InstantiationException, IllegalAccessException {
		//tip: use CTRL+left click (on any function) and CTRL+F to navigate through all the functions, it saves time
		_setLanguage();
		//configuring the game window and the text-description window
		configureTextArea();
		j.getTargetFrame().requestFocus();
		//getting json objects from the json files
		//they will be used to retrieve words when forming sentences
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
		//this variable sets a counter that, when it gets to 0, plays the water drop sound
		countAction = Util.rand(12, 25);
		//this one is used with enemies' cries
		possibleCry = Util.rand(3, 10);
		
		//setting up the controls
		_setKeyMap();
		menuLoop();
		
		if (!testMode){
			gameFlow();
		}
	}
}