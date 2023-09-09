package com.quartzshard.aasb.client.model;

import java.util.function.Supplier;

import com.quartzshard.aasb.AsAboveSoBelow;
import com.quartzshard.aasb.common.entity.living.HorrorEntity;

import net.minecraft.client.model.ZombieModel;
import net.minecraft.client.model.geom.ModelPart;

public class HorrorModel extends ZombieModel<HorrorEntity> {

	public HorrorModel(ModelPart root) {
		super(root);
	}

	/**
	 * Sets this entity's model rotation angles
	 */
	@Override
	public void setupAnim(HorrorEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		int r = 3;
		Supplier<Integer> rint = () -> entity.getRandom().nextInt(-r,r+1);
		super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw+rint.get(), headPitch+rint.get());
		//Supplier<Boolean> rbool = () -> entity.getRandom().nextBoolean();
		//AnimationUtils.animateZombieArms(rbool.get() ? leftArm : rightArm, rbool.get() ? leftArm : rightArm, isAggressive(entity), this.attackTime, ageInTicks);
	}

	@Override
	public boolean isAggressive(HorrorEntity entity) {
		return entity.isAggressive() ? AsAboveSoBelow.RAND.nextBoolean() : AsAboveSoBelow.RAND.nextInt(8) == 0;
	}

}
