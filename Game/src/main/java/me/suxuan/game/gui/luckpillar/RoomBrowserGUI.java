package me.suxuan.game.gui.luckpillar;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import me.suxuan.game.Game;
import me.suxuan.game.dialog.luckpillar.Dialog;
import me.suxuan.game.game.Arena;
import me.suxuan.game.game.ArenaState;
import me.suxuan.game.util.ItemBuilder;
import me.suxuan.game.util.StringFormat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class RoomBrowserGUI {

	private final Game plugin;
	private final MiniMessage mm = MiniMessage.miniMessage();

	private BukkitTask refreshTask;

	public RoomBrowserGUI(Game plugin) {
		this.plugin = plugin;
	}

	public void open(Player player) {
		ChestGui gui = new ChestGui(6, "房间列表");
		gui.setOnGlobalClick(e -> e.setCancelled(true));

		PaginatedPane pages = new PaginatedPane(0, 0, 9, 5);
		gui.addPane(pages);

		StaticPane navBar = new StaticPane(0, 5, 9, 1);
		gui.addPane(navBar);

		setupNavBar(gui, pages, navBar, player);

		updateContent(gui, pages, player);

		gui.show(player);

		this.refreshTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
			if (gui.getViewers().isEmpty()) {
				if (refreshTask != null && !refreshTask.isCancelled()) {
					refreshTask.cancel();
				}
				return;
			}

			updateContent(gui, pages, player);

			gui.update();

		}, 20L, 20L);

		gui.setOnClose(event -> {
			if (refreshTask != null && !refreshTask.isCancelled()) {
				refreshTask.cancel();
				refreshTask = null;
			}
		});
	}

	private void updateContent(ChestGui gui, PaginatedPane pages, Player player) {
		int currentPage = pages.getPage();

		pages.clear();

		List<GuiItem> items = new ArrayList<>();
		List<Arena> activeArenas = plugin.getGameManager().getActiveArenas();

		activeArenas.sort(Comparator.comparingInt(a -> a.getState().ordinal()));

		for (Arena arena : activeArenas) {
			if (arena.getState() == ArenaState.ENDING || arena.getState() == ArenaState.RESETTING) {
				continue;
			}
			items.add(createArenaIcon(player, arena));
		}

		if (items.isEmpty()) {
			items.add(new GuiItem(ItemBuilder.of(Material.BARRIER)
					.name(StringFormat.componentString("<red>暂无房间"))
					.lore(StringFormat.componentString("<gray>还没有人创建房间"),
							StringFormat.componentString("<yellow>点击下方绿宝石创建！"))
					.build()));
		}

		// 4. 填充新数据
		pages.populateWithGuiItems(items);

		// 5. 恢复页码
		if (currentPage < pages.getPages()) {
			pages.setPage(currentPage);
		} else if (pages.getPages() > 0) {
			pages.setPage(pages.getPages() - 1);
		} else {
			pages.setPage(0);
		}
	}

	private void setupNavBar(ChestGui gui, PaginatedPane pages, StaticPane navBar, Player player) {
		navBar.fillWith(ItemBuilder.of(Material.GRAY_STAINED_GLASS_PANE).name(StringFormat.componentString(" ")).build());

		navBar.addItem(new GuiItem(ItemBuilder.of(Material.ARROW).name(StringFormat.componentString("<yellow>上一页")).build(), event -> {
			if (pages.getPage() > 0) {
				pages.setPage(pages.getPage() - 1);
				gui.update();
			}
		}), 0, 0);

		navBar.addItem(new GuiItem(ItemBuilder.of(Material.EMERALD).name(StringFormat.componentString("<green><bold>创建新房间")).build(), event -> {
			player.closeInventory();
			Dialog.openCreateRoomDialog(player);
		}), 3, 0);

		navBar.addItem(new GuiItem(ItemBuilder.of(Material.COMPASS).name(StringFormat.componentString("<aqua>手动刷新")).build(), event -> {
			updateContent(gui, pages, player);
			gui.update();
			player.sendRichMessage("<green>列表已刷新");
		}), 5, 0);

		navBar.addItem(new GuiItem(ItemBuilder.of(Material.ARROW).name(StringFormat.componentString("<yellow>下一页")).build(), event -> {
			if (pages.getPage() < pages.getPages() - 1) {
				pages.setPage(pages.getPage() + 1);
				gui.update();
			}
		}), 8, 0);
	}

	private GuiItem createArenaIcon(Player player, Arena arena) {
		boolean isPrivate = arena.isPrivate();
		boolean isFull = arena.getPlayers().size() >= arena.getConfig().maxPlayers();
		boolean inGame = arena.getState() == ArenaState.IN_GAME || arena.getState() == ArenaState.STARTING;

		Material mat = arena.getConfig().pillarMaterial();
		if (isPrivate) mat = Material.IRON_DOOR;
		if (inGame) mat = Material.REDSTONE_BLOCK;

		ItemBuilder builder = ItemBuilder.of(mat);
		String prefix = isPrivate ? "<red>[私密] " : "<green>[公开] ";
		builder.name(StringFormat.componentString(prefix + "<white>" + arena.getRoomName()));

		List<Component> lore = new ArrayList<>();
		lore.add(StringFormat.componentString("<gray>地图: <yellow>" + arena.getConfig().displayName()));
		lore.add(StringFormat.componentString("<gray>房主: <white>" + org.bukkit.Bukkit.getOfflinePlayer(arena.getHostUuid()).getName()));
		lore.add(StringFormat.componentString("<gray>人数: <aqua>" + arena.getPlayers().size() + "/" + arena.getConfig().maxPlayers()));
		lore.add(StringFormat.componentString("<gray>状态: " + getStateString(arena.getState())));
		lore.add(StringFormat.componentString(""));

		if (inGame) lore.add(StringFormat.componentString("<red>游戏进行中"));
		else if (isFull) lore.add(StringFormat.componentString("<red>房间已满"));
		else if (isPrivate) lore.add(StringFormat.componentString("<yellow>点击输入密码"));
		else lore.add(StringFormat.componentString("<green>点击加入"));

		builder.lore(lore);

		if (arena.getState() == ArenaState.WAITING && !isFull) {
			builder.enchant(Enchantment.UNBREAKING, 1).flags(ItemFlag.HIDE_ENCHANTS);
		}

		return new GuiItem(builder.build(), event -> {
			if (inGame || isFull) {
				player.sendRichMessage("<red>无法加入该房间！");
				return;
			}
			if (isPrivate) {
				Dialog.openInputPasswordDialog(player, arena);
			} else {
				player.closeInventory();
				plugin.getGameManager().joinArena(player, arena);
			}
		});
	}

	private String getStateString(ArenaState state) {
		return switch (state) {
			case WAITING -> "<green>等待中";
			case STARTING -> "<yellow>即将开始";
			case IN_GAME -> "<red>游戏中";
			default -> "<gray>结束";
		};
	}
}
