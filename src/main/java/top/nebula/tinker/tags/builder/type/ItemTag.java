package top.nebula.tinker.tags.builder.type;

import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import top.nebula.tinker.tags.builder.AbstractTagBuilder;

public class ItemTag extends AbstractTagBuilder<Item> {
	public ItemTag(String name) {
		super(name);
	}

	@Override
	public TagKey<Item> build() {
		return ItemTags.create(id());
	}
}