package com.quartzshard.aasb.common.item.equipment.curio;

import java.util.UUID;

import com.google.common.collect.Multimap;
import com.quartzshard.aasb.api.item.IRuneable;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

public class CharmItem extends AbilityCurioItem implements IRuneable, ICurioItem {

	public CharmItem(int maxRunes, Properties props) {
		super(maxRunes, props);
		
	}

	@Override
	public ItemAbility getAbility(ItemStack stack) {
		return ItemAbility.PASSIVE;
	}
	
	@Override
	public void curioTick(SlotContext ctx, ItemStack stack) {
		if (ctx.entity() instanceof ServerPlayer plr) {
			tickRunes(stack, plr, plr.serverLevel(), false);
		}
	}
	
	@Override
	public void onUnequip(SlotContext ctx, ItemStack next, ItemStack prev) {
		if (ctx.entity() instanceof ServerPlayer plr) {
			//System.out.print("PREV = ");
			tickRunes(prev, plr, plr.serverLevel(), true);
			//System.out.print("NEXT = ");
			//tickRunes(next, plr, plr.serverLevel(), true);
		}
	}

}
