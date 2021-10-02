package org.ceiridge.mcchallenges.challenges;

import java.util.ArrayList;
import java.util.HashMap;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.ceiridge.mcchallenges.MCChallenges;
import org.ceiridge.mcchallenges.challenges.Scheduler.SchedulerType;

public abstract class Challenge implements Listener {
	public String name;
	public String[] explanations;
	public boolean enabled, dontEnableTwice, isModifier;
	public HashMap<String, Object> settings;

	private ArrayList<Scheduler> schedulers;

	public Challenge(String name, String[] explanations) {
		this.name = name;
		this.explanations = explanations;
		this.enabled = false;
		this.schedulers = new ArrayList<>();
		this.settings = new HashMap<>();
	}

	public boolean setSetting(String key, String value) {
		return false;
	}

	public void reset() {}

	public void setEnabled(boolean enable) {
		this.enabled = enable;
		if (!MCChallenges.instance.started)
			return;
		this.reset();

		for (Scheduler sched : this.schedulers) {
			sched.reset();

			if (enable) {
				if (sched.getType() == SchedulerType.SYNC_ONCE) {
					sched.taskId = Bukkit.getScheduler().scheduleSyncDelayedTask(MCChallenges.instance, sched.getRunnable(), sched.getTicks1());
				} else if (sched.getType() == SchedulerType.SYNC_REPEATING) {
					sched.taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(MCChallenges.instance, sched.getRunnable(), sched.getTicks1(),
							sched.getTicks2());
				} else if (sched.getType() == SchedulerType.ASYNC_ONCE) {
					sched.taskId = Bukkit.getScheduler().runTaskLaterAsynchronously(MCChallenges.instance, sched.getRunnable(), sched.getTicks1())
							.getTaskId();
				} else if (sched.getType() == SchedulerType.ASYNC_REPEATING) {
					sched.taskId = Bukkit.getScheduler()
							.runTaskTimerAsynchronously(MCChallenges.instance, sched.getRunnable(), sched.getTicks1(), sched.getTicks2()).getTaskId();
				}
			} else if (sched.taskId != -1) {
				Bukkit.getScheduler().cancelTask(sched.taskId);
			}
		}

		if (enable) {
			Bukkit.getServer().getPluginManager().registerEvents(this, MCChallenges.instance);
		} else {
			HandlerList.unregisterAll(this);
		}
	}

	public boolean isVisible() {
		return this.explanations != null && this.enabled;
	}

	protected void addScheduler(Scheduler sched) {
		this.schedulers.add(sched);
	}
}
