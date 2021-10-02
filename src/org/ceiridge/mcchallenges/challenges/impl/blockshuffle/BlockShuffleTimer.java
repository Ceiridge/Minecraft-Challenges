package org.ceiridge.mcchallenges.challenges.impl.blockshuffle;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.ceiridge.mcchallenges.MCChallenges;
import org.ceiridge.mcchallenges.TimeHelper;
import org.ceiridge.mcchallenges.challenges.impl.main.TimedScheduler;

public class BlockShuffleTimer extends TimedScheduler {
	private BlockShuffleChallenge bchallenge;

	public BlockShuffleTimer(BlockShuffleChallenge challenge, TimeHelper time) {
		super(challenge, time, true, time.getTargetMS());
		this.bchallenge = challenge;
	}

	@Override
	public void onFinish() {
		for (Player p : MCChallenges.getPlayers()) {
			UUID uuid = p.getUniqueId();

			if (this.bchallenge.assignedBlocks.containsKey(uuid) && this.bchallenge.assignedBlocks.get(uuid) != null) {
				p.setHealth(0d);
				Bukkit.broadcastMessage(MCChallenges.formatMessage("§a" + p.getDisplayName() + " §7didn't find their block in time!"));
			}
		}
	}

	@Override
	public void reset() {
		super.reset();
		
		this.countdownMillis = this.time.getTargetMS();
		Bukkit.getScheduler().scheduleSyncDelayedTask(MCChallenges.instance, new Runnable() {

			@Override
			public void run() {
				bchallenge.scoreBoardBlocks.clear();

				for (Player p : MCChallenges.getPlayers()) {
					UUID uuid = p.getUniqueId();

					if (!bchallenge.assignedBlocks.containsKey(uuid) || bchallenge.assignedBlocks.get(uuid) == null) {
						Material mat;

						bchallenge.assignedBlocks.put(uuid,
								mat = bchallenge.possibleBlocks.get(ThreadLocalRandom.current().nextInt(0, bchallenge.possibleBlocks.size())));

						String userFriendlyMat = mat.name().toLowerCase().replace("_", " ");
						MCChallenges.sendBigMessage("BlockShuffle Task",
								"You will need to stand on a/an §a§l" + userFriendlyMat + " §r§ein " + bchallenge.getDurationMins() + " minutes or less.",
								p);

						bchallenge.scoreBoardBlocks.add("§7" + p.getDisplayName() + ": §c" + userFriendlyMat);
					}
				}
			}
		}, 1l);
	}

	@Override
	public void onCountdown(int secLeft, long millisLeft, boolean secChanged) {
		super.onCountdown(secLeft, millisLeft, secChanged);

		if (secChanged && secLeft <= 10) {
			for (Player p : MCChallenges.getPlayers()) {
				UUID uuid = p.getUniqueId();

				if (this.bchallenge.assignedBlocks.containsKey(uuid) && this.bchallenge.assignedBlocks.get(uuid) != null)
					this.countdownNotify("You're losing", secLeft, p);
			}
		}
	}
}
