package me.suxuan.game.util;

import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ItemBuilder {

	private final ItemStack item;
	private final ItemMeta meta;

	private ItemBuilder(Material material) {
		this.item = new ItemStack(material);
		this.meta = item.getItemMeta();
	}

	private ItemBuilder(ItemStack item) {
		this.item = item.clone();
		this.meta = this.item.getItemMeta();
	}

	public static ItemBuilder of(Material material) {
		return new ItemBuilder(material);
	}

	public static ItemBuilder of(ItemStack item) {
		return new ItemBuilder(item);
	}

	/**
	 * 设置显示名称
	 */
	public ItemBuilder name(Component name) {
		if (meta != null) {
			meta.displayName(name);
		}
		return this;
	}

	/**
	 * 设置 Lore (描述)
	 */
	public ItemBuilder lore(Component... lines) {
		if (meta != null) {
			List<Component> lore = new ArrayList<>(Arrays.asList(lines));
			meta.lore(lore);
		}
		return this;
	}

	public ItemBuilder lore(List<Component> lines) {
		return lore(lines.toArray(new Component[0]));
	}

	/**
	 * 设置数量
	 */
	public ItemBuilder amount(int amount) {
		item.setAmount(amount);
		return this;
	}

	/**
	 * 添加附魔
	 */
	public ItemBuilder enchant(Enchantment enchant, int level) {
		if (meta != null) {
			meta.addEnchant(enchant, level, true);
		}
		return this;
	}

	/**
	 * 添加不安全的附魔 (例如锋利10)
	 */
	public ItemBuilder unsafeEnchant(Enchantment enchant, int level) {
		item.addUnsafeEnchantment(enchant, level);
		return this;
	}

	/**
	 * 添加 ItemFlags (例如隐藏附魔光效)
	 */
	public ItemBuilder flags(ItemFlag... flags) {
		if (meta != null) {
			meta.addItemFlags(flags);
		}
		return this;
	}

	public ItemBuilder modelData(int data) {
		if (meta != null) {
			meta.setCustomModelData(data);
		}
		return this;
	}

	/**
	 * [特殊] 设置皮革甲颜色
	 */
	public ItemBuilder leatherColor(Color color) {
		if (meta instanceof LeatherArmorMeta) {
			((LeatherArmorMeta) meta).setColor(color);
		}
		return this;
	}

	/**
	 * [特殊] 设置头颅的拥有者 (如果是 PLAYER_HEAD)
	 */
	public ItemBuilder skullOwner(org.bukkit.OfflinePlayer player) {
		if (meta instanceof SkullMeta) {
			((SkullMeta) meta).setOwningPlayer(player);
		}
		return this;
	}

	/**
	 * 设置不可破坏
	 */
	public ItemBuilder unbreakable(boolean unbreakable) {
		if (meta != null) {
			meta.setUnbreakable(unbreakable);
		}
		return this;
	}

	// --- 构建 ---

	/**
	 * 完成构建，返回 ItemStack
	 */
	public ItemStack build() {
		if (meta != null) {
			item.setItemMeta(meta);
		}
		return item;
	}
}