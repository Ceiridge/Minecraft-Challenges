package org.ceiridge.mcchallenges.challenges.impl.posswap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.ceiridge.mcchallenges.MCChallenges;
import org.ceiridge.mcchallenges.TimeHelper;
import org.ceiridge.mcchallenges.challenges.Challenge;
import org.ceiridge.mcchallenges.challenges.impl.main.TimedScheduler;

public class PosSwapTimer extends TimedScheduler {

	public PosSwapTimer(Challenge challenge, TimeHelper time) {
		super(challenge, time, true);
	}

	@Override
	public void onFinish() {
		HashMap<Player, Location> playerLocs = new HashMap<>();
		ArrayList<Player> usedPlayers = new ArrayList<Player>();

		for (Player p : MCChallenges.getPlayers()) {
			playerLocs.put(p, p.getLocation());
		}

		for (Player p : MCChallenges.getPlayers()) {
			ArrayList<Player> targetPlayers = new ArrayList<Player>();

			for (Player pp : MCChallenges.getPlayers()) {
				if (!usedPlayers.contains(pp) && p != pp)
					targetPlayers.add(pp);
			}

			if (targetPlayers.size() > 0) {
				Player target = targetPlayers.get(ThreadLocalRandom.current().nextInt(0, targetPlayers.size()));

				p.teleport(playerLocs.get(target));
				//						p.sendBlockChange(p.getLocation().subtract(0, 1, 0), Material.STONE.createBlockData());
				usedPlayers.add(target);
			}
			
			MCChallenges.playSuccessEffect(p);
		}
	}

	@Override
	public void onCountdown(int secLeft, long millisLeft, boolean secChanged) {
		super.onCountdown(secLeft, millisLeft, secChanged);
		if (secChanged)
			this.countdownNotifyAll("Swapping", secLeft);
	}
}
