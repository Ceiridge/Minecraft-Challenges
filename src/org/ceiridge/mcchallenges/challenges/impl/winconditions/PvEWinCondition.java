package org.ceiridge.mcchallenges.challenges.impl.winconditions;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.ceiridge.mcchallenges.MCChallenges;
import org.ceiridge.mcchallenges.challenges.impl.main.MainChallenge;
import org.ceiridge.mcchallenges.teams.PlayTeam;

public class PvEWinCondition extends MainWinCondition implements Listener {

	public PvEWinCondition() {
		super("PvEWinCondition", new String[] {"Everybody wins if the game is beaten by killing the ender dragon."});
		this.settings.put("GiveCompass", true);
	}

	@Override
	public boolean setSetting(String key, String value) {
		if (key.equals("GiveCompass")) {
			this.settings.put(key, Boolean.parseBoolean(value));
			return true;
		}

		return false;
	}

	@EventHandler
	@Override
	public void onDeath(PlayerDeathEvent e) {}

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		PlayTeam allTeam = MCChallenges.instance.teams.getOrCreateTeamByColor(ChatColor.GREEN);

		allTeam.players.add(p);
		MCChallenges.instance.teams.setupTeams();
	}

	@Override
	protected void win(String how, Player... winners) {
		this.win("Everyone", how);
	}

	@Override
	public void reset() {
		if (this.enabled) {
			PlayTeam allTeam = MCChallenges.instance.teams.getOrCreateTeamByColor(ChatColor.GREEN);
			allTeam.friendlyFire = false;

			Player tpTarget = null;
			for (Player p : MCChallenges.getPlayers()) {
				allTeam.players.add(p);
				if ((Boolean) this.settings.get("GiveCompass")) {
					MainChallenge.giveTrackingCompass(p);
				}
				
				if (tpTarget == null) {
					tpTarget = p;
					continue;
				}
				p.teleport(tpTarget);
			}

			MCChallenges.instance.teams.setupTeams();
		}
	}
}
