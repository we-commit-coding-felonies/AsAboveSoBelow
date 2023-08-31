package com.quartzshard.aasb.api.alchemy.aspects.stack;

import org.jetbrains.annotations.Nullable;

import com.quartzshard.aasb.api.alchemy.aspects.*;
import com.quartzshard.aasb.init.AlchemyInit.FormTree;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

public class FormStack {
	public static final String TYPE_KEY = "aspect_type";
	public static final String AMOUNT_KEY = "amount";
	public static final String TYPE = "form";
	private final AspectForm form;
	private long amount;

	public FormStack(AspectForm form, long amount) {
		this.form = form;
		this.amount = amount;
	}
	public FormStack(AspectForm form) {
		this(form, 1);
	}
	
	public long getAmount() {
		return isValid() ? amount : 0;
	}
	
	public void setAmount(long newAmount) {
		if (validate(newAmount)) {
			amount = newAmount;
		}
	}
	
	public AspectForm getForm() {
		return form;
	}
	
	private static boolean validate(AspectForm form, long amount) {
		return validate(form) && validate(amount);
	}
	private static boolean validate(AspectForm form) {
		return form != null && FormTree.exists(form.getName());
	}
	private static boolean validate(long amount) {
		return amount > 0;
	}
	
	public boolean isValid() {
		return validate(form, amount);
	}
	
	@Nullable
	public static FormStack fromTag(CompoundTag tag) {
		if (tag.getString(TYPE_KEY) == TYPE) {
			AspectForm form = FormTree.get(ResourceLocation.tryParse(tag.getString(TYPE)));
			long amount = tag.getLong(AMOUNT_KEY);
			if (validate(form, amount))
				return new FormStack(form, amount);
		}
		return null;
	}
	
	public CompoundTag toTag() {
		CompoundTag tag = new CompoundTag();
		tag.putString(TYPE_KEY, TYPE);
		tag.putString(TYPE, form.getName().toString());
		tag.putLong(AMOUNT_KEY, amount);
		return tag;
	}
}
