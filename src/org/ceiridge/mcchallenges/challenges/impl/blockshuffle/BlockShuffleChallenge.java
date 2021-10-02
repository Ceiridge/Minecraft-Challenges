package org.ceiridge.mcchallenges.challenges.impl.blockshuffle;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import org.apache.logging.log4j.core.util.IOUtils;
import org.bukkit.Material;
import org.ceiridge.mcchallenges.MCChallenges;
import org.ceiridge.mcchallenges.TimeHelper;
import org.ceiridge.mcchallenges.challenges.Challenge;

public class BlockShuffleChallenge extends Challenge {
	private TimeHelper time = new TimeHelper(5 * 60 * 1000); // 5 mins

	public HashMap<UUID, Material> assignedBlocks = new HashMap<>();
	public List<Material> possibleBlocks = new ArrayList<>();
	public List<String> scoreBoardBlocks = new ArrayList<>();

	public BlockShuffleChallenge() {
		super("BlockShuffle", new String[] {});

		this.addScheduler(new BlockShuffleTimer(this, this.time));
		this.addScheduler(new BlockShuffleCheckScheduler(this));

		try {
			this.possibleBlocks.clear();
			String blocks = IOUtils.toString(new InputStreamReader(this.getClass().getResourceAsStream("blocks.txt")));

			for (String block : blocks.replace("\r", "").split("\n")) {
				try {
					this.possibleBlocks.add(Material.valueOf(block));
				} catch (Throwable e) {
				}
			}
		} catch (Throwable e) {
			e.printStackTrace();
			System.out.println("Couldn't load blocks");
		}

		this.settings.put("DurationMins", 5);
		this.updateVars();
	}

	@Override
	public void reset() {
		this.time.reset();

		for (UUID uuid : assignedBlocks.keySet()) {
			assignedBlocks.put(uuid, null);
		}
		
		if(this.enabled) {
			MCChallenges.instance.extraScoreData.add(this.scoreBoardBlocks);
		} else {
			MCChallenges.instance.extraScoreData.remove(this.scoreBoardBlocks);
		}
	}

	@Override
	public boolean setSetting(String key, String value) {
		if (key.equals("DurationMins")) {
			this.settings.put(key, Integer.parseInt(value));
			this.updateVars();
			return true;
		}

		return false;
	}

	private void updateVars() {
		int durMins = this.getDurationMins();
		this.explanations = new String[] {"Each player is assigned a completely random block every " + durMins + " minutes.",
				"You have " + durMins + " minutes to stand on that block our you'll die.",
				"If you find/craft your block in than in " + durMins + " minutes, you will get some free time."};

		this.time.setTargetMS(durMins * 60 * 1000);
	}

	public int getDurationMins() {
		return (Integer) this.settings.get("DurationMins");
	}
}
