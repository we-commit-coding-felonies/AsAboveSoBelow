package com.quartzshard.aasb.api.alchemy.aspects;

import java.util.Arrays;

import com.quartzshard.aasb.AsAboveSoBelow;
import com.quartzshard.aasb.api.alchemy.IAlchemicalFlow;
import com.quartzshard.aasb.common.item.equipment.trinket.rune.TrinketRune;
import com.quartzshard.aasb.data.AASBLang;
import com.quartzshard.aasb.util.LogHelper;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class AspectForm extends ForgeRegistryEntry<AspectForm> implements IAlchemicalFlow<AspectForm> {
	
	private AspectForm parent;
	private AspectForm[] children;
	private ResourceLocation name;
	private final int distance, color;
	private final Component loc, fLoc;
	
	/**
	 * Creates a new form node on the tree. Will throw an exception if you try to assign multiple parents, don't make cycles!
	 * @param name
	 * @param parent
	 * @param children
	 * @throws FormTreeException 
	 */
	public AspectForm(ResourceLocation name, String langKey, AspectForm parent, AspectForm[] children, int color) {
		if((!name.equals(AsAboveSoBelow.rl("materia")) && parent == null) || (name.equals(AsAboveSoBelow.rl("materia")) && parent != null)) {
			throw new FormTreeException("Bad root node specified. Don't try to assign parents to materia, or make a node with no parents.");
		}
		this.name = name;
		this.parent = parent;
		this.children = children;
		this.color = color;
		
		if(!checkChildrenAgree()) throw new FormTreeException("Tried to make a cycle. Nodes can't have multiple parents.");
		if (parent != null) {
			this.distance = parent.getDistance() + 1;
			if (!Arrays.asList(parent.getChildren()).contains(this)) {
				parent.addChildNode(this);
			}
		} else {
			this.distance = 0;
		}
		
		loc = AASBLang.tc(langKey);
		fLoc = loc.copy().withStyle(Style.EMPTY.withColor(color));
	}

	/**
	 * Creates a new form node on the tree, with no existing children. Will throw an exception if you try to assign multiple parents, don't make cycles!
	 * @param name
	 * @param parent
	 * @throws FormTreeException
	 */
	public AspectForm(ResourceLocation name, AspectForm parent, int color) {
		this(name, autoLangKey(name), parent, color);
	}

	/**
	 * Creates a new form node on the tree, with no existing children. Will throw an exception if you try to assign multiple parents, don't make cycles! <br>
	 * This version lets you manually set the localization string, if you need that for some reason.
	 * @param name
	 * @param parent
	 * @throws FormTreeException
	 */
	public AspectForm(ResourceLocation name, String langKey, AspectForm parent, int color) {
		this(name, langKey, parent, new AspectForm[0], color);
	}
	
	public MutableComponent loc() {
		return loc.copy();
	}
	public MutableComponent fLoc() {
		return fLoc.copy();
	}
	
	private static String autoLangKey(ResourceLocation loc) {
		String langKey = "misc."+loc.getNamespace()+".aspect.form."+loc.getPath();
		LogHelper.debug("AspectForm.autoLangKey()", "MadeKey", langKey);
		return langKey;
	}
	
	/**
	 * Will confirm that each child node agrees that this is the parent. If false, tree structure is invalid.
	 * @return
	 */
	public boolean checkChildrenAgree() {
		for (AspectForm child: this.children) {
			if (child.getParent() != this) {
				return false;
			}
		}
		return true;
	}
		
	public AspectForm getParent() {
		return this.parent;
	}

	public AspectForm[] getChildren() {
		return this.children;
	}
	
	public ResourceLocation getName() {
		return name;
	}
	
	public int getDistance() {
		return this.distance;
	}
		
	//Yes, this is much more code than ArrayList would need. No, I don't care.
	public void addChildNode(AspectForm adoptee) {
		AspectForm prev[] = this.children;
		this.children = new AspectForm[prev.length + 1];
		System.arraycopy(prev, 0 ,this.children, 0, prev.length);
		this.children[this.children.length - 1] = adoptee;
	}

	// Little exception to notify addon devs of their bad behaviour with the form tree.
	class FormTreeException extends RuntimeException {
		private static final long serialVersionUID = 1L;
		String cause;
		FormTreeException(String cause) {
			this.cause = cause;
		}
		@Override
		public String toString() {
			return ("Tried to assign an invalid node in the form tree. Reason: " + this.cause + "\n        If you're develping an addon, this is on you. If you're a normal player, please report!");
		}
	}
	
	public int getColor() {
		return color;
	}

	@Override
	public boolean flows(AspectForm to) {
		return this == to.getParent();
	}
	@Override
	public boolean perpendicular(AspectForm to) {
		return this == to;
	}
	@Override
	public boolean violates(AspectForm to) {
		return !this.flows(to) && !this.perpendicular(to);
	}	
}
