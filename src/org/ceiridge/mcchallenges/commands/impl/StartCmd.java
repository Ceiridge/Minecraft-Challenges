package org.ceiridge.mcchallenges.commands.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.ceiridge.mcchallenges.MCChallenges;
import org.ceiridge.mcchallenges.challenges.Challenge;
import org.ceiridge.mcchallenges.challenges.WinCondition;
import org.ceiridge.mcchallenges.challenges.impl.main.MainChallenge;

public class StartCmd extends Command {
	public StartCmd() {
		super("start");
	}

	@Override
	public boolean execute(CommandSender sender, String label, String[] args) {
		if (sender instanceof ConsoleCommandSender || sender.isOp()) {
			if (!MCChallenges.instance.started) {
				sender.sendMessage("Starting...");

				for (Player p : Bukkit.getOnlinePlayers()) {
					MainChallenge.sendToWorld(MCChallenges.instance.playWorld, p);
					p.removeScoreboardTag("wantsCompassInFuture");

					p.setHealth(20d);
					p.setExp(0);
					p.setLevel(0);
					p.setFireTicks(0);
					for(PotionEffect effect : p.getActivePotionEffects()) {
						p.removePotionEffect(effect.getType());
					}
					p.closeInventory();

					p.getInventory().clear();
					p.getInventory().setHelmet(null);
					p.getInventory().setChestplate(null);
					p.getInventory().setLeggings(null);
					p.getInventory().setBoots(null);
				}
				Bukkit.getScheduler().scheduleSyncDelayedTask(MCChallenges.instance, new Runnable() {
					@Override
					public void run() {
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "spreadplayers 0 0 20 1000 false @a");
						//						for (Player p : Bukkit.getOnlinePlayers()) {
						//							Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "randomteleport 1 2000 -p " + p.getName() + " -w world -x 0 -z 0");
						//						}
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "advancement revoke @a everything");
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "recipe take @a *");
					}
				}, 40);

				int waitSec = 5;
				try {
					waitSec = Integer.parseInt(args[0]);
				} catch (Throwable e) {
				}

				final int[] explanationSched = {-1};
				explanationSched[0] = Bukkit.getScheduler().scheduleSyncRepeatingTask(MCChallenges.instance, new Runnable() {
					private int index;
					private Challenge explaining;
					private String[] explanation;
					private ArrayList<Challenge> skipExplain = new ArrayList<>();

					@Override
					public void run() {
						if (explaining == null || explanation == null || index >= explanation.length) {
							List<Challenge> sortedChallenges = MCChallenges.instance.challenges.challenges;
							sortedChallenges.sort(new Comparator<Challenge>() {
								@Override
								public int compare(Challenge c1, Challenge c2) {
									return (c1 instanceof WinCondition && c2 instanceof WinCondition) ? 0 : (c1 instanceof WinCondition ? 1 : -1);
								}
							});

							boolean changedExplaining = false;
							for (Challenge challenge : sortedChallenges) {
								if ((challenge.isVisible() || challenge instanceof WinCondition) && !skipExplain.contains(challenge)) {
									if (challenge instanceof WinCondition && challenge.enabled) {
										WinCondition winc = (WinCondition) challenge;

										if (winc.winExplanations == null)
											continue;
										explanation = winc.winExplanations;
									} else
										explanation = challenge.explanations;

									if (explanation == null)
										continue;

									explaining = challenge;
									index = 0;
									skipExplain.add(explaining);

									changedExplaining = true;
									break;
								}
							}

							if (!changedExplaining) {
								MCChallenges.instance.started = true;

								for (Player p : Bukkit.getOnlinePlayers()) {
									p.sendTitle("§aGame started!", "", 1, 40, 10);
									p.setGameMode(GameMode.SURVIVAL);

									p.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 10 * 20, 255, false, false));
									p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 40, 255, false, false));

									Location l = p.getLocation();
									l.setY(255);
									if (MainChallenge.isLobby(l))
										continue;

									for (int i = 256; i > 0; i++) {
										l = l.subtract(0, 1, 0);
										Block b = l.getBlock();

										if (b != null && !b.isEmpty()) {
											b.setType(Material.BEDROCK);
											p.teleport(l.add(0, 1, 0));
											break;
										}
									}
								}

								for (Challenge challenge : MCChallenges.instance.challenges.challenges) {
									if (challenge.enabled && !challenge.dontEnableTwice)
										challenge.setEnabled(true);

									if (!challenge.enabled && challenge.dontEnableTwice)
										challenge.setEnabled(false);
								}

								Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "time set 0");
								Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "weather clear");
								Bukkit.getScheduler().cancelTask(explanationSched[0]);
								return;
							}
						}

						String text = explanation[index];
						MCChallenges.sendBigMessage("Explanation for " + explaining.name, text);
						index++;
					}
				}, 20 * waitSec, 20 * waitSec);
				return true;
			}
		}

		return false;
	}
}
