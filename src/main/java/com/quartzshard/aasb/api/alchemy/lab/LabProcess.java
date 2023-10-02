package com.quartzshard.aasb.api.alchemy.lab;

import static com.quartzshard.aasb.api.alchemy.lab.LabRecipeData.fl;
import static com.quartzshard.aasb.api.alchemy.lab.LabRecipeData.hasAspectStacks;
import static com.quartzshard.aasb.api.alchemy.lab.LabRecipeData.hasFluidStacks;
import static com.quartzshard.aasb.api.alchemy.lab.LabRecipeData.hasItemStacks;
import static com.quartzshard.aasb.api.alchemy.lab.LabRecipeData.il;
import static com.quartzshard.aasb.api.alchemy.lab.LabRecipeData.sl;
import static com.quartzshard.aasb.api.alchemy.lab.LabRecipeData.wl;

import java.util.Map;
import java.util.Map.Entry;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.NonNullList;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.material.Fluids;

import net.minecraftforge.fluids.FluidStack;

import com.quartzshard.aasb.api.alchemy.aspects.AspectForm;
import com.quartzshard.aasb.api.alchemy.aspects.AspectShape;
import com.quartzshard.aasb.api.alchemy.aspects.AspectWay;
import com.quartzshard.aasb.api.alchemy.aspects.stack.FormStack;
import com.quartzshard.aasb.api.alchemy.aspects.stack.ShapeStack;
import com.quartzshard.aasb.api.alchemy.aspects.stack.WayStack;
import com.quartzshard.aasb.common.item.flask.FlaskItem;
import com.quartzshard.aasb.common.item.flask.StorageFlaskItem;
import com.quartzshard.aasb.init.AlchemyInit.FormTree;
import com.quartzshard.aasb.init.ObjectInit;
import com.quartzshard.aasb.init.ObjectInit.Items;

public enum LabProcess implements StringRepresentable {
	// item, fluid, way, shape, form
	EVAPORATION((input) -> {
		if (hasItemStacks(input.items)) {
			
			@SuppressWarnings("null")
			ItemStack toBurn = input.items.get(0);
			
			AspectForm itemAspect = FormTree.WITCHCRAFT.get(); // TODO: implement once mapper is finished
			NonNullList<ItemStack> itemsOut = il(1);
			
			ItemStack junk = new ItemStack(ObjectInit.Items.SOOT.get());
			@Nullable Map<Enchantment,Integer> enchants = null;
			if (toBurn.isEnchanted()) // TODO enchanted junk can enchant hermetic, bypass vanilla level cap
				enchants = EnchantmentHelper.getEnchantments(toBurn);
			if (enchants != null && !enchants.isEmpty()) {
				for (Entry<Enchantment,Integer> e : enchants.entrySet())
					junk.enchant(e.getKey(), e.getValue());
			}
			itemsOut.set(0, junk);
			
			NonNullList<FormStack> formsOut = fl(1);
			formsOut.set(0, new FormStack(itemAspect));
			return new LabRecipeData(itemsOut, null, null, null, formsOut);
		}
		return null;
	}),

	// item, fluid, way, shape, form
	FIXATION(/* true, false, false, false, false, */ (input) -> {
		if (hasAspectStacks(input.forms) && hasItemStacks(input.items)) {
			AspectForm inForm = input.forms.get(0).getAspect();
			if (inForm != null && inForm.getChildren().length > 0) {
				ItemStack focus = input.items.get(0);
				AspectForm focusForm = FormTree.MARS.get(); // TODO mapper
				if (focusForm.getChildren().length == 0) {
					NonNullList<FormStack> formsOut = fl(1);
					formsOut.set(0, new FormStack(focusForm));
					return new LabRecipeData(input.items, null, null, null, formsOut);
				}
			}
		}
		return null;
	}),

	// item, fluid, way, shape, form
	AMALGAMATION(/* true, false, false, false, false, */ (input) -> {
		if (hasAspectStacks(input.forms) && hasItemStacks(input.items)) {
			AspectForm inForm = input.forms.get(0).getAspect();
			ItemStack focus = input.items.get(0);
			AspectForm focusForm = FormTree.SUN.get(); // TODO mapper
			if (inForm != null && inForm != focusForm) {
				AspectForm inPar = inForm.getParent(),
						focusPar = focusForm.getParent();
				if (inPar != null && inPar == focusPar) {
					NonNullList<FormStack> formsOut = fl(1);
					formsOut.set(0, new FormStack(focusForm));
					return new LabRecipeData(input.items, null, null, null, formsOut);
				}
			}
		}
		return null;
	}),

	// item, fluid, way, shape, form
	HOMOGENIZATION(/* true, false, false, false, false, */ (input) -> {
		if (hasAspectStacks(input.forms) && input.forms.size() == 2) {
			AspectForm inForm = input.forms.get(0).getAspect();
			AspectForm inPar = inForm == null ? null : inForm.getParent();
			if (inPar != null && input.forms.get(1).getAspect() == FormTree.MATERIA.get()) {
				NonNullList<FormStack> formsOut = fl(1);
				formsOut.set(0, new FormStack(inPar));
				return new LabRecipeData(null, null, null, null, formsOut);
			}
		}
		return null;
	}),
	
	
	
	// item, fluid, way, shape, form
	DESICCATION(/* true, false, false, false, false, */ (input) -> {
		if (hasItemStacks(input.items)) {
			
			@SuppressWarnings("null")
			ItemStack toBurn = input.items.get(0);
			
			AspectShape itemAspect = AspectShape.FIRE; // TODO: implement once mapper is finished
			NonNullList<ItemStack> itemsOut = il(1);
			
			ItemStack junk = new ItemStack(ObjectInit.Items.SALT.get());
			@Nullable Map<Enchantment,Integer> enchants = null;
			if (toBurn.isEnchanted()) // TODO enchanted junk can enchant hermetic, bypass vanilla level cap
				enchants = EnchantmentHelper.getEnchantments(toBurn);
			if (enchants != null && !enchants.isEmpty()) {
				for (Entry<Enchantment,Integer> e : enchants.entrySet())
					junk.enchant(e.getKey(), e.getValue());
			}
			itemsOut.set(0, junk);
			
			NonNullList<ShapeStack> aspectsOut = sl(1);
			aspectsOut.set(0, new ShapeStack(itemAspect));
			return new LabRecipeData(itemsOut, null, null, aspectsOut, null);
		}
		return null;
	}),

	// item, fluid, way, shape, form
	OXIDATION(/* true, false, false, false, false, */ (input) -> {
		if (hasAspectStacks(input.shapes)) {
			ShapeStack inShape = input.shapes.get(0);
			if (inShape.getAspect() == AspectShape.AIR) {
				NonNullList<ShapeStack> shapesOut = sl(1);
				shapesOut.set(0, new ShapeStack(AspectShape.FIRE));
				return new LabRecipeData(null, null, null, shapesOut, null);
			}
		}
		return null;
	}),

	// item, fluid, way, shape, form
	CONGELATION(/* true, false, false, false, false, */ (input) -> {
		if (hasAspectStacks(input.shapes)) {
			ShapeStack inShape = input.shapes.get(0);
			if (inShape.getAspect() == AspectShape.FIRE) {
				NonNullList<ShapeStack> shapesOut = sl(1);
				shapesOut.set(0, new ShapeStack(AspectShape.EARTH));
				return new LabRecipeData(null, null, null, shapesOut, null);
			}
		}
		return null;
	}),

	// item, fluid, way, shape, form
	CERATION(/* true, false, false, false, false, */ (input) -> {
		if (hasAspectStacks(input.shapes)) {
			ShapeStack inShape = input.shapes.get(0);
			if (inShape.getAspect() == AspectShape.EARTH) {
				NonNullList<ShapeStack> shapesOut = sl(1);
				shapesOut.set(0, new ShapeStack(AspectShape.WATER));
				return new LabRecipeData(null, null, null, shapesOut, null);
			}
		}
		return null;
	}),

	// item, fluid, way, shape, form
	DEHYDRATION(/* true, false, false, false, false, */ (input) -> {
		if (hasAspectStacks(input.shapes)) {
			ShapeStack inShape = input.shapes.get(0);
			if (inShape.getAspect() == AspectShape.WATER) {
				NonNullList<ShapeStack> shapesOut = sl(1);
				shapesOut.set(0, new ShapeStack(AspectShape.AIR));
				return new LabRecipeData(null, null, null, shapesOut, null);
			}
		}
		return null;
	}),

	// item, fluid, way, shape, form
	EXALTATION(/* true, false, false, false, false, */ (input) -> {
		if (hasAspectStacks(input.shapes) && input.shapes.size() == 4) {
			boolean w,e,f,a;
			w = e = f = a = false;
			for (ShapeStack shape : input.shapes) {
				switch (shape.getAspect()) {
				case WATER:
					w = true;
					break;
				case EARTH:
					e = true;
					break;
				case FIRE:
					f = true;
					break;
				case AIR:
					a = true;
					break;
				default:
					return null;
				}
			}
			if (w && e && f && a) {
				NonNullList<ShapeStack> shapesOut = sl(1);
				shapesOut.set(0, new ShapeStack(AspectShape.UNIVERSAL));
				return new LabRecipeData(null, null, null, shapesOut, null);
			}
		}
		return null;
	}),

	// item, fluid, way, shape, form
	CONDEMNATION(/* true, false, false, false, false, */ (input) -> {
		if (hasAspectStacks(input.shapes) && hasItemStacks(input.items)) {
			ShapeStack inShape = input.shapes.get(0);
			AspectShape focusShape = AspectShape.AIR; // TODO mapper!!!!!
			if (inShape.getAspect() == AspectShape.UNIVERSAL) {
				NonNullList<ShapeStack> shapesOut = sl(1);
				shapesOut.set(0, new ShapeStack(AspectShape.AIR, 4));
				return new LabRecipeData(input.items, null, null, shapesOut, null);
			}
		}
		return null;
	}),
	
	

	// item, fluid, way, shape, form
	SUBLIMATION(/* true, false, false, false, false, */ (input) -> {
		if (hasItemStacks(input.items)) {
			
			@SuppressWarnings("null")
			ItemStack toBurn = input.items.get(0);
			
			long itemAspect = 11; // TODO: implement once mapper is finished
			NonNullList<ItemStack> itemsOut = il(1);
			
			ItemStack junk = new ItemStack(ObjectInit.Items.SALT.get());
			@Nullable Map<Enchantment,Integer> enchants = null;
			if (toBurn.isEnchanted()) // TODO enchanted junk can enchant hermetic, bypass vanilla level cap
				enchants = EnchantmentHelper.getEnchantments(toBurn);
			if (enchants != null && !enchants.isEmpty()) {
				for (Entry<Enchantment,Integer> e : enchants.entrySet())
					junk.enchant(e.getKey(), e.getValue());
			}
			itemsOut.set(0, junk);
			
			NonNullList<WayStack> aspectsOut = wl(1);
			aspectsOut.set(0, new WayStack(itemAspect));
			return new LabRecipeData(itemsOut, null, aspectsOut, null, null);
		}
		return null;
	}),

	// item, fluid, way, shape, form
	CONJUNCTION(/* true, false, false, false, false, */ (input) -> {
		if (hasAspectStacks(input.ways) && input.ways.size() == 2) {
			AspectWay a = input.ways.get(0).getAspect(),
					b = input.ways.get(1).getAspect();
			if (a != null && b != null && a.flows(b)) {
				NonNullList<WayStack> aspectsOut = wl(1);
				aspectsOut.set(0, new WayStack(a.getValue() + b.getValue()));
				return new LabRecipeData(null, null, aspectsOut, null, null);
			}
		}
		return null;
	}),

	// item, fluid, way, shape, form
	STAGNATION(/* true, false, false, false, false, */ (input) -> {
		if (hasAspectStacks(input.ways) && input.ways.get(0).getValue() > 3) {
			WayStack s = input.ways.get(0);
			s.setValue(s.getValue()-3);
			NonNullList<WayStack> aspectsOut = wl(1);
			aspectsOut.set(0, s);
			return new LabRecipeData(null, null, aspectsOut, null, null);
		}
		return null;
	}),

	// item, fluid, way, shape, form
	SEPARATION(/* true, false, false, false, false, */ (input) -> {
		if (hasAspectStacks(input.ways) && input.ways.get(0).getValue() % 2 == 0) {
			WayStack way = new WayStack(input.ways.get(0).getValue()/2);
			NonNullList<WayStack> aspectsOut = wl(2);
			aspectsOut.set(0, way);
			aspectsOut.set(1, way.dupe());
			return new LabRecipeData(null, null, aspectsOut, null, null);
		}
		return null;
	}),

	// item, fluid, way, shape, form
	FILTRATION(/* true, false, false, false, false, */ (input) -> {
		if (hasAspectStacks(input.ways) && hasItemStacks(input.items)) {
			ItemStack focusItem = input.items.get(0);
			WayStack inWay = input.ways.get(0);
			long inWayVal = inWay.getValue();
			long focusWay = 11; // TODO: make this actually get the items way value, waiting on shard to finish the mapper
			if (inWayVal > focusWay) {
				long remainder = inWayVal % focusWay;
				NonNullList<WayStack> waysOut = wl(remainder != 0 ? 2 : 1);
				if (remainder != 0) {
					// TODO: shenanigans
					inWayVal -= remainder;
					inWay.setValue(remainder);
					waysOut.set(1, inWay); // this entry in the list acts as a signal to perform shenanigans
				}
				long outVal = inWayVal / focusWay;
				waysOut.set(0, new WayStack(outVal, (int)focusWay));
				return new LabRecipeData(input.items, null, waysOut, null, null);
			}
		}
		return null;
	}),

	// item, fluid, way, shape, form
	CONDENSATION(/* true, false, false, false, false, */ (input) -> {
		System.out.println("NYI: Condensation"); // TODO Condensation
		return null;
	}),
	
	

	// item, fluid, way, shape, form
	PROJECTION(/* true, false, false, false, false, */ (input) -> {
		System.out.println("NYI: Projection"); // TODO Projection
		return null;
	}),

	/**
	 * Due to how things are implemented, DISTILLATION acts abnormally <br>
	 * It uses the way input as a flag for which mode to use: shape if == null, form if != null <br>
	 * This does not apply to Aetherglass flasks for obvious reasons
	 */
	DISTILLATION((input) -> {
		if (hasItemStacks(input.items)) {
			ItemStack item = input.items.get(0);
			if (item.getItem() instanceof StorageFlaskItem sFlask && sFlask.canExtract(item)) {
				AspectForm form = sFlask.getStoredForm(item);
				LabRecipeData out = null;
				NonNullList<ItemStack> itemsOut = il(1);
				itemsOut.set(0, new ItemStack(Items.FLASK_AETHER.get()));
				if (form != null) {
					NonNullList<FormStack> formsOut = fl(1);
					formsOut.set(0, new FormStack(form));
					return new LabRecipeData(itemsOut, null, null, null, formsOut);
				}
				AspectShape shape = sFlask.getStoredShape(item);
				if (shape != null) {
					NonNullList<ShapeStack> shapesOut = sl(1);
					shapesOut.set(0, new ShapeStack(shape));
					return new LabRecipeData(itemsOut, null, null, shapesOut, null);
				}
			} else if (item.getItem() instanceof FlaskItem flask && flask.canExtract(item)) {
				if (input.ways == null) {
					AspectShape shape = flask.getStoredShape(item);
					if (shape != null) {
						ItemStack badFlask = item.copy();
						flask.setContaminated(badFlask, true);
						NonNullList<ItemStack> itemsOut = il(1);
						NonNullList<ShapeStack> shapesOut = sl(1);
						itemsOut.set(0, badFlask);
						shapesOut.set(0, new ShapeStack(shape));
						return new LabRecipeData(itemsOut, null, null, shapesOut, null);
					}
				} else if (input.ways != null) {
					AspectForm form = flask.getStoredForm(item);
					if (form != null) {
						ItemStack badFlask = item.copy();
						flask.setContaminated(badFlask, true);
						NonNullList<ItemStack> itemsOut = il(1);
						NonNullList<FormStack> formsOut = fl(1);
						itemsOut.set(0, badFlask);
						formsOut.set(0, new FormStack(form));
						return new LabRecipeData(itemsOut, null, null, null, formsOut);
					}
				}
			}
		}
		return null;
	}),

	// item, fluid, way, shape, form
	COHOBATION(/* /* true, false, false, false, false, */ (input) -> {
		if (hasItemStacks(input.items)) {
			ItemStack item = input.items.get(0);
			if (item.is(Items.FLASK_GOLD.get()) && item.getItem() instanceof FlaskItem flask && flask.canExtract(item)) {
				AspectShape shape = flask.getStoredShape(item);
				AspectForm form = flask.getStoredForm(item);
				if (shape != null && form != null) {
					ItemStack badFlask = item.copy();
					flask.clearStored(badFlask);
					//((FlaskItem)badFlask.getItem()).setContaminated(badFlask, true);
					NonNullList<ItemStack> itemsOut = il(1);
					NonNullList<ShapeStack> shapesOut = sl(1);
					NonNullList<FormStack> formsOut = fl(1);
					itemsOut.set(0, badFlask);
					shapesOut.set(0, new ShapeStack(shape));
					formsOut.set(0, new FormStack(form));
					return new LabRecipeData(itemsOut, null, null, shapesOut, formsOut);
				}
			}
		}
		return null;
	}),

	/**
	 * Due to how things are implemented, SOLUTION acts abnormally <br>
	 * It parses the way input as the current game time in ticks, using it to set a proper expiration date
	 */
	// item, fluid, way, shape, form
	SOLUTION(/* true, false, false, false, false, */ (input) -> {
		if (hasItemStacks(input.items) && hasFluidStacks(input.fluids) && hasAspectStacks(input.ways) && hasAspectStacks(input.shapes) && hasAspectStacks(input.forms)) {
			long currentTime = input.ways.get(0).getValue();
			FluidStack inFluid = input.fluids.get(0);
			if (inFluid.getFluid().isSame(Fluids.WATER) && inFluid.getAmount() == 1000) {
				ItemStack inItem = input.items.get(0);
				ItemStack inItem2 = null;
				boolean aether = false;
				boolean invalid = false;
				boolean multi = input.items.size() == 2;
				if (multi) {
					inItem2 = input.items.get(1);
				}
				if (inItem.getItem() instanceof StorageFlaskItem) {
					if (multi && inItem2.getItem() instanceof StorageFlaskItem sf) {
						boolean bad1 = sf.hasStored(inItem) || sf.isExpired(inItem, currentTime),
								bad2 = sf.hasStored(inItem2) || sf.isExpired(inItem2, currentTime);
						aether = !bad1 && !bad2;
					}
					invalid = !aether; // aetherglass must be put in 2 at a time
				} else if (inItem.getItem() instanceof FlaskItem flask) {
					invalid = flask.hasStored(inItem) || flask.isExpired(inItem, currentTime);
				}

				// we did flask-checking earlier, so we can get right to output processing
				if (!invalid) {
					NonNullList<ItemStack> itemsOut = il(2);
					ItemStack f1 = inItem.copy(), f2 = multi ? inItem2.copy() : null;
					if (aether) {
						StorageFlaskItem sFlask = ((StorageFlaskItem)inItem.getItem());
						sFlask.setStored(f1, input.shapes.get(0).getAspect(), null, currentTime);
						itemsOut.set(0, f1);
						
						sFlask.setStored(f2, null, input.forms.get(0).getAspect(), currentTime);
						itemsOut.set(1, f2);
					} else {
						FlaskItem flask = ((FlaskItem)inItem.getItem());
						flask.setStored(f1, input.shapes.get(0).getAspect(), input.forms.get(0).getAspect(), currentTime);
						itemsOut.set(0, f1);
					}
					return new LabRecipeData(itemsOut, null, null, null, null);
				}
			}
		}
		return null;
	}),

	// item, fluid, way, shape, form
	DIGESTION(/*true, false, false, false, false,*/ (input) -> {
		// TODO: this recipe should be sped up by higher way values. figure out a way to do this
		if (hasAspectStacks(input.ways) && hasAspectStacks(input.shapes) && hasAspectStacks(input.forms)) {
			NonNullList<ItemStack> itemsOut = il(1);
			itemsOut.set(0, new ItemStack(Items.AETHER.get()));
			return new LabRecipeData(itemsOut, null, null, null, null);
		}
		return null;
	}),

	// item, fluid, way, shape, form
	MULTIPLICATION(/*true, false, false, false, false,*/ (input) -> {
		System.out.println("NYI: Multiplication"); // TODO Multiplication
		return null;
	});
	
	private LabProcess(/*boolean i, boolean fl, boolean w, boolean s, boolean f,*/ LabFunction func) {
		//this.usesItem = i;
		//this.usesFluid = fl;
		//this.usesWay = w;
		//this.usesShape = s;
		//this.usesForm = f;
		
		this.func = func;
	}
	//public final boolean usesItem, usesFluid, usesWay, usesShape, usesForm;
	private final LabFunction func;
	
	@Nullable
	public LabRecipeData on(@NotNull LabRecipeData input) {
		LabRecipeData output = func.apply(input);
		return output;
	}
	
	public LabFunction getFunc() {
		return func;
	}

	@Override
	public String getSerializedName() {
		return this.toString().toLowerCase();
	}
}
