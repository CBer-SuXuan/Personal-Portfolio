package me.suxuan.game.listener;

import io.papermc.paper.connection.PlayerGameConnection;
import io.papermc.paper.dialog.DialogResponseView;
import io.papermc.paper.event.player.PlayerCustomClickEvent;
import me.suxuan.game.Game;
import me.suxuan.game.config.luckpillar.ArenaConfig;
import me.suxuan.game.game.Arena;
import me.suxuan.game.game.GameManager;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.nbt.api.BinaryTagHolder;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PasswordListener implements Listener {

	private final GameManager gameManager;

	public PasswordListener(GameManager gameManager) {
		this.gameManager = gameManager;
	}

	@EventHandler
	public void onPlayerCreateRoomEvent(PlayerCustomClickEvent event) {

		if (!event.getIdentifier().equals(Key.key("papermc:user_input/create_room"))) {
			return;
		}

		DialogResponseView view = event.getDialogResponseView();
		if (view == null) {
			return;
		}

		String type = view.getText("type");
		String password = view.getText("password");

		if (event.getCommonConnection() instanceof PlayerGameConnection conn) {
			Player player = conn.getPlayer();

			ArenaConfig config = Game.getInstance().getArenaConfigManager().getConfig(type);
			gameManager.createAndJoinRoom(player, config, password);
			player.sendRichMessage(
					password.isEmpty() ?
							"<color:#00ff00>成功创建公共房间！" :
							"<click:copy_to_clipboard:" + password + "><color:#00ff00>成功创建私密房间！密码为：</color><color:#ccfffd> " + password + "</color> <color:#606060>（点击密码可以复制）"
			);
		}
	}

	@EventHandler
	public void onPlayerInputPasswordEvent(PlayerCustomClickEvent event) {

		if (!event.getIdentifier().equals(Key.key("papermc:user_input/input_password"))) {
			return;
		}

		DialogResponseView view = event.getDialogResponseView();
		BinaryTagHolder tagHolder = view.payload();
		if (view == null) {
			return;
		}

		String password = view.getText("password");
		String id = view.getText("id");

		if (event.getCommonConnection() instanceof PlayerGameConnection conn) {
			Player player = conn.getPlayer();

			if (password == null || password.isEmpty()) {
				player.sendRichMessage("<color:#ff0000>请输入房间密码！");
				player.closeDialog();
			}

			Arena arena = gameManager.getArena(id);
			if (arena == null) {
				player.sendRichMessage("<color:#ff0000>房间不存在！");
				player.closeDialog();
				return;
			}
			if (arena.getPassword().equals(password)) {
				player.sendRichMessage("<color:#00ff00>成功加入房间！");
				player.closeInventory();
				gameManager.joinArena(player, arena);
			} else {
				player.sendRichMessage("<color:#ff0000>密码错误！");
				player.closeDialog();
			}
			return;
		}
	}

	@EventHandler
	public void onPlayerCancelInputEvent(PlayerCustomClickEvent event) {

		if (!event.getIdentifier().equals(Key.key("papermc:user_input/cancel_input"))) {
			return;
		}

		DialogResponseView view = event.getDialogResponseView();
		if (view == null) {
			return;
		}

		if (event.getCommonConnection() instanceof PlayerGameConnection conn) {
			Player player = conn.getPlayer();
			player.closeDialog();
		}
	}
}
