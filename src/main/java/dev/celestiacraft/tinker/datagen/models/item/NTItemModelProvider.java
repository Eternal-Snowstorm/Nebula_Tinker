package dev.celestiacraft.tinker.datagen.models.item;

import dev.celestiacraft.tinker.NebulaTinker;
import dev.celestiacraft.tinker.common.register.NTItem;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;

public class NTItemModelProvider extends ItemModelProvider {
	public NTItemModelProvider(PackOutput output, ExistingFileHelper helper) {
		super(output, NebulaTinker.MODID, helper);
	}

	@Override
	protected void registerModels() {
		NTItem.CREATIVE_TAB_ITEMS.forEach((supplier) -> {
			Item item = supplier.get();
			String name = ForgeRegistries.ITEMS.getKey(item).getPath();

			basicItem(name);
		});

		basicItem("demonization_stone");
		basicItem("divinization_stone");
		basicItem("spider_fang");
	}

	private void basicItem(String name, String path) {
		withExistingParent(name, mcLoc("item/generated"))
				.texture("layer0", modLoc(path));
	}

	private void basicItem(String name) {
		basicItem(name, "item/" + name);
	}

	private void handheld(String name, String path) {
		withExistingParent(name, mcLoc("item/handheld"))
				.texture("layer0", modLoc(path));
	}

	private void handheld(String name) {
		handheld(name, "item/" + name);
	}

	private void existing(String name, String path) {
		withExistingParent(name, modLoc(path));
	}

	private void existing(String name) {
		existing(name, "item/" + name);
	}
}