package me.suxuan.game.command.luckpillar.impl;

import me.suxuan.game.command.SubCommand;
import me.suxuan.game.gui.luckpillar.RoomBrowserGUI;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BrowseCommand implements SubCommand {

	@Override
	public String name() {
		return "browse";
	}

	@Override
	public boolean playerOnly() {
		return true;
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		Player player = asPlayer(sender);
		if (plugin.getGameManager().getArena(player) == null) {
			new RoomBrowserGUI(plugin).open(player);
			return true;
		}
		return true;
	}

}
