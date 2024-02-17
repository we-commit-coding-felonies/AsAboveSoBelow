package com.quartzshard.aasb.data.tags;

import java.util.concurrent.CompletableFuture;

import org.jetbrains.annotations.Nullable;

import com.quartzshard.aasb.AASB;
import com.quartzshard.aasb.init.object.EntityInit;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraftforge.common.data.ExistingFileHelper;

public class DmgTP extends TagsProvider<DamageType> {

	public DmgTP(PackOutput out, CompletableFuture<Provider> lp, @Nullable ExistingFileHelper help) {
		super(out, Registries.DAMAGE_TYPE, lp, AASB.MODID, help);
	}
	
	public static final TagKey<DamageType>
		FORCEFIELD_EZBLOCK = c("forcefield_blocks_for_free"),
		
		BYPASSES_DMG_SPONGE = c("bypasses_dmg_sponge"), // bypasses hermetic armor DR
		BYPASSES_FORCEFIELD = c("bypasses_forcefield"), // bypasses the alchemical barrier
		
		BYPASSES_PHYSICAL = c("bypasses_physical"), // bypass all physical defenses (armor, shield, etc)
		BYPASSES_MAGICAL = c("bypasses_magical"), // bypass "lesser" magical defenses (ench, resistance pot, etc)
		BYPASSES_ALCHEMICAL = c("bypasses_alchemical"), // bypass "greater" alchemical defenses (hermetic, barrier, etc)
		UNSTOPPABLE_FORCE = c("unstoppable_force"), // bypass everything short of stuff like creative mode & iframes
		
		IS_ALCHEMY = c("is_alchemy"), // increase effectiveness vs alchemical defenses
		IS_STRONG_FIRE = c("is_strong_fire") // things that are fiery, but go beyond ordinary "fire damage"
		;

	@Override
	protected void addTags(Provider pProvider) {
		tag(FORCEFIELD_EZBLOCK)
			.add(DamageTypes.CACTUS)
			.add(DamageTypes.HOT_FLOOR)
			.add(DamageTypes.ON_FIRE)
			.add(DamageTypes.SWEET_BERRY_BUSH)
			.add(DamageTypes.STING);
		
		// BYPASS SECTION
		tag(UNSTOPPABLE_FORCE)
			.add(EntityInit.DMG_TRANSMUTE)
			.add(EntityInit.DMG_TRANSMUTE_ENV);

		tag(BYPASSES_ALCHEMICAL)
			.addTag(UNSTOPPABLE_FORCE)
			.add(DamageTypes.DROWN)
			.add(DamageTypes.DRY_OUT)
			.add(DamageTypes.FREEZE)
			.add(DamageTypes.IN_WALL)
			.add(DamageTypes.STARVE)
			.add(DamageTypes.WITHER);
		tag(BYPASSES_FORCEFIELD)
			.addTag(BYPASSES_ALCHEMICAL);
		tag(BYPASSES_DMG_SPONGE)
			.addTag(BYPASSES_ALCHEMICAL)
			.add(EntityInit.DMG_MUSTANG)
			.add(EntityInit.DMG_SENTIENT_ARROW)
			.add(DamageTypes.ON_FIRE)
			.add(DamageTypes.SONIC_BOOM);
		
		tag(BYPASSES_MAGICAL)
			.addTag(UNSTOPPABLE_FORCE)
			.add(EntityInit.DMG_MUSTANG)
			.add(EntityInit.DMG_SENTIENT_ARROW);
		
		tag(BYPASSES_PHYSICAL)
			.addTag(UNSTOPPABLE_FORCE)
			.add(EntityInit.DMG_WAYBOMB)
			.add(EntityInit.DMG_SENTIENT_ARROW)
			.add(EntityInit.DMG_AUTOSLASH);
		
		tag(DamageTypeTags.BYPASSES_ARMOR)
			.addTag(BYPASSES_PHYSICAL);
		tag(DamageTypeTags.BYPASSES_SHIELD)
			.addTag(BYPASSES_PHYSICAL);
		
		// vanilla iframe ignore hell yeah!!!!!!!
		tag(DamageTypeTags.BYPASSES_COOLDOWN)
			.add(EntityInit.DMG_AUTOSLASH)
			.add(EntityInit.DMG_TRANSMUTE)
			.add(EntityInit.DMG_TRANSMUTE_ENV)
			.add(EntityInit.DMG_SENTIENT_ARROW)
			.add(EntityInit.DMG_ARROW_SWARM);
		

		// IS SECTION
		tag(IS_ALCHEMY)
			.add(EntityInit.DMG_AUTOSLASH)
			.add(EntityInit.DMG_MUSTANG)
			.add(EntityInit.DMG_SENTIENT_ARROW)
			.add(EntityInit.DMG_TRANSMUTE)
			.add(EntityInit.DMG_TRANSMUTE_ENV)
			.add(EntityInit.DMG_WAYBOMB);
		tag(IS_STRONG_FIRE)
			.add(EntityInit.DMG_MUSTANG);

		tag(DamageTypeTags.IS_EXPLOSION)
			.add(EntityInit.DMG_MUSTANG)
			.add(EntityInit.DMG_WAYBOMB);
		tag(DamageTypeTags.IS_FALL)
			.add(EntityInit.DMG_SURFACE_TENSION_ENV);
		//tag(DamageTypeTags.IS_FIRE)
		//	.addTag(IS_STRONG_FIRE);
		tag(DamageTypeTags.IS_PROJECTILE)
			.add(EntityInit.DMG_ARROW_SWARM);
			//.add(EntityInit.DMG_SENTIENT_ARROW);
		
		// OTHER SECTION
		tag(DamageTypeTags.AVOIDS_GUARDIAN_THORNS)
			.add(EntityInit.DMG_AUTOSLASH)
			.add(EntityInit.DMG_MUSTANG)
			.add(EntityInit.DMG_SENTIENT_ARROW)
			.add(EntityInit.DMG_TRANSMUTE)
			.add(EntityInit.DMG_TRANSMUTE_ENV)
			.add(EntityInit.DMG_WAYBOMB);
		tag(DamageTypeTags.BURNS_ARMOR_STANDS)
			.add(EntityInit.DMG_MUSTANG);
		tag(DamageTypeTags.IGNITES_ARMOR_STANDS)
			.add(EntityInit.DMG_MUSTANG);
		tag(DamageTypeTags.WITCH_RESISTANT_TO)
			.addTag(IS_ALCHEMY)
			.remove(EntityInit.DMG_MUSTANG);
	}
	
	private static TagKey<DamageType> c(String name) {
		return TagKey.create(Registries.DAMAGE_TYPE, AASB.rl(name));
	}
	@Override
	public String getName() {
		return AASB.MODID.toUpperCase() + " | Damage Type Tags";
	}

}
