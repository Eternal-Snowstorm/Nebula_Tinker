package dev.celestiacraft.tinker.common.register;

import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import dev.celestiacraft.tinker.NebulaTinker;
import dev.celestiacraft.tinker.common.item.DemonizationStoneItem;
import dev.celestiacraft.tinker.common.item.DivinizationStoneItem;
import dev.celestiacraft.tinker.common.item.InvincibleFrameEgregatorItem;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class NTItem {
	public static final DeferredRegister<Item> ITEMS;

	public static final List<Supplier<? extends Item>> CREATIVE_TAB_ITEMS = new ArrayList<>();

	public static final Supplier<Item> INVINCIBLE_FRAME_EGREGATOR;
	public static final Supplier<Item> DEMONIZATION_STONE;
	public static final Supplier<Item> DIVINIZATION_STONE;
	public static final Supplier<Item> SPIDER_FANG; // 暂无获取方式

	static {
		ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, NebulaTinker.MODID);

		DEMONIZATION_STONE = addItem("demonization_stone", DemonizationStoneItem::new);
		DIVINIZATION_STONE = addItem("divinization_stone", DivinizationStoneItem::new);
		INVINCIBLE_FRAME_EGREGATOR = addItem("invincible_frame_egregator", InvincibleFrameEgregatorItem::new, false);
		SPIDER_FANG = addItem("spider_fang", Item::new);
	}

	private static <T extends Item> Supplier<T> addItem(
			String id,
			Function<Item.Properties, T> factory,
			Consumer<Item.Properties> properties,
			boolean addToCreativeTab
	) {
		Supplier<T> item = ITEMS.register(id, () -> {
			Item.Properties props = new Item.Properties();

			if (properties != null) {
				properties.accept(props);
			}

			return factory.apply(props);
		});

		if (addToCreativeTab) {
			CREATIVE_TAB_ITEMS.add(item);
		}

		return item;
	}

	private static Supplier<Item> addItem(String id) {
		return addItem(id, Item::new, null, true);
	}

	private static <T extends Item> Supplier<T> addItem(String id, Function<Item.Properties, T> factory) {
		return addItem(id, factory, null, true);
	}

	private static <T extends Item> Supplier<T> addItem(String id, Function<Item.Properties, T> factory, boolean addToCreativeTab) {
		return addItem(id, factory, null, addToCreativeTab);
	}

	public static void register(IEventBus bus) {
		NebulaTinker.LOGGER.info("Nebula Tinker Items Registered!");
		ITEMS.register(bus);
	}
}