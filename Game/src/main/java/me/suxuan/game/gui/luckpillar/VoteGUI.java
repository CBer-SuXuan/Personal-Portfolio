package me.suxuan.game.gui.luckpillar;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import lombok.Getter;
import me.suxuan.game.Game;
import me.suxuan.game.game.Arena;
import me.suxuan.game.game.effect.GlobalEffect;
import me.suxuan.game.util.ItemBuilder;
import me.suxuan.game.util.StringFormat;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;

import java.util.ArrayList;
import java.util.List;

@Getter
public class VoteGUI {

	private final Game plugin;
	private final Arena arena;
	private final MiniMessage mm = MiniMessage.miniMessage();

	private final List<GlobalEffect> options = new ArrayList<>();

	public VoteGUI(Arena arena, Game plugin) {
		this.arena = arena;
		this.plugin = plugin;

		this.options.addAll(plugin.getEffectManager().createUniqueRandomEffects(5));
	}

	public void open(Player player) {
		AutoUpdateGui gui = new AutoUpdateGui(3, "投票全局效果", plugin, 20L, (g) -> {
			populate(g, player);
		});

		populate(gui, player);
		gui.show(player);
	}

	private void populate(ChestGui gui, Player player) {
		StaticPane pane;
		if (!gui.getPanes().isEmpty()) {
			pane = (StaticPane) gui.getPanes().getFirst();
		} else {
			pane = new StaticPane(0, 1, 9, 1);
			gui.addPane(pane);
		}

		int[] slots = {0, 2, 4, 6, 8};

		for (int i = 0; i < options.size() && i < slots.length; i++) {
			GlobalEffect effect = options.get(i);
			int slotX = slots[i];

			boolean isSelected = false;
			if (arena.getPlayerVotes().containsKey(player.getUniqueId())) {
				String myVote = arena.getPlayerVotes().get(player.getUniqueId());
				isSelected = effect.getId().equals(myVote);
			}

			// 获取当前总票数
			int votes = arena.getEffectVotes().get(effect.getId());

			// 构建图标
			ItemBuilder builder = ItemBuilder.of(Material.PAPER);
			if (isSelected) {
				builder = ItemBuilder.of(Material.MAP);
				builder.enchant(Enchantment.UNBREAKING, 1).flags(ItemFlag.HIDE_ENCHANTS);
				builder.name(StringFormat.componentString("<green><bold>[已选] " + effect.getName()));
			} else {
				builder.name(StringFormat.componentString("<light_purple>" + effect.getName()));
			}

			builder.lore(
					StringFormat.componentString("<gray>" + effect.getDescription()),
					StringFormat.componentString(""),
					StringFormat.componentString("<bold>当前票数: <yellow>" + votes),
					StringFormat.componentString(""),
					StringFormat.componentString(isSelected ? "<gray>你已经投过票了" : "<green>➤ 点击投票")
			);

			pane.addItem(new GuiItem(builder.build(), event -> {
				arena.castVote(player, effect.getId());
				player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1f, 1f);

				populate(gui, player);
				gui.update();

			}), slotX, 0);
		}
	}
}