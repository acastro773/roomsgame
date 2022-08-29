package magic;

import java.util.ArrayList;

import characters.Character.Mood;
import characters.active.ActiveCharacter;
import util.RandUtil;
import util.Tuple;

public class ConfuseRay extends Spell {

	public ConfuseRay() {
		super(4, 10);
		this.setName("confuse ray");
		ArrayList<String> adjectives = new ArrayList<String>();
		adjectives.add("magic");
		this.setAdjectives(adjectives);
	}

	@Override
	public
	ArrayList<Tuple<Integer, Integer>> getDamagedPositions(ActiveCharacter user) {
		ArrayList<Tuple<Integer, Integer>> positionsToAdd = new ArrayList<Tuple<Integer, Integer>>();
		positionsToAdd.add(new Tuple<Integer, Integer>(0, 1));
		positionsToAdd.add(new Tuple<Integer, Integer>(0, -1));
		positionsToAdd.add(new Tuple<Integer, Integer>(1, 0));
		positionsToAdd.add(new Tuple<Integer, Integer>(-1, 0));
		ArrayList<Tuple<Integer, Integer>> damagedPositions = new ArrayList<Tuple<Integer, Integer>>();
		for(Tuple<Integer, Integer> tuple : positionsToAdd) {			
			damagedPositions.add(RandUtil.add(user.getPosition(), tuple));
		}
		return damagedPositions;
	}

	@Override
	public boolean checkEffect(ActiveCharacter userAffected) {
		int probConfusion = RandUtil.RandomNumber(0, 100);
		switch(userAffected.getMood()) {
		case CONFUSED:
			return false;
		default:
			//20% of probability to confuse
			if (probConfusion > 81) {
				userAffected.setConfusionTurns(RandUtil.RandomNumber(1, 4));
				userAffected.setMood(Mood.CONFUSED);
				return true;
			}
		}
		return false;
	}

}
