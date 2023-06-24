package com.quartzshard.aasb.common.item.flask;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;

import org.jetbrains.annotations.Nullable;

import com.google.common.base.Enums;
import com.quartzshard.aasb.api.alchemy.aspects.AspectForm;
import com.quartzshard.aasb.api.alchemy.aspects.AspectShape;
import com.quartzshard.aasb.data.AASBLang;
import com.quartzshard.aasb.init.AlchemyInit.FormTree;
import com.quartzshard.aasb.util.ClientHelper;
import com.quartzshard.aasb.util.ColorsHelper;
import com.quartzshard.aasb.util.ColorsHelper.Color;
import com.quartzshard.aasb.util.MiscHelper;
import com.quartzshard.aasb.util.NBTHelper;

import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

public class StorageFlaskItem extends FlaskItem {
	public StorageFlaskItem(Properties props) {
		super(Integer.MAX_VALUE, props);
	}
	
	@Override
	public void appendHoverText(ItemStack stack, Level level, List<Component> tips, TooltipFlag flags) {
		if (hasStored(stack)) {
			AspectShape shape = getStoredShape(stack);
			AspectForm form = getStoredForm(stack);
			tips.add(AASBLang.tc(AASBLang.TIP_FLASK_ASPECTS_ONE, form == null ? shape.fLoc() : form.fLoc()).withStyle(ChatFormatting.GRAY));
			if (isExpired(stack, level.getGameTime())) {
				tips.add(AASBLang.tc(AASBLang.TIP_FLASK_BAD).withStyle(ChatFormatting.RED));
			} else {
				int ticks = (int) (getExpiry(stack) - level.getGameTime());
				String ft = "";
				long[] time = MiscHelper.ticksToTime(ticks);
				for (int i = 0; i < time.length; i++) {
					long t = time[i];
					if (t > 0 || ft != "") {
						char sep;
						if (i == 3) {
							if (ft == "") ft = "0";
							t /= 10;
							sep = '.';
						} else {
							sep = ':';
						}
						String txt = TIME_FORMAT.format(t);
						ft = ft == "" ? txt : ft+sep+txt;
					}
				}
				if (!ft.contains("."))
					ft += ".00";
				boolean soon = !ft.contains(":");
				Component timeTxt = new TextComponent(ft).withStyle(soon ? ChatFormatting.RED : ChatFormatting.WHITE);
				tips.add(AASBLang.tc(AASBLang.TIP_FLASK_EXPIRY, timeTxt).withStyle(ChatFormatting.GRAY));
			}
		}
	}
	
	@Override
	public boolean canExtract(ItemStack stack) {
		return !isContaminated(stack) && hasStored(stack)
				&& !(hasStoredShape(stack) && hasStoredForm(stack));
	}
	
	@Override
	public boolean hasStored(ItemStack stack) {
		return hasStoredShape(stack) || hasStoredForm(stack);
	}

	/**
	 * will accept if and only if one input is null, and it doesnt already has an aspect stored
	 * @param stack
	 * @param shape
	 * @param form
	 * @return if insertion suceeded
	 */
	@Override
	public boolean setStored(ItemStack stack, AspectShape shape, AspectForm form, long currentTime) {
		if ( (shape != null || form != null) && !hasStored(stack) ) {
			boolean didDo = false;
			if (shape == null) {
				NBTHelper.Item.removeEntry(stack, TAG_SHAPE);
				NBTHelper.Item.setString(stack, TAG_FORM, form.getName().toString());
				didDo = true;
			} else if (form == null) {
				NBTHelper.Item.setString(stack, TAG_SHAPE, Enums.stringConverter(AspectShape.class).reverse().convert(shape));
				NBTHelper.Item.removeEntry(stack, TAG_FORM);
				didDo = true;
			}
			if (didDo) {
				setExpiry(stack, currentTime+lifetime);
				return true;
			}
		}
		return false;
	}
}
