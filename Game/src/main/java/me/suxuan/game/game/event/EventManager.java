package me.suxuan.game.game.event;

import com.google.common.reflect.ClassPath;
import me.suxuan.game.game.Arena;
import me.suxuan.game.game.event.impl.NothingEvent;
import me.suxuan.game.util.StringFormat;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

public class EventManager {

	private final Map<String, LuckyEvent> registeredEvents = new HashMap<>();
	private final JavaPlugin plugin;
	private final MiniMessage mm = MiniMessage.miniMessage();

	public EventManager(JavaPlugin plugin) {
		this.plugin = plugin;

		// 自动扫描并注册
		autoRegisterEvents();
	}

	/**
	 * 自动扫描指定包下的所有 LuckyEvent 子类并实例化注册
	 */
	@SuppressWarnings("UnstableApiUsage")
	private void autoRegisterEvents() {
		plugin.getLogger().info("开始自动扫描并注册随机事件...");
		int count = 0;

		try {
			// 1. 获取插件的类加载器
			ClassLoader loader = plugin.getClass().getClassLoader();

			// 2. 指定要扫描的包名
			String packageName = "me.suxuan.pillar.game.event.impl";

			// 3. 使用 Guava ClassPath 扫描
			ClassPath classPath = ClassPath.from(loader);

			// 遍历所有顶层类
			for (ClassPath.ClassInfo classInfo : classPath.getTopLevelClassesRecursive(packageName)) {
				try {
					Class<?> clazz = classInfo.load();

					// 4. 判断是否是 LuckyEvent 的非抽象子类
					if (LuckyEvent.class.isAssignableFrom(clazz)
							&& !Modifier.isAbstract(clazz.getModifiers())
							&& !clazz.isInterface()) {

						// 5. 实例化并注册
						LuckyEvent event = (LuckyEvent) clazz.getDeclaredConstructor().newInstance();
						register(event);
						count++;
					}
				} catch (Exception e) {
					plugin.getLogger().warning("无法注册事件类: " + classInfo.getName() + " - " + e.getMessage());
				}
			}
		} catch (IOException e) {
			plugin.getLogger().severe("扫描事件包失败！");
			e.printStackTrace();
		}

		plugin.getLogger().info("随机事件注册完成，共加载 " + count + " 个事件。");
	}

	private void register(LuckyEvent event) {
		if (registeredEvents.containsKey(event.getId())) {
			plugin.getLogger().warning("发现重复的事件ID: " + event.getId() + "，将覆盖旧事件。");
		}
		registeredEvents.put(event.getId(), event);
	}

	/**
	 * 根据房间配置的允许列表，随机触发一个事件
	 *
	 * @param arena           目标房间
	 * @param allowedEventIds 配置文件中允许的事件ID列表
	 */
	public LuckyEvent pickRandomEvent(Arena arena, List<String> allowedEventIds) {
		// 过滤出当前地图启用的事件
		List<LuckyEvent> pool = allowedEventIds.stream()
				.map(registeredEvents::get)
				.filter(Objects::nonNull)
				.collect(Collectors.toList());

		if (pool.isEmpty()) {
			// 如果配置列表为空，则为所有事件
			pool = new ArrayList<>(registeredEvents.values());
		}

		if (pool.isEmpty()) return new NothingEvent();

		// 权重随机算法
		int totalWeight = pool.stream().mapToInt(LuckyEvent::getWeight).sum();
		int random = new Random().nextInt(totalWeight);
		int current = 0;

		LuckyEvent selected = null;
		for (LuckyEvent event : pool) {
			current += event.getWeight();
			if (random < current) {
				selected = event;
				break;
			}
		}

		if (selected == null) selected = pool.getFirst();

		announceEvent(arena, selected);

		return selected;
	}

	private void announceEvent(Arena arena, LuckyEvent event) {
		String title = event.getRarity().displayName + "事件";
		String subtitle = "<white>" + event.getName();

		for (UUID uuid : arena.getPlayers()) {
			org.bukkit.entity.Player p = org.bukkit.Bukkit.getPlayer(uuid);
			if (p != null) {
				p.showTitle(Title.title(StringFormat.componentString(title), StringFormat.componentString(subtitle), 10, 20, 20));
				p.sendMessage(StringFormat.componentString(" "));
				p.sendMessage(StringFormat.componentString("<yellow>========================================"));
				p.sendMessage(StringFormat.componentString(" <white>5s后触发事件: <bold>" + event.getName()));
				p.sendMessage(StringFormat.componentString(" <gray>" + event.getDescription()));
				p.sendMessage(StringFormat.componentString("<yellow>========================================"));
				p.sendMessage(StringFormat.componentString(" "));
			}
		}
	}

	public LuckyEvent getEventById(String id) {
		return registeredEvents.get(id);
	}

	public Collection<LuckyEvent> getAllEvents() {
		return registeredEvents.values();
	}
}