package dev.celestiacraft.tinker.datagen.loot;

import dev.celestiacraft.tinker.NebulaTinker;
import dev.celestiacraft.tinker.common.register.NTItem;
import dev.celestiacraft.tinker.tags.ModEntityTags;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceWithLootingCondition;
import net.minecraftforge.common.data.GlobalLootModifierProvider;

public class NTLootModifierProvider extends GlobalLootModifierProvider {
	public NTLootModifierProvider(PackOutput output) {
		super(output, NebulaTinker.MODID);
	}

	@Override
	protected void start() {
		add("spider_fang", new AddItemModifier(new LootItemCondition[] {
				LootItemEntityPropertyCondition.hasProperties(
						LootContext.EntityTarget.THIS,
						EntityPredicate.Builder
								.entity()
								.of(ModEntityTags.SPIDERS)
				).build(),
				LootItemRandomChanceWithLootingCondition.randomChanceAndLootingBoost(0.05f, 0.02f).build()
		}, NTItem.SPIDER_FANG.asItem().getDefaultInstance()));
	}
}