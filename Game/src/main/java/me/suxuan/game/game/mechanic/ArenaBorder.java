package me.suxuan.game.game.mechanic;

import lombok.Getter;
import me.suxuan.game.config.luckpillar.ArenaConfig;
import me.suxuan.game.game.Arena;
import me.suxuan.game.game.ArenaState;
import me.suxuan.game.util.StringFormat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldBorder;

import java.util.List;
import java.util.UUID;

@Getter
public class ArenaBorder {

	private final MiniMessage mm = MiniMessage.miniMessage();

	private final Arena arena;
	private final double minSize;     // 直径
	private final double shrinkAmount; // 直径减少量

	private final int safeTime;
	private final int shrinkTime;
	private final List<UUID> activePlayers;

	private int timer;
	private State state;

	private enum State {
		STABLE,    // 安全期
		SHRINKING  // 收缩期
	}

	public ArenaBorder(Arena arena, List<UUID> activePlayers) {
		this.arena = arena;
		ArenaConfig config = arena.getConfig();

		this.minSize = config.minBorderRadius() * 2;
		this.shrinkAmount = config.borderShrinkAmount() * 2;

		this.safeTime = config.boarderSafeTime();
		this.shrinkTime = config.boarderShrinkTime();
		this.activePlayers = activePlayers;

		this.state = State.STABLE;
		this.timer = safeTime;
	}

	public void init() {
		World world = Bukkit.getWorld(arena.getWorldName());
		if (world == null) return;

		WorldBorder wb = world.getWorldBorder();
		wb.setCenter(arena.getConfig().center().getX(), arena.getConfig().center().getZ());
		wb.setSize(getBorderRadius(activePlayers.size()) * 2);
		wb.setDamageBuffer(0);
		wb.setDamageAmount(arena.getConfig().borderDamage());
		wb.setWarningDistance(1);

		this.state = State.STABLE;
		this.timer = safeTime;
	}

	public void tick() {
		if (arena.getState() != ArenaState.IN_GAME) return;

		World world = Bukkit.getWorld(arena.getWorldName());
		if (world == null) return;
		WorldBorder wb = world.getWorldBorder();

		if (wb.getSize() <= minSize + 0.1) return;

		timer--;

		if (timer <= 0) {
			switchState(wb);
		}

		if (state == State.STABLE && timer <= 5 && timer > 0) {
			arena.broadcast(StringFormat.componentString("<red>⚠ 边界将在 " + timer + " 秒后开始收缩！"));
		}
	}

	private void switchState(WorldBorder wb) {
		if (state == State.STABLE) {
			double currentSize = wb.getSize();
			double targetSize = Math.max(minSize, currentSize - shrinkAmount);

			// 核心 API：在 shrinkTime 秒内匀速变到 targetSize
			wb.changeSize(targetSize, shrinkTime * 20L);

			arena.broadcast(StringFormat.componentString("<red>[警告] 边界开始收缩！请前往安全区！"));
			arena.playSound(org.bukkit.Sound.BLOCK_END_PORTAL_FRAME_FILL, 1f);

			this.state = State.SHRINKING;
			this.timer = shrinkTime;

		} else {
			arena.broadcast(StringFormat.componentString("<green>[提示] 边界停止收缩。"));

			this.state = State.STABLE;
			this.timer = safeTime;
		}
	}

	public Double getBorderRadius(int playerCount) {
		ArenaConfig config = arena.getConfig();
		return config.pillarBaseRadius() + (playerCount * config.pillarRadiusGrowth()) + config.floorMargin();
	}

	public void reset() {
		World world = Bukkit.getWorld(arena.getWorldName());
		if (world != null) world.getWorldBorder().reset();
	}

	public Component getStatusDisplay() {
		if (state == State.STABLE) {
			return StringFormat.componentString("<green>缩圈倒计时: " + timer + "s");
		} else {
			return StringFormat.componentString("<red>正在收缩: " + timer + "s");
		}
	}
}