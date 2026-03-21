package me.suxuan.game.game.effect;

import me.suxuan.game.game.effect.impl.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

public class EffectManager {

	private final List<Supplier<GlobalEffect>> factories = new ArrayList<>();

	public EffectManager() {
		registerAll();
	}

	private void registerAll() {
		factories.add(TitanMode::new);
		factories.add(GlassCannon::new);
		factories.add(MoonGravity::new);
		factories.add(ExplosivePunch::new);
		factories.add(Speedster::new);
		factories.add(ThornsArmor::new);
		factories.add(Darkness::new);
		factories.add(EnderCurse::new);
		factories.add(SoulLink::new);
		factories.add(DwarfMode::new);
	}

	/**
	 * 随机创建一个全新的效果实例
	 *
	 * @return GlobalEffect 实例，如果列表为空返回 null
	 */
	public GlobalEffect createRandomEffect() {
		if (factories.isEmpty()) return null;

		Supplier<GlobalEffect> factory = factories.get(ThreadLocalRandom.current().nextInt(factories.size()));

		return factory.get();
	}

	/**
	 * 根据 ID 获取特定效果 (用于 Config 指定或调试)
	 *
	 * @param id 效果ID (如 "TITAN")
	 */
	public GlobalEffect createEffectById(String id) {
		for (Supplier<GlobalEffect> factory : factories) {
			GlobalEffect temp = factory.get();
			if (temp.getId().equalsIgnoreCase(id)) {
				return temp;
			}
		}
		return null;
	}

	public String getDisplayById(String id) {

		for (Supplier<GlobalEffect> factory : factories) {
			GlobalEffect temp = factory.get();
			if (temp.getId().equalsIgnoreCase(id)) {
				return temp.getName();
			}
		}
		return "未知效果";
	}

	public List<GlobalEffect> createUniqueRandomEffects(int count) {
		if (factories.isEmpty()) return new ArrayList<>();

		// 1. 创建一个索引列表 [0, 1, 2, ..., size-1]
		List<Integer> indices = new ArrayList<>();
		for (int i = 0; i < factories.size(); i++) {
			indices.add(i);
		}

		// 2. 打乱索引 (洗牌算法)
		Collections.shuffle(indices);

		// 3. 取前 count 个索引，并生成实例
		List<GlobalEffect> result = new ArrayList<>();
		int limit = Math.min(count, indices.size());

		for (int i = 0; i < limit; i++) {
			int index = indices.get(i);
			result.add(factories.get(index).get()); // 调用工厂生成新实例
		}

		return result;
	}
}