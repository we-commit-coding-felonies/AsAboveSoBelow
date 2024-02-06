package com.quartzshard.aasb.api.alchemy.aspect;

import java.util.Arrays;

import org.jetbrains.annotations.Nullable;

import com.quartzshard.aasb.AASB;
import com.quartzshard.aasb.data.LangData;
import com.quartzshard.aasb.init.AlchInit;
import com.quartzshard.aasb.util.Logger;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;

/**
 * Form is an arbitrary size tree <br>
 * It's flow branches out from the root, going towards its children <br>
 * Flow violation is 10% staying the same (unless it is a leaf, in which case it is 0%), 1/(distance from root) going to its parent, 100% everything else <br>
 * The root is special, it flows to anything else on the tree, but violates 100% towards itself
 */
public class FormAspect implements IAspect<FormAspect> {
	
	@Nullable private FormAspect parent;
	private FormAspect[] children;
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
	public FormAspect(ResourceLocation name, String langKey, @Nullable FormAspect parent, FormAspect[] children, int color) {
		if((!name.equals(AASB.rl("materia")) && parent == null) || (name.equals(AASB.rl("materia")) && parent != null)) {
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
		
		loc = LangData.tc(langKey);
		fLoc = loc.copy().withStyle(Style.EMPTY.withColor(color));
	}

	/**
	 * Creates a new form node on the tree, with no existing children. Will throw an exception if you try to assign multiple parents, don't make cycles!
	 * @param name
	 * @param parent
	 * @throws FormTreeException
	 */
	public FormAspect(ResourceLocation name, FormAspect parent, int color) {
		this(name, autoLangKey(name), parent, color);
	}

	/**
	 * Creates a new form node on the tree, with no existing children. Will throw an exception if you try to assign multiple parents, don't make cycles! <br>
	 * This version lets you manually set the localization string, if you need that for some reason.
	 * @param name
	 * @param parent
	 * @throws FormTreeException
	 */
	public FormAspect(ResourceLocation name, String langKey, FormAspect parent, int color) {
		this(name, langKey, parent, new FormAspect[0], color);
	}
	
	public MutableComponent loc() {
		return loc.copy();
	}
	public MutableComponent fLoc() {
		return fLoc.copy();
	}
	
	private static String autoLangKey(ResourceLocation loc) {
		String langKey = "misc."+loc.getNamespace()+".aspect.form."+loc.getPath();
		Logger.debug("AspectForm.autoLangKey()", "MadeKey", langKey);
		return langKey;
	}
	
	/**
	 * Will confirm that each child node agrees that this is the parent. If false, tree structure is invalid.
	 * @return
	 */
	public boolean checkChildrenAgree() {
		for (FormAspect child: this.children) {
			if (child.getParent() != this) {
				return false;
			}
		}
		return true;
	}
	
	@Nullable
	public FormAspect getParent() {
		return this.parent;
	}

	public FormAspect[] getChildren() {
		return this.children;
	}
	
	public ResourceLocation getName() {
		return name;
	}
	
	public int getDistance() {
		return this.distance;
	}
		
	// Yes, this is much more code than ArrayList would need. No, I don't care.
	public void addChildNode(FormAspect adoptee) {
		FormAspect prev[] = this.children;
		this.children = new FormAspect[prev.length + 1];
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
		public String getLocalizedMessage() {
			return this.cause;
		}
	}
	
	public int getColor() {
		return color;
	}

	@Override
	public boolean flowsTo(FormAspect other) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean flowsFrom(FormAspect other) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public float violationTo(FormAspect other) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float violationFrom(FormAspect other) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String toString() {
		return "Form."+getName().toString();
	}

	@Override
	public String serialize() {
		return toString();
	}

	/**
	 * Deserializes a ShapeAspect from a String <br>
	 * Expected format is "Form.aasb:metal", returns null if it fails
	 * @param dat
	 * @return 
	 */
	@Nullable
	public static FormAspect deserialize(String dat) {
		if (dat != "" && dat.startsWith("Form.")) {
			return AlchInit.getForm(ResourceLocation.tryParse(dat.replace("Form.", "")));
		}
		return null;
	}
}
