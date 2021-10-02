package org.ceiridge.mcchallenges.commands;

import java.util.ArrayList;
import org.bukkit.command.Command;
import org.ceiridge.mcchallenges.commands.impl.*;

public class Commands {
	public ArrayList<Command> commands = new ArrayList<>();
	
	public Commands() {
		this.commands.clear();
		
		this.commands.add(new ChallengeCmd());
		this.commands.add(new ChallengePresetCmd());
		this.commands.add(new StartCmd());
		this.commands.add(new ResetCmd());
		this.commands.add(new GiveCompCmd());
		
		this.commands.add(new SpawnCmd());
		this.commands.add(new HubCmd());
	}
}
