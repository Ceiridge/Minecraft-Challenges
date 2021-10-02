package org.ceiridge.mcchallenges.commands.impl;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.ceiridge.mcchallenges.MCChallenges;
import org.ceiridge.mcchallenges.challenges.impl.main.MainChallenge;

public class SpawnCmd extends Command {

	public SpawnCmd() {
		super("spawn");
	}

	@Override
	public boolean execute(CommandSender sender, String label, String[] args) {
		if(sender instanceof Player) {
			Player p = (Player) sender;
			
			if(MainChallenge.isLobby(p)) {
				MainChallenge.sendToWorld(MCChallenges.instance.lobbyWorld, p);
			} else {
				p.sendMessage(MCChallenges.formatMessage("§cYou have to be in the lobby to get to its spawn!"));
			}
			return true;
		}
		
		return false;
	}
}
