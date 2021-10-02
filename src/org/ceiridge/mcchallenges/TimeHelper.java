package org.ceiridge.mcchallenges;

public class TimeHelper {
	private long lastMS;
	private long targetTime;
	
	public TimeHelper(long targetTime) {
		this.targetTime = targetTime;
	}
	
	public TimeHelper() {
		this(-1);
	}

	public long getCurrentMS() {
		return System.nanoTime() / 1000000L;
	}

	public long getLastMS() {
		return this.lastMS;
	}
	
	public long getReachedMS() {
		return getCurrentMS() - this.lastMS;
	}
	
	public long getTargetMS() {
		return this.targetTime;
	}

	public boolean hasReached(long milliseconds) {
		return getReachedMS() >= milliseconds;
	}
	
	public boolean hasReached() {
		return hasReached(this.targetTime);
	}

	public void reset() {
		this.lastMS = getCurrentMS();
	}

	public void setLastMS(long currentMS) {
		this.lastMS = currentMS;
	}
	
	public void setTargetMS(long targetMS) {
		this.targetTime = targetMS;
	}
}
