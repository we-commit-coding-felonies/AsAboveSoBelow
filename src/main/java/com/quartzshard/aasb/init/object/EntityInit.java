package com.quartzshard.aasb.init.object;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.quartzshard.aasb.AASB;
import com.quartzshard.aasb.common.entity.buff.TransmutingBuff;
import com.quartzshard.aasb.common.entity.projectile.*;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageEffects;
import net.minecraft.world.damagesource.DamageScaling;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DeathMessageType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

// Handles initialization on entities and related (such as MobEffects & Damage Types)
public class EntityInit {
	private static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, AASB.MODID);
	private static final DeferredRegister<MobEffect> BUFFS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, AASB.MODID);
	
	public static void init(IEventBus bus) {
		ENTITIES.register(bus);
		BUFFS.register(bus);
	}

	public static final RegistryObject<EntityType<WayGrenadeEntity>>
	ENT_WAY_GRENADE = ENTITIES.register("cracked_waystone", () -> EntityType.Builder.<WayGrenadeEntity>of(WayGrenadeEntity::new, MobCategory.MISC)
			.sized(.25f, .25f)
			.clientTrackingRange(4)
			.updateInterval(10)
			.build("cracked_waystone"));
	
	public static final RegistryObject<EntityType<MustangEntity>>
	ENT_MUSTANG = ENTITIES.register("mustang", () -> EntityType.Builder.<MustangEntity>of(MustangEntity::new, MobCategory.MISC)
			.sized(0.5f, 0.5f)
			.clientTrackingRange(6)
			.updateInterval(10)
			.fireImmune()
			.noSummon().noSave() // TODO stop being lazy and save this entity to disk
			.build("mustang"));
	
	public static final RegistryObject<EntityType<SmartArrowEntity>>
	ENT_SMART_ARROW = ENTITIES.register("smart_arrow", () -> EntityType.Builder.<SmartArrowEntity>of(SmartArrowEntity::new, MobCategory.MISC)
			.sized(0.5F, 0.5F)
			.clientTrackingRange(4)
			.updateInterval(20)
			.fireImmune()
			.noSummon().noSave() // TODO stop being lazy and save this entity to disk
			.build("smart_arrow"));

	public static final RegistryObject<EntityType<SentientArrowEntity>>
	ENT_SENTIENT_ARROW = ENTITIES.register("sentient_arrow", () -> EntityType.Builder.<SentientArrowEntity>of(SentientArrowEntity::new, MobCategory.MISC)
			.sized(0.5F, 0.5F)
			.clientTrackingRange(4)
			.updateInterval(1)
			.fireImmune()
			.noSummon().noSave() // TODO stop being lazy and save this entity to disk
			.build("sentient_arrow"));
	
	public static final ResourceKey<DamageType>
		// "Direct" (always have a killer)
		DMG_TRANSMUTE = regDmg("transmute"),
		DMG_MUSTANG = regDmg("mustang"),
		DMG_AUTOSLASH = regDmg("autoslash"),
		DMG_WAYBOMB = regDmg("waybomb"),
		DMG_SENTIENT_ARROW = regDmg("sentient_arrow"),
		DMG_ARROW_SWARM = regDmg("arrow_swarm"),
		
		// "Environmental" (generally dont have a killer)
		DMG_TRANSMUTE_ENV = regDmg("transmute_env"),
		DMG_SURFACE_TENSION_ENV = regDmg("surface_tension_env");
	

	public static final RegistryObject<MobEffect>
		BUFF_TRANSMUTING = BUFFS.register("transmuting", () -> new TransmutingBuff());

	private static ResourceKey<DamageType> regDmg(String id) {
		return ResourceKey.create(Registries.DAMAGE_TYPE, AASB.rl(id));
	}

	/**
	 * Creates a damage source with a direct cause, a real culprit, and a position of the cause
	 * @param type
	 * @param level
	 * @param directCause what entity *directly* caused this damage: an Arrow
	 * @param actualCulprit what entity is the *original* cause of the damage: the Player who shot the Arrow 
	 * @param causePos where this damage is coming from in the world
	 * @return DamageSource
	 */
	public static DamageSource dmg(ResourceKey<DamageType> type, @NotNull LevelReader level, @Nullable Entity directCause, @Nullable Entity actualCulprit, @Nullable Vec3 causePos) {
		@NotNull Registry<DamageType> reg = level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE);
		return new DamageSource(reg.getHolderOrThrow(type), directCause, actualCulprit, causePos);
	}
	/**
	 * Creates a damage source with a direct cause and a real culprit
	 * @param type
	 * @param level
	 * @param directCause what entity *directly* caused this damage: an Arrow
	 * @param actualCulprit what entity is the *original* cause of the damage: the Player who shot the Arrow 
	 * @return DamageSource
	 */
	public static @NotNull DamageSource dmg(ResourceKey<DamageType> type, @NotNull LevelReader level, @Nullable Entity directCause, @Nullable Entity actualCulprit) {
		return dmg(type, level, directCause, actualCulprit, directCause != null ? directCause.position() : null);
	}
	/**
	 * Creates a damage source with an attacker
	 * @param type
	 * @param level
	 * @param directCause what entity caused this damage
	 * @return DamageSource w/ attacker
	 */
	public static DamageSource dmg(@NotNull ResourceKey<DamageType> type, @NotNull LevelReader level, @Nullable Entity directCause) {
		return dmg(type, level, directCause, directCause);
	}
	/**
	 * Simply creates a damage source
	 * @param type
	 * @param level
	 * @return DamageSource
	 */
	public static @NotNull DamageSource dmg(ResourceKey<DamageType> type, @NotNull LevelReader level) {
		return dmg(type, level, null);
	}
	
	// I AM AT MY FUCKING LIMIT WHY IS IT TYPO
	public static void bootstrapDmg(@NotNull BootstapContext<DamageType> ctx) {
		// "Direct" (always have a killer)
		new DmgBuilder(DMG_TRANSMUTE).exhaust(5).reg(ctx);
		new DmgBuilder(DMG_MUSTANG).exhaust(1).reg(ctx);
		new DmgBuilder(DMG_AUTOSLASH).exhaust(0.5).reg(ctx);
		new DmgBuilder(DMG_WAYBOMB).reg(ctx);
		new DmgBuilder(DMG_SENTIENT_ARROW).exhaust(2).reg(ctx);
		new DmgBuilder(DMG_ARROW_SWARM).exhaust(0.1).reg(ctx);
		
		// "Environmental" (generally dont have a killer)
		new DmgBuilder(DMG_TRANSMUTE_ENV).exhaust(5).scales(false).reg(ctx);
		new DmgBuilder(DMG_SURFACE_TENSION_ENV).scales(false).reg(ctx);
	}
	
	/**
	 * based on vvv
	 * https://github.com/Creators-of-Create/Create/blob/9a70cfff41bd5e0f3eb0bbd397ac3e53038b5ff6/src/main/java/com/simibubi/create/foundation/damageTypes/DamageTypeBuilder.java
	 */
	protected static class DmgBuilder {
		private final ResourceKey<DamageType> key;
		
		private DmgBuilder(ResourceKey<DamageType> key) {
			this.key = key;
		}
		
		@Nullable private String lang;
		private boolean scales = true;
		private float exhaust = 0;
		@Nullable private DamageEffects fx;
		@Nullable private DeathMessageType msg;
		
		protected @NotNull DmgBuilder lang(String lang) {
			this.lang = lang;
			return this;
		}
		protected @NotNull DmgBuilder scales(boolean scales) {
			this.scales = scales;
			return this;
		}
		protected DmgBuilder exhaust(float exhaust) {
			this.exhaust = exhaust;
			return this;
		}
		protected DmgBuilder exhaust(double exhaust) {
			exhaust((float) exhaust);
			return this;
		}
		protected @NotNull DmgBuilder fx(DamageEffects fx) {
			this.fx = fx;
			return this;
		}
		protected DmgBuilder msg(DeathMessageType msg) {
			this.msg = msg;
			return this;
		}
		
		private @NotNull DamageType build() {
			if (lang == null) {
				lang(key.location().getNamespace()+"."+key.location().getPath());
			}
			if (fx == null) {
				fx(DamageEffects.HURT);
			}
			if (msg == null) {
				msg(DeathMessageType.DEFAULT);
			}
			
			return new DamageType(lang, scales ? DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER : DamageScaling.NEVER, exhaust, fx, msg);
		}
		
		protected DamageType reg(BootstapContext<DamageType> ctx) {
			@NotNull DamageType type = build();
			ctx.register(key, type);
			return type;
		}
	}
}
