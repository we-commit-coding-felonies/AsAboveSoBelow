package com.quartzshard.aasb.api.alchemy.lab;

import java.util.ArrayList;

import org.jetbrains.annotations.Nullable;

import com.quartzshard.aasb.api.alchemy.aspects.*;
import com.quartzshard.aasb.api.alchemy.aspects.stack.*;
import com.quartzshard.aasb.common.item.flask.*;
import com.quartzshard.aasb.init.ObjectInit;

import net.minecraft.world.item.ItemStack;

/**
 * functions related to the lab
 * the process functionsh here will return null if the recipe is invalid
 */
public class LabFunctions {	
	public static boolean hasStacks(ArrayList<?> input) {
		return input != null && !input.isEmpty();
	}
	
	//////////////////////
	// RECIPE FUNCTIONS //
	//////////////////////
	
	// Way recipes
	
	/** extracts way from an item */
	@Nullable
	public static LabRecipeData sublimation(LabRecipeData input) {
		if (hasStacks(input.items)) {
			long itemWay = 11; // TODO: implement once mapper is finished
			ArrayList<ItemStack> itemsOut = new ArrayList<>();
			itemsOut.add(new ItemStack(ObjectInit.Items.SUBLIT.get()));
			ArrayList<WayStack> waysOut = new ArrayList<>();
			waysOut.add(new WayStack(itemWay));
			return new LabRecipeData(itemsOut, null, waysOut, null, null);
		}
		return null;
	}
	
	/** combines 2 ways that are within flow distance */
	@Nullable
	public static LabRecipeData conjunction(LabRecipeData input) {
		if (hasStacks(input.ways) && input.ways.size() == 2) {
			AspectWay a = new AspectWay(input.ways.get(0).getAmount()),
					b = new AspectWay(input.ways.get(1).getAmount());
			if (a.flows(b)) {
				ArrayList<WayStack> waysOut = new ArrayList<>();
				waysOut.add(new WayStack(a.getValue() + b.getValue()));
				return new LabRecipeData(null, null, waysOut, null, null);
			}
		}
		return null;
	}
	
	/** removes exactly 3 way from the inputs total */
	@Nullable
	public static LabRecipeData stagnation(LabRecipeData input) {
		if (hasStacks(input.ways) && input.ways.get(0).getAmount() > 3) {
			ArrayList<WayStack> waysOut = new ArrayList<>();
			WayStack modStack = input.ways.get(0);
			modStack.setAmount(modStack.getAmount()-3);
			waysOut.add(modStack);
			return new LabRecipeData(null, null, waysOut, null, null);
		}
		return null;
	}

	/** divides a waystack in half */
	@Nullable
	public static LabRecipeData separation(LabRecipeData input) {
		if (hasStacks(input.ways) && input.ways.get(0).getAmount() % 2 == 0) {
			ArrayList<WayStack> waysOut = new ArrayList<>();
			WayStack a = input.ways.get(0);
			long newVal = a.getAmount()/2;
			a.setAmount(newVal);
			WayStack b = new WayStack(newVal);
			waysOut.add(a);
			waysOut.add(b);
			return new LabRecipeData(null, null, waysOut, null, null);
		}
		return null;
	}

	/** divides a waystack into an arbitrary number of smaller waystacks, each the same size as the focus's way value. voids remainder */
	@Nullable
	public static LabRecipeData filtration(LabRecipeData input) {
		if (hasStacks(input.ways) && hasStacks(input.items)) {
			ItemStack focusItem = input.items.get(0);
			WayStack inWay = input.ways.get(0);
			long inWayVal = inWay.getAmount();
			long focusWay = 11; // TODO: make this actually get the items way value, waiting on shard to finish the mapper
			if (inWayVal > focusWay) {
				long remainder = inWay.getAmount() % focusWay;
				if (remainder != 0) {
					// TODO: shenanigans
					inWayVal -= remainder;
				}
				long numOutWays = inWayVal / focusWay;
				ArrayList<WayStack> waysOut = new ArrayList<>();
				inWay.setAmount(remainder);
				waysOut.add(inWay); // this entry in the list acts as a signal to perform shenanigans
				for (int i = 0; i < numOutWays; i++)
					waysOut.add(new WayStack(focusWay));
				return new LabRecipeData(input.items, null, waysOut, null, null);
			}
		}
		return null;
	}
	
	/** stores an entire waystack into a klein star. if the klein star cant hold it all, excess is voided */
	@Nullable
	public static LabRecipeData condensation(LabRecipeData input) {
		// TODO: implement klein stars, and also this function
		return null;
	}
	
	// Shape recipes

	/** extracts the shape from a flask. lead & gold flasks are dirtied, but aetherglass is not */
	@Nullable
	public static LabRecipeData shapeDistillation(LabRecipeData input) {
		if (hasStacks(input.items)) {
			ItemStack item = input.items.get(0);
			if (item.getItem() instanceof FlaskItem flask && flask.hasStoredShape(item) && flask.canExtract(item)) {
				AspectShape shape = flask.getStoredShape(item);
				if (shape != null) {
					ItemStack badFlask = item.copy();
					if (badFlask.getItem() instanceof StorageFlaskItem sf) {
						sf.clearStored(badFlask);
					} else {
						flask.setContaminated(badFlask, true);
					}
					ArrayList<ItemStack> itemsOut = new ArrayList<>();
					ArrayList<ShapeStack> shapesOut = new ArrayList<>();
					itemsOut.add(badFlask);
					shapesOut.add(new ShapeStack(shape));
					return new LabRecipeData(itemsOut, null, null, shapesOut, null);
				}
			}
		}
		return null;
	}

	/** turns air into fire */
	@Nullable
	public static LabRecipeData oxidation(LabRecipeData input) {
		if (hasStacks(input.shapes)) {
			ShapeStack inShape = input.shapes.get(0);
			if (inShape.isValid() && inShape.getShape() == AspectShape.AIR) {
				ArrayList<ShapeStack> shapesOut = new ArrayList<>();
				shapesOut.add(new ShapeStack(AspectShape.FIRE));
				return new LabRecipeData(null, null, null, shapesOut, null);
			}
		}
		return null;
	}

	/** turns fire into earth */
	@Nullable
	public static LabRecipeData congelation(LabRecipeData input) {
		if (hasStacks(input.shapes)) {
			ShapeStack inShape = input.shapes.get(0);
			if (inShape.isValid() && inShape.getShape() == AspectShape.FIRE) {
				ArrayList<ShapeStack> shapesOut = new ArrayList<>();
				shapesOut.add(new ShapeStack(AspectShape.EARTH));
				return new LabRecipeData(null, null, null, shapesOut, null);
			}
		}
		return null;
	}

	/** turns earth into water */
	@Nullable
	public static LabRecipeData ceration(LabRecipeData input) {
		if (hasStacks(input.shapes)) {
			ShapeStack inShape = input.shapes.get(0);
			if (inShape.isValid() && inShape.getShape() == AspectShape.EARTH) {
				ArrayList<ShapeStack> shapesOut = new ArrayList<>();
				shapesOut.add(new ShapeStack(AspectShape.WATER));
				return new LabRecipeData(null, null, null, shapesOut, null);
			}
		}
		return null;
	}
	
	/** turns water into air */
	@Nullable
	public static LabRecipeData dehydration(LabRecipeData input) {
		if (hasStacks(input.shapes)) {
			ShapeStack inShape = input.shapes.get(0);
			if (inShape.isValid() && inShape.getShape() == AspectShape.WATER) {
				ArrayList<ShapeStack> shapesOut = new ArrayList<>();
				shapesOut.add(new ShapeStack(AspectShape.AIR));
				return new LabRecipeData(null, null, null, shapesOut, null);
			}
		}
		return null;
	}
	
	/** turns 1 of each of the 4 normal shapes into 1 quintessence shape */
	@Nullable
	public static LabRecipeData exaltation(LabRecipeData input) {
		if (hasStacks(input.shapes) && input.shapes.size() == 4) {
			boolean w,e,f,a;
			w = e = f = a = false;
			for (ShapeStack shape : input.shapes) {
				switch (shape.getShape()) {
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
				ArrayList<ShapeStack> shapesOut = new ArrayList<>();
				shapesOut.add(new ShapeStack(AspectShape.UNIVERSAL));
				return new LabRecipeData(null, null, null, shapesOut, null);
			}
		}
		return null;
	}
	
	/** takes 1 quintessence and turns it into 4 of any single basic shape, as decided by the focus */
	@Nullable
	public static LabRecipeData condemnation(LabRecipeData input) {
		if (hasStacks(input.shapes) && hasStacks(input.items)) {
			ShapeStack inShape = input.shapes.get(0);
			AspectShape focusShape = AspectShape.AIR; // TODO: implement this properly with the mapper
			if (inShape.isValid() && inShape.getShape() == AspectShape.UNIVERSAL) {
				ArrayList<ShapeStack> shapesOut = new ArrayList<>();
				shapesOut.add(new ShapeStack(focusShape, 4));
				ArrayList<ItemStack> itemsOut = new ArrayList<>();
				itemsOut.add(input.items.get(0));
				return new LabRecipeData(itemsOut, null, null, shapesOut, null);
			}
		}
		return null;
	}
	
	// Form recipes
	
	@Nullable
	public static LabRecipeData formDistillation(LabRecipeData input) {
		if (hasStacks(input.items) && !hasStacks(input.forms)) {
			ItemStack item = input.items.get(0);
			if (item.getItem() instanceof FlaskItem flask && flask.hasStoredForm(item) && flask.canExtract(item)) {
				AspectForm form = flask.getStoredForm(item);
				if (form != null) {
					ItemStack badFlask = item.copy();
					((FlaskItem)badFlask.getItem()).setContaminated(badFlask, true);
					ArrayList<ItemStack> itemsOut = new ArrayList<>();
					ArrayList<FormStack> formsOut = new ArrayList<>();
					itemsOut.add(badFlask);
					formsOut.add(new FormStack(form));
					return new LabRecipeData(itemsOut, null, null, null, formsOut);
				}
			}
		}
		return null;
	}
	
	
	
}
