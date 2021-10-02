package org.ceiridge.mcchallenges.commands.impl;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.ceiridge.mcchallenges.MCChallenges;

public class ResetCmd extends Command {
	public ResetCmd() {
		super("reset");
	}

	@Override
	public boolean execute(CommandSender sender, String label, String[] args) {
		if (sender instanceof ConsoleCommandSender || sender.isOp()) {
			for (Player p : Bukkit.getOnlinePlayers()) {
				p.kickPlayer("§eThe challenge server is resetting... This may take 2-5 minutes");
			}

			Bukkit.getScheduler().scheduleSyncDelayedTask(MCChallenges.instance, new Runnable() {

				@Override
				public void run() {
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "stop");
				}
			}, 20l);

			return true;
		}
		return false;
	}

}
