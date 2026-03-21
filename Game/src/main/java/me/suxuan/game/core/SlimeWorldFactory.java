package me.suxuan.game.core;

import com.infernalsuite.asp.api.AdvancedSlimePaperAPI;
import com.infernalsuite.asp.api.loaders.SlimeLoader;
import com.infernalsuite.asp.api.world.SlimeWorld;
import com.infernalsuite.asp.api.world.properties.SlimeProperties;
import com.infernalsuite.asp.api.world.properties.SlimePropertyMap;
import com.infernalsuite.asp.loaders.file.FileLoader;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;

/**
 * 核心工厂类：管理地图模板的缓存，并基于模板生成世界
 */
public class SlimeWorldFactory {

	private final JavaPlugin plugin;
	private final AdvancedSlimePaperAPI asp;

	// Map<模板名称, 世界数据对象>
	private final Map<String, SlimeWorld> templateCache = new HashMap<>();

	public SlimeWorldFactory(JavaPlugin plugin) {
		this.plugin = plugin;
		this.asp = AdvancedSlimePaperAPI.instance();
	}

	/**
	 * 加载指定的模板列表到内存
	 *
	 * @param templateNames 模板名称列表 (例如 ["desert_map", "ice_map", "void_map"])
	 */
	public void loadTemplates(List<String> templateNames) {
		plugin.getLogger().info("开始加载地图模板...");

		SlimeLoader loader = new FileLoader(new File("slime_worlds"));

		int successCount = 0;
		for (String name : templateNames) {
			try {
				// 读取世界数据 (IO操作)
				SlimeWorld world = asp.readWorld(loader, name, true, new SlimePropertyMap());
				templateCache.put(name, world);
				successCount++;
				plugin.getLogger().info(" -> 已缓存模板: " + name);
			} catch (Exception e) {
				plugin.getLogger().log(Level.WARNING, " -> 加载模板失败: " + name, e);
			}
		}
		plugin.getLogger().info("模板加载完成，成功: " + successCount + " / " + templateNames.size());
	}

	/**
	 * 指定模板：创建一个新的游戏世界实例
	 *
	 * @param templateName 想要使用的模板名称
	 * @return 生成后的 Bukkit 世界名称
	 */
	public String createGameInstance(String templateName) {
		SlimeWorld template = templateCache.get(templateName);

		if (template == null) {
			plugin.getLogger().severe("尝试创建世界但模板不存在或未加载: " + templateName);
			return null;
		}

		return cloneAndLoad(template);
	}

	/**
	 * 随机模板：随机从已加载的模板中选择一个创建世界
	 *
	 * @return 生成后的 Bukkit 世界名称
	 */
	public String createRandomGameInstance() {
		if (templateCache.isEmpty()) {
			plugin.getLogger().severe("缓存中没有任何模板，无法随机创建！");
			return null;
		}

		List<SlimeWorld> values = new ArrayList<>(templateCache.values());
		SlimeWorld randomTemplate = values.get(ThreadLocalRandom.current().nextInt(values.size()));

		return cloneAndLoad(randomTemplate);
	}

	/**
	 * 内部私有方法：执行具体的克隆和加载逻辑
	 */
	private String cloneAndLoad(SlimeWorld template) {
		// 生成唯一的临时世界名
		String instanceName = "game_" + UUID.randomUUID().toString().substring(0, 8);

		try {
			// 1. 克隆
			SlimeWorld instance = template.clone(instanceName);

			// 2. 设置属性
			instance.getPropertyMap().setValue(SlimeProperties.PVP, true);
			instance.getPropertyMap().setValue(SlimeProperties.DIFFICULTY, "normal");
			instance.getPropertyMap().setValue(SlimeProperties.ALLOW_ANIMALS, false);
			instance.getPropertyMap().setValue(SlimeProperties.ALLOW_MONSTERS, false);

			// 3. 加载进 Bukkit
			asp.loadWorld(instance, true);

			plugin.getLogger().fine("成功创建世界实例: " + instanceName + " (源模板: " + template.getName() + ")");
			return instanceName;

		} catch (Exception e) {
			plugin.getLogger().log(Level.SEVERE, "创建世界实例异常: " + instanceName, e);
			return null;
		}
	}

	/**
	 * 卸载并销毁世界
	 */
	public void deleteInstance(String worldName, Location location) {
		World world = Bukkit.getWorld(worldName);
		if (world == null) return;

		// 保险起见，踢出所有玩家
		if (!world.getPlayers().isEmpty()) {
			for (org.bukkit.entity.Player p : world.getPlayers()) {
				p.teleport(location);
				p.setHealth(20);
				p.setFoodLevel(20);
				p.getInventory().clear();
				p.setGameMode(GameMode.ADVENTURE);
				p.getActivePotionEffects().forEach(e -> p.removePotionEffect(e.getType()));
				p.setFireTicks(0);
			}
		}

		Bukkit.unloadWorld(world, false); // false = 不保存
		plugin.getLogger().fine("已销毁世界: " + worldName);
	}
}