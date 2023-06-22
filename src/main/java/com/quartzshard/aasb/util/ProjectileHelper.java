package com.quartzshard.aasb.util;

import org.jetbrains.annotations.Nullable;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class ProjectileHelper {
	public static SmallFireball fireball(Level level, Vec3 pos, Vec3 target, @Nullable LivingEntity owner) {
        double dist = pos.distanceToSqr(target);//owner.distanceToSqr(target);
        Vec3 shootVec = new Vec3(
        	target.x - pos.x,
        	target.y - pos.y,
        	target.z - pos.z
        );
        double acc = Math.sqrt(Math.sqrt(dist)) * 0.25;
		SmallFireball fb = new SmallFireball(level, owner,
				shootVec.x + owner.getRandom().nextGaussian() * acc,
				shootVec.y + owner.getRandom().nextGaussian() * acc,
				shootVec.z + owner.getRandom().nextGaussian() * acc);
		fb.setPos(pos);
		level.addFreshEntity(fb);
		return fb;
	}
}
