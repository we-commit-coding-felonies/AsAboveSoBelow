package com.quartzshard.aasb.common.item.equipment;

import com.quartzshard.aasb.AASB;
import com.quartzshard.aasb.common.entity.projectile.WayGrenadeEntity;
import com.quartzshard.aasb.util.NBTUtil;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class WayGrenadeItem extends Item {
	public static final String TK_DETPOWER = "DetPower";

	public WayGrenadeItem(@NotNull Properties props) {
		super(props);
	}

	/**
	 * Called to trigger the item's "innate" right click behavior. To handle when this item is used on a Block, see
	 * {@link #onItemUse}.
	 */
	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.SNOWBALL_THROW, SoundSource.NEUTRAL, 0.5f, 0.4f / (AASB.RNG.nextFloat() * 0.4f + 0.8f));
		if (!level.isClientSide) {
			@NotNull WayGrenadeEntity thrown = new WayGrenadeEntity(level, player, stack);
			thrown.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0f, 1.5f, 1.0f);
			level.addFreshEntity(thrown);
		}
		player.awardStat(Stats.ITEM_USED.get(this));
		if (!player.getAbilities().instabuild) {
			stack.shrink(1);
		}
		return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
	}

	public float getDetPower(ItemStack stack) {
		return NBTUtil.getFloat(stack, TK_DETPOWER, 0);
	}
	public void setDetPowerDirect(ItemStack stack, float detPower) {
		NBTUtil.setFloat(stack, TK_DETPOWER, detPower);
	}
	/**
	 * Sets the explosion power based on an input way value
	 * @param stack
	 * @param way
	 * @return
	 */
	public long setDetPower(@NotNull ItemStack stack, long way) {
		int exp = 0;
		long rem = 0;
		for (int i = 0; i < 63; i++) {
			long p2 = (long) Math.pow(2, i);
			if (p2 == way || i == 62) {
				exp = i;
				rem = way - p2;
				break;
			} else if (p2 > way) {
				exp = i-1;
				rem = way - ((long)Math.pow(2, exp));
				break;
			}
		}
		setDetPowerDirect(stack, exp/2f);
		return rem;
	}
}