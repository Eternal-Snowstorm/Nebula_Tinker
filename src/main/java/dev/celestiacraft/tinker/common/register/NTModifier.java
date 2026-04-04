package dev.celestiacraft.tinker.common.register;

import dev.celestiacraft.tinker.common.modifier.*;
import net.minecraftforge.eventbus.api.IEventBus;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.util.DynamicModifier;
import slimeknights.tconstruct.library.modifiers.util.ModifierDeferredRegister;
import slimeknights.tconstruct.library.modifiers.util.StaticModifier;
import dev.celestiacraft.tinker.NebulaTinker;

import java.util.function.Supplier;

/**
 * 由于暂时没空写Json, 因此在这里写一下一些注意事项, 到时候写Json时避免忘记
 * 每个强化的暴击效果不可叠加, 例如施虐者和迅捷之刃不能叠加
 */
public class NTModifier {
	public static final ModifierDeferredRegister MODIFIERS;
	public static final StaticModifier<Abuser> ABUSER;
	public static final StaticModifier<Acupoint> ACUPOINT;
	public static final StaticModifier<CaptureKing> CAPTURE_KING;
	public static final StaticModifier<CausalTruncation> CAUSAL_TRUNCATION;
	public static final StaticModifier<Modifier> CONVERGE;
	public static final StaticModifier<DeathEcho> DEATH_ECHO;
	public static final StaticModifier<Demonization> DEMONIZATION;
	public static final StaticModifier<Divinization> DIVINIZATION;
	public static final StaticModifier<Clockwork> CLOCKWORK;
	public static final StaticModifier<KillingRhythm> KILLING_RHYTHM;
	public static final StaticModifier<ForceLiberation> FORCE_LIBERATION;
	public static final StaticModifier<Frenzy> FRENZY;

	public static final DynamicModifier DIVINE_DEMONIC_HARMONY;

	static {
		MODIFIERS = ModifierDeferredRegister.create(NebulaTinker.MODID);

		// 动态Modifiers
		ABUSER = register("abuser", Abuser::new);
		ACUPOINT = register("acupoint", Acupoint::new);
		CAPTURE_KING = register("capture_king", CaptureKing::new);
		CAUSAL_TRUNCATION = register("causal_truncation", CausalTruncation::new);
		CONVERGE = register("converge", Modifier::new);
		DEATH_ECHO = register("death_echo", DeathEcho::new);
		DEMONIZATION = register("demonization", Demonization::new);
		DIVINIZATION = register("divinization", Divinization::new);
		FRENZY = register("frenzy", Frenzy::new);
		CLOCKWORK = register("clockwork", Clockwork::new);
		KILLING_RHYTHM = register("killing_rhythm", KillingRhythm::new);
		FORCE_LIBERATION = register("force_liberation", ForceLiberation::new);

		// 静态Modifiers
		DIVINE_DEMONIC_HARMONY = register("divine_demonic_harmony");
	}

	public static void register(IEventBus bus) {
		NebulaTinker.LOGGER.info("Nebula Tinker TCon Modifiers Registered!");
		MODIFIERS.register(bus);
	}

	private static <T extends Modifier> StaticModifier<T> register(String name, Supplier<? extends T> supplier) {
		return MODIFIERS.register(name, supplier);
	}

	private static DynamicModifier register(String name) {
		return MODIFIERS.registerDynamic(name);
	}
}