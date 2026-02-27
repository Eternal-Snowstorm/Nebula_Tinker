package dev.celestiacraft.tinker.compat.kubejs;

import dev.celestiacraft.tinker.api.attribute.*;
import dev.celestiacraft.tinker.entity.EAttributeType;
import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.script.BindingsEvent;

import java.util.List;

public class ModKubeJSPlugin extends KubeJSPlugin {
	public void registerBindings(BindingsEvent event) {
		super.registerBindings(event);

		List<Class<?>> simpleClassList = List.of(
				AttributeApplicator.class,
				AttributeHelper.class,
				AttributeLock.class,
				AttributeLockManager.class,
				AttributeOverrideHelper.class,
				EAttributeType.class
		);
		simpleClassList.forEach((cls) -> {
			event.add(cls.getSimpleName(), cls);
		});
	}
}