package magic;

import java.util.ArrayList;

import characters.active.ActiveCharacter;
import grammars.grammars.PrintableObject;
import util.Tuple;

public abstract class Spell extends PrintableObject{
	
	int damage;
	int manaCost;
	boolean hasBeenUsed = false;
	ArrayList<String> adjectives = new ArrayList<String>();
	public Spell (int damage, int manaCost) {
		super("spell", "", new ArrayList<String>(), new Tuple<Integer, Integer>(0, 0));
		this.damage = damage;
		this.manaCost = manaCost;
		ArrayList<String> prepositions = new ArrayList<String>();
		prepositions.add("with");
		this.setPrepositions(prepositions);
	}
	
	public abstract ArrayList<Tuple<Integer, Integer>> getDamagedPositions(ActiveCharacter user);
	
	public abstract boolean checkEffect(ActiveCharacter userAffected);

	public int getDamage() {
		return damage;
	}

	public void setDamage(int damage) {
		this.damage = damage;
	}

	public int getManaCost() {
		return manaCost;
	}

	public void setManaCost(int manaCost) {
		this.manaCost = manaCost;
	}

	public ArrayList<String> getAdjectives() {
		return adjectives;
	}

	public void setAdjectives(ArrayList<String> adjectives) {
		this.adjectives = adjectives;
	}

	public boolean isHasBeenUsed() {
		return hasBeenUsed;
	}

	public void setHasBeenUsed(boolean hasBeenUsed) {
		this.hasBeenUsed = hasBeenUsed;
	}

}
