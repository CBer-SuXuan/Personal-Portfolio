package me.suxuan.game.config;

import me.suxuan.game.Game;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class MessageManager {

	private final Game plugin;
	private FileConfiguration config;
	private File configFile;

	private final Map<String, String> messageCache = new HashMap<>();

	public MessageManager(Game plugin) {
		this.plugin = plugin;
		load();
	}

	public void load() {
		configFile = new File(plugin.getDataFolder(), "messages.yml");

		// 如果文件不存在，保存默认资源
		if (!configFile.exists()) {
			plugin.saveResource("messages.yml", false);
		}

		config = YamlConfiguration.loadConfiguration(configFile);

		// 处理版本更新：如果有新的 key 在默认文件里有但在本地文件里没有，补全它
		// (简单起见，这里先只加载，暂不写复杂的合并逻辑)
		reloadCache();
	}

	public void reload() {
		load();
		plugin.getLogger().info("消息文件 messages.yml 已重载。");
	}

	private void reloadCache() {
		messageCache.clear();
		for (String key : config.getKeys(true)) {
			if (config.isString(key)) {
				String value = config.getString(key);
				// 预处理颜色代码
				messageCache.put(key, ChatColor.translateAlternateColorCodes('&', value));
			}
		}
	}

	/**
	 * 获取原始消息 (带前缀)
	 */
	public String get(String key) {
		String prefix = messageCache.getOrDefault("prefix", "");
		String msg = messageCache.get(key);
		if (msg == null) return "<red>Missing message: " + key;
		return prefix + msg;
	}

	/**
	 * 获取原始消息 (不带前缀)
	 */
	public String getRaw(String key) {
		return messageCache.getOrDefault(key, "<red>Missing message: " + key);
	}
}