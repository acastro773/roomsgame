package grammars.grammars;

import java.util.ArrayList;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import grammars.parsing.JSONParsing;
import main.Main;
import net.slashie.util.Pair;

public abstract class GrammarSelector {
	private static String[] WORD_IMPORTANCE = {"SIMPLE", "SIMPLEPREP", "NP", "N", "DET", "GENERAL", "V", "ADJ", "ADJECTIVE"};
	private GrammarIndividual grammar;
	private JsonObject wordsGrammar;
	
	public GrammarSelector(GrammarIndividual grammar, JsonObject wordsGrammar) {
		this.grammar = grammar;
		this.wordsGrammar = wordsGrammar;
	}
	
	protected abstract ArrayList<Pair<String, JsonArray>> fillWords();
	protected abstract ArrayList<Pair<String, JsonArray>> changeValue(ArrayList<Pair<String, JsonArray>> sentenceArray, String valueToChange, String changeToValue, String typeChangeToValue);
	
	protected String getImportantRestriction(String value1Type, String value1, String value2Type, String value2) {
		if (Main.debug) {
			System.out.println("I receive value1Type: " + value1Type + " value1: " + value1 + " value2Type: " 
				+ value2Type + " value2: " + value2);
		}
		for (int i = 0; i < WORD_IMPORTANCE.length; i++) {
			if (WORD_IMPORTANCE[i].equals(value1Type)) {
				return value2;
			}
			if (WORD_IMPORTANCE[i].equals(value2Type)) {
				return value1;
			}
		}
		return "";
	}
	
	protected ArrayList<Pair<String, JsonArray>> applyRestrictions(Pair<String, String> restriction, 
			ArrayList<Pair<String, JsonArray>> sentenceArray, ArrayList<Integer> numItems, String type) {
		
		ArrayList<String> grammar = this.getGrammar().getGrammar().get("keys");
		String firstType = restriction.getA();
		String secondType = restriction.getB();
		System.out.println("Restricciones: " + firstType + " - " + secondType);
		JsonArray restrictions1 = null;
		JsonArray restrictions2 = null;
		String restrictions1Value = "";
		String restrictions2Value = "";
		if (numItems == null) {
			restrictions1Value = sentenceArray.get(grammar.indexOf(firstType)).getA();
			restrictions2Value = sentenceArray.get(grammar.indexOf(secondType)).getA();
			restrictions1 = sentenceArray.get(grammar.indexOf(firstType)).getB();
			restrictions2 = sentenceArray.get(grammar.indexOf(secondType)).getB();
		} else {
			int wordPos = 0;
			String type2nd = secondType.split("_")[0];
			//if the structure is SIMPLEPREP, the word to consider in comparing to the first type should be N
			//if the structure is SIMPLEPREPPOS, the word to consider in comparing to the first type should be POS
			//if not, the word position doesn't matter
			switch(type2nd) {
			case "SIMPLEPREP":
				wordPos = 2;
				break;
			case "SIMPLEPREPPOS":
				wordPos = 1;
				System.out.println("Valor ahora: " + wordPos);
				break;
			default:
				wordPos = 0;
				break;
			}
			int totalFirstItem = 0;
			int totalSecondItem = 0;
			for (int i = 0; i <= grammar.indexOf(firstType); i++) {
				totalFirstItem += numItems.get(i);
			}
			restrictions1 = sentenceArray.get(totalFirstItem - 1).getB();
			restrictions1Value = sentenceArray.get(totalFirstItem - 1).getA();
			for (int i = 0; i < grammar.indexOf(secondType); i++) {
				totalSecondItem += numItems.get(i);
			}
			restrictions2 = sentenceArray.get(totalSecondItem + wordPos).getB();
			restrictions2Value = sentenceArray.get(totalSecondItem + wordPos).getA();
			if (grammar.indexOf(secondType) == 0) {
				restrictions2 = sentenceArray.get(0).getB();
				restrictions2Value = sentenceArray.get(0).getA();
			}
		}
		String value1 = restrictions1Value;
		String value2 = restrictions2Value;
		String value1Num = JSONParsing.getElement(restrictions1, type);
		String value2Num = JSONParsing.getElement(restrictions2, type);
		if (value1Num != null && value2Num != null && !value1Num.equals(value2Num) 
				&& value1Num.length() > 0 && value2Num.length() > 0) {
			String changeToValue = "";
			String typeFirstRestriction = firstType.substring(0, firstType.indexOf("_"));
			String typeSecondRestriction = secondType.substring(0, secondType.indexOf("_"));
			String toChange = getImportantRestriction(typeFirstRestriction, value1, typeSecondRestriction, value2);
			String typeChangeToValue = "";
			String typeGen = "";
			if (type.equals("gen")) {
				typeGen = value1Num;
			}
			if (toChange.equals(value1)) {
				changeToValue = JSONParsing.getElement(restrictions1, type + "opposite");
				typeChangeToValue = typeFirstRestriction; 
				 
			} else {
				changeToValue = JSONParsing.getElement(restrictions2, type + "opposite" + typeGen);
				typeChangeToValue = typeSecondRestriction;
			}
			System.out.println("changetovalue: " + changeToValue + " - type1st: " + typeFirstRestriction
					+ " - type2nd: " + typeSecondRestriction + " ::: toChange: " + toChange);
			this.changeValue(sentenceArray, toChange, changeToValue, typeChangeToValue);
		}
		return sentenceArray;
	}
	
	protected ArrayList<Pair<String, JsonArray>> applyRestrictions(ArrayList<Pair<String, JsonArray>> sentenceArray) {
		for(Pair<String, String> restriction : this.getGrammar().getRestrictions()) {
			int dotPointA = restriction.getA().indexOf(".");
			int dotPointB = restriction.getB().indexOf(".");
			String restrictionType = restriction.getA().substring(dotPointA + 1, restriction.getA().length());
			String elementA = "";
			String elementB = "";
			Pair<String, String> pair = null;
			if (Main.debug) {
				for (Pair<String, JsonArray> a : sentenceArray) {
					System.out.println(a.getA() + " ");
				}
			}
			switch (restrictionType) {
				case "num": 
					elementA = restriction.getA().substring(0, dotPointA);
					elementB = restriction.getB().substring(0, dotPointB);
					pair = new Pair<String, String>(elementA, elementB);
					sentenceArray = applyRestrictions(pair, sentenceArray, null, "num");
					break;
				case "gen": 
					elementA = restriction.getA().substring(0, dotPointA);
					elementB = restriction.getB().substring(0, dotPointB);
					System.out.println("ACÁ ENTRÓ: " + elementA + " --- " + elementB);
					pair = new Pair<String, String>(elementA, elementB);
					sentenceArray = applyRestrictions(pair, sentenceArray, null, "gen");
					break;
			}
		}
		return sentenceArray;
	}
	
	public boolean emptySentenceArray(ArrayList<Pair<String, JsonArray>> sentenceArray) {
		for (Pair<String, JsonArray> a : sentenceArray) {
			if (a == null) return true;
		}
		return false;
	}
	
	protected ArrayList<String> getGrammarTypes() {
		ArrayList<String> parsedGrammarTypes = new ArrayList<String>();
		ArrayList<String> grammarTypes = this.getGrammar().getGrammar().get("keys");
		for (int i = 0; i < grammarTypes.size(); i++) {
			parsedGrammarTypes.add(this.returnParseString(grammarTypes.get(i), "_"));
		}
		return parsedGrammarTypes;
	}
	
	public String returnParseString(String string, String element) {
		return string.substring(0, string.indexOf(element));
	}
	
	public abstract String getRandomSentence();
	
	public GrammarIndividual getGrammar() {
		return grammar;
	}

	public void setGrammar(GrammarIndividual grammar) {
		this.grammar = grammar;
	}

	public JsonObject getWordsGrammar() {
		return wordsGrammar;
	}

	public void setWordsGrammar(JsonObject wordsGrammar) {
		this.wordsGrammar = wordsGrammar;
	}
}
