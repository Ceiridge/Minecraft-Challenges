package org.ceiridge.mcchallenges.challenges.impl.fastmode;

import java.util.ArrayList;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.ceiridge.mcchallenges.MCChallenges;
import org.ceiridge.mcchallenges.challenges.Challenge;
import org.ceiridge.mcchallenges.challenges.Scheduler;

public class FastModeScheduler extends Scheduler {
	private ArrayList<Material> getFortune = new ArrayList<>();
	private final Enchantment[] toolEnchantments = {Enchantment.LOOT_BONUS_BLOCKS, Enchantment.LOOT_BONUS_MOBS, Enchantment.WATER_WORKER};

	public FastModeScheduler(Challenge challenge) {
		super(challenge, SchedulerType.SYNC_REPEATING, 10, 10);

		Material[] getFortune =
				{Material.WOODEN_PICKAXE, Material.STONE_PICKAXE, Material.IRON_PICKAXE, Material.GOLDEN_PICKAXE, Material.DIAMOND_PICKAXE};
		for (Material fortMat : getFortune) {
			this.getFortune.add(fortMat);
		}
	}

	@Override
	public void run(Challenge challenge) {
		for (Player p : MCChallenges.getPlayers()) {
			p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 219, 1)); // 219 = permanent visual 10 seconds
			p.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 219, 3));
			p.addPotionEffect(new PotionEffect(PotionEffectType.DOLPHINS_GRACE, 219, 1));

			PlayerInventory inv = p.getInventory();
			ItemStack stack;

			if (((stack = inv.getItemInMainHand()) != null || (stack = inv.getItemInOffHand()) != null)
					&& this.getFortune.contains(stack.getType())) {
				
				for (Enchantment toolEnch : this.toolEnchantments) {
					if (!stack.getEnchantments().containsKey(toolEnch)) {
						try {
							stack.addEnchantment(toolEnch, 1);
						} catch (Throwable e) {
						}
					}
				}
			}
		}
	}
}
