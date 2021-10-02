package org.ceiridge.mcchallenges.challenges.impl.winconditions;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.ceiridge.mcchallenges.MCChallenges;
import org.ceiridge.mcchallenges.teams.PlayTeam;

public class TeamWinCondition extends MainWinCondition implements Listener {
	public TeamWinCondition() {
		super("TeamWinCondition", new String[] {"The last team that has living players wins.",
				"Alternatively, the first team to kill the ender dragon or to jump into the credits end portal wins."});
	}

	@EventHandler
	@Override
	public void onDeath(PlayerDeathEvent e) {
		if (!this.noDeathSched) {
			this.noDeathSched = true;

			Bukkit.getScheduler().scheduleSyncDelayedTask(MCChallenges.instance, new Runnable() {
				@Override
				public void run() {
					List<Player> leftPlayers = MCChallenges.getPlayers();
					List<PlayTeam> survivedTeams = new ArrayList<PlayTeam>();

					CONTINUE_TEAMS: for (PlayTeam pTeam : MCChallenges.instance.teams.teams) {
						for (Player p : leftPlayers) {
							if (pTeam.players.contains(p)) {
								survivedTeams.add(pTeam);
								continue CONTINUE_TEAMS;
							}
						}
					}

					if (survivedTeams.size() == 0) {
						TeamWinCondition.this.tie();
					} else if (survivedTeams.size() == 1) {
						TeamWinCondition.this.win(survivedTeams.get(0).getUserfriendlyName() + " Team",
								"They achieved this by being the last team alive");
					}

					noDeathSched = false;
				}
			}, 20l);
		}
	}

	@Override
	protected void win(String how, Player... winners) {
		if (winners.length > 0) {
			for (PlayTeam pTeam : MCChallenges.instance.teams.teams) {
				if (pTeam.players.contains(winners[0])) {
					this.win(pTeam.getUserfriendlyName() + " Team", how);
					return;
				}
			}
		}
	}
}
