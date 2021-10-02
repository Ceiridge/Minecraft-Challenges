package org.ceiridge.mcchallenges.challenges;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.ceiridge.mcchallenges.commands.impl.ChallengePresetCmd;

public class ChallengePreset {
	public String name;
	public List<String> commands;
	public int votes;
	
	public ChallengePreset(String name, List<String> commands) {
		this.name = name;
		this.commands = commands;
	}
	
	public void execute(CommandSender sender) {
		for (String line : this.commands) {
			if(line.startsWith("#"))
				continue;
			
			if (line.startsWith("/") && line.length() > 2)
				line = line.substring(1);
			Bukkit.dispatchCommand(sender, line);
		}
	}
	
	public static class RandomChallengePreset extends ChallengePreset {
		public RandomChallengePreset() {
			super("Random Challenge", null);
			this.votes = 1;
		}

		@Override
		public void execute(CommandSender sender) {
			ArrayList<ChallengePreset> possibleChallenges = new ArrayList<>();
			
			for(ChallengePreset preset : ChallengePresetCmd.instance.challengePresets) {
				if(preset != this)
					possibleChallenges.add(preset);
			}
			
			possibleChallenges.get(ThreadLocalRandom.current().nextInt(0, possibleChallenges.size())).execute(sender);
		}
	}
}
