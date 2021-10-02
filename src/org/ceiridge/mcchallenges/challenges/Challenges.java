package org.ceiridge.mcchallenges.challenges;

import java.util.ArrayList;
import org.ceiridge.mcchallenges.challenges.impl.blockshuffle.BlockShuffleChallenge;
import org.ceiridge.mcchallenges.challenges.impl.deathmatch.DeathmatchChallenge;
import org.ceiridge.mcchallenges.challenges.impl.fastmode.FastModeChallenge;
import org.ceiridge.mcchallenges.challenges.impl.juggermanhunt.JuggerManhuntChallenge;
import org.ceiridge.mcchallenges.challenges.impl.main.MainChallenge;
import org.ceiridge.mcchallenges.challenges.impl.posswap.PosSwapChallenge;
import org.ceiridge.mcchallenges.challenges.impl.random.RandomDropsChallenge;
import org.ceiridge.mcchallenges.challenges.impl.random.RandomRecipesChallenge;
import org.ceiridge.mcchallenges.challenges.impl.winconditions.MainWinCondition;
import org.ceiridge.mcchallenges.challenges.impl.winconditions.PvEWinCondition;
import org.ceiridge.mcchallenges.challenges.impl.winconditions.TeamWinCondition;

public class Challenges {
	public ArrayList<Challenge> challenges = new ArrayList<>();
	
	public Challenges() {
		this.challenges.clear();
		
		this.challenges.add(new MainChallenge());
		this.challenges.add(new PosSwapChallenge());
		this.challenges.add(new BlockShuffleChallenge());
		this.challenges.add(new JuggerManhuntChallenge());
		this.challenges.add(new RandomDropsChallenge());
		this.challenges.add(new RandomRecipesChallenge());
		this.challenges.add(new FastModeChallenge());
		this.challenges.add(new DeathmatchChallenge());
		
		this.challenges.add(new MainWinCondition());
		this.challenges.add(new TeamWinCondition());
		this.challenges.add(new PvEWinCondition());
	}
	
	public Challenge getChallengeByClass(Class<? extends Challenge> clazz) {
		for(Challenge challenge : challenges) {
			if(challenge.getClass() == clazz)
				return challenge;
		}
		
		return null;
	}
	
	public Challenge getChallengeByName(String name) {
		for(Challenge challenge : challenges) {
			if(challenge.name.trim().equalsIgnoreCase(name.trim()))
				return challenge;
		}
		
		return null;
	}
}
