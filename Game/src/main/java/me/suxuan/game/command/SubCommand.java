package me.suxuan.game.command;

import me.suxuan.game.Game;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public interface SubCommand {

	Game plugin = Game.getInstance();

	String name();

	default List<String> aliases() {
		return Collections.emptyList();
	}

	default String permission() {
		return null;
	}

	default boolean playerOnly() {
		return false;
	}

	boolean execute(CommandSender sender, String[] args);

	default List<String> tabComplete(CommandSender sender, String[] args) {
		return Collections.emptyList();
	}

	default Player asPlayer(CommandSender sender) {
		if (sender instanceof Player player) {
			return player;
		}
		throw new IllegalStateException("Command sender is not a player.");
	}

	default boolean canUse(CommandSender sender) {
		String perm = permission();
		if (perm == null || perm.isBlank()) return true;
		return sender.hasPermission(perm);
	}

	default boolean checkPlayerOnly(CommandSender sender) {
		return !playerOnly() || sender instanceof Player;
	}
}
