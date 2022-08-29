package shop;

import java.util.ArrayList;

import characters.active.ActiveCharacter;
import grammars.grammars.GrammarIndividual;
import grammars.grammars.PrintableObject;
import grammars.parsing.JSONParsing;
import items.Item;
import items.consumables.LifePotion;
import items.consumables.MagicPotion;
import items.consumables.SuperLifePotion;
import items.consumables.SuperMagicPotion;
import items.wereables.Axe;
import items.wereables.LongSword;
import items.wereables.NormalArmor;
import items.wereables.NormalGloves;
import items.wereables.NormalHelmet;
import items.wereables.NormalPants;
import items.wereables.ShortSword;
import items.wereables.WereableArmor;
import items.wereables.WereableWeapon;
import main.Main;
import map.Map;
import map.Room;
import util.RandUtil;
import util.Tuple;

public class Shop extends PrintableObject{
	
	private Map map;
	private Room room;
	private String symbolRepresentation;
	private ArrayList<Item> storedItems = new ArrayList<>();
	
	public Shop(String name, String description, ArrayList<String> adjectives, Map map, Room room, Tuple<Integer, Integer> position
			, String symbolRepresentation) {
		super(name, description, adjectives, position);
		this.map = map;
		this.room = room;
		this.symbolRepresentation = symbolRepresentation;
		setRandomStoredItems();
	}
	
	public void addConsumable() {
		int level = Main.user.getLevel();
		int rand = RandUtil.RandomNumber(0, 10);
		ArrayList<Item> store = this.getStoredItems();
		if (level < 5) {
			switch(rand) {
			case 0:
				SuperLifePotion superLifePotion = new SuperLifePotion(null, this.getMap(), this.getRoom(), null, 0);
				superLifePotion.setRandomPrice();
				superLifePotion.setSellPriceInit();
				store.add(superLifePotion);
				break;
			case 1:
				SuperMagicPotion superMagicPotion = new SuperMagicPotion(null, this.getMap(), this.getRoom(), null, 0);
				superMagicPotion.setRandomPrice();
				superMagicPotion.setSellPriceInit();
				store.add(superMagicPotion);
				break;
			case 2:
			case 3:
			case 4:
			case 5:
				MagicPotion magicPotion = new MagicPotion(null, this.getMap(), this.getRoom(), null, 0);
				magicPotion.setRandomPrice();
				magicPotion.setSellPriceInit();
				store.add(magicPotion);
				break;
			default:
				LifePotion lifePotion = new LifePotion(null, this.getMap(), this.getRoom(), null, 0);
				lifePotion.setRandomPrice();
				lifePotion.setSellPriceInit();
				store.add(lifePotion);
				break;
			}
		} else {
			switch(rand) {
			case 0:
			case 1:
				LifePotion lifePotion = new LifePotion(null, this.getMap(), this.getRoom(), null, 0);
				lifePotion.setRandomPrice();
				lifePotion.setSellPriceInit();
				store.add(lifePotion);
				break;
			case 2:
			case 3:
				MagicPotion magicPotion = new MagicPotion(null, this.getMap(), this.getRoom(), null, 0);
				magicPotion.setRandomPrice();
				magicPotion.setSellPriceInit();
				store.add(magicPotion);
				break;
			case 4:
			case 5:
			case 6:
				SuperMagicPotion superMagicPotion = new SuperMagicPotion(null, this.getMap(), this.getRoom(), null, 0);
				superMagicPotion.setRandomPrice();
				superMagicPotion.setSellPriceInit();
				store.add(superMagicPotion);
				break;
			default:
				SuperLifePotion superLifePotion = new SuperLifePotion(null, this.getMap(), this.getRoom(), null, 0);
				superLifePotion.setRandomPrice();
				superLifePotion.setSellPriceInit();
				store.add(superLifePotion);
				break;
			}
		}
		this.setStoredItems(store);
	}
	
	public void addArmor() {
		int level = Main.user.getLevel();
		if (level > 3) {
			int randNum = 20 - level;
			if (randNum < 7)
				randNum = 7;
			int n = RandUtil.RandomNumber(0, randNum);
			int numberMagic = RandUtil.RandomNumber(0, 6);
			boolean isMagic = (numberMagic < 2);
			WereableArmor armor = null;
			switch(n) {
			//choose armor
			case 0:
				armor = new NormalArmor(null, this.getMap(), this.getRoom(), null, level, isMagic, 0);
				break;
			//choose gloves
			case 1:
			case 2:
			case 3:
				armor = new NormalGloves(null, this.getMap(), this.getRoom(), null, level, isMagic, 0);
				break;
			//choose helmet
			case 4:
			case 5:
				armor = new NormalHelmet(null, this.getMap(), this.getRoom(), null, level, isMagic, 0);
				break;
			//choose pants
			case 6:
				armor = new NormalPants(null, this.getMap(), this.getRoom(), null, level, isMagic, 0);
				break;
			default:
				break;
			}
			if (armor != null) {
				armor.setRandomPrice();
				armor.setSellPriceInit();
				ArrayList<Item> store = this.getStoredItems();
				store.add(armor);
				this.setStoredItems(store);
			}	
		}
	}
	
	public void addWeapon() {
		int number;
		WereableWeapon oneHandSword;
		int level = Main.user.getLevel();
		if (level < 3) {
			number = RandUtil.RandomNumber(0, 7);
			if (number == 0) {
				oneHandSword = new LongSword(null,this.getMap(), this.getRoom(), null, level, false, 0);
			} else
				oneHandSword = new ShortSword(null,this.getMap(), this.getRoom(), null, level, false, 0);
		} else if (level < 6) {
			if (RandUtil.RandomNumber(0, 5) == 0) {
				oneHandSword = new LongSword(null,this.getMap(), this.getRoom(), null, level, false, 0);
			} else
				oneHandSword = new ShortSword(null,this.getMap(), this.getRoom(), null, level, false, 0);
		} else {
			int possibility = RandUtil.RandomNumber(0, 8);
			if (possibility == 0) {
				oneHandSword = new Axe(null,this.getMap(), this.getRoom(), null, level, false, 0);
			} else if (possibility < 4)
				oneHandSword = new LongSword(null,this.getMap(), this.getRoom(), null, level, false, 0);
			else
				oneHandSword = new ShortSword(null,this.getMap(), this.getRoom(), null, level, false, 0);
		}
		oneHandSword.setRandomPrice();
		oneHandSword.setSellPriceInit();
		ArrayList<Item> store = this.getStoredItems();
		store.add(oneHandSword);
		this.setStoredItems(store);
	}
	
	public void setRandomStoredItems() {
		while(getStoredItems().size() < 4) {
			int randNum = RandUtil.RandomNumber(0, 4);
			//every shop has different items on sale
			//the higher the user's level, the greater the possibility of being able to buy
			//something more valuable and strong
			switch(randNum) {
			case 0:
				addWeapon();
				break;
			case 1:
				addArmor();
				break;
			default:
				addConsumable();
				break;
			}	
		}
	}
	
	public boolean buyItem(int pointer) {
		ActiveCharacter user = Main.user;
		int moneyGot = user.getMoney();
		ArrayList<Item> stored = this.getStoredItems();
		for (Item item : stored)
			System.out.println(item.getName() + " - " + item.getPrice());
		Item item = stored.get(pointer);
		//checks if the user has enough money to buy the selected item 
		if (moneyGot >= item.getPrice()) {
			if (user.getInventorySpace() > 0) {
				stored.remove(pointer);
				user.setMoney(moneyGot-item.getPrice());
				user.putItemInventory(item);
				GrammarIndividual grammarIndividual = Main.grammarGeneralDescription.getRandomGrammar();
				ArrayList<PrintableObject> names = new ArrayList<PrintableObject>();
				names.add(user);
				names.add(item);
				String message = main.Main._getMessage(grammarIndividual, names, "BUY", "BUY", true, false, false);
				Main.printMessage(message);
				return true;
			} else
				Main.printMessage(JSONParsing.getTranslationWord("not enough space", "OTHERS", Main.rootObjWords));
		} else
			Main.printMessage(JSONParsing.getTranslationWord("not enough money", "OTHERS", Main.rootObjWords));
		return false;
	}
	
	public void sellItem(int pointer) {
		ActiveCharacter user = Main.user;
		ArrayList<Item> inventory = user.getInventory();
		for (Item item : inventory)
			System.out.println(item.getName());
		Item item = inventory.get(pointer);
		System.out.println("A VENDER -> " + item.getName() + " - " + item.getSellPrice());
		user.setMoney(user.getMoney() + item.getSellPrice());
		inventory.remove(item);
		user.setInventory(inventory);
		GrammarIndividual grammarIndividual = Main.grammarGeneralDescription.getRandomGrammar();
		ArrayList<PrintableObject> names = new ArrayList<PrintableObject>();
		names.add(user);
		names.add(item);
		String message = main.Main._getMessage(grammarIndividual, names, "SELL", "SELL", true, false, false);
		Main.printMessage(message);
	}
	
	public Map getMap(){
		return map;
	}

	public Room getRoom() {
		return room;
	}

	public void setRoom(Room room) {
		this.room = room;
	}
	
	public String getSymbolRepresentation() {
		return symbolRepresentation;
	}

	public void setSymbolRepresentation(String symbolRepresentation) {
		this.symbolRepresentation = symbolRepresentation;
	}
	
	public ArrayList<Item> getStoredItems() {
		return storedItems;
	}
	
	public void setStoredItems(ArrayList<Item> storedItems) {
		this.storedItems = storedItems;
	}
	
}
