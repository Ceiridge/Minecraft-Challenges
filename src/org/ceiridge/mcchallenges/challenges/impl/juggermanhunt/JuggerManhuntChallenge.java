package org.ceiridge.mcchallenges.challenges.impl.juggermanhunt;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.ceiridge.mcchallenges.MCChallenges;
import org.ceiridge.mcchallenges.challenges.Challenge;
import org.ceiridge.mcchallenges.challenges.impl.main.MainChallenge;
import org.ceiridge.mcchallenges.teams.PlayTeam;

public class JuggerManhuntChallenge extends Challenge {

	public JuggerManhuntChallenge() {
		super("JuggernautManhunt", new String[] {"A random player will be the hunter and instantly gets a full diamond equipment.",
				"The hunter also receives a tracking compass that only works overworld."});
	}

	@Override
	public void reset() {
		if (this.enabled) {
			List<Player> players = MCChallenges.getPlayers();

			if (players.size() >= 2) {
				Player juggernaut = players.get(ThreadLocalRandom.current().nextInt(0, players.size()));
				Bukkit.broadcastMessage(MCChallenges.formatMessage("§a" + juggernaut.getDisplayName() + "§a is the juggernaut!"));

				juggernaut.getInventory().addItem(new ItemStack(Material.DIAMOND_SWORD), new ItemStack(Material.COOKED_BEEF, 3));
				juggernaut.getInventory()
						.setArmorContents(new ItemStack[] {new ItemStack(Material.DIAMOND_BOOTS), new ItemStack(Material.DIAMOND_LEGGINGS),
								new ItemStack(Material.DIAMOND_CHESTPLATE), new ItemStack(Material.DIAMOND_HELMET)});
				MainChallenge.giveTrackingCompass(juggernaut);

				PlayTeam hunted = MCChallenges.instance.teams.getOrCreateTeamByColor(ChatColor.GREEN);
				PlayTeam hunter = MCChallenges.instance.teams.getOrCreateTeamByColor(ChatColor.RED);

				hunter.players.add(juggernaut);
				hunter.friendlyName = "Juggernaut";
				hunted.friendlyFire = false;
				hunted.friendlyName = "Hunted";

				Player playerTp = null;
				for (Player p : players) {
					if (p != juggernaut) {
						if (playerTp == null) {
							playerTp = p;
						} else {
							p.teleport(playerTp);
						}

						hunted.players.add(p);
					}
				}

				MCChallenges.instance.teams.setupTeams();
			}
		}
	}
}
