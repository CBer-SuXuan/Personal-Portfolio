package me.suxuan.game.game;


import lombok.Getter;
import me.suxuan.game.Game;
import me.suxuan.game.config.luckpillar.ArenaConfig;
import me.suxuan.game.core.SlimeWorldFactory;
import me.suxuan.game.game.board.BoardProvider;
import me.suxuan.game.game.effect.GlobalEffect;
import me.suxuan.game.game.event.LuckyEvent;
import me.suxuan.game.game.generator.GeneratorFactory;
import me.suxuan.game.game.generator.MapGenerator;
import me.suxuan.game.game.mechanic.ArenaBorder;
import me.suxuan.game.gui.luckpillar.VoteGUI;
import me.suxuan.game.util.ItemBuilder;
import me.suxuan.game.util.SimpleScoreboard;
import me.suxuan.game.util.StringFormat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 游戏房间实体类
 * 代表一局独立的游戏，拥有独立的世界和玩家列表。
 */
@Getter
public class Arena {

	private final Game plugin;
	private final MiniMessage mm = MiniMessage.miniMessage();
	private final SlimeWorldFactory worldFactory;
	private final Arena arena;

	// 基础信息
	private final String id; // 房间唯一ID
	private String worldName; // 临时世界名
	private ArenaState state; // 当前状态

	// 玩家数据
	private final Set<UUID> players = new HashSet<>(); // 所有在场玩家
	private final Set<UUID> spectators = new HashSet<>(); // 淘汰后的观察者
	private final List<UUID> participants = new ArrayList<>(); // 参与者

	// 计分板
	private final Map<UUID, SimpleScoreboard> boards = new HashMap<>();
	private final BoardProvider boardProvider = new BoardProvider();
	private BukkitTask boardTask;

	// 游戏配置
	private final ArenaConfig config;

	// 投票系统
	private final Map<String, Integer> effectVotes = new HashMap<>(); // EffectID -> 票数
	private final Map<UUID, String> playerVotes = new HashMap<>(); // Player -> EffectID
	private VoteGUI voteGUI;

	// 游戏主要系统
	private BukkitTask gameTask;
	private int gameStartCountdown;

	// 全局效果
	private GlobalEffect activeGlobalEffect;  // 当前全局效果

	// 随机事件系统
	private LuckyEvent nextEvent; // 下一个要发生的事件
	private int timeUntilNextEvent;  // 下一事件发生倒计时
	private boolean isEventWarningActive = false; // 是否处于预警阶段

	// 物品给予系统
	private BukkitTask itemTask;
	private int itemGiveCountdown;

	// 房间系统
	private UUID hostUuid; // 房主UUID
	private String password; // 密码
	private String roomName; // 房间名
	private final Map<UUID, Boolean> readyStatus = new HashMap<>(); // 玩家准备状态

	// 边界系统
	private ArenaBorder border;
	private BukkitTask borderTask;

	private String winnerName;

	// 柱子位置映射
	private final Map<UUID, Location> pillarLocations = new HashMap<>();

	public Arena(Game plugin, SlimeWorldFactory worldFactory, ArenaConfig config, String id, Player host,
				 String password) {
		this.plugin = plugin;
		this.worldFactory = worldFactory;
		this.config = config;
		this.id = id;
		this.state = ArenaState.WAITING;
		this.gameStartCountdown = config.countdownSeconds();
		this.timeUntilNextEvent = config.eventInterval();
		this.itemGiveCountdown = config.itemGiveInterval();

		this.hostUuid = host.getUniqueId();
		this.password = password;
		this.roomName = host.getName() + "的房间";

		this.worldName = worldFactory.createGameInstance(config.templateName());

		if (this.worldName == null) {
			throw new RuntimeException("房间 " + id + " 初始化失败：模板 " + config.templateName() + " 加载失败！");
		}

		World world = Bukkit.getWorld(worldName);
		world.setGameRule(GameRules.ADVANCE_TIME, false);

		startBoardTask();
		arena = this;
	}

	// ================== 玩家管理 ==================

	// 玩家加入
	public void addPlayer(Player player) {

		if (arena.state != ArenaState.WAITING) {
			player.sendMessage(StringFormat.componentString("<red>不能加入该房间！"));
			return;
		}

		players.add(player.getUniqueId());

		World world = Bukkit.getWorld(worldName);
		if (world != null) {
			Vector lobbyVec = config.lobbyLocation();
			player.teleport(new Location(world, lobbyVec.getX(), lobbyVec.getY(), lobbyVec.getZ()));

			plugin.getTabManager().updateTab(player);
			resetPlayerStatus(player);
			player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, Integer.MAX_VALUE, 0, false, false, false));
			broadcast(StringFormat.componentString(plugin.getMessageManager().get("game.join-broadcast")
					.replace("%player%", player.getName())
					.replace("%count%", String.valueOf(players.size()))
					.replace("%max%", String.valueOf(config.maxPlayers()))));

			SimpleScoreboard board = new SimpleScoreboard(player);
			boards.put(player.getUniqueId(), board);
			boardProvider.update(board, this);

			readyStatus.put(player.getUniqueId(), true); // 默认准备
			giveLobbyItems(player); // 发放大厅物品

			Bukkit.getScheduler().runTaskLater(plugin, () -> {
				if (voteGUI == null) {
					voteGUI = new VoteGUI(this, plugin);
					for (GlobalEffect effect : voteGUI.getOptions()) {
						effectVotes.put(effect.getId(), 0);
					}
				}
				voteGUI.open(player);
			}, 10L);
		}
	}

	// 玩家离开
	public void removePlayer(Player player) {
		players.remove(player.getUniqueId());
		spectators.remove(player.getUniqueId());

		config.returnLocation().setWorld(Bukkit.getWorld(config.returnLocationWorldName()));
		player.teleport(config.returnLocation());

		plugin.getTabManager().updateTab(player);
		resetPlayerStatus(player);

		broadcast(StringFormat.componentString(plugin.getMessageManager().get("game.leave-broadcast")
				.replace("%player%", player.getName())
				.replace("%count%", String.valueOf(players.size()))
				.replace("%max%", String.valueOf(config.maxPlayers()))));

		SimpleScoreboard board = boards.remove(player.getUniqueId());
		if (board != null) {
			board.delete(); // 清除计分板
		}

		String chooseEffect = playerVotes.get(player.getUniqueId());
		effectVotes.put(chooseEffect, effectVotes.getOrDefault(chooseEffect, 1) - 1);
		playerVotes.remove(player.getUniqueId());

		// 如果游戏正在进行且人走光了/只剩一人
		if (state == ArenaState.IN_GAME) {
			checkWinCondition();
		} else if (state == ArenaState.WAITING || state == ArenaState.STARTING) {
			// 如果没人了直接结束
			if (players.isEmpty()) {
				endGameWithoutWinner();
				return;
			}
			// 如果倒计时中人数不够了，取消倒计时
			if (players.size() < config.minPlayers() && state == ArenaState.STARTING) {
				state = ArenaState.WAITING;
				broadcast(StringFormat.componentString(plugin.getMessageManager().get("game.not-enough-players")));
				gameStartCountdown = config.countdownSeconds();
			}
		}

		readyStatus.remove(player.getUniqueId());

		if (player.getUniqueId().equals(hostUuid) && state == ArenaState.WAITING) {
			broadcast(StringFormat.componentString("<red>房主已离开，房间解散！"));
			endGameWithoutWinner();
		}
	}

	// 玩家淘汰
	public void eliminatePlayer(Player player) {
		if (state != ArenaState.IN_GAME) return;
		if (spectators.contains(player.getUniqueId())) return;

		spectators.add(player.getUniqueId());
		player.setGameMode(GameMode.SPECTATOR);

		player.teleport(pillarLocations.get(player.getUniqueId()));

		if (activeGlobalEffect != null) {
			activeGlobalEffect.onPlayerEliminated(player);
		}

		broadcast(StringFormat.componentString(plugin.getMessageManager().get("game.eliminated")
				.replace("%player%", player.getName())));
		player.showTitle(Title.title(
				StringFormat.componentString(plugin.getMessageManager().getRaw("game.eliminated-title")),
				StringFormat.componentString(plugin.getMessageManager().getRaw("game.eliminated-subtitle")),
				10, 70, 20));

		checkWinCondition();
	}

	// 玩家投票
	public void castVote(Player p, String effectId) {
		if (state != ArenaState.WAITING && state != ArenaState.STARTING) return;

		if (playerVotes.containsKey(p.getUniqueId())) {
			String oldEffect = playerVotes.get(p.getUniqueId());
			effectVotes.put(oldEffect, effectVotes.getOrDefault(oldEffect, 1) - 1);
		}

		playerVotes.put(p.getUniqueId(), effectId);
		effectVotes.put(effectId, effectVotes.getOrDefault(effectId, 0) + 1);

		p.sendMessage(StringFormat.componentString("<green>你投票给了: " + plugin.getEffectManager().getDisplayById(effectId)));
	}

	// 玩家准备
	public void setPlayerReady(Player player, boolean ready) {
		if (state != ArenaState.WAITING) return;
		readyStatus.put(player.getUniqueId(), ready);

		broadcast(StringFormat.componentString("<yellow>" + player.getName() + (ready ? " <green>已准备" : " <red>取消准备")));

		playSound(Sound.BLOCK_NOTE_BLOCK_HAT, 1f);
	}

	// 检查玩家是否准备
	public boolean isPlayerReady(Player player) {
		return readyStatus.getOrDefault(player.getUniqueId(), true);
	}

	// 是否可以开始游戏
	public boolean canStart() {
		return players.size() >= config.minPlayers();
	}

	// 给予大厅物品
	private void giveLobbyItems(Player p) {
		p.getInventory().clear();

		// 离开床
		p.getInventory().setItem(8, ItemBuilder.of(Material.RED_BED).name(StringFormat.componentString("<red>离开房间")).build());
		// 设置
		p.getInventory().setItem(0, ItemBuilder.of(Material.COMPARATOR).name(StringFormat.componentString("<yellow>设置")).build());

		if (p.getUniqueId().equals(hostUuid)) {
			p.getInventory().setItem(4, ItemBuilder.of(Material.EMERALD).name(StringFormat.componentString("<green><bold>开始游戏")).build());
		} else {
			updateReadyItem(p);
		}
	}

	// 更新准备状态
	public void updateReadyItem(Player p) {
		boolean ready = isPlayerReady(p);
		if (ready) {
			p.getInventory().setItem(4, ItemBuilder.of(Material.LIME_DYE)
					.name(StringFormat.componentString("<green><bold>已准备"))
					.lore(StringFormat.componentString("<gray>点击切换为暂离状态"),
							StringFormat.componentString("<gray>(暂离后游戏开始将自动旁观)"))
					.build());
		} else {
			p.getInventory().setItem(4, ItemBuilder.of(Material.GRAY_DYE)
					.name(StringFormat.componentString("<gray><bold>已暂离"))
					.lore(StringFormat.componentString("<gray>点击切换为准备状态"))
					.build());
		}
	}

	// ================== 流程控制 ==================

	public String getWinnerEffect() {
		return effectVotes.entrySet().stream()
				.max(Map.Entry.comparingByValue())
				.map(Map.Entry::getKey)
				.orElse(null);
	}

	// 开始游戏
	private void startGame() {

		state = ArenaState.STARTING;

		for (UUID uuid : new ArrayList<>(players)) {
			Player p = Bukkit.getPlayer(uuid);
			if (p == null) continue;

			boolean isReady = readyStatus.getOrDefault(uuid, false);
			if (uuid.equals(hostUuid)) isReady = true;

			if (isReady) {
				participants.add(uuid);
			} else {
				spectators.add(uuid);
				p.setGameMode(GameMode.SPECTATOR);
				p.sendRichMessage("<red>你未处于准备状态，本局游戏将作为旁观者。");
			}
		}

		if (participants.size() < config.minPlayers()) {
			state = ArenaState.WAITING;
			spectators.clear();
			participants.clear();
			broadcast(StringFormat.componentString("<red>有效玩家人数不足 (部分玩家未准备)，游戏取消开始！"));
			return;
		}

		broadcast(StringFormat.componentString(plugin.getMessageManager().get("game.start")));

		World world = Bukkit.getWorld(worldName);
		if (world == null) return;
		setWorldGameRule(world);

		generatePillarsAndTeleport(world);

		initGlobalEffect();

		startEventTask();

		startItemDistribution();

		startBorderTask();

		state = ArenaState.IN_GAME;
	}

	// 计分板更新任务
	private void startBoardTask() {
		boardTask = new BukkitRunnable() {
			@Override
			public void run() {
				for (Map.Entry<UUID, SimpleScoreboard> entry : boards.entrySet()) {
					if (Bukkit.getPlayer(entry.getKey()) != null) {
						boardProvider.update(entry.getValue(), Arena.this);
					}
				}
			}
		}.runTaskTimer(plugin, 0L, 5L);
	}

	// 检查是否满足开始条件
	private void checkCountdown() {
		if (state == ArenaState.WAITING && players.size() >= config.minPlayers()) {
			startCountdown();
		}
	}

	// 开始倒计时
	public void startCountdown() {
		state = ArenaState.STARTING;

		new BukkitRunnable() {
			@Override
			public void run() {
				if (state != ArenaState.STARTING) {
					this.cancel();
					return;
				}

				if (gameStartCountdown <= 0) {
					startGame();
					this.cancel();
					return;
				}

				if (gameStartCountdown <= 5 || gameStartCountdown % 10 == 0) {
					broadcast(StringFormat.componentString(plugin.getMessageManager().get("game.countdown")
							.replace("%time%", String.valueOf(gameStartCountdown))));
					playSound(Sound.BLOCK_NOTE_BLOCK_PLING, 1f);
				}
				gameStartCountdown--;
			}
		}.runTaskTimer(plugin, 0L, 20L);
	}

	// 玩家传送
	private void generatePillarsAndTeleport(World world) {
		int count = participants.size();
		MapGenerator generator = GeneratorFactory.getGenerator(config.floorStyle());

		List<Location> spawnLocs = generator.generate(this, participants.size());

		for (int i = 0; i < count; i++) {
			if (i >= spawnLocs.size()) break;
			Player p = Bukkit.getPlayer(participants.get(i));
			if (p != null) {
				Location loc = spawnLocs.get(i);
				p.teleport(loc);
				resetPlayerStatus(p);
				p.closeInventory();
				p.setGameMode(GameMode.SURVIVAL);
				pillarLocations.put(p.getUniqueId(), loc);
			}
		}

		for (UUID specUuid : spectators) {
			Player sp = Bukkit.getPlayer(specUuid);
			if (sp != null) {
				sp.teleport(config.center().toLocation(world).add(0, config.pillarHeight(), 0));
			}
		}
	}

	// 开启随机全局效果
	private void initGlobalEffect() {
		GlobalEffect effect;

		effect = plugin.getEffectManager().createEffectById(getWinnerEffect());

		// 应用效果
		if (effect != null) {
			this.activeGlobalEffect = effect;

			effect.apply(this, plugin);

			for (UUID uuid : players) {
				Player p = Bukkit.getPlayer(uuid);
				if (p != null) {
					p.showTitle(Title.title(StringFormat.componentString("<light_purple><bold>" + effect.getName()),
							StringFormat.componentString("<gray>" + effect.getDescription()),
							10, 70, 20));
					p.sendMessage(StringFormat.componentString(" "));
					p.sendMessage(StringFormat.componentString("<light_purple><bold>[全局效果] :<yellow>" + effect.getName()));
					p.sendMessage(StringFormat.componentString("<gray>" + effect.getDescription()));
					p.sendMessage(StringFormat.componentString(" "));
					p.playSound(p.getLocation(), org.bukkit.Sound.BLOCK_END_PORTAL_SPAWN, 0.5f, 0.5f);
				}
			}
		}
	}

	// 开始随机事件
	private void startEventTask() {
		if (timeUntilNextEvent <= 0) return; // 没开启或没有事件

		gameTask = new BukkitRunnable() {
			@Override
			public void run() {

				if (state != ArenaState.IN_GAME) {
					this.cancel();
					return;
				}

				// 1. 如果处于预警阶段 (最后5秒)
				if (isEventWarningActive) {
					handleEventWarning();
				}
				// 2. 正常倒计时阶段
				else {
					timeUntilNextEvent--;

					// 到达预警阈值 (比如还剩 5 秒)
					if (timeUntilNextEvent <= 5) {
						// 提前随机选好下一个事件
						nextEvent = plugin.getEventManager().pickRandomEvent(arena, config.enabledEvents());
						isEventWarningActive = true;
					}
				}
			}
		}.runTaskTimer(plugin, 0L, 20L);
	}

	// 开始物品分发
	private void startItemDistribution() {
		if (itemGiveCountdown <= 0 || config.itemPool().isEmpty()) return; // 没开启或池子是空的

		itemTask = new BukkitRunnable() {
			@Override
			public void run() {
				if (state != ArenaState.IN_GAME) {
					this.cancel();
					return;
				}

				if (itemGiveCountdown <= 0) {
					List<Material> pool = config.itemPool();

					// 遍历所有存活玩家
					for (UUID uuid : players) {
						if (spectators.contains(uuid)) continue;

						Player p = Bukkit.getPlayer(uuid);
						if (p != null) {
							Material randomMat = pool.get(ThreadLocalRandom.current().nextInt(pool.size()));

							ItemStack item = new ItemStack(randomMat, 1);
							p.getInventory().addItem(item);

							p.playSound(p.getLocation(), Sound.ENTITY_ITEM_PICKUP, 0.5f, 1.5f);
						}
					}

					itemGiveCountdown = config.itemGiveInterval();
				}
				itemGiveCountdown--;

			}
		}.runTaskTimer(plugin, 0L, 20L);
	}

	// 边界更新任务
	private void startBorderTask() {

		this.border = new ArenaBorder(this, participants);
		this.border.init();
		borderTask = new BukkitRunnable() {
			@Override
			public void run() {
				border.tick();
			}
		}.runTaskTimer(plugin, 0L, 20L);
	}

	// 处理预警逻辑
	private void handleEventWarning() {
		timeUntilNextEvent--;

		if (timeUntilNextEvent > 0) {
			playSound(Sound.BLOCK_NOTE_BLOCK_HAT, 1.0f); // 滴答声
		} else {
			triggerEvent(nextEvent);

			isEventWarningActive = false;
			timeUntilNextEvent = config.eventInterval();
			nextEvent = null;
		}
	}

	private void triggerEvent(LuckyEvent event) {
		if (event == null) return;

		playSound(Sound.ENTITY_WITHER_SHOOT, 0.5f);
		broadcast(StringFormat.componentString("<light_purple>[幸运事件] <white>已触发: <bold>" + event.getName()));

		event.execute(this);
	}

	// 检查胜利条件
	private void checkWinCondition() {
		// 计算存活人数
		long aliveCount = players.stream().filter(uuid -> !spectators.contains(uuid)).count();

		if (aliveCount <= 1) {
			// 找出赢家
			UUID winnerUUID = players.stream()
					.filter(uuid -> !spectators.contains(uuid))
					.findFirst()
					.orElse(null);

			endGame(winnerUUID);
		}
	}

	// 结束游戏
	public void endGame(UUID winnerUUID) {
		state = ArenaState.ENDING;

		if (winnerUUID != null) {
			winnerName = Bukkit.getOfflinePlayer(winnerUUID).getName();
			broadcast(StringFormat.componentString(plugin.getMessageManager().get("game.win")
					.replace("%winner%", winnerName)));
		} else {
			broadcast(StringFormat.componentString(plugin.getMessageManager().get("game.no-winner")));
		}

		stopTasks();

		// 10秒后清理房间
		Bukkit.getScheduler().runTaskLater(plugin, this::resetRoom, 200L);
	}

	public void endGameWithoutWinner() {
		for (UUID uuid : players) {
			Player player = Bukkit.getPlayer(uuid);
			if (player == null) continue;
			removePlayer(player);
		}
		stopTasks();
		stop(worldName);
	}

	private void stopTasks() {
		if (gameTask != null) gameTask.cancel();
		if (itemTask != null) itemTask.cancel();
		if (borderTask != null) borderTask.cancel();
		if (border != null) {
			border.reset();
			border = null;
		}
	}

	private void resetRoom() {
		if (boardTask != null) boardTask.cancel();
		boards.clear();

		state = ArenaState.RESETTING;
		broadcast(StringFormat.componentString("<yellow>正在重置房间地图..."));

		if (players.isEmpty() || !players.contains(hostUuid)) {
			broadcast(StringFormat.componentString("<red>房间没有玩家或房主已退出！"));
			for (UUID uuid : players) {
				Player p = Bukkit.getPlayer(uuid);
				if (p != null) {
					p.teleport(config.returnLocation());
					resetPlayerStatus(p);
				}
			}
			stop(worldName);
			return;
		}

		String oldWorldName = this.worldName;
		this.worldName = worldFactory.createGameInstance(config.templateName());

		resetData();

		state = ArenaState.WAITING;

		World newWorld = Bukkit.getWorld(worldName);

		for (UUID uuid : players) {
			Player p = Bukkit.getPlayer(uuid);
			if (p != null) {

				Vector lobbyVec = config.lobbyLocation();
				p.teleport(new Location(newWorld, lobbyVec.getX(), lobbyVec.getY(), lobbyVec.getZ()));
				resetPlayerStatus(p);
				p.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, Integer.MAX_VALUE, 0, false, false, false));
				readyStatus.put(uuid, true);
				giveLobbyItems(p); // 重新发准备道具

				SimpleScoreboard board = new SimpleScoreboard(p);
				boards.put(p.getUniqueId(), board);
				boardProvider.update(board, this);

				plugin.getTabManager().updateTab(p);
			}
		}

		setWorldGameRule(Bukkit.getWorld(worldName));

		broadcast(StringFormat.componentString("<green>房间重置完成！请准备开始下一局。"));
		startBoardTask();
		stopOld(oldWorldName);
	}

	public void resetData() {
		this.spectators.clear();
		this.participants.clear();
		this.pillarLocations.clear();
		this.readyStatus.clear();
		for (UUID uuid : players) readyStatus.put(uuid, false);

		if (activeGlobalEffect != null) {
			activeGlobalEffect.remove();
			activeGlobalEffect = null;
		}

		this.effectVotes.clear();
		this.playerVotes.clear();
		this.voteGUI = new VoteGUI(this, plugin);
		for (GlobalEffect effect : voteGUI.getOptions()) {
			effectVotes.put(effect.getId(), 0);
		}

		this.winnerName = null;

		this.gameStartCountdown = config.countdownSeconds();
		this.timeUntilNextEvent = config.eventInterval();
		this.itemGiveCountdown = config.itemGiveInterval();

	}

	// 删除房间
	public void stop(String worldName) {
		config.returnLocation().setWorld(Bukkit.getWorld(config.returnLocationWorldName()));
		worldFactory.deleteInstance(worldName, config.returnLocation());

		plugin.getGameManager().removeArena(this.id);
		plugin.getLogger().info("Arena " + id + " 已停止并销毁。");
	}

	public void stopOld(String oldWorldName) {
		config.returnLocation().setWorld(Bukkit.getWorld(config.returnLocationWorldName()));
		worldFactory.deleteInstance(oldWorldName, config.returnLocation());
	}

	// ================== 辅助方法 ==================

	// 广播消息给所有玩家
	public void broadcast(Component msg) {
		for (UUID uuid : players) {
			Player p = Bukkit.getPlayer(uuid);
			if (p != null) p.sendMessage(msg);
		}
	}

	// 播放声音给所有玩家
	public void playSound(Sound sound, float pitch) {
		for (UUID uuid : players) {
			Player p = Bukkit.getPlayer(uuid);
			if (p != null) p.playSound(p.getLocation(), sound, 1f, pitch);
		}
	}

	// 重置玩家状态
	private void resetPlayerStatus(Player p) {
		p.setHealth(20);
		p.setFoodLevel(20);
		p.getInventory().clear();
		p.setGameMode(GameMode.ADVENTURE);
		p.setFireTicks(0);
		p.getActivePotionEffects().forEach(e -> p.removePotionEffect(e.getType()));
		p.getAttribute(Attribute.JUMP_STRENGTH).setBaseValue(0.42);
		p.getAttribute(Attribute.ATTACK_DAMAGE).setBaseValue(1);
		p.getAttribute(Attribute.ENTITY_INTERACTION_RANGE).setBaseValue(3);
		p.getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(0.1);
	}

	public boolean isPrivate() {
		return password != null && !password.isEmpty();
	}

	public void setWorldGameRule(World world) {
		world.setGameRule(GameRules.ADVANCE_TIME, false);
		world.setGameRule(GameRules.ADVANCE_WEATHER, false);
		world.setGameRule(GameRules.KEEP_INVENTORY, false);
		world.setGameRule(GameRules.IMMEDIATE_RESPAWN, true);
		world.setGameRule(GameRules.SHOW_ADVANCEMENT_MESSAGES, false);
		world.setGameRule(GameRules.SPAWN_MOBS, false);
		world.setGameRule(GameRules.SPAWN_MONSTERS, false);
		world.setGameRule(GameRules.SPAWN_PATROLS, false);
		world.setGameRule(GameRules.SPAWN_PHANTOMS, false);
		world.setGameRule(GameRules.SPAWN_WANDERING_TRADERS, false);
		world.setGameRule(GameRules.SPAWN_WARDENS, false);
	}
}
