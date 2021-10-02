package org.ceiridge.mcchallenges.commands.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;
import org.ceiridge.mcchallenges.MCChallenges;
import org.ceiridge.mcchallenges.challenges.Challenge;

public class ChallengeCmd extends Command implements TabCompleter {
	private static final String[] ARGS1 = {"enable", "disable", "set"};

	public ChallengeCmd() {
		super("challenge");
	}

	@Override
	public boolean execute(CommandSender sender, String var2, String[] args) {
		if (sender instanceof ConsoleCommandSender || sender.isOp()) {
			if (args.length < 2) {
				for (Challenge challenge : MCChallenges.instance.challenges.challenges) {
					sender.sendMessage(
							MCChallenges.formatMessage("§r" + challenge.name + " §8is " + (challenge.enabled ? "§aenabled" : "§cdisabled")));
				}
				return true;
			} else {
				for (Challenge challenge : MCChallenges.instance.challenges.challenges) {
					if (challenge.name.equalsIgnoreCase(args[0])) {
						switch (args[1].toLowerCase()) {
							case "enable":
								challenge.dontEnableTwice = false;
								challenge.setEnabled(true);
								sender.sendMessage(MCChallenges.formatMessage(challenge.name + " enabled."));
								return true;
							case "disable":
								challenge.setEnabled(false);
								sender.sendMessage(MCChallenges.formatMessage(challenge.name + " disabled."));
								return true;
							case "set": {
								if (args.length < 4) {
									return false;
								}

								if (challenge.setSetting(args[2], args[3])) {
									sender.sendMessage(
											MCChallenges.formatMessage("Set §a" + args[2] + " §8to §a" + args[3] + " §8for §a" + challenge.name));
								} else {
									sender.sendMessage(MCChallenges.formatMessage("§cCouldn't set or find setting!"));
								}
								return true;
							}
							default:
								return false;
						}
					}
				}
			}
		}

		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		final List<String> completions = new ArrayList<>();

		if (args.length == 1) {
			final List<String> challengeNames = new ArrayList<>();
			for (Challenge challenge : MCChallenges.instance.challenges.challenges)
				challengeNames.add(challenge.name);

			StringUtil.copyPartialMatches(args[0], challengeNames, completions);
		} else if (args.length == 2) {
			StringUtil.copyPartialMatches(args[1], Arrays.asList(ARGS1), completions);
		} else if (args.length == 3 && args[1].equalsIgnoreCase("set")) {
			Challenge chall = MCChallenges.instance.challenges.getChallengeByName(args[0]);
			if (chall != null)
				StringUtil.copyPartialMatches(args[2], chall.settings.keySet(), completions);
		}

		return completions;
	}
}
