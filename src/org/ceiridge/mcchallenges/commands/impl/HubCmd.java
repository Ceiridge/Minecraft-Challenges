package org.ceiridge.mcchallenges.commands.impl;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HubCmd extends Command {

	public HubCmd() {
		super("hub");
	}

	@Override
	public boolean execute(CommandSender sender, String label, String[] args) {
		if (sender instanceof Player) {
			Bukkit.dispatchCommand(sender, "spawn");
			return true;
		}
		return false;
	}
}
