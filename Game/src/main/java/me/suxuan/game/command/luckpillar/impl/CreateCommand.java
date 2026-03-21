package me.suxuan.game.command.luckpillar.impl;

import me.suxuan.game.command.SubCommand;
import me.suxuan.game.dialog.luckpillar.Dialog;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CreateCommand implements SubCommand {

	@Override
	public String name() {
		return "create";
	}

	@Override
	public boolean playerOnly() {
		return true;
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		Player player = asPlayer(sender);
		Dialog.openCreateRoomDialog(player);
		return true;
	}
	
}
