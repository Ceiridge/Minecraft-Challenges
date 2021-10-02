package org.ceiridge.mcchallenges.challenges.impl.main;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.ceiridge.mcchallenges.MCChallenges;
import org.ceiridge.mcchallenges.TimeHelper;
import org.ceiridge.mcchallenges.challenges.Challenge;
import org.ceiridge.mcchallenges.challenges.Scheduler;

public abstract class TimedScheduler extends Scheduler {
	protected TimeHelper time;
	protected long countdownMillis;

	private BossBar boss;
	private int lastSecLeft;
	private boolean repeat;

	public TimedScheduler(Challenge challenge, TimeHelper time, boolean repeating, long countdownMillis) {
		super(challenge, SchedulerType.SYNC_REPEATING, 1l, 1l);
		this.time = time;
		this.countdownMillis = countdownMillis;
		this.repeat = repeating;
	}

	public TimedScheduler(Challenge challenge, TimeHelper time, boolean repeating) {
		this(challenge, time, repeating, 10000l);
	}

	@Override
	public void run(Challenge challenge) {
		if (this.time.hasReached(this.time.getTargetMS() - countdownMillis)) {
			long millisLeft = Math.max(0, this.time.getTargetMS() - this.time.getReachedMS());
			int secLeft = (int) Math.ceil(millisLeft / 1000f);

			this.onCountdown(secLeft, millisLeft, secLeft != this.lastSecLeft);
			this.lastSecLeft = secLeft;
		}

		if (this.time.hasReached()) {
			this.reset();
			this.onFinish();

			if (this.repeat) {
				this.time.reset();
			} else if (this.taskId != -1) {
				Bukkit.getScheduler().cancelTask(this.taskId);
				this.taskId = -1;
			}
		}
	}

	@Override
	public void reset() {
		if (this.boss != null) {
			this.boss.setVisible(false);
			this.boss = null;
		}
	}

	public abstract void onFinish();

	public void onCountdown(int secLeft, long millisLeft, boolean secChanged) {
		if (this.boss == null) {
			this.boss = Bukkit.getServer().createBossBar(this.challenge.name, BarColor.YELLOW, BarStyle.SEGMENTED_10);
			this.boss.setVisible(true);
		}

		this.boss.setProgress((double) millisLeft / (double) countdownMillis);

		if (secChanged) {
			for (Player p : Bukkit.getOnlinePlayers()) {
				if (!this.boss.getPlayers().contains(p))
					this.boss.addPlayer(p);
			}
		}
	}

	protected void countdownNotifyAll(String message, int secLeft) {
		for (Player p : Bukkit.getOnlinePlayers()) {
			this.countdownNotify(message, secLeft, p);
		}
	}

	protected void countdownNotify(String message, int secLeft, Player p) {
		p.sendMessage(MCChallenges.formatMessage("§4" + message + " in " + secLeft + " seconds!"));
		p.playSound(p.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 1f, 1.3f);
	}
}
