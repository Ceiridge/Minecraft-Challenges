package org.ceiridge.mcchallenges.challenges.impl.main;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.ceiridge.mcchallenges.MCChallenges;
import org.ceiridge.mcchallenges.challenges.Challenge;
import org.ceiridge.mcchallenges.challenges.Scheduler;
import org.ceiridge.mcchallenges.challenges.WinCondition;

public class ScoreboardScheduler extends Scheduler {
	public ScoreboardScheduler(Challenge challenge) {
		super(challenge, SchedulerType.SYNC_REPEATING, 20l, 20l);
	}

	@Override
	public void run(Challenge challenge) {
		Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
		Objective objective = board.registerNewObjective("InfoBoard", "dummy", "§e§lChallenges: " + MainAdScheduler.DISCORD_LINK.replace("https://", ""));
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);

		ArrayList<String> info = new ArrayList<String>();

		for (Challenge chall : MCChallenges.instance.challenges.challenges) {
			if (chall.isVisible())
				info.add("§a" + chall.name);
		}
		for (Challenge chall : MCChallenges.instance.challenges.challenges) {
			if (chall instanceof WinCondition && chall.enabled)
				info.add("§6" + chall.name);
		}

		info.add("*PLACEHOLDER*");
		info.add("§7Players: §a" + MCChallenges.getPlayers().size() + "§7/§a" + Bukkit.getOnlinePlayers().size());
		info.add("§7Teaming is");
		info.add((Boolean) MCChallenges.instance.challenges.getChallengeByClass(MainChallenge.class).settings.get("Teaming") ? "§aAllowed"
				: "§cForbidden");
		info.add("§7Hardcore is");
		info.add((Boolean) MCChallenges.instance.challenges.getChallengeByClass(MainChallenge.class).settings.get("Hardcore") ? "§cEnabled"
				: "§aDisabled");
		info.add("*PLACEHOLDER*");

		if (MCChallenges.instance.scoreMap.size() > 0) {
			for (String name : MCChallenges.instance.scoreMap.keySet()) {
				info.add(name + ": " + MCChallenges.instance.scoreMap.get(name));
			}

			info.add("*PLACEHOLDER*");
		}

		for (List<String> data : MCChallenges.instance.extraScoreData) {
			info.addAll(data);
			info.add("*PLACEHOLDER*");
		}

		for (int i = 0; i < info.size(); i++) {
			String key = info.get(i);
			if (key.equals("*PLACEHOLDER*")) {
				key = "----------" + StringUtils.repeat(" ", i);
			}

			Score score = objective.getScore(StringUtils.abbreviate(key, 40));
			score.setScore(info.size() - i);
		}

		for (Player p : Bukkit.getOnlinePlayers()) {
			p.setScoreboard(board);
		}
	}
}
