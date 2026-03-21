package me.suxuan.game.command.luckpillar.impl;

import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import io.papermc.paper.registry.data.dialog.input.SingleOptionDialogInput;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import me.suxuan.game.command.SubCommand;
import me.suxuan.game.config.luckpillar.ArenaConfig;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class TestCommand implements SubCommand {

	@Override
	public String name() {
		return "test";
	}

	@Override
	public String permission() {
		return "luckypillars.admin.test";
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		List<SingleOptionDialogInput.OptionEntry> entryList = new ArrayList<>();
		List<ArenaConfig> configList = plugin.getArenaConfigManager().getAllConfigs();
		for (int i = 0; i < configList.size(); i++) {
			ArenaConfig config = configList.get(i);
			entryList.add(SingleOptionDialogInput.OptionEntry.create(
					config.identifier(),
					Component.text(config.displayName()),
					i == 0));
		}

		Dialog dialog = Dialog.create(builder -> builder.empty()
				.base(DialogBase.builder(Component.text("创建房间"))
						.canCloseWithEscape(true)
						.body(List.of(
								DialogBody.plainMessage(Component.text("请配置房间属性！"))
						))
						.inputs(List.of(
								DialogInput.singleOption(
												"type",
												Component.text("选择地图"),
												entryList)
										.build(),
								DialogInput.text(
												"password",
												Component.text("输入密码（不输入则为公共房间）"))
										.build()
						))
						.build()
				)
				.type(DialogType.confirmation(
						ActionButton.create(
								Component.text("确认", TextColor.color(0xAEFFC1)),
								Component.text("点击确认创建房间！"),
								100,
								DialogAction.customClick(Key.key("papermc:user_input/create_room"), null)
						),
						ActionButton.create(
								Component.text("退出", TextColor.color(0xFFA0B1)),
								Component.text("点击退出创建房间！"),
								100,
								null
						)
				))
		);
		sender.showDialog(dialog);
		return true;
	}
}
