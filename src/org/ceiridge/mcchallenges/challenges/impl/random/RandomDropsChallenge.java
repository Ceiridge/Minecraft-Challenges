package org.ceiridge.mcchallenges.challenges.impl.random;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;
import org.apache.logging.log4j.core.util.IOUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.ceiridge.mcchallenges.challenges.Challenge;

public class RandomDropsChallenge extends Challenge implements Listener {
	private ArrayList<Material> randomMaterials = new ArrayList<>();
	private long seed;

	public RandomDropsChallenge() {
		super("RandomDrops", new String[] {"Every mined block and killed mob will drop a random item"});

		try {
			this.randomMaterials.clear();
			String blocks = IOUtils.toString(new InputStreamReader(this.getClass().getResourceAsStream("items.txt")));

			for (String block : blocks.replace("\r", "").split("\n")) {
				try {
					this.randomMaterials.add(Material.valueOf(block));
				} catch (Throwable e) {
				}
			}
		} catch (Throwable e) {
			e.printStackTrace();
			System.out.println("Couldn't load items");
		}
	}
	
	@Override
	public void reset() {
		this.seed = System.currentTimeMillis();
	}

	private void dropRandomItems(Collection<ItemStack> stacks, Location loc) {
		for (ItemStack stack : stacks) {
			Random rnd = new Random(stack.getType().ordinal() + this.seed);

			try {
				loc.getWorld().dropItemNaturally(loc,
						new ItemStack(this.randomMaterials.get(rnd.nextInt(this.randomMaterials.size())), rnd.nextInt(64)));
			} catch (Throwable ex) {
			}
		}
	}

	@EventHandler
	public void onBreak(BlockBreakEvent e) {
		e.setDropItems(false);
		Player p = e.getPlayer();
		Block b = e.getBlock();

		this.dropRandomItems(b.getDrops(p.getInventory().getItemInMainHand()), b.getLocation());
	}

	@EventHandler
	public void onEntDeath(EntityDeathEvent e) {
		Entity ent = e.getEntity();

		if (!(ent instanceof Player)) {
			this.dropRandomItems(e.getDrops(), ent.getLocation());
			e.getDrops().clear();
		}
	}
}
