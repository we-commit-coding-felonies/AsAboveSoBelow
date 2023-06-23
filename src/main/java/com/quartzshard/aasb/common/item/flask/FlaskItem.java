package com.quartzshard.aasb.common.item.flask;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.google.common.base.Enums;
import com.quartzshard.aasb.api.alchemy.aspects.AspectForm;
import com.quartzshard.aasb.api.alchemy.aspects.AspectShape;
import com.quartzshard.aasb.init.AlchemyInit.FormTree;
import com.quartzshard.aasb.util.NBTHelper;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class FlaskItem extends Item {
	public FlaskItem(Properties props) {
		super(props);
	}
	
	private static final String
		TAG_SHAPE = "StoredShape",
		TAG_FORM = "StoredForm",
		TAG_DIRTY = "IsDirty";

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
	
	public boolean hasStored(ItemStack stack) {
		return getStoredShape(stack) != null && getStoredForm(stack) != null;
	}

	/**
	 * will reject if either input is null, or it already has aspects stored
	 * @param stack
	 * @param shape
	 * @param form
	 * @return
	 */
	public boolean setStored(ItemStack stack, AspectShape shape, AspectForm form) {
		if (shape != null && form != null && !hasStored(stack)) {
			NBTHelper.Item.setString(stack, TAG_SHAPE, Enums.stringConverter(AspectShape.class).reverse().convert(shape));
			NBTHelper.Item.setString(stack, TAG_FORM, form.getName().toString());
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
	}
}
