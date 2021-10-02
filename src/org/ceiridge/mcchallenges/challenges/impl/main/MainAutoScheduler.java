package org.ceiridge.mcchallenges.challenges.impl.main;

import java.io.File;
import java.util.ArrayList;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.libs.org.apache.commons.io.FileUtils;
import org.bukkit.entity.Player;
import org.ceiridge.mcchallenges.MCChallenges;
import org.ceiridge.mcchallenges.TimeHelper;
import org.ceiridge.mcchallenges.challenges.Challenge;
import org.ceiridge.mcchallenges.challenges.ChallengePreset;
import org.ceiridge.mcchallenges.challenges.Scheduler;
import org.ceiridge.mcchallenges.commands.impl.ChallengePresetCmd;

public class MainAutoScheduler extends Scheduler {
	private MainChallenge mchallenge;
	private ArrayList<String> scoreboardText = new ArrayList<>();
	private TimeHelper starter = new TimeHelper(2 * 60 * 1000);

	public MainAutoScheduler(MainChallenge challenge) {
		super(challenge, SchedulerType.SYNC_REPEATING, 10, 10);
		this.mchallenge = challenge;
	}

	@Override
	public void run(Challenge challenge) {
		MCChallenges.instance.playWorld = Bukkit.getWorld("world"); // Update the world, because it sometimes resets
		this.updateScoreboard();
		int playerCount = Bukkit.getOnlinePlayers().size();

		if (!MCChallenges.instance.started) {
			if (playerCount >= 2) {
				if (this.starter.hasReached()) {
					this.starter.reset();
					ChallengePresetCmd.instance.getWinningVote().execute(Bukkit.getConsoleSender());
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "start");
				}
				return;
			}

			if (playerCount == 0) {
				((MainChallenge) MCChallenges.instance.challenges.getChallengeByClass(MainChallenge.class)).votedPlayers.clear();

				for (ChallengePreset cp : ChallengePresetCmd.instance.challengePresets) {
					cp.votes = 0;
				}
			}
			
			MCChallenges.instance.winner = null;
		} else {
			if (MCChallenges.instance.winner != null) {
				Bukkit.broadcastMessage(MCChallenges.formatMessage("§4§lHOLD ON! THE SERVER IS RESETTING THE WORLD! EXPECT HUGE LAGS"));

				for (Player p : Bukkit.getOnlinePlayers()) {
					p.getInventory().clear();
					p.setExp(0f);
					p.setDisplayName(p.getName());
					p.setPlayerListName(p.getName());
					MainChallenge.sendToWorld(MCChallenges.instance.lobbyWorld, p);
				}

				MCChallenges.instance.winner = null;
				MCChallenges.instance.extraScoreData.clear();
				MCChallenges.instance.scoreMap.clear();
				MCChallenges.instance.onDisable();

				MCChallenges.instance.started = false;
				MCChallenges.instance.onEnable();

				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "save-off");
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mv unload world");
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mv unload world_nether");
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mv unload world_the_end");

				try {
					FileUtils.deleteDirectory(new File("world"));
					FileUtils.deleteDirectory(new File("world_nether"));
					FileUtils.deleteDirectory(new File("world_the_end"));
				} catch (Throwable e) {
					e.printStackTrace();
				}

				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mv create world normal");
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mv create world_nether nether");
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mv create world_the_end end");

				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mv modify set difficulty easy world");
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mv modify set difficulty easy world_nether");
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mv modify set difficulty easy world_the_end");
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "save-on");
			} else if (playerCount == 0) {
				MCChallenges.instance.winner = "Everybody left";
			}
		}

		this.starter.reset();
	}

	@Override
	public void reset() {
		if (this.mchallenge.enabled) {
			MCChallenges.instance.extraScoreData.add(this.scoreboardText);
		} else {
			MCChallenges.instance.extraScoreData.remove(this.scoreboardText);
		}

		this.starter.reset();
	}

	private void updateScoreboard() {
		this.scoreboardText.clear();

		if (!MCChallenges.instance.started) {
			int startSec = (int) Math.ceil((this.starter.getTargetMS() - this.starter.getReachedMS()) / 1000d);
			if (startSec >= (int) Math.ceil(this.starter.getTargetMS() / 1000d))
				this.scoreboardText.add("§7Not enough players to start");
			else
				this.scoreboardText.add("§7Starting in: §e" + startSec + "s");

			this.scoreboardText.add("§7Winning Vote:");
			this.scoreboardText.add("§e" + ChallengePresetCmd.instance.getWinningVote().name);
		} else {
			this.scoreboardText.add("§7The server resets when:");
			this.scoreboardText.add("§7- Somebody wins");
			this.scoreboardText.add("§7- Everybody leaves");
		}
	}
}
