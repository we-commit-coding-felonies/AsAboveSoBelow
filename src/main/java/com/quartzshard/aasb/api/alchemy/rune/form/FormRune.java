package com.quartzshard.aasb.api.alchemy.rune.form;

import org.jetbrains.annotations.Nullable;

import com.quartzshard.aasb.api.alchemy.aspect.FormAspect;
import com.quartzshard.aasb.api.alchemy.rune.Rune;
import com.quartzshard.aasb.init.AlchInit;

import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

public abstract class FormRune extends Rune {
	
	/**
	 * @param form MUST correspond with a form registered in the form tree!
	 */
	public FormRune(ResourceLocation form) {
		this.form = form;
	}
	
	private final ResourceLocation form;
	
	@Override
	public MutableComponent loc() {
		return getForm().loc();
	}
	@Override
	public MutableComponent fLoc() {
		return getForm().fLoc();
	}

	
	public FormAspect getForm() {
		@Nullable FormAspect fa = AlchInit.getForm(form);
		if (fa == null) throw new IllegalArgumentException("Invalid FormRune "+form.toString());
		return fa;
	}
}
