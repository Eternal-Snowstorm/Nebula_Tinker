//package top.nebula.nebula_tinker.entity;
//
//import net.minecraft.world.entity.EntityType;
//import net.minecraft.world.entity.LivingEntity;
//import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
//import net.minecraftforge.eventbus.api.SubscribeEvent;
//import net.minecraftforge.fml.common.Mod;
//import top.nebula.nebula_tinker.NebulaTinker;
//import top.nebula.nebula_tinker.config.CommonConfig;
//import top.nebula.nebula_tinker.common.register.attribute.GlobalCritAttributes;
//
//@Mod.EventBusSubscriber(modid = NebulaTinker.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
//public class EntityAttributeHandler {
//	@SubscribeEvent
//	public static void onEntityAttributeModification(EntityAttributeModificationEvent event) {
//		// 为所有生物添加全局暴击属性
//		for (EntityType<? extends LivingEntity> entityType : event.getTypes()) {
//			// 添加攻击方属性
//			if (!event.has(entityType, GlobalCritAttributes.GLOBAL_CRITICAL_CHANCE.get())) {
//				event.add(entityType, GlobalCritAttributes.GLOBAL_CRITICAL_CHANCE.get(),
//						CommonConfig.BASE_CRIT_CHANCE.get());
//			}
//
//			if (!event.has(entityType, GlobalCritAttributes.GLOBAL_CRITICAL_DAMAGE.get())) {
//				event.add(entityType, GlobalCritAttributes.GLOBAL_CRITICAL_DAMAGE.get(),
//						CommonConfig.BASE_CRIT_DAMAGE.get());
//			}
//
//			// 添加防御方属性
//			if (!event.has(entityType, GlobalCritAttributes.CRITICAL_RESISTANCE.get())) {
//				event.add(entityType, GlobalCritAttributes.CRITICAL_RESISTANCE.get(),
//						CommonConfig.BASE_CRIT_RESISTANCE.get());
//			}
//
//			if (!event.has(entityType, GlobalCritAttributes.CRITICAL_DAMAGE_REDUCTION.get())) {
//				event.add(entityType, GlobalCritAttributes.CRITICAL_DAMAGE_REDUCTION.get(),
//						CommonConfig.BASE_CRIT_DAMAGE_REDUCTION.get());
//			}
//		}
//
//		NebulaTinker.LOGGER.info("Global crit attributes added to all entities!");
//	}
//}