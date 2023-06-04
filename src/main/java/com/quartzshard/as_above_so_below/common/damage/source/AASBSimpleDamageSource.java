package com.quartzshard.as_above_so_below.common.damage.source;

import com.quartzshard.as_above_so_below.common.damage.source.AASBDmgSrc.ICustomDamageSource;

import net.minecraft.world.damagesource.DamageSource;

public class AASBSimpleDamageSource extends DamageSource implements ICustomDamageSource {
	public AASBSimpleDamageSource(String id) {
		super(id);
	}

	private boolean bypassAlchShield;
	private boolean bypassDr;
	private boolean plasma;
	private boolean alchemy;
	private boolean divine;
	
	/** source should not be blocked by IAlchShield */
	public boolean isBypassAlchShield() {return this.bypassAlchShield;}
	public AASBSimpleDamageSource bypassAlchShield() {
		this.bypassAlchShield = true;
		return this;
	}
	
	/** source should bypass any 'damage reduction', such as matter armors */
	public boolean isBypassDr() {return this.bypassDr;}
	public AASBSimpleDamageSource bypassDr() {
		this.bypassDr = true;
		return this;
	}
	
	/** bypasses everything that isnt just invincible */
	public AASBSimpleDamageSource bypassNotInvul() {
		return (AASBSimpleDamageSource) this
				.bypassAlchShield()
				.bypassDr()
				.bypassMagic()
				.bypassArmor();
	}
	
	/** immovable object? never heard of it */
	public AASBSimpleDamageSource bypassEverything() {
		return (AASBSimpleDamageSource) this
				.bypassNotInvul()
				.bypassInvul();
	}
	
	/** source should behave like fire, but should NOT be blocked by standard fire immunity */
	public boolean isPlasma() {return this.plasma;}
	public AASBSimpleDamageSource setPlasma() {
		this.plasma = true;
		return this;
	}
	
	/** source is "alchemical", which makes it stronger vs IAlchShield */
	public boolean isAlchemy() {return this.alchemy;}
	public AASBSimpleDamageSource setAlchemy() {
		this.alchemy = true;
		return this;
	}
	
	/** source that comes from any higher power (deity, god, etc) */
	public boolean isDivine() {return this.divine;}
	public AASBSimpleDamageSource setDivine() {
		this.divine = true;
		return this;
	}

}
