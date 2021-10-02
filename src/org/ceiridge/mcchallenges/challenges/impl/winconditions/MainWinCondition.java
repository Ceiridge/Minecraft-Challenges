package org.ceiridge.mcchallenges.challenges.impl.winconditions;

import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.World.Environment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityPortalEnterEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.ceiridge.mcchallenges.MCChallenges;
import org.ceiridge.mcchallenges.challenges.WinCondition;

public class MainWinCondition extends WinCondition implements Listener {
	protected boolean noDeathSched = false;

	public MainWinCondition(String name, String[] explanation) {
		super(name, explanation);
	}
	
	public MainWinCondition() {
		this("MainWinCondition", new String[] {"You win by being the last man standing or by killing the ender dragon.",
				"Alternatively, you also win if you are the first to jump into the credits end portal."});
		this.dontEnableTwice = true;
	}

	@EventHandler
	public void onDeath(PlayerDeathEvent e) {
		if (!this.noDeathSched) {
			this.noDeathSched = true;

			Bukkit.getScheduler().scheduleSyncDelayedTask(MCChallenges.instance, new Runnable() {

				@Override
				public void run() {
					List<Player> leftPlayers = MCChallenges.getPlayers();

					if (leftPlayers.size() == 1) {
						MainWinCondition.this.win("They achieved this by being the last man standing", leftPlayers.get(0));
					} else if (leftPlayers.size() == 0 && MCChallenges.instance.winner == null) {
						MainWinCondition.this.tie();
					}

					MainWinCondition.this.noDeathSched = false;
				}
			}, 20l);
		}
	}

	@EventHandler
	public void onDeath(EntityDeathEvent e) {
		Entity ent = e.getEntity();

		if (ent instanceof EnderDragon) {
			LivingEntity livEnt = (LivingEntity) ent;

			if (livEnt.getKiller() != null) {
				this.win("They achieved this by killing the ender dragon", livEnt.getKiller());
				return;
			}

			EntityDamageEvent damageCause = ent.getLastDamageCause();
			if (damageCause != null) {
				Entity damager = damageCause.getEntity();
				Player winner = null;

				if (damager instanceof Arrow) {
					Arrow arrow = (Arrow) damager;
					if (arrow.getShooter() instanceof Player)
						winner = (Player) arrow.getShooter();
				} else if (damager instanceof Player) {
					winner = (Player) damager;
				}

				if (winner != null) {
					this.win("They achieved this by killing the ender dragon", winner);
				}
			}
		}
	}

	@EventHandler
	public void onEnd(EntityPortalEnterEvent e) {
		if (e.getLocation().getWorld().getEnvironment() == Environment.THE_END && e.getEntity() instanceof Player) {
			Player winner = (Player) e.getEntity();
			this.win("They achieved this by being the first to jump into the end portal", winner);
		}
	}
}
