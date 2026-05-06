package dev.celestiacraft.tinker.datagen.loot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;

public class AddItemModifier extends LootModifier {
	public static final Codec<AddItemModifier> CODEC = RecordCodecBuilder.create((instance) -> {
		return codecStart(instance)
				.and(ItemStack.CODEC.fieldOf("item").forGetter((modifier) -> {
					return modifier.stack;
				}))
				.apply(instance, AddItemModifier::new);
	});

	private final ItemStack stack;

	protected AddItemModifier(LootItemCondition[] conditions, ItemStack stack) {
		super(conditions);
		this.stack = stack;
	}

	@Override
	protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
		generatedLoot.add(stack.copy());
		return generatedLoot;
	}

	@Override
	public Codec<? extends IGlobalLootModifier> codec() {
		return CODEC;
	}
}