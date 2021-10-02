package org.ceiridge.mcchallenges.challenges.impl.main;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.ceiridge.mcchallenges.MCChallenges;
import org.ceiridge.mcchallenges.challenges.Challenge;
import org.ceiridge.mcchallenges.challenges.Scheduler;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class MainAdScheduler extends Scheduler {
	private static final long DELAY = 20 * 60 * 5; // 5 mins
	public static final String DISCORD_LINK = "https://discord.gg/S6fp7fW";

	public MainAdScheduler(Challenge challenge) {
		super(challenge, SchedulerType.SYNC_REPEATING, DELAY, DELAY);
	}

	@Override
	public void run(Challenge challenge) {
		TextComponent message = new TextComponent(MCChallenges.formatMessage(
				"§rDon't forget to join our discord to be informed about updates and to find other players! §6" + DISCORD_LINK + " §l[CLICK]"));
		message.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, DISCORD_LINK));
		message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to join the discord").create()));

		for (Player p : Bukkit.getOnlinePlayers()) {
			p.sendMessage(message);
			p.playSound(p.getLocation(), Sound.BLOCK_BAMBOO_BREAK, 1f, 1f);
		}
	}
}
