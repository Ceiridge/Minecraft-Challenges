package org.ceiridge.mcchallenges;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.ceiridge.mcchallenges.challenges.Challenge;
import org.ceiridge.mcchallenges.challenges.Challenges;
import org.ceiridge.mcchallenges.challenges.impl.main.MainChallenge;
import org.ceiridge.mcchallenges.challenges.impl.winconditions.MainWinCondition;
import org.ceiridge.mcchallenges.commands.Commands;
import org.ceiridge.mcchallenges.teams.PlayTeams;

public class MCChallenges extends JavaPlugin {
	public static MCChallenges instance;
	
	public Commands commands;
	public Challenges challenges;
	public PlayTeams teams;
	public boolean started;
	public String winner;
	public World lobbyWorld, playWorld;
	
	public HashMap<String, Integer> scoreMap = new HashMap<>();
	public ArrayList<List<String>> extraScoreData = new ArrayList<>();

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		for(Command cmd : commands.commands) {
			if(cmd.getName().equalsIgnoreCase(command.getName())) {
				return cmd.execute(sender, label, args);
			}
		}
		
		return true;
	}

	@Override
	public void onDisable() {
		for(Challenge challenge : challenges.challenges) {
			challenge.setEnabled(false);
		}
		
		System.out.println("[MCChallenges] Stopped");
	}

	@Override
	public void onEnable() {
		instance = this;
		commands = new Commands();
		challenges = new Challenges();
		teams = new PlayTeams();
		lobbyWorld = Bukkit.getWorld("lobby");
		playWorld = Bukkit.getWorld("world");

		for (Command command : commands.commands) {
			try {
				this.getCommand(command.getName()).setTabCompleter((TabCompleter) command);
			} catch (Exception e) {
			}
		}
		
		MCChallenges.instance.started = true;
		challenges.getChallengeByClass(MainChallenge.class).setEnabled(true);
		challenges.getChallengeByClass(MainWinCondition.class).setEnabled(true);
		MCChallenges.instance.started = false;
		
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "gamerule spectatorsGenerateChunks false");
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "gamerule doImmediateRespawn true");
		
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mv unload lobby_nether"); // TODO: Remove this
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mv unload lobby_the_end");
		
//		sendBigMessage("Challenges", "Server reloaded");
		System.out.println("[MCChallenges] Started");		
	}
	
	public int getScore(String name) {
		return this.scoreMap.containsKey(name) ? this.scoreMap.get(name) : 0;
	}
	
	public void setScore(String name, int score) {
		this.scoreMap.put(name, score);
	}
	
	public void addScore(String name, int value) {
		if(!this.scoreMap.containsKey(name)) {
			this.scoreMap.put(name, 0);
		}
		
		this.scoreMap.put(name, this.scoreMap.get(name) + value);
	}
	
	
	public static String formatMessage(String msg) {
		return "�7[�eChallenges�7] �8" + msg;
	}
	
	private final static int CENTER_PX = 154;
	private final static int MAX_PX = 250;
	
	// Taken from https://www.spigotmc.org/threads/free-code-sending-perfectly-centered-chat-message.95872/
	public static void sendCenteredMessage(Player player, String message) {
		int messagePxSize = 0;
		boolean previousCode = false;
		boolean isBold = false;
		int charIndex = 0;
		int lastSpaceIndex = 0;
		String toSendAfter = null;
		String recentColorCode = "";
		for (char c : message.toCharArray()) {
			if (c == '�') {
				previousCode = true;
				continue;
			} else if (previousCode == true) {
				previousCode = false;
				recentColorCode = "�" + c;
				if (c == 'l' || c == 'L') {
					isBold = true;
					continue;
				} else
					isBold = false;
			} else if (c == ' ')
				lastSpaceIndex = charIndex;
			else {
				DefaultFontInfo dFI = DefaultFontInfo.getDefaultFontInfo(c);
				messagePxSize += isBold ? dFI.getBoldLength() : dFI.getLength();
				messagePxSize++;
			}
			if (messagePxSize >= MAX_PX) {
				toSendAfter = recentColorCode + message.substring(lastSpaceIndex + 1, message.length());
				message = message.substring(0, lastSpaceIndex + 1);
				break;
			}
			charIndex++;
		}
		int halvedMessageSize = messagePxSize / 2;
		int toCompensate = CENTER_PX - halvedMessageSize;
		int spaceLength = DefaultFontInfo.SPACE.getLength() + 1;
		int compensated = 0;
		StringBuilder sb = new StringBuilder();
		while (compensated < toCompensate) {
			sb.append(" ");
			compensated += spaceLength;
		}
		player.sendMessage(sb.toString() + message);
		if (toSendAfter != null)
			sendCenteredMessage(player, toSendAfter);
	}
	
	public static void sendBigMessage(String title, String text, Player p) {
		p.sendMessage("�a---------------------------------------------------");
		sendCenteredMessage(p, "�f�l" + title);
		p.sendMessage("");
		sendCenteredMessage(p, "�e" + text);
		p.sendMessage("�a---------------------------------------------------");
		
		p.playSound(p.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 1f, 0.5f);
	}
	public static void sendBigMessage(String title, String text) {
		for(Player p : Bukkit.getOnlinePlayers())
			sendBigMessage(title, text, p);
	}
	
	public static void playSuccessEffect(Player p) {
		p.playSound(p.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 1f, 0.75f);
		p.spawnParticle(Particle.TOTEM, p.getLocation(), 100);
	}
	
	public static List<Player> getPlayers() {
		ArrayList<Player> result = new ArrayList<>();
		for(Player p : Bukkit.getOnlinePlayers()) {
			if(p.getGameMode() != GameMode.SPECTATOR && !p.isDead() && p.getWorld() != MCChallenges.instance.lobbyWorld)
				result.add(p);
		}
		
		return result;
	}
}
