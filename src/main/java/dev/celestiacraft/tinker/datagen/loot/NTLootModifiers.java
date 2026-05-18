package dev.celestiacraft.tinker.datagen.loot;

import com.mojang.serialization.Codec;
import dev.celestiacraft.tinker.NebulaTinker;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class NTLootModifiers {
	public static final DeferredRegister<Codec<? extends IGlobalLootModifier>> REGISTRY;

	public static final Supplier<Codec<AddItemModifier>> ADD_ITEM;

	static {
		REGISTRY = DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, NebulaTinker.MODID);

		ADD_ITEM = REGISTRY.register("add_item", () -> {
			return AddItemModifier.CODEC;
		});
	}
}