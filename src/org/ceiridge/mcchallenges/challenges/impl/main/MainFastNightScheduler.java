package org.ceiridge.mcchallenges.challenges.impl.main;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.ceiridge.mcchallenges.challenges.Challenge;
import org.ceiridge.mcchallenges.challenges.Scheduler;

public class MainFastNightScheduler extends Scheduler {
	private MainChallenge mchallenge;
	
	public MainFastNightScheduler(MainChallenge challenge) {
		super(challenge, SchedulerType.SYNC_REPEATING, 0l, 1l);
		this.mchallenge = challenge;
	}

	@Override
	public void run(Challenge challenge) {
		if(!this.mchallenge.isFastNights())
			return;
		
		World world = Bukkit.getWorld("world");
		
		if(world != null && world.getTime() > 12000) { // Is night time
			world.setFullTime(world.getFullTime() + 1l);
		}
	}
}
