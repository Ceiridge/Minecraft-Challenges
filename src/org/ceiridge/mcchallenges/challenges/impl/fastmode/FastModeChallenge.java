package org.ceiridge.mcchallenges.challenges.impl.fastmode;

import org.ceiridge.mcchallenges.challenges.Challenge;

public class FastModeChallenge extends Challenge {

	public FastModeChallenge() {
		super("FastMode", new String[] {"Everyone gets positive status effects like speed, haste or luck to accelerate the game."});
		this.addScheduler(new FastModeScheduler(this));
		this.isModifier = true;
	}
}
