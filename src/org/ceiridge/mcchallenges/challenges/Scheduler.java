package org.ceiridge.mcchallenges.challenges;

public abstract class Scheduler {
	private SchedulerType type;
	protected Challenge challenge;
	private long ticks1, ticks2;
	
	public int taskId = -1;

	public Scheduler(Challenge challenge, SchedulerType type, long ticks1, long ticks2) {
		this.type = type;
		this.ticks1 = ticks1;
		this.ticks2 = ticks2;
		this.challenge = challenge;
	}

	public abstract void run(Challenge challenge);
	public void reset() {}
	
	public SchedulerType getType() {
		return type;
	}

	public void setType(SchedulerType type) {
		this.type = type;
	}

	public long getTicks1() {
		return ticks1;
	}

	public void setTicks1(long ticks1) {
		this.ticks1 = ticks1;
	}

	public long getTicks2() {
		return ticks2;
	}

	public void setTicks2(long ticks2) {
		this.ticks2 = ticks2;
	}

	public Runnable getRunnable() {
		return new Runnable() {

			@Override
			public void run() {
				Scheduler.this.run(Scheduler.this.challenge);
			}
		};
	}

	public static enum SchedulerType {
		SYNC_ONCE, SYNC_REPEATING, ASYNC_ONCE, ASYNC_REPEATING;
	}
}
