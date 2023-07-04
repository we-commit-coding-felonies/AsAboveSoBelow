package com.quartzshard.aasb.common.item.equipment.tool;

import com.quartzshard.aasb.AsAboveSoBelow;
import com.quartzshard.aasb.api.item.IStaticSpeedBreaker;
import com.quartzshard.aasb.api.item.bind.ICanHandleKeybind;
import com.quartzshard.aasb.common.network.server.KeyPressPacket.BindState;
import com.quartzshard.aasb.common.network.server.KeyPressPacket.PressContext;
import com.quartzshard.aasb.init.AlchemyInit.TrinketRunes;
import com.quartzshard.aasb.util.NBTHelper;

import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/**
 * tool that can break any block <br>
 * used internally by the shovel areablast, but is also a nice dev / test item
 * @author solunareclipse1
 */
public class InternalOmnitool extends DiggerItem implements IStaticSpeedBreaker, ICanHandleKeybind {
	public InternalOmnitool(float damage, float speed, Tier tier, TagKey<Block> breakableBlocks, Properties props) {
		super(damage, speed, tier, breakableBlocks, props);
	}
	
	@Override
	public int getBarWidth(ItemStack stack) {
		return NBTHelper.Item.getBoolean(stack, "Instamine", false) ? 13 : 0;
	}
	
	@Override
	public boolean isBarVisible(ItemStack stack) {
		return true;
	}
	
	@Override
	public int getBarColor(ItemStack stack) {
		return AsAboveSoBelow.RAND.nextInt(0, (0xffffff)+1);
	}
	
	@Override
	public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
		if (selected) {
			if (entity instanceof Player plr && plr.hasPermissions(4)) {
				if (!isFoil(stack)) {
					NBTHelper.Item.setBoolean(stack, "IsExtremelyOP", true);
				}
			} else {
				if (isFoil(stack)) {
					NBTHelper.Item.setBoolean(stack, "IsExtremelyOP", false);
				}
			}
		}
	}
	
	@Override
	public boolean isFoil(ItemStack stack) {
		return NBTHelper.Item.getBoolean(stack, "IsExtremelyOP", false);
	}
	
	@Override
	public boolean isCorrectToolForDrops(ItemStack stack, BlockState state) {
		return isFoil(stack) || net.minecraftforge.common.TierSortingRegistry.isCorrectTierForDrops(AASBToolTier.HERMETIC, state);
	}

	public boolean toggleInstamine(ItemStack stack) {
		boolean wasInstamine = NBTHelper.Item.getBoolean(stack, "Instamine", false);
		NBTHelper.Item.setBoolean(stack, "Instamine", !wasInstamine);
		//if (wasInstamine)
		//	AlchemyInit.TrinketRunes.FIRE.get().combatAbility(stack, player, level, BindState.PRESSED);
		//else
		//	AlchemyInit.TrinketRunes.WATER.get().combatAbility(stack, player, level, BindState.PRESSED);
		return true;
	}

	@Override
	public int blockBreakSpeedInTicks(ItemStack stack, BlockState state) {
		return NBTHelper.Item.getBoolean(stack, "Instamine", false) ? 1 : 2;
	}

	/**
	 * serverside code can be thrown in here to quickly test it
	 */
	@Override
	public boolean handle(PressContext ctx) {
		switch (ctx.bind()) {
		case ITEMMODE:
			ServerPlayer plr = ctx.player();
			if (ctx.state() == BindState.PRESSED) {
				if (!plr.isShiftKeyDown()) {
					plr.displayClientMessage(new TextComponent("GODMODE"), false);
					plr.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 4));
					plr.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, Integer.MAX_VALUE, 0));
					plr.addEffect(new MobEffectInstance(MobEffects.HEAL, Integer.MAX_VALUE, 99));
					plr.addEffect(new MobEffectInstance(MobEffects.SATURATION, Integer.MAX_VALUE, 99));
				} else {
					plr.displayClientMessage(new TextComponent("mortal mode"), false);
					plr.removeEffect(MobEffects.DAMAGE_RESISTANCE);
					plr.removeEffect(MobEffects.FIRE_RESISTANCE);
					plr.removeEffect(MobEffects.HEAL);
					plr.removeEffect(MobEffects.SATURATION);
				}
			}
			return true;
		case ITEMFUNC_1:
			return ctx.state() == BindState.PRESSED
			&& TrinketRunes.FIRE.get().combatAbility(ctx.stack(), ctx.player(), ctx.level(), BindState.PRESSED, true);
		case ITEMFUNC_2:
			return ctx.state() == BindState.PRESSED
			&& TrinketRunes.ETHEREAL.get().combatAbility(ctx.stack(), ctx.player(), ctx.level(), BindState.PRESSED, true);
		case EMPOWER:
			return ctx.state() == BindState.PRESSED
			&& toggleInstamine(ctx.stack());
		default:
			return false;
		}
	}

}
