package me.suxuan.game.game.board;

import me.suxuan.game.game.Arena;
import me.suxuan.game.game.ArenaState;
import me.suxuan.game.util.SimpleScoreboard;
import me.suxuan.game.util.StringFormat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BoardProvider {

	private final MiniMessage mm = MiniMessage.miniMessage();

	public void update(SimpleScoreboard board, Arena arena) {
		List<Component> lines = new ArrayList<>();

		board.updateTitle(StringFormat.componentString("<gold><bold>幸运之柱"));

		lines.add(Component.text(""));

		lines.add(StringFormat.componentString("<white>模式: <yellow>" + arena.getConfig().displayName()));

		if (arena.getState() == ArenaState.WAITING) {
			lines.add(Component.text(""));

			lines.add(StringFormat.componentString("<gold>[房主]" + " " + Bukkit.getOfflinePlayer(arena.getHostUuid()).getName()));
			// 显示玩家列表和状态
//			List<Component> ready = new ArrayList<>();
//			List<Component> notReady = new ArrayList<>();
//			for (UUID uuid : arena.getPlayers()) {
//				String name = Bukkit.getOfflinePlayer(uuid).getName();
//
//				if (uuid.equals(arena.getHostUuid())) continue;
//
//				if (arena.isPlayerReady(Bukkit.getPlayer(uuid))) {
//					ready.add(StringFormat.componentString("<green>[✔] " + name));
//				} else {
//					notReady.add(StringFormat.componentString("<gray>[✘] " + name));
//				}
//			}
//			lines.addAll(ready);
//			lines.addAll(notReady);

			lines.add(Component.text(""));
			if (arena.canStart()) {
				lines.add(StringFormat.componentString("<green>等待房主开始..."));
			} else {
				lines.add(StringFormat.componentString("<gray>等待玩家准备..."));
			}
		}

		if (arena.getState() == ArenaState.WAITING) {
			lines.add(StringFormat.componentString("<white>状态: <green>等待中..."));
			lines.add(StringFormat.componentString("<white>人数: <green>" + arena.getPlayers().size() + "/" + arena.getConfig().maxPlayers()));
			lines.add(StringFormat.componentString(""));
			lines.add(StringFormat.componentString("<gray>需要 " + arena.getConfig().minPlayers() + " 人开始"));

		} else if (arena.getState() == ArenaState.STARTING) {
			lines.add(StringFormat.componentString("<white>状态: <yellow>即将开始"));
			lines.add(StringFormat.componentString("<white>倒计时: <red>" + (arena.getGameStartCountdown() + 1) + "s"));
			lines.add(StringFormat.componentString("<white>人数: <green>" + arena.getPlayers().size()));

		} else if (arena.getState() == ArenaState.IN_GAME) {
			lines.add(StringFormat.componentString("<white>状态: <red>战斗中"));
			long alive = arena.getPlayers().size() - arena.getSpectators().size();
			lines.add(StringFormat.componentString("<white>存活: <green>" + alive));
			lines.add(StringFormat.componentString("<white>事件: <yellow>" + arena.getTimeUntilNextEvent() + "s"));
			lines.add(StringFormat.componentString("<white>物品: <aqua>" + (arena.getItemGiveCountdown() + 1) + "s"));
		} else if (arena.getState() == ArenaState.ENDING) {
			lines.add(StringFormat.componentString("<white>状态: <gold>游戏结束"));
			lines.add(StringFormat.componentString("<white>获胜者: <yellow>" + (arena.getWinnerName() == null ? "无" : arena.getWinnerName())));
		}

		if (arena.getBorder() != null) {
			lines.add(arena.getBorder().getStatusDisplay());
		}

		lines.add(StringFormat.componentString(""));
		lines.add(StringFormat.componentString("<yellow>mc.zenoxs.cn"));

		// 日期
		lines.add(StringFormat.componentString("<gray>" + new SimpleDateFormat("MM/dd").format(new Date())));

		board.updateLines(lines);
	}
}
