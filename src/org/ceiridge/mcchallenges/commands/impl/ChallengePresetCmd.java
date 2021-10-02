package org.ceiridge.mcchallenges.commands.impl;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;
import org.ceiridge.mcchallenges.MCChallenges;
import org.ceiridge.mcchallenges.challenges.ChallengePreset;
import org.ceiridge.mcchallenges.challenges.ChallengePreset.RandomChallengePreset;

public class ChallengePresetCmd extends Command implements TabCompleter {
	public List<ChallengePreset> challengePresets = new ArrayList<>();
	public static ChallengePresetCmd instance;
	
	public ChallengePresetCmd() {
		super("challengepreset");
		instance = this;
		this.challengePresets.clear();
		this.challengePresets.add(new RandomChallengePreset());

		File presetFolder = new File(MCChallenges.instance.getDataFolder(), "presets");
		if (!presetFolder.exists())
			presetFolder.mkdirs();

		for (File presetFile : presetFolder.listFiles()) {
			if (presetFile.isFile()) {
				try {
					this.challengePresets.add(new ChallengePreset(presetFile.getName().split("\\.")[0], Files.readAllLines(presetFile.toPath())));
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public boolean execute(CommandSender sender, String label, String[] args) {
		if (sender instanceof ConsoleCommandSender || sender.isOp()) {
			if (args.length < 1) {
				return false;
			}

			String name = args[0];
			ChallengePreset preset;
			
			if ((preset = this.getChallengePresetByName(name)) != null) {
				preset.execute(sender);
			} else {
				sender.sendMessage(MCChallenges.formatMessage("Preset not found"));
			}

			return true;
		}
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		final List<String> completions = new ArrayList<>();
		
		if(args.length == 1) {
			ArrayList<String> presetNames = new ArrayList<>();
			for(ChallengePreset preset : this.challengePresets) {
				presetNames.add(preset.name);
			}
			
			StringUtil.copyPartialMatches(args[0], presetNames, completions);
		}
		
		return completions;
	}
	
	public ChallengePreset getChallengePresetByName(String name) {
		for(ChallengePreset preset : this.challengePresets) {
			if(preset.name.trim().equalsIgnoreCase(name.trim()))
				return preset;
		}
		
		return null;
	}
	
	public ChallengePreset getWinningVote() {
		ChallengePreset maximum = null;
		for(ChallengePreset preset : this.challengePresets) {
			if(maximum == null || maximum.votes < preset.votes)
				maximum = preset;
		}
		
		return maximum;
	}
}
