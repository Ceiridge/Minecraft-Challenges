package org.ceiridge.mcchallenges.teams;

import java.util.ArrayList;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class PlayTeam {
	public ChatColor color;
	public ArrayList<Player> players;
	public boolean friendlyFire;
	public String friendlyName;
	
	public PlayTeam(ChatColor color) {
		this.color = color;
		this.players = new ArrayList<>();
		this.friendlyFire = false;
		this.friendlyName = null;
	}
	
	public String getUserfriendlyColor() {
		return this.color.name().substring(0, 1) + this.color.name().toLowerCase().substring(1);
	}
	
	public String getUserfriendlyName() {
		return this.friendlyName == null ? this.getUserfriendlyColor() : ("§" + this.color.getChar() + this.friendlyName);
	}
}
