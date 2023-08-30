package com.quartzshard.aasb.common.damage.source;

import com.quartzshard.aasb.common.damage.source.AASBDmgSrc.ICustomDamageSource;

import net.minecraft.world.damagesource.DamageSource;

@SuppressWarnings("unchecked")
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
	@Override
	public boolean isBypassAlchShield() {return this.bypassAlchShield;}
	@Override
	public AASBSimpleDamageSource bypassAlchShield() {
		this.bypassAlchShield = true;
		return this;
	}
	
	/** source should bypass any 'damage reduction', such as matter armors */
	@Override
	public boolean isBypassDr() {return this.bypassDr;}
	@Override
	public AASBSimpleDamageSource bypassDr() {
		this.bypassDr = true;
		return this;
	}
	
	/** bypasses everything that isnt just invincible */
	@Override
	public AASBSimpleDamageSource bypassNotInvul() {
		return (AASBSimpleDamageSource) this
				.bypassAlchShield()
				.bypassDr()
				.bypassMagic()
				.bypassArmor();
	}
	
	/** immovable object? never heard of it */
	@Override
	public AASBSimpleDamageSource bypassEverything() {
		return (AASBSimpleDamageSource) this
				.bypassNotInvul()
				.bypassInvul();
	}
	
	/** source should behave like fire, but should NOT be blocked by standard fire immunity */
	@Override
	public boolean isPlasma() {return this.plasma;}
	@Override
	public AASBSimpleDamageSource setPlasma() {
		this.plasma = true;
		return this;
	}
	
	/** source is "alchemical", which makes it stronger vs IAlchShield */
	@Override
	public boolean isAlchemy() {return this.alchemy;}
	@Override
	public AASBSimpleDamageSource setAlchemy() {
		this.alchemy = true;
		return this;
	}
	
	/** source that comes from any higher power (deity, god, etc) */
	@Override
	public boolean isDivine() {return this.divine;}
	@Override
	public AASBSimpleDamageSource setDivine() {
		this.divine = true;
		return this;
	}

}
