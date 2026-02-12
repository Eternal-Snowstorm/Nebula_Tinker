package top.nebula.tinker.common.register;

import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import top.nebula.tinker.NebulaTinker;
import top.nebula.tinker.common.item.DemonizationStoneItem;
import top.nebula.tinker.common.item.DivinizationStoneItem;
import top.nebula.tinker.common.item.InvincibleFrameEgregatorItem;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ModItem {
	public static final DeferredRegister<Item> ITEMS;

	public static final List<Supplier<Item>> CREATIVE_TAB_ITEMS = new ArrayList<>();

	public static final Supplier<Item> DEMONIZATION_STONE;
	public static final Supplier<Item> DIVINIZATION_STONE;
	public static final Supplier<Item> INVINCIBLE_FRAME_EGREGATOR;

	static {
		ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, NebulaTinker.MODID);

		DEMONIZATION_STONE = addItem("demonization_stone", DemonizationStoneItem::new);
		DIVINIZATION_STONE = addItem("divinization_stone", DivinizationStoneItem::new);
		INVINCIBLE_FRAME_EGREGATOR = addItem("invincible_frame_egregator", InvincibleFrameEgregatorItem::new, false);
	}

	private static Supplier<Item> addItem(String id, Supplier<Item> supplier, boolean addToCreativeTab) {
		Supplier<Item> item = ITEMS.register(id, supplier);
		if (addToCreativeTab) {
			CREATIVE_TAB_ITEMS.add(item);
		}
		return item;
	}

	private static Supplier<Item> addItem(String id, Supplier<Item> supplier) {
		return addItem(id, supplier, true);
	}

	public static void register(IEventBus bus) {
		NebulaTinker.LOGGER.info("Nebula Tinker Items Registered!");
		ITEMS.register(bus);
	}
}