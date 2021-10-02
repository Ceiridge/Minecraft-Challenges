package org.ceiridge.mcchallenges.challenges.impl.posswap;

import org.ceiridge.mcchallenges.TimeHelper;
import org.ceiridge.mcchallenges.challenges.Challenge;

public class PosSwapChallenge extends Challenge {
	public TimeHelper time = new TimeHelper(5 * 60 * 1000); // 5 mins

	public PosSwapChallenge() {
		super("PositionSwap", new String[] {});
		this.addScheduler(new PosSwapTimer(this, this.time));

		this.settings.put("DurationMins", 5);
		this.updateVars();
	}

	@Override
	public void reset() {
		this.time.reset();
	}

	private void updateVars() {
		int durMins = this.getDurationMins();
		this.explanations = new String[] {"Every " + durMins + " minutes, every alive player will be teleported to another random player.",
				"You will be informed about it 10 seconds before the teleportation."};

		this.time.setTargetMS(durMins * 60 * 1000);
	}

	@Override
	public boolean setSetting(String key, String value) {
		if (key.equals("DurationMins")) {
			this.settings.put(key, Integer.parseInt(value));
			this.updateVars();
			return true;
		}

		return false;
	}

	public int getDurationMins() {
		return (Integer) this.settings.get("DurationMins");
	}
}
