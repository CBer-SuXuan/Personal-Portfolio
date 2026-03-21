package me.suxuan.game.game.event.impl;

import me.suxuan.game.Game;
import me.suxuan.game.game.Arena;
import me.suxuan.game.game.event.LuckyEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class RainEvent extends LuckyEvent {

	private static final String TAG = "event.6_rain";

	private static final List<DisplaySpec> DISPLAY_SPECS = Arrays.asList(
			new DisplaySpec(Material.STONE_SWORD, new Vector(0, 0, 0), new Vector(0, 0, 2.5f), new Vector(1, 1, 1)),
			new DisplaySpec(Material.TRIDENT, new Vector(0.4, -1.0, -0.5), new Vector(0, 0, 1.5f), new Vector(1, 1, 1)),
			new DisplaySpec(Material.ANVIL, null, null, null),
			new DisplaySpec(Material.IRON_SPEAR, new Vector(0, 0, 0), new Vector(0, 0, -2.0f), new Vector(1, 1, 1)),
			new DisplaySpec(Material.TNT, null, null, null),
			new DisplaySpec(Material.DIAMOND_AXE, new Vector(0, 0, 0), new Vector(0, 0, 2.5f), new Vector(1, 1, 1)),
			new DisplaySpec(Material.GOLDEN_PICKAXE, new Vector(0, 0, 0), new Vector(0, 0, 2.5f), new Vector(1, 1, 1))
	);

	public RainEvent() {
		super("RAIN", "箭雨", "什么都有？(10s)", Rarity.RARE);
	}

	@Override
	public void execute(Arena arena) {
		new BukkitRunnable() {
			int remain = 10;

			@Override
			public void run() {

				for (UUID uuid : arena.getParticipants()) {
					Player p = Bukkit.getPlayer(uuid);
					if (p == null || !p.isOnline()) continue;

					World world = p.getWorld();
					world.getEntitiesByClass(Entity.class).stream()
							.filter(e -> e.getScoreboardTags().contains(TAG))
							.forEach(Entity::remove);
					break;
				}

				if (remain-- <= 0) {
					cancel();
					return;
				}

				for (UUID uuid : arena.getParticipants()) {
					Player p = Bukkit.getPlayer(uuid);
					if (p == null || !p.isOnline()) continue;

					Location base = p.getLocation().clone().add(0, 6, 0);

					DisplaySpec spec = randomSpec();
					spawnArrowWithDisplay(p.getWorld(), base, spec.itemStack(), spec.translation, spec.leftRot, spec.scale);

					for (int i = 0; i < 6; i++) {
						DisplaySpec s = randomSpec();
						spawnArrowWithDisplay(p.getWorld(), base, s.itemStack(), s.translation, s.leftRot, s.scale);
					}
				}
			}
		}.runTaskTimer(Game.getInstance(), 0L, 20L);
	}

	private static DisplaySpec randomSpec() {
		return DISPLAY_SPECS.get(ThreadLocalRandom.current().nextInt(DISPLAY_SPECS.size()));
	}

	private void spawnArrowWithDisplay(World world, Location loc, ItemStack displayItem,
									   Vector translation, Vector leftRot, Vector scale) {

		Arrow arrow = world.spawn(loc, Arrow.class, a -> {
			a.addScoreboardTag(TAG);
			a.setCritical(true);
			a.setVelocity(new Vector(0, -0.3, 0));
			a.setTicksLived(1);
			a.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
			a.setPersistent(false);
		});

		ItemDisplay display = world.spawn(loc, ItemDisplay.class, d -> {
			d.addScoreboardTag(TAG);
			d.setItemStack(displayItem);
			if (translation != null)
				d.getTransformation().getTranslation().set(translation.getX(), translation.getY(), translation.getZ());
			if (leftRot != null) d.getTransformation().getLeftRotation().set(0, 0, (float) leftRot.getZ(), 1);
			if (scale != null) d.getTransformation().getScale().set(scale.getX(), scale.getY(), scale.getZ());
		});
		arrow.addPassenger(display);

		double dx = (Math.random() * 4.0) - 2.0;
		double dz = (Math.random() * 4.0) - 2.0;
		arrow.teleport(arrow.getLocation().add(dx, 0, dz));
	}

	private record DisplaySpec(Material material, Vector translation, Vector leftRot, Vector scale) {

		private ItemStack itemStack() {
			return new ItemStack(material);
		}
	}
}
