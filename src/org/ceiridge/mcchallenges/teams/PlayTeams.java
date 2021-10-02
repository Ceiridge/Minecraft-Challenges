package org.ceiridge.mcchallenges.teams;

import java.util.ArrayList;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class PlayTeams {
	public ArrayList<PlayTeam> teams = new ArrayList<PlayTeam>();

	public PlayTeams() {
		this.teams.clear();
	}

	public void setupTeams() {
		for(PlayTeam t : this.teams) {
			for(Player p : t.players) {
				p.setDisplayName("§" + t.color.getChar() + p.getName());
				p.setPlayerListName("§" + t.color.getChar() + p.getName());
			}
		}
	}

	public PlayTeam getOrCreateTeamByColor(ChatColor color) {
		for (PlayTeam team : this.teams) {
			if (team.color == color)
				return team;
		}

		PlayTeam team = new PlayTeam(color);
		this.teams.add(team);
		return team;
	}
}
