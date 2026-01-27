package top.nebula.tinker.common.register;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import top.nebula.tinker.NebulaTinker;

import java.util.function.Supplier;

public class ModCreativeTab {

	public static final DeferredRegister<CreativeModeTab> TABS =
			DeferredRegister.create(Registries.CREATIVE_MODE_TAB, NebulaTinker.MODID);

	public static final Supplier<CreativeModeTab> NEBULA_TINKER_TAB =
			TABS.register("nebula_tinker_tab", () -> {
				return CreativeModeTab.builder()
						.title(Component.translatable("itemGroup.nebula_tinker.tab"))
						.icon(() -> new ItemStack(ModItem.DEMONIZATION_STONE.get()))
						.displayItems((parameters, output) -> {
							ModItem.CREATIVE_TAB_ITEMS.forEach((item) -> {
								output.accept(item.get());
							});
						})
						.build();
			});

	public static void register(IEventBus bus) {
		NebulaTinker.LOGGER.info("Nebula Tinker Creative Item Tab Registered!");
		TABS.register(bus);
	}
}