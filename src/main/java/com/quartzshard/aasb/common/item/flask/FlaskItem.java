package com.quartzshard.aasb.common.item.flask;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import org.jetbrains.annotations.Nullable;

import com.google.common.base.Enums;
import com.quartzshard.aasb.AsAboveSoBelow;
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

public class FlaskItem extends Item {
	public FlaskItem(int lifetime, Properties props) {
		super(props);
		this.lifetime = lifetime;
	}
	
	public static final DecimalFormat TIME_FORMAT = Util.make(new DecimalFormat("00"), (df) -> {
		df.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ROOT));
	});
	public final int lifetime;
	protected static final String
		TAG_SHAPE = "StoredShape",
		TAG_FORM = "StoredForm",
		TAG_EXPIRES = "ExpirationDate",
		TAG_DIRTY = "IsDirty";
	
	@Override
	public void appendHoverText(ItemStack stack, Level level, List<Component> tips, TooltipFlag flags) {
		if (hasStored(stack)) {
			AspectShape shape = getStoredShape(stack);
			AspectForm form = getStoredForm(stack);
			tips.add(AASBLang.tc(AASBLang.TIP_FLASK_ASPECTS, shape.fLoc(), form.fLoc()).withStyle(ChatFormatting.GRAY));
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
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		clearStored(stack);
		if (!player.isShiftKeyDown()) {
			Random r = level.random;
			AspectShape cShape = null;
			AspectForm cForm = null;
			if (hand != InteractionHand.MAIN_HAND) {
				AspectShape[] shapes = AspectShape.values();
				AspectForm[] forms = FormTree.getReg().getValues().toArray(new AspectForm[FormTree.getReg().getValues().size()]);
				cShape = shapes[r.nextInt(shapes.length)];
				cForm = forms[r.nextInt(forms.length)];
			} else {
				if (r.nextInt(10) == 0) {
					AspectShape[] shapes = AspectShape.values();
					cShape = shapes[r.nextInt(shapes.length)];
				} else {
					AspectForm[] forms = FormTree.getReg().getValues().toArray(new AspectForm[FormTree.getReg().getValues().size()]);
					cForm = forms[r.nextInt(forms.length)];
				}
			}
			if (!level.isClientSide) {
				if (cForm == null) {
					player.displayClientMessage(cShape.fLoc(), true);
				} else if (cShape == null) {
					player.displayClientMessage(cForm.fLoc(), true);
				} else {
					player.displayClientMessage(AASBLang.tc("%s & %s", cShape.fLoc(), cForm.fLoc()), true);
				}
			}
			setStored(stack, cShape, cForm, level.getGameTime());
		}
		return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
	}
	
	@Override
	public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
		if (!level.isClientSide && !isContaminated(stack) && isExpired(stack, level.getGameTime())) {
			setContaminated(stack, true);
		}
	}
	
	@Nullable
	public AspectShape getStoredShape(ItemStack stack) {
		String shapeStr = NBTHelper.Item.getString(stack, TAG_SHAPE, null);
		if (shapeStr != null) {
			try {
				return Enums.stringConverter(AspectShape.class).convert(shapeStr);
			} catch (IllegalArgumentException e) {}
		}
		return null;
	}

	@Nullable
	public AspectForm getStoredForm(ItemStack stack) {
		String s = NBTHelper.Item.getString(stack, TAG_FORM, null);
		if (s != null) {
			ResourceLocation rl = ResourceLocation.tryParse(s);
			if (rl != null) {
				return FormTree.get(rl);
			}
		}
		return null;
	}
	
	public boolean canExtract(ItemStack stack) {
		return !isContaminated(stack) && hasStored(stack);
	}
	
	public boolean hasStored(ItemStack stack) {
		return hasStoredShape(stack) && hasStoredForm(stack);
	}
	
	public boolean hasStoredShape(ItemStack stack) {
		return getStoredShape(stack) != null;
	}
	
	public boolean hasStoredForm(ItemStack stack) {
		return getStoredForm(stack) != null;
	}

	/**
	 * will reject if either input is null, or it already has aspects stored
	 * @param stack
	 * @param shape
	 * @param form
	 * @return if insertion suceeded
	 */
	public boolean setStored(ItemStack stack, AspectShape shape, AspectForm form, long currentTime) {
		if (shape != null && form != null && !hasStored(stack)) {
			NBTHelper.Item.setString(stack, TAG_SHAPE, Enums.stringConverter(AspectShape.class).reverse().convert(shape));
			NBTHelper.Item.setString(stack, TAG_FORM, form.getName().toString());
			setExpiry(stack, currentTime+lifetime);
			return true;
		}
		return false;
	}
	
	public boolean isContaminated(ItemStack stack) {
		return NBTHelper.Item.getBoolean(stack, TAG_DIRTY, false);
	}
	
	public void setContaminated(ItemStack stack, boolean dirty) {
		NBTHelper.Item.setBoolean(stack, TAG_DIRTY, dirty);
	}
	
	public void clearStored(ItemStack stack) {
		NBTHelper.Item.removeEntry(stack, TAG_SHAPE);
		NBTHelper.Item.removeEntry(stack, TAG_FORM);
		NBTHelper.Item.removeEntry(stack, TAG_DIRTY);
		NBTHelper.Item.removeEntry(stack, TAG_EXPIRES);
	}
	
	public long getExpiry(ItemStack stack) {
		return NBTHelper.Item.getLong(stack, TAG_EXPIRES, -1);
	}
	
	public void setExpiry(ItemStack stack, long tick) {
		NBTHelper.Item.setLong(stack, TAG_EXPIRES, tick);
	}
	
	public boolean isExpired(ItemStack stack, long currentTime) {
		return isContaminated(stack) || getExpiry(stack) < currentTime;
	}
	
	/**
	 * DO NOT RUN THIS SERVER SIDE!!!!!!!!!!!!
	 * @param stack
	 * @param layer
	 * @return
	 */
	public static int getLiquidColor(ItemStack stack, int layer) {
		if (stack.getItem() instanceof FlaskItem flask) {
			if (layer < 2 && flask.hasStored(stack)) {
				long time = ClientHelper.mc().level.getGameTime();
				if (flask.isExpired(stack, time)) {
					return ColorsHelper.randomGray(0x80);
				} else {
					// covalence colors if you have both universals
					AspectShape shape = flask.getStoredShape(stack);
					AspectForm form = flask.getStoredForm(stack);
					if (shape == AspectShape.UNIVERSAL && form == FormTree.MATERIA.get()) {
						return Mth.hsvToRgb(
								ColorsHelper.loopFade(time, Math.min(6000, flask.lifetime/10), 0, Color.COVALENCE_GREEN.H/360f, Color.PHILOSOPHERS.H/360f),
								Color.PHILOSOPHERS.S/100f,
								Color.PHILOSOPHERS.V/100f);
					}
					
					if (layer == 0) {
						return form == null ? shape.color.I : form.getColor();
					} else {
						return shape == null ? form.getColor() : shape.color.I;
					}
				}
			}
		}
		return -1;
	}
}
