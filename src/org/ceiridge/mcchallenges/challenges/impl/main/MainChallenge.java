package org.ceiridge.mcchallenges.challenges.impl.main;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Pattern;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.ceiridge.mcchallenges.MCChallenges;
import org.ceiridge.mcchallenges.challenges.Challenge;
import org.ceiridge.mcchallenges.challenges.ChallengePreset;
import org.ceiridge.mcchallenges.commands.impl.ChallengePresetCmd;
import org.ceiridge.mcchallenges.teams.PlayTeam;

public class MainChallenge extends Challenge implements Listener {
	public List<UUID> votedPlayers = new ArrayList<UUID>();

	public MainChallenge() {
		super("Main", null);
		this.addScheduler(new ScoreboardScheduler(this));
		this.addScheduler(new MainAutoScheduler(this));
		this.addScheduler(new MainAdScheduler(this));
		this.addScheduler(new MainFastNightScheduler(this));

		this.dontEnableTwice = true;
		this.settings.put("Teaming", false);
		this.settings.put("Hardcore", true);
		this.settings.put("FastNights", true);
	}

	private boolean isHardcore() {
		return (Boolean) this.settings.get("Hardcore");
	}
	
	public boolean isFastNights() {
		return (Boolean) this.settings.get("FastNights");
	}

	public static boolean isLobby(Location l) {
		return l.getWorld() == MCChallenges.instance.lobbyWorld;
	}

	public static boolean isLobby(Player p) {
		return isLobby(p.getLocation());
	}

	public static void sendToWorld(World world, Player p) {
		p.teleport(world.getSpawnLocation());
	}

	@Override
	public boolean setSetting(String key, String value) {
		if (key.equals("Teaming") || key.equals("Hardcore") || key.equals("FastNights")) {
			this.settings.put(key, Boolean.parseBoolean(value));
			return true;
		}

		return false;
	}

	@EventHandler
	public void onLeave(PlayerQuitEvent e) {
		Player p = e.getPlayer();

		if (MCChallenges.getPlayers().contains(p) && this.isHardcore()) {
			e.setQuitMessage("§4" + p.getDisplayName() + "§r§4 left the game and thus automatically died.");
			p.setHealth(0d);
		}
	}
	
	@EventHandler
	public void onDeath(PlayerDeathEvent e) {
		Player p = e.getEntity();
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(MCChallenges.instance, new Runnable() {
			
			@Override
			public void run() {
				if(p.isDead()) {
					p.spigot().respawn();
				}
			}
		}, 10);
	}

	@EventHandler
	public void onMove(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		if (!p.isOp() && !MCChallenges.instance.started && !isLobby(p)) {
			e.setCancelled(true);
		}

		if (MCChallenges.instance.started && isLobby(p)) {
			p.kickPlayer("§ePlease reconnect");
		}
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		p.setGameMode(GameMode.SPECTATOR);

		if (!MCChallenges.instance.started) {
			MCChallenges.sendBigMessage("Please wait", "We're waiting for more players and the game will start soon!", p);
			sendToWorld(MCChallenges.instance.lobbyWorld, p);
			Bukkit.getScheduler().scheduleSyncDelayedTask(MCChallenges.instance, new Runnable() {
				@Override
				public void run() {
					p.setGameMode(GameMode.ADVENTURE);
				}
			}, 20);
		} else {
			if (this.isHardcore())
				MCChallenges.sendBigMessage("Spectate",
						"You can spectate others by pressing the key 1. You will be able to vote for a new challenge after this ends.", p);
			else {
				Bukkit.getScheduler().scheduleSyncDelayedTask(MCChallenges.instance, new Runnable() {
					@Override
					public void run() {
						p.setGameMode(GameMode.SURVIVAL);
					}
				}, 20);
				MCChallenges.sendBigMessage("Late Join", "You can play now, because this game isn't in hardcore mode", p);
			}

			if (isLobby(p)) {
				sendToWorld(MCChallenges.instance.playWorld, p);
			}
		}

		if (p.getGameMode() == GameMode.SPECTATOR || p.getGameMode() == GameMode.SURVIVAL) {
			p.setDisplayName(p.getName());
			p.setPlayerListName(p.getName());
		}
	}

	@EventHandler
	public void onRespawn(PlayerRespawnEvent e) {
		Player p = e.getPlayer();

		if (!MCChallenges.instance.started) {
			Bukkit.getScheduler().scheduleSyncDelayedTask(MCChallenges.instance, new Runnable() {

				@Override
				public void run() {
					sendToWorld(MCChallenges.instance.lobbyWorld, p);
				}
			}, 20);
			return;
		}

		if (this.isHardcore()) {
			MCChallenges.sendBigMessage("You lost",
					"You lost the game by dying, but you can still spectate as long as you don't provide your friends with information :)! Teleport to others by pressing 1!",
					p);
			
			p.setGameMode(GameMode.SPECTATOR);
		} else {
			Bukkit.getScheduler().scheduleSyncDelayedTask(MCChallenges.instance, new Runnable() {
				@Override
				public void run() {
					p.setGameMode(GameMode.SURVIVAL);

					if (p.getScoreboardTags().contains("wantsCompassInFuture")) {
						giveTrackingCompass(p);
					}
				}
			}, 20);
		}
	}

	private static boolean isCompassPlayerValid(Player p) {
		if (p == null)
			return false;
		if (p.getWorld().getEnvironment() != Environment.NORMAL)
			return false;

		return true;
	}

	private static boolean isCompass(ItemStack stack) {
		return stack != null && stack.getType() == Material.COMPASS && stack.getItemMeta() != null
				&& stack.getItemMeta().getDisplayName().equals("§4Tracking Compass");
	}

	private static void reAssignCompassTarget(ItemStack stack, Player p) {
		ItemMeta meta = stack.getItemMeta();
		List<String> lore = new ArrayList<String>();
		List<Player> possibleTargets = new ArrayList<Player>();

		for (Player pp : MCChallenges.getPlayers()) {
			if (pp != p && isCompassPlayerValid(pp)) {
				possibleTargets.add(pp);
			}
		}

		if (possibleTargets.size() > 0) {
			lore.add(possibleTargets.get(ThreadLocalRandom.current().nextInt(0, possibleTargets.size())).getName());
		}

		meta.setLore(lore);
		stack.setItemMeta(meta);
	}

	@EventHandler
	public void onMoveTrackingCompass(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		ItemStack stack;

		if (isCompass(stack = p.getInventory().getItemInMainHand()) || isCompass(stack = p.getInventory().getItemInOffHand())) {
			ItemMeta meta = stack.getItemMeta();
			List<String> lore;
			Player target = null;

			if ((lore = meta.getLore()) == null || lore.size() == 0 || !isCompassPlayerValid(target = Bukkit.getPlayerExact(lore.get(0)))) {
				lore = new ArrayList<String>();
				List<Player> possibleTargets = new ArrayList<Player>();

				for (Player pp : MCChallenges.getPlayers()) {
					if (pp != p && isCompassPlayerValid(pp)) {
						possibleTargets.add(pp);
					}
				}

				if (possibleTargets.size() > 0) {
					target = possibleTargets.get(ThreadLocalRandom.current().nextInt(0, possibleTargets.size()));
					lore.add(target.getName());
				}

				meta.setLore(lore);
				stack.setItemMeta(meta);
			}

			if (target != null && isCompassPlayerValid(target)) {
				p.setCompassTarget(target.getLocation());
				p.sendActionBar("§eTracking §a" + target.getDisplayName() + " §r§e("
						+ (target.getLocation().getY() < p.getLocation().getY() ? "§cBelow" : "§aAbove")
						+ " you§e) §7(Compass only updates when held! Right click to change targets)");
			}
		}
	}

	@EventHandler
	public void onRightClick(PlayerInteractEvent e) {
		if (isCompass(e.getItem())) {
			reAssignCompassTarget(e.getItem(), e.getPlayer());
		}
	}

	public static void giveTrackingCompass(Player p) {
		ItemStack compass = new ItemStack(Material.COMPASS);
		ItemMeta meta = compass.getItemMeta();

		meta.setDisplayName("§4Tracking Compass");
		compass.setItemMeta(meta);

		p.getInventory().addItem(compass);
		if (!p.getScoreboardTags().contains("wantsCompassInFuture"))
			p.addScoreboardTag("wantsCompassInFuture");
	}

	@EventHandler
	public void onDamage(EntityDamageByEntityEvent e) {
		if (e.getEntity() instanceof Player && e.getDamager() instanceof Player) {
			Player p = (Player) e.getEntity();
			Player dmg = (Player) e.getDamager();

			for (PlayTeam team : MCChallenges.instance.teams.teams) {
				if (team.players.contains(p) && team.players.contains(dmg) && !team.friendlyFire) {
					e.setCancelled(true);
					dmg.sendMessage(MCChallenges.formatMessage("You can't hurt your team mates!"));
					break;
				}
			}
		}
	}


	@EventHandler
	public void onInteractLobby(PlayerInteractEvent e) {
		Player p = e.getPlayer();

		if (isLobby(p) && p.getGameMode() != GameMode.CREATIVE) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onDamageLobby(EntityDamageEvent e) {
		if (isLobby(e.getEntity().getLocation())) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onEntInteractLobby(PlayerInteractEntityEvent e) {
		Player p = e.getPlayer();
		Entity ent = e.getRightClicked();

		if (isLobby(p) && ent != null && ent.getCustomName() != null && ent.getCustomName().equals("§eChallenges")) {
			e.setCancelled(true);
			Inventory voteInv = Bukkit.createInventory(p, 54, "§eChallenge Vote");

			for (ChallengePreset c : ChallengePresetCmd.instance.challengePresets) {
				ItemStack stack = new ItemStack(Material.YELLOW_SHULKER_BOX);
				ItemMeta meta = stack.getItemMeta();

				meta.setDisplayName("§e" + c.name);
				List<String> lore = meta.getLore();
				if (lore == null)
					lore = new ArrayList<String>();

				lore.add("§a" + Math.max(0, c.votes - 1) + " Votes");
				lore.add("----------");

				if (c.commands != null && c.commands.size() > 0 && c.commands.get(0).startsWith("#")) {
					String[] challengeNames = c.commands.get(0).substring(1).split(Pattern.quote(","));

					for (String challengeName : challengeNames) {
						try {
							Challenge chall = MCChallenges.instance.challenges.getChallengeByName(challengeName);

							if (chall != null) {
								lore.add("");
								for (String explanation : chall.explanations) {
									StringBuilder explanationB = new StringBuilder();
									for (int i = 0; i < explanation.length(); i++) {
										if (i != 0 && i % 20 == 0) {
											explanationB.append("\n  ");
										}

										explanationB.append(explanation.charAt(i));
									}

									for (String str : explanationB.toString().split("\n"))
										lore.add(str);
								}
							}
						} catch (Throwable ex) {
						}
					}
				}

				meta.setLore(lore);
				stack.setItemMeta(meta);
				voteInv.addItem(stack);

				p.openInventory(voteInv);
			}
		}
	}

	@EventHandler
	public void onInventoryActionLobby(InventoryClickEvent e) {
		Player p = (Player) e.getWhoClicked();
		Inventory inv = e.getClickedInventory();
		InventoryView view = e.getView();

		if (isLobby(p) && inv != null && view.getTitle().startsWith("§e")) {
			e.setCancelled(true);

			ItemStack stack = e.getCurrentItem();

			if (stack != null) {
				if (view.getTitle().equals("§eChallenge Vote") && stack.getType() == Material.YELLOW_SHULKER_BOX) {
					ChallengePreset preset =
							ChallengePresetCmd.instance.getChallengePresetByName(stack.getItemMeta().getDisplayName().replace("§e", ""));

					if (preset == null) {
						p.sendMessage(MCChallenges.formatMessage("§cChallenge Vote Error!"));
					} else {
						if (this.votedPlayers.contains(p.getUniqueId())) {
							p.sendMessage(MCChallenges.formatMessage("§cYou already voted for a challenge"));
							p.closeInventory();
							return;
						}

						if (preset.votes == 0)
							preset.votes = 2;
						else
							preset.votes++;

						this.votedPlayers.add(p.getUniqueId());

						p.sendMessage(MCChallenges.formatMessage("§aVoted for challenge"));
						p.closeInventory();
					}
				}
			}
		}
	}

	@EventHandler
	public void onLeavePortal(PlayerPortalEvent e) { // fix for nether spectator switch
		final Player p = e.getPlayer();
		final GameMode mode = p.getGameMode();

		Bukkit.getScheduler().scheduleSyncDelayedTask(MCChallenges.instance, new Runnable() {

			@Override
			public void run() {
				p.setGameMode(mode);
			}
		}, 2);
	}

	@EventHandler
	public void onPaintingInteractLobby(HangingBreakEvent e) {
		if (isLobby(e.getEntity().getLocation()))
			e.setCancelled(true);
	}
}
