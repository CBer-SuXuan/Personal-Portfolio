package me.suxuan.game.command.luckpillar.impl;

import me.suxuan.game.command.SubCommand;
import me.suxuan.game.util.StringFormat;
import org.bukkit.command.CommandSender;

import java.util.List;

public class ListCommand implements SubCommand {

	@Override
	public String name() {
		return "list";
	}

	@Override
	public String permission() {
		return "luckypillars.admin.list";
	}

	@Override
	public boolean playerOnly() {
		return true;
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		sender.sendMessage(StringFormat.componentString("<aqua>========== 游戏模式列表 =========="));
		plugin.getArenaConfigManager().getAllConfigs().forEach(cfg -> {
			sender.sendMessage(StringFormat.componentString("<yellow>ID: <white>" + cfg.identifier()));
			sender.sendMessage(StringFormat.componentString("  <gray>名称: " + cfg.displayName()));
			sender.sendMessage(StringFormat.componentString("  <gray>人数: " + cfg.minPlayers() + "-" + cfg.maxPlayers()));
		});
		sender.sendMessage(StringFormat.componentString("<aqua>=================================="));
		return true;
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		return SubCommand.super.tabComplete(sender, args);
	}
}