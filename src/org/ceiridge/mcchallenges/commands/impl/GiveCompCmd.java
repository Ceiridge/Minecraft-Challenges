package org.ceiridge.mcchallenges.commands.impl;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.ceiridge.mcchallenges.MCChallenges;
import org.ceiridge.mcchallenges.challenges.impl.main.MainChallenge;

public class GiveCompCmd extends Command {

	public GiveCompCmd() {
		super("givecomp");
	}

	@Override
	public boolean execute(CommandSender sender, String label, String[] args) {
		if (sender instanceof ConsoleCommandSender || sender.isOp()) {
			if (args.length < 1) {
				return false;
			}

			Player p = Bukkit.getPlayerExact(args[0]);
			if (p == null) {
				sender.sendMessage(MCChallenges.formatMessage("Player not found"));
			} else {
				MainChallenge.giveTrackingCompass(p);
			}

			return true;
		}
		return false;
	}
}
