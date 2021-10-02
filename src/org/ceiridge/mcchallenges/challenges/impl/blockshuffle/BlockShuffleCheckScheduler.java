package org.ceiridge.mcchallenges.challenges.impl.blockshuffle;

import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.ceiridge.mcchallenges.MCChallenges;
import org.ceiridge.mcchallenges.challenges.Challenge;
import org.ceiridge.mcchallenges.challenges.Scheduler;

public class BlockShuffleCheckScheduler extends Scheduler {
	private BlockShuffleChallenge bchallenge;
	
	public BlockShuffleCheckScheduler(BlockShuffleChallenge challenge) {
		super(challenge, SchedulerType.SYNC_REPEATING, 1l, 1l);
		this.bchallenge = challenge;
	}

	@Override
	public void run(Challenge challenge) {
		for(Player p : MCChallenges.getPlayers()) {
			UUID uuid = p.getUniqueId();
			
			if(this.bchallenge.assignedBlocks.containsKey(uuid)) {
				Material target = this.bchallenge.assignedBlocks.get(uuid);
				
				if(target != null && p.getLocation().subtract(0, 1, 0).getBlock().getType() == target) {
					this.bchallenge.assignedBlocks.put(uuid, null);
					
					Bukkit.broadcastMessage(MCChallenges.formatMessage("§a" + p.getDisplayName() + " §7found their block!"));
					MCChallenges.playSuccessEffect(p);
					
					int i = 0;
					for(String str : this.bchallenge.scoreBoardBlocks) {
						if(str.startsWith("§7" + p.getDisplayName() + ": ")) {
							this.bchallenge.scoreBoardBlocks.set(i, str.replace("§c", "§a"));
							break;
						}
						
						i++;
					}
				}
			}
		}
	}
}
