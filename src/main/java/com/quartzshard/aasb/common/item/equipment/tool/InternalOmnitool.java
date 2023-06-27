package com.quartzshard.aasb.common.item.equipment.tool;

import com.quartzshard.aasb.AsAboveSoBelow;
import com.quartzshard.aasb.api.item.IStaticSpeedBreaker;
import com.quartzshard.aasb.api.item.bind.ICanItemMode;
import com.quartzshard.aasb.common.network.server.KeyPressPacket.BindState;
import com.quartzshard.aasb.init.AlchemyInit;
import com.quartzshard.aasb.init.ObjectInit;
import com.quartzshard.aasb.util.NBTHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
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
public class InternalOmnitool extends DiggerItem implements IStaticSpeedBreaker, ICanItemMode {
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
	
	@Override
	public boolean onPressedItemMode(ItemStack stack, ServerPlayer player, ServerLevel level) {
		boolean wasInstamine = NBTHelper.Item.getBoolean(stack, "Instamine", false);
		NBTHelper.Item.setBoolean(stack, "Instamine", !wasInstamine);
		//if (wasInstamine)
		//	AlchemyInit.TrinketRunes.FIRE.get().combatAbility(stack, player, level, BindState.PRESSED);
		//else
		//	AlchemyInit.TrinketRunes.WATER.get().combatAbility(stack, player, level, BindState.PRESSED);
		return false;
	}

	@Override
	public int blockBreakSpeedInTicks(ItemStack stack, BlockState state) {
		return NBTHelper.Item.getBoolean(stack, "Instamine", false) ? 1 : 2;
	}

}
