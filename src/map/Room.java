package map;

import items.Item;
import items.consumables.LifePotion;
import items.consumables.MagicPotion;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import org.omg.PortableInterceptor.USER_EXCEPTION;

import com.google.gson.JsonObject;

import characters.active.ActiveCharacter;
import characters.active.enemies.Dragon;
import characters.active.enemies.Goblin;
import characters.active.enemies.Rat;
import grammars.grammars.GrammarIndividual;
import net.slashie.libjcsi.wswing.WSwingConsoleInterface;
import net.slashie.util.Pair;
import main.Main;
import util.RandUtil;
import util.Tuple;

/**
 * A room is randomly generated depending on the given size and
 * number of elements in it. These values don't have to be specified.
 * @author Dario
 *
 */
public class Room {
	
	/**
	 *  individual_x and individual_y represent the length of the room
	 *  itself, whereas global_x and global_y refers to the location of the
	 *  room inside a map
	 */
	private Tuple<Integer, Integer> individual_initial;
	private Tuple<Integer, Integer> individual_final;
	private Tuple<Integer, Integer> global_initial;
	private Tuple<Integer, Integer> global_final;
	private Map map;
	private ArrayList<Door> doors = new ArrayList<Door>();
	private ArrayList<Item> itemsRoom = new ArrayList<Item>();
	private ArrayList<Room> connected_rooms = new ArrayList<Room>();
	private ArrayList<ActiveCharacter> monsters = new ArrayList<ActiveCharacter>();
	private ArrayList<Tuple<Integer, Integer>> bordersMap = new ArrayList<Tuple<Integer, Integer>>();
	private ArrayList<Tuple<Integer, Integer>> borders = new ArrayList<Tuple<Integer, Integer>>();
	private ArrayList<Tuple<Integer, Integer>> corners = new ArrayList<Tuple<Integer, Integer>>();
	private ArrayList<Tuple<Integer, Integer>> insidePositions = new ArrayList<Tuple<Integer, Integer>>();
	private ArrayList<Tuple<Integer, Integer>> insidecolumns = new ArrayList<Tuple<Integer, Integer>>();
	private ArrayList<Tuple<Integer, Integer>> portals = new ArrayList<Tuple<Integer, Integer>>();
	private ArrayList<Tuple<Integer, Integer>> freePositions = new ArrayList<Tuple<Integer, Integer>>();
	int ini_x;
	int ini_y;
	int fin_x;
	int fin_y;
	
	public Room(Map map, Tuple<Integer, Integer> global_initial, Tuple<Integer, Integer> global_final){
		this.map = map;
		int individual_final_x = global_final.x - global_initial.x;
		int individual_final_y = global_final.y - global_initial.y;
		this.individual_initial = new Tuple<Integer, Integer>(0, 0);
		this.individual_final = new Tuple<Integer, Integer>(individual_final_x, individual_final_y);
		this.global_initial = global_initial;
		this.global_final = global_final;
		ini_x = this.getGlobal_initial().x;
		ini_y = this.getGlobal_initial().y;
		fin_x = this.getGlobal_final().x;
		fin_y = this.getGlobal_final().y;
		this.initializeBorders();
		this.initializeCorners();
		this.initializeInsidePositions();
		this.checkFreePositions();
	}
	
	public ArrayList<Tuple<Integer, Integer>> checkFreePositions() {
		ArrayList<Tuple<Integer, Integer>> newFreePositions = new ArrayList<Tuple<Integer, Integer>>();
		for (Tuple<Integer, Integer> pos : this.getInsidePositions()) {
			if (!RandUtil.containsTuple(pos, this.getInsidecolumns())) {
				newFreePositions.add(pos);
			}
		}
		this.setFreePositions(newFreePositions);
		return this.getFreePositions();
	}
	
	public ArrayList<Tuple<Integer, Integer>> reachablePositionsCharacter(ActiveCharacter character) {
		return character.getImmediateReachablePositions();
	}
	
	public ArrayList<String> printableReachablePositionsCharacter(ActiveCharacter character) {
		ArrayList<String> printablePositions = new ArrayList<String>();
		Tuple<Integer, Integer> currentPosition = character.getPosition();
		for (Tuple<Integer, Integer> pos : this.reachablePositionsCharacter(character)) {
			if (pos.x > currentPosition.x) {
				printablePositions.add("south");
			} else if (pos.x < currentPosition.x) {
				printablePositions.add("north");
			} else if (pos.y > currentPosition.y) {
				printablePositions.add("east");
			} else if (pos.y < currentPosition.y) {
				printablePositions.add("west");
			}
		}
		return printablePositions;
	}
	
	public void printItems(WSwingConsoleInterface j, ArrayList<Tuple<Integer, Integer>> visiblePositions){
		for (Item item : getItemsRoom()){
			if (RandUtil.containsTuple(item.getPosition(), visiblePositions)){
				j.print(item.getPosition().y, item.getPosition().x, item.getSymbolRepresentation(), main.Main.arrayColors[main.Main.selectedColor][5]);
			}
		}
	}
	
	public void printMonsters(WSwingConsoleInterface j, ArrayList<Tuple<Integer, Integer>> visiblePositions){
		for (ActiveCharacter monster : getMonsters()){
			if (RandUtil.containsTuple(monster.getPosition(), visiblePositions) && !monster.isDead()){
				j.print(monster.getPosition().y, monster.getPosition().x, monster.getSymbolRepresentation(), main.Main.arrayColors[main.Main.selectedColor][6]);
			}
		}
	}
	
	public ArrayList<ActiveCharacter> getMonstersPosition(Tuple<Integer, Integer> pos){
		ArrayList<ActiveCharacter> monsters = new ArrayList<ActiveCharacter>();
		for (ActiveCharacter monster : this.getMonsters()){
			if (RandUtil.sameTuple(pos, monster.getPosition())){
				monsters.add(monster);
			}
		}
		
		return monsters;
	}
	
	public ArrayList<Tuple<Integer, Integer>> getPositionsOfMonsters() {
		ArrayList<Tuple<Integer, Integer>> monsterPositions = new ArrayList<Tuple<Integer, Integer>>();
		for (ActiveCharacter monster: this.getMonsters()) {
			if (!monster.isDead()) {
				monsterPositions.add(monster.getPosition());
			}
		}
		return monsterPositions;
	}
	
	public ArrayList<Item> getItemsPosition(Tuple<Integer, Integer> pos){
		ArrayList<Item> items = new ArrayList<Item>();
		for (Item item : this.getItemsRoom()){
			if (RandUtil.sameTuple(pos, item.getPosition())){
				items.add(item);
			}
		}
		
		return items;
	}
	
	public ArrayList<Door> getDoorsPosition(Tuple<Integer, Integer> pos){
		ArrayList<Door> doors = new ArrayList<Door>();
		for (Door door: this.getDoors()){
			if (RandUtil.sameTuple(pos, door.getPositionRoom(pos))){
				doors.add(door);
			}
		}
		
		return doors;
	}
	
	public ArrayList<Tuple<Integer, Integer>> getPortalsPosition(Tuple<Integer, Integer> pos){
		ArrayList<Tuple<Integer, Integer>> portals = new ArrayList<Tuple<Integer, Integer>>();
		for (Tuple<Integer, Integer> portal: this.getPortals()){
			if (RandUtil.sameTuple(pos, portal)){
				portals.add(portal);
			}
		}
		
		return portals;
	}
	
	public boolean putItemRoom(Item item){
		if (isMapPositionHere(item.getPosition())){
			this.getItemsRoom().add(item);
			return true;
		}
		return false;
	}
	
	public Pair<Boolean, String> monsterTurn(ActiveCharacter user, GrammarIndividual grammarAttack, JsonObject rootObjWords){
		for (ActiveCharacter monster : this.getMonsters()){
			return monster.doTurn(user, grammarAttack, rootObjWords);
		}
		return new Pair<Boolean, String>(false, "");
	}
	
	public String getSymbolPosition(Tuple<Integer, Integer> tuple){
		for (ActiveCharacter monster: monsters){
			if (!monster.isDead()){
				if (RandUtil.sameTuple(monster.getPosition(), tuple)){
					return monster.getSymbolRepresentation();
				}
			}
		}
		
		for (Item item: itemsRoom){
			if (RandUtil.sameTuple(item.getPosition(), tuple)){
				return item.getSymbolRepresentation();
			}
		}
		
		return ".";
	}
	
	public boolean isInCorner(Tuple<Integer, Integer> tuple){
		for (Tuple<Integer, Integer> tuple2 : this.getCorners()){
			if (tuple.x == tuple2.x){
				if (tuple.y == tuple2.y){
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean isInside(Tuple<Integer, Integer> tuple){
		if (Main.debug){
			System.out.println("Initial Values: (" + ini_x + "," + ini_y + ")");
			System.out.println("Initial Values: (" + fin_x + "," + fin_y + ")");
			for (Tuple<Integer, Integer> pos : this.getInsidePositions()){
				System.out.println("Inside position: (" + pos.x + "," + pos.y + ")");
			}
		}
		for (Tuple<Integer, Integer> tuple2 : this.getInsidePositions()){
			if (tuple.x == tuple2.x){
				if (tuple.y == tuple2.y){
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean isADoor(Tuple<Integer, Integer> position){
		for (Door door : this.getDoors()){
			Tuple<Integer, Integer> posDoor = door.getPositionRoom(position);
			if (posDoor != null){
				if (posDoor.x == position.x && posDoor.y == position.y){
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean isMapPositionHere(Tuple<Integer, Integer> position){
		Tuple<Integer, Integer> initialPoint = this.getGlobal_initial();
		Tuple<Integer, Integer> finalPoint = this.getGlobal_final();
		if (position.x >= initialPoint.x && position.y >= initialPoint.y){
			if (position.x <= finalPoint.x && position.y <= finalPoint.y){
				return true;
			}
		}
		return false;
	}
	
	public Tuple<Integer, Integer> getRandomInsidePosition(){
		if (this.getInsidePositions().size() > 0) {
			return this.getInsidePositions().get(RandUtil.RandomNumber(0, this.getInsidePositions().size()));
		}
		return null;
	}
	
	public void initializeInsidePositions(){	
		for (int i = ini_x + 1; i < fin_x; i++){
			for (int j = ini_y + 1; j < fin_y; j++){
				insidePositions.add(new Tuple<Integer, Integer>(i, j));
			}
		}
	}
	
	public boolean initializePortals() {
		int initialNumberPortals = this.getPortals().size();
		int tries = 0;
		int maxTries = 10;
		while (this.getPortals().size() <= initialNumberPortals && tries < maxTries) {
			Tuple<Integer, Integer> pos = this.getRandomInsidePosition();
			if (pos != null) {
				if (!RandUtil.containsTuple(pos, this.getInsidecolumns()) && this.getItemsPosition(pos).size() <= 0
						&& this.getDoorsPosition(pos).size() <= 0){
					this.getPortals().add(pos);
					return true;
				}
			}
			tries++;
		}
		return false;
	}
	
	public ArrayList<Tuple<Integer, Integer>> getNextPositions(Tuple<Integer, Integer> position){
		ArrayList<Tuple<Integer, Integer>> nextPositions = new ArrayList<Tuple<Integer, Integer>>();
		ArrayList<Tuple<Integer, Integer>> finalNextPositions = new ArrayList<Tuple<Integer, Integer>>();
		nextPositions.add(new Tuple<Integer, Integer>(position.x, position.y));
		nextPositions.add(new Tuple<Integer, Integer>(position.x - 1, position.y));
		nextPositions.add(new Tuple<Integer, Integer>(position.x + 1, position.y));
		nextPositions.add(new Tuple<Integer, Integer>(position.x, position.y - 1));
		nextPositions.add(new Tuple<Integer, Integer>(position.x, position.y + 1));
		
		for(Tuple<Integer, Integer> tuple : nextPositions){
			if (RandUtil.containsTuple(tuple, this.getInsidePositions())){
				finalNextPositions.add(tuple);
			}
		}
		
		return finalNextPositions;
	}
	
	public void initializeCorners(){
		
		this.corners.add(new Tuple<Integer, Integer>(ini_x, ini_y));
		this.corners.add(new Tuple<Integer, Integer>(fin_x, fin_y));
		this.corners.add(new Tuple<Integer, Integer>(fin_x, ini_y));
		this.corners.add(new Tuple<Integer, Integer>(ini_x, fin_y));
	}
	
	public void initializeBorders(){
		
		for (int i = ini_y; i <= fin_y; i++){
			bordersMap.add(new Tuple<Integer, Integer>(i, ini_x));
			borders.add(new Tuple<Integer, Integer>(ini_x, i));
		}
		
		for (int i = ini_y; i <= fin_y; i++){
			bordersMap.add(new Tuple<Integer, Integer>(i, fin_x));
			borders.add(new Tuple<Integer, Integer>(fin_x, i));
		}
		
		for (int i = ini_x; i <= fin_x; i++){
			bordersMap.add(new Tuple<Integer, Integer>(ini_y, i));
			borders.add(new Tuple<Integer, Integer>(i, ini_y));
		}
		
		for (int i = ini_x; i <= fin_x; i++){
			bordersMap.add(new Tuple<Integer, Integer>(fin_y, i));
			borders.add(new Tuple<Integer, Integer>(i, fin_y));
		}
	}
	
	public void initializeColumns() {
		int numberColumns = Math.round(this.getInsidePositions().size() / 10);
		int tries = 0;
		ArrayList<Tuple<Integer, Integer>> doorPositions = new ArrayList<>();
		Tuple<Integer, Integer> doorPosition;
		
		for (Door d: this.getDoors()) {
			if (d.getRoom1() == this) {
				doorPosition = d.getPositionRoom1(); 
			} else {
				doorPosition = d.getPositionRoom2();
			}
			
			Tuple<Integer,Integer> pos1 = new Tuple<Integer, Integer>(doorPosition.x - 1, doorPosition.y);
			Tuple<Integer,Integer> pos2 = new Tuple<Integer, Integer>(doorPosition.x + 1, doorPosition.y);
			Tuple<Integer,Integer> pos3 = new Tuple<Integer, Integer>(doorPosition.x, doorPosition.y - 1);
			Tuple<Integer,Integer> pos4 = new Tuple<Integer, Integer>(doorPosition.x, doorPosition.y + 1);
			doorPositions.add(pos1);
			doorPositions.add(pos2);
			doorPositions.add(pos3);
			doorPositions.add(pos4);
		}
		ArrayList<Tuple<Integer, Integer>> dangerousPositions = new ArrayList<Tuple<Integer, Integer>>();
		 
		while (this.getInsidecolumns().size() <= numberColumns && this.getInsidePositions().size() > 0 && tries <= 15) {
			int randomNumber = RandUtil.RandomNumber(0, this.getInsidePositions().size());
			Tuple<Integer, Integer> columnPosition = this.getInsidePositions().get(randomNumber);
			if (!RandUtil.containsTuple(columnPosition, this.getInsidecolumns()) && 
					!RandUtil.containsTuple(columnPosition, doorPositions) &&
					!RandUtil.containsTuple(columnPosition, dangerousPositions)){
				dangerousPositions.add(new Tuple<Integer, Integer>(columnPosition.x - 1, columnPosition.y));
				dangerousPositions.add(new Tuple<Integer, Integer>(columnPosition.x + 1, columnPosition.y));
				dangerousPositions.add(new Tuple<Integer, Integer>(columnPosition.x, columnPosition.y - 1));
				dangerousPositions.add(new Tuple<Integer, Integer>(columnPosition.x, columnPosition.y + 1));
				this.getInsidecolumns().add(columnPosition);
			}
			tries++;
		}
	}
	
	public boolean isPortal(Tuple<Integer, Integer> position) {
		for (Tuple<Integer, Integer> portal : this.getPortals()){
			if (RandUtil.sameTuple(position, portal)) {
				return true;
			}
		}
		return false;
	}
	
	public void putRandomPotions() {
		if (this.checkFreePositions().size() > 0 && RandUtil.RandomNumber(0, 5) == 1) {
			int numberLife = RandUtil.RandomNumber(0, this.checkFreePositions().size());
			Tuple<Integer, Integer> positionLife = this.getFreePositions().get(numberLife);
			LifePotion lifePotion = new LifePotion(null, map, this, positionLife);
			this.getItemsRoom().add(lifePotion);
		}
		if (this.checkFreePositions().size() > 0 && RandUtil.RandomNumber(0, 5) == 1) {
			int numberMagic = RandUtil.RandomNumber(0, this.checkFreePositions().size());
			Tuple<Integer, Integer> positionMagic = this.getFreePositions().get(numberMagic);
			MagicPotion magicPotion = new MagicPotion(null, map, this, positionMagic);
			this.getItemsRoom().add(magicPotion);
		}
	}
	
	public void generateRandomEnemies(ActiveCharacter user) {
		if (RandUtil.RandomNumber(0, 2) == 1 && this.getFreePositions().size() > 0 && this.getMonsters().size() == 0) {
			int positionNumber = RandUtil.RandomNumber(0, this.checkFreePositions().size());
			Tuple<Integer, Integer> position = this.getFreePositions().get(positionNumber);
			int number = RandUtil.RandomNumber(0, 3);
			int level = RandUtil.RandomNumber(user.getLevel(), user.getLevel() + 2);
			switch(number) {
				case 0: 
					Rat rat = new Rat(this.getMap(), this, position, new ArrayList<String>(), level);
					this.getMonsters().add(rat);
					break;
				case 1: 
					Goblin goblin = new Goblin(this.getMap(), this, position, new ArrayList<String>(), level);
					this.getMonsters().add(goblin);
					break;
				case 2: 
					Dragon dragon = new Dragon(this.getMap(), this, position, new ArrayList<String>(), level);
					this.getMonsters().add(dragon);
					break;
			}
		}
	}
	
	public Tuple<Integer, Integer> getRandomPosition(){
		this.checkFreePositions();
		int value = RandUtil.RandomNumber(0, this.getInsidePositions().size());
		return this.getInsidePositions().get(value);
	}

	public ArrayList<Tuple<Integer, Integer>> getInsidecolumns() {
		return insidecolumns;
	}

	public void setInsidecolumns(ArrayList<Tuple<Integer, Integer>> insidecolumns) {
		this.insidecolumns = insidecolumns;
	}

	public Tuple<Integer, Integer> getIndividual_initial() {
		return individual_initial;
	}

	public void setIndividual_initial(Tuple<Integer, Integer> individual_initial) {
		this.individual_initial = individual_initial;
	}

	public Tuple<Integer, Integer> getIndividual_final() {
		return individual_final;
	}

	public void setIndividual_final(Tuple<Integer, Integer> individual_final) {
		this.individual_final = individual_final;
	}

	public Tuple<Integer, Integer> getGlobal_initial() {
		return global_initial;
	}

	public void setGlobal_initial(Tuple<Integer, Integer> global_initial) {
		this.global_initial = global_initial;
	}

	public Tuple<Integer, Integer> getGlobal_final() {
		return global_final;
	}

	public void setGlobal_final(Tuple<Integer, Integer> global_final) {
		this.global_final = global_final;
	}

	public ArrayList<Door> getDoors() {
		return doors;
	}

	public void setDoors(ArrayList<Door> doors) {
		this.doors = doors;
	}

	public ArrayList<Room> getConnected_rooms() {
		return connected_rooms;
	}

	public void setConnected_rooms(ArrayList<Room> connected_rooms) {
		this.connected_rooms = connected_rooms;
	}
	
	public ArrayList<ActiveCharacter> getMonsters() {
		return monsters;
	}

	public void setMonsters(ArrayList<ActiveCharacter> monsters) {
		this.monsters = monsters;
	}

	public ArrayList<Tuple<Integer, Integer>> getBordersMap(){
		return this.bordersMap;
	}
	
	public void setBordersMap(ArrayList<Tuple<Integer, Integer>> borders){
		this.bordersMap = borders;
	}
	
	public ArrayList<Tuple<Integer, Integer>> getBorders(){
		return this.borders;
	}
	
	public void setBorders(ArrayList<Tuple<Integer, Integer>> borders){
		this.borders = borders;
	}
	
	public ArrayList<Tuple<Integer, Integer>> getCorners(){
		return this.corners;
	}
	
	public void setCorners(ArrayList<Tuple<Integer, Integer>> corners){
		this.corners = corners;
	}
	
	public ArrayList<Tuple<Integer, Integer>> getInsidePositions(){
		return this.insidePositions;
	}
	
	public void setInsidePositions(ArrayList<Tuple<Integer, Integer>> insidePositions){
		this.insidePositions = insidePositions;
	}

	public ArrayList<Item> getItemsRoom() {
		return itemsRoom;
	}

	public void setItemsRoom(ArrayList<Item> itemsRoom) {
		this.itemsRoom = itemsRoom;
	}

	public ArrayList<Tuple<Integer, Integer>> getPortals() {
		return portals;
	}

	public void setPortals(ArrayList<Tuple<Integer, Integer>> portals) {
		this.portals = portals;
	}

	public ArrayList<Tuple<Integer, Integer>> getFreePositions() {
		return freePositions;
	}

	public void setFreePositions(ArrayList<Tuple<Integer, Integer>> freePositions) {
		this.freePositions = freePositions;
	}

	public Map getMap() {
		return map;
	}

	public void setMap(Map map) {
		this.map = map;
	}
}
