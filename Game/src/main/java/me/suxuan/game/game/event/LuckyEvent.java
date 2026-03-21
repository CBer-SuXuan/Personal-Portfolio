package me.suxuan.game.game.event;

import lombok.Getter;
import me.suxuan.game.game.Arena;

@Getter
public abstract class LuckyEvent {

	private final String id;         // 事件唯一ID (对应配置文件中的 events 列表)
	private final String name;       // 显示在聊天栏的名字
	private final String description; // 事件描述
	private final Rarity rarity;     // 稀有度 (决定权重)

	public LuckyEvent(String id, String name, String description, Rarity rarity) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.rarity = rarity;
	}

	/**
	 * 对整个房间触发事件
	 */
	public abstract void execute(Arena arena);

	public int getWeight() {
		return rarity.weight;
	}

	// 稀有度枚举
	public enum Rarity {
		COMMON(100, "<green>普通"),
		RARE(30, "<aqua>稀有"),
		EPIC(10, "<light_purple>史诗"),
		LEGENDARY(2, "<gold>传说");

		final int weight;
		final String displayName;

		Rarity(int weight, String displayName) {
			this.weight = weight;
			this.displayName = displayName;
		}
	}
}