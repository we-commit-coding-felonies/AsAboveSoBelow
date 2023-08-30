package com.quartzshard.aasb.common.damage.source;

import com.quartzshard.aasb.common.damage.source.AASBDmgSrc.ICustomDamageSource;

import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.entity.Entity;

@SuppressWarnings("unchecked")
public class AASBEntityDamageSource extends EntityDamageSource implements ICustomDamageSource {
	public AASBEntityDamageSource(String id, Entity entity) {
		super(id, entity);
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
	public AASBEntityDamageSource bypassAlchShield() {
		this.bypassAlchShield = true;
		return this;
	}
	
	/** source should bypass any 'damage reduction', such as matter armors */
	@Override
	public boolean isBypassDr() {return this.bypassDr;}
	@Override
	public AASBEntityDamageSource bypassDr() {
		this.bypassDr = true;
		return this;
	}
	
	/** bypasses everything that isnt just invincible */
	@Override
	public AASBEntityDamageSource bypassNotInvul() {
		return (AASBEntityDamageSource) this
				.bypassAlchShield()
				.bypassDr()
				.bypassMagic()
				.bypassArmor();
	}
	
	/** immovable object? never heard of it */
	@Override
	public AASBEntityDamageSource bypassEverything() {
		return (AASBEntityDamageSource) this
				.bypassNotInvul()
				.bypassInvul();
	}
	
	/** source should behave like fire, but should NOT be blocked by standard fire immunity */
	@Override
	public boolean isPlasma() {return this.plasma;}
	@Override
	public AASBEntityDamageSource setPlasma() {
		this.plasma = true;
		return this;
	}
	
	/** source is "alchemical", which makes it stronger vs IAlchShield */
	@Override
	public boolean isAlchemy() {return this.alchemy;}
	@Override
	public AASBEntityDamageSource setAlchemy() {
		this.alchemy = true;
		return this;
	}
	
	/** source that comes from any higher power (deity, god, etc) */
	@Override
	public boolean isDivine() {return this.divine;}
	@Override
	public AASBEntityDamageSource setDivine() {
		this.divine = true;
		return this;
	}
}