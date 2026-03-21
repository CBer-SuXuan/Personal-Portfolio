package me.suxuan.game.gui.luckpillar;

import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.function.Consumer;

/**
 * 支持自动刷新的 ChestGui
 */
public class AutoUpdateGui extends ChestGui {

	private final Plugin plugin;
	private final Consumer<AutoUpdateGui> refreshAction;
	private BukkitTask task;

	public AutoUpdateGui(int rows, String title, Plugin plugin, long periodTicks, Consumer<AutoUpdateGui> refreshAction) {
		super(rows, title);
		this.plugin = plugin;
		this.refreshAction = refreshAction;

		this.setOnGlobalClick(e -> e.setCancelled(true)); // 默认禁止点击

		// 当窗口关闭时停止刷新
		this.setOnClose(event -> stopTask());

		// 构造时启动任务
		startTask(periodTicks);
	}

	private void startTask(long period) {
		this.task = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
			if (refreshAction != null) {
				// 执行刷新逻辑
				refreshAction.accept(this);
			}
			// IF 的 update 会重绘界面
			this.update();
		}, period, period);
	}

	private void stopTask() {
		if (task != null && !task.isCancelled()) {
			task.cancel();
			task = null;
		}
	}
}
