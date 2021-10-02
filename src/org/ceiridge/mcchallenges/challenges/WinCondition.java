package org.ceiridge.mcchallenges.challenges;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.GameMode;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.meta.FireworkMeta;
import org.ceiridge.mcchallenges.MCChallenges;

public abstract class WinCondition extends Challenge implements Listener {
	public String[] winExplanations;

	public WinCondition(String name, String[] explanations) {
		super(name, null);
		this.winExplanations = explanations;
	}

	protected void win(String winnerStr, String how) {
		if (MCChallenges.instance.winner != null || !MCChallenges.instance.started)
			return;
		
		MCChallenges.instance.winner = winnerStr;
		MCChallenges.sendBigMessage("Game Over", "§a" + winnerStr + " §ewon the game! " + how);

		for (Player p : Bukkit.getOnlinePlayers()) {
			p.setGameMode(GameMode.SPECTATOR);
			p.sendTitle("Game Over", "§a" + winnerStr + " §7won the game!", 10, 20 * 7, 10);
		}
		
		for(Player p : Bukkit.getOnlinePlayers()) {
			for (int i = 0; i < 3; i++) {
				Firework fw = (Firework) p.getWorld().spawnEntity(p.getLocation(), EntityType.FIREWORK);
				FireworkMeta meta = fw.getFireworkMeta();

				meta.addEffect(FireworkEffect.builder().with(Type.STAR).withColor(Color.YELLOW).trail(true).build());
				meta.addEffect(FireworkEffect.builder().with(Type.BALL_LARGE).withColor(Color.NAVY).flicker(true).build());

				fw.setFireworkMeta(meta);
			}
		}
	}
	
	protected void win(String how, Player... winners) {
		String winnerStr = "";
		for (Player w : winners) {
			winnerStr += w.getDisplayName() + ", ";
		}
		if (winnerStr.length() == 0)
			return;
		winnerStr = winnerStr.substring(0, winnerStr.length() - 2);
		
		this.win(winnerStr, how);
	}
	
	protected void tie() {
		if (MCChallenges.instance.winner != null || !MCChallenges.instance.started)
			return;
		
		MCChallenges.sendBigMessage("Game Over", "It's a tie, because everybody died!");
		MCChallenges.instance.winner = "Tie";
	}
}
