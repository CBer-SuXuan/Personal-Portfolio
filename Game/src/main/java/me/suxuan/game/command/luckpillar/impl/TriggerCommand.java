package me.suxuan.game.command.luckpillar.impl;

import me.suxuan.game.command.SubCommand;
import me.suxuan.game.game.event.LuckyEvent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TriggerCommand implements SubCommand {

	@Override
	public String name() {
		return "trigger";
	}

	@Override
	public String permission() {
		return "luckypillars.admin.trigger";
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		Player player = asPlayer(sender);
		String eventId = args[1].toUpperCase();
		plugin.getGameManager().forceTriggerEvent(player, eventId);
		return true;
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {

		List<String> allEvents = new ArrayList<>();
		for (LuckyEvent event : plugin.getEventManager().getAllEvents()) {
			allEvents.add(event.getId());
		}
		List<String> result = new ArrayList<>();
		StringUtil.copyPartialMatches(args[1], allEvents, result);

		Collections.sort(result);

		return result;
	}
}
