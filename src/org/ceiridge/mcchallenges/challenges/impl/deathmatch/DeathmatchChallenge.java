package org.ceiridge.mcchallenges.challenges.impl.deathmatch;

import org.bukkit.entity.Player;
import org.ceiridge.mcchallenges.MCChallenges;
import org.ceiridge.mcchallenges.challenges.Challenge;
import org.ceiridge.mcchallenges.challenges.impl.main.MainChallenge;

public class DeathmatchChallenge extends Challenge {

	public DeathmatchChallenge() {
		super("Deathmatch", new String[] {"The game is sped up and the goal is the kill all others.", "You will get a tracker to find others."});
	}

	@Override
	public void reset() {
		if(this.enabled) {
			for(Player p : MCChallenges.getPlayers()) {
				MainChallenge.giveTrackingCompass(p);
			}
		}
	}
}
