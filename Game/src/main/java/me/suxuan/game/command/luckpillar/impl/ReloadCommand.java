package me.suxuan.game.command.luckpillar.impl;

import me.suxuan.game.command.SubCommand;
import me.suxuan.game.util.StringFormat;
import org.bukkit.command.CommandSender;

public class ReloadCommand implements SubCommand {

	@Override
	public String name() {
		return "reload";
	}

	@Override
	public String permission() {
		return "luckypillars.admin.reload";
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		long start = System.currentTimeMillis();

		// 重载消息
		plugin.getMessageManager().reload();

		// 重载游戏配置
		plugin.getArenaConfigManager().loadConfigs();

		long time = System.currentTimeMillis() - start;
		sender.sendMessage(StringFormat.componentString(
				plugin.getMessageManager().get("command.reload-success") + " <gray>(" + time + "ms)"));

		return true;
	}
}