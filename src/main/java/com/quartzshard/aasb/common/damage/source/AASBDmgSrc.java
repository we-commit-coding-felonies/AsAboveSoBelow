package com.quartzshard.aasb.common.damage.source;

import net.minecraft.world.entity.Entity;

/**
 * contains a bunch of stuff for custom damage sources
 * @author solunareclipse1
 */
public class AASBDmgSrc {

	/** alchemical magic status effect damage, ignores almost all forms of protection, doesnt aggro */
	public static final AASBSimpleDamageSource TRANSMUTATION_POTION = (AASBSimpleDamageSource) new AASBSimpleDamageSource("transmutation2.indirect")
			.setAlchemy()
			.bypassNotInvul()
			.setNoAggro().setMagic();
	
	/** divine, ignores everything, doesnt aggro */
	public static final AASBSimpleDamageSource GOD = (AASBSimpleDamageSource) new AASBSimpleDamageSource("god")
			.setDivine()
			.bypassEverything()
			.setNoAggro();
	
	/** alchemical magic, bypasses armor, enchants & DR */
	public static AASBEntityDamageSource transmutation(Entity culprit) {
		return (AASBEntityDamageSource) new AASBEntityDamageSource("transmutation", culprit)
			.setAlchemy()
			.bypassDr().bypassMagic().bypassArmor()
			.setMagic();
	}

	/** alchemical magic, ignores almost all forms of protection */
	public static AASBEntityDamageSource strongTransmutation(Entity culprit) {
		return (AASBEntityDamageSource) new AASBEntityDamageSource("transmutation2", culprit)
			.setAlchemy()
			.bypassNotInvul()
			.setMagic();
	}
	
	/** alchemical magic explosion, ignores non-physical protection */
	public static AASBEntityDamageSource waybomb(Entity culprit) {
		return (AASBEntityDamageSource) new AASBEntityDamageSource("waybomb", culprit)
			.setAlchemy()
			.bypassDr().bypassMagic()
			.setMagic().setExplosion();
	}
	
	/** alchemical magic explosion, ignores non-physical protection, doesnt aggro */
	public static AASBEntityDamageSource waybombAccident(Entity culprit) {
		return (AASBEntityDamageSource) new AASBEntityDamageSource("waybomb.accident", culprit)
			.setAlchemy()
			.bypassDr().bypassMagic()
			.setNoAggro().setMagic().setExplosion();
	}
	
	/** alchemical plasma magic explosion projectile, ignores armor */
	public static AASBEntityDamageSource mustang(Entity culprit) {
		return (AASBEntityDamageSource) new AASBEntityDamageSource("mustang", culprit)
			.setAlchemy().setPlasma()
			.bypassArmor()
			.setMagic().setExplosion().setProjectile();
	}
	
	/** alchemical magic, ignores armor */
	public static AASBEntityDamageSource autoslash(Entity culprit) {
		return (AASBEntityDamageSource) new AASBEntityDamageSource("autoslash", culprit)
			.setAlchemy()
			.bypassArmor()
			.setMagic();
	}
	
	/**
	 * contains shared functions for custom damage sources <br>
	 * you should test for instanceof this when checking for any magitekkit damage source types
	 * @author solunareclipse1
	 */
	public interface ICustomDamageSource {
		public boolean isBypassAlchShield();
		public <T extends ICustomDamageSource> T bypassAlchShield();
		
		/** source should bypass any 'damage reduction', such as matter armors */
		public boolean isBypassDr();
		public <T extends ICustomDamageSource> T bypassDr();
		
		/** bypasses everything that isnt just invincible */
		public <T extends ICustomDamageSource> T bypassNotInvul();
		
		/** immovable object? never heard of it */
		public <T extends ICustomDamageSource> T bypassEverything();
		
		/** source should behave like fire, but should NOT be blocked by standard fire immunity */
		public boolean isPlasma();
		public <T extends ICustomDamageSource> T setPlasma();
		
		/** source is "alchemical", which makes it stronger vs IAlchShield */
		public boolean isAlchemy();
		public <T extends ICustomDamageSource> T setAlchemy();
		
		/** source that comes from any higher power (deity, god, etc) */
		public boolean isDivine();
		public <T extends ICustomDamageSource> T setDivine();
	}
}
