package me.suxuan.game.dialog.luckpillar;

import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import io.papermc.paper.registry.data.dialog.input.SingleOptionDialogInput;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import me.suxuan.game.Game;
import me.suxuan.game.config.luckpillar.ArenaConfig;
import me.suxuan.game.game.Arena;
import me.suxuan.game.util.StringFormat;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.nbt.api.BinaryTagHolder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Dialog {

	private static final Game plugin = Game.getInstance();

	public static void openCreateRoomDialog(Player player) {

		List<SingleOptionDialogInput.OptionEntry> entryList = new ArrayList<>();
		List<ArenaConfig> configList = plugin.getArenaConfigManager().getAllConfigs();
		for (int i = 0; i < configList.size(); i++) {
			ArenaConfig config = configList.get(i);
			entryList.add(SingleOptionDialogInput.OptionEntry.create(
					config.identifier(),
					StringFormat.componentString(config.displayName()),
					i == 0));
		}

		io.papermc.paper.dialog.Dialog dialog = io.papermc.paper.dialog.Dialog.create(builder -> builder.empty()
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
								Component.text("点击确认创建房间！", TextColor.color(0xAEFFC1)),
								100,
								DialogAction.customClick(Key.key("papermc:user_input/create_room"), null)
						),
						ActionButton.create(
								Component.text("退出", TextColor.color(0xFFA0B1)),
								Component.text("点击退出创建房间！", TextColor.color(0xFFA0B1)),
								100,
								null
						)
				))
		);
		player.showDialog(dialog);
	}

	public static void openInputPasswordDialog(Player player, Arena targetArena) {

		io.papermc.paper.dialog.Dialog dialog = io.papermc.paper.dialog.Dialog.create(builder -> builder.empty()
				.base(DialogBase.builder(Component.text("输入密码"))
						.canCloseWithEscape(true)
						.inputs(List.of(
								DialogInput.text(
												"password",
												Component.text("输入房间密码（区分大小写）"))
										.build()
						))
						.build()
				)
				.type(DialogType.confirmation(
						ActionButton.create(
								Component.text("确认", TextColor.color(0xAEFFC1)),
								Component.text("点击检查密码！", TextColor.color(0xAEFFC1)),
								100,
								DialogAction.customClick(Key.key("papermc:user_input/input_password"),
										BinaryTagHolder.binaryTagHolder("{id:\"" + targetArena.getId() + "\"}"))
						),
						ActionButton.create(
								Component.text("退出", TextColor.color(0xFFA0B1)),
								Component.text("点击返回！", TextColor.color(0xFFA0B1)),
								100,
								DialogAction.customClick(Key.key("papermc:user_input/cancel_input"), null)
						)
				))
		);
		player.showDialog(dialog);
	}
}
