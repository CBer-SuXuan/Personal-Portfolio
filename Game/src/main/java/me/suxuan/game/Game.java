package me.suxuan.game;

import lombok.Getter;
import me.suxuan.game.command.RootCommandRegistry;
import me.suxuan.game.command.luckpillar.PillarRootCommand;
import me.suxuan.game.command.luckpillar.impl.*;
import me.suxuan.game.command.potato.PotatoRootCommand;
import me.suxuan.game.config.MessageManager;
import me.suxuan.game.config.luckpillar.ArenaConfigManager;
import me.suxuan.game.core.SlimeWorldFactory;
import me.suxuan.game.game.GameManager;
import me.suxuan.game.game.effect.EffectManager;
import me.suxuan.game.game.event.EventManager;
import me.suxuan.game.listener.GameMechanicListener;
import me.suxuan.game.listener.LobbyInteractListener;
import me.suxuan.game.listener.PasswordListener;
import me.suxuan.game.listener.PlayerConnectionListener;
import me.suxuan.game.util.TabManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

@Getter
public final class Game extends JavaPlugin {

	@Getter
	private static Game instance;

	private SlimeWorldFactory worldFactory;
	private MessageManager messageManager;
	private ArenaConfigManager arenaConfigManager;
	private GameManager gameManager;
	private EventManager eventManager;
	private TabManager tabManager;
	private EffectManager effectManager;

	@Override
	public void onEnable() {
		instance = this;
		// 检查依赖是否可用
		if (!isASPEnvironment()) {
			getLogger().severe("错误：未检测到 Advanced Slime Paper 环境！插件将自动禁用。");
			getLogger().severe("请从 https://infernalsuite.com/ 下载 Advanced Slime Paper 服务端核心。");
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}

		getLogger().info("正在初始化 Game 插件...");
		getLogger().info("- 玩法 幸运之柱 加载中...");

		saveDefaultConfig();

		// 初始化消息管理器
		messageManager = new MessageManager(this);

		// 初始化配置管理器
		this.arenaConfigManager = new ArenaConfigManager(this);
		this.arenaConfigManager.loadConfigs();

		// 初始化世界工厂
		this.worldFactory = new SlimeWorldFactory(this);

		// 根据配置管理器扫描到的所有模板名称，进行预加载
		List<String> requiredTemplates = this.arenaConfigManager.getAllTemplateNames();
		if (requiredTemplates.isEmpty()) {
			getLogger().warning("警告：未在配置中发现任何模板引用，请检查 plugins/Game/games/ 下的配置文件。");
		} else {
			this.worldFactory.loadTemplates(requiredTemplates);
		}

		// 初始化游戏管理器
		this.gameManager = new GameManager(this, worldFactory, arenaConfigManager);

		// 初始化游戏内事件管理器
		this.eventManager = new EventManager(this);

		// 初始化 Tab 管理器
		this.tabManager = new TabManager(this);

		// 初始化全局效果管理器
		this.effectManager = new EffectManager();

		// 注册事件监听器
		getServer().getPluginManager().registerEvents(new GameMechanicListener(gameManager), this);
		getServer().getPluginManager().registerEvents(new PlayerConnectionListener(gameManager), this);
		getServer().getPluginManager().registerEvents(new LobbyInteractListener(gameManager), this);
		getServer().getPluginManager().registerEvents(new PasswordListener(gameManager), this);

		RootCommandRegistry roots = new RootCommandRegistry(this);
		roots.registerRoot("pillar", new PillarRootCommand(
				new BrowseCommand(),
				new CreateCommand(),
				new LeaveCommand(),
				new ListCommand(),
				new ReloadCommand(),
				new TestCommand(),
				new TriggerCommand()
		));

		roots.registerRoot("potato", new PotatoRootCommand(
		));

		getLogger().info("Game插件 启动成功！");
	}

	@Override
	public void onDisable() {
		if (gameManager != null) {
			getLogger().info("正在清理游戏房间...");
			gameManager.shutdown();
		}

		getLogger().info("Game插件 已卸载。");
	}

	/**
	 * 检测运行环境是否为 Advanced Slime Paper
	 */
	private boolean isASPEnvironment() {
		try {
			Class.forName("com.infernalsuite.asp.api.AdvancedSlimePaperAPI");
			return true;
		} catch (ClassNotFoundException e) {
			return false;
		}
	}

}
