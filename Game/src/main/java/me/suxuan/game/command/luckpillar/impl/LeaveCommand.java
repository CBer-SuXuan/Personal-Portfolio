package me.suxuan.game.command.luckpillar.impl;

import me.suxuan.game.command.SubCommand;
import me.suxuan.game.util.StringFormat;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class LeaveCommand implements SubCommand {

	@Override
	public String name() {
		return "leave";
	}

	@Override
	public List<String> aliases() {
		return List.of("quit");
	}

	@Override
	public boolean playerOnly() {
		return SubCommand.super.playerOnly();
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		Player player = asPlayer(sender);
		if (!plugin.getGameManager().isInGame(player)) {
			player.sendMessage(StringFormat.componentString(plugin.getMessageManager().get("command.not-in-game")));
			return true;
		}
		plugin.getGameManager().quitGame(player);
		player.sendMessage(StringFormat.componentString(plugin.getMessageManager().get("command.leave-success")));
		return true;
	}
}
