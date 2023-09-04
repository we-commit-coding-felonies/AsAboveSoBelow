package com.quartzshard.aasb.api.alchemy.lab;

import java.util.ArrayList;

import org.jetbrains.annotations.Nullable;

import com.quartzshard.aasb.api.alchemy.aspects.*;
import com.quartzshard.aasb.api.alchemy.aspects.stack.legacy.*;
import com.quartzshard.aasb.common.item.flask.*;
import com.quartzshard.aasb.init.AlchemyInit.FormTree;
import com.quartzshard.aasb.init.ObjectInit;
import com.quartzshard.aasb.init.ObjectInit.Items;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluids;

import net.minecraftforge.fluids.FluidStack;

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
			ArrayList<LegacyWayStack> waysOut = new ArrayList<>();
			waysOut.add(new LegacyWayStack(itemWay));
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
				ArrayList<LegacyWayStack> waysOut = new ArrayList<>();
				waysOut.add(new LegacyWayStack(a.getValue() + b.getValue()));
				return new LabRecipeData(null, null, waysOut, null, null);
			}
		}
		return null;
	}
	
	/** removes exactly 3 way from the inputs total */
	@Nullable
	public static LabRecipeData stagnation(LabRecipeData input) {
		if (hasStacks(input.ways) && input.ways.get(0).getAmount() > 3) {
			ArrayList<LegacyWayStack> waysOut = new ArrayList<>();
			LegacyWayStack modStack = input.ways.get(0);
			modStack.setAmount(modStack.getAmount()-3);
			waysOut.add(modStack);
			return new LabRecipeData(null, null, waysOut, null, null);
		}
		return null;
	}

	/** divides an even waystack in half */
	@Nullable
	public static LabRecipeData separation(LabRecipeData input) {
		if (hasStacks(input.ways) && input.ways.get(0).getAmount() % 2 == 0) {
			ArrayList<LegacyWayStack> waysOut = new ArrayList<>();
			LegacyWayStack a = input.ways.get(0);
			long newVal = a.getAmount()/2;
			a.setAmount(newVal);
			LegacyWayStack b = new LegacyWayStack(newVal);
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
			LegacyWayStack inWay = input.ways.get(0);
			long inWayVal = inWay.getAmount();
			long focusWay = 11; // TODO: make this actually get the items way value, waiting on shard to finish the mapper
			if (inWayVal > focusWay) {
				long remainder = inWay.getAmount() % focusWay;
				ArrayList<LegacyWayStack> waysOut = new ArrayList<>();
				if (remainder != 0) {
					// TODO: shenanigans
					inWayVal -= remainder;
					inWay.setAmount(remainder);
					waysOut.add(inWay); // this entry in the list acts as a signal to perform shenanigans
				}
				long numOutWays = inWayVal / focusWay;
				for (int i = 0; i < numOutWays; i++)
					waysOut.add(new LegacyWayStack(focusWay));
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
	
	/** extracts shape from an item */
	@Nullable
	public static LabRecipeData desiccation(LabRecipeData input) {
		if (hasStacks(input.items)) {
			AspectShape itemShape = AspectShape.FIRE; // TODO: implement once mapper is finished
			ArrayList<ItemStack> itemsOut = new ArrayList<>();
			itemsOut.add(new ItemStack(ObjectInit.Items.SALT.get()));
			ArrayList<LegacyShapeStack> shapesOut = new ArrayList<>();
			shapesOut.add(new LegacyShapeStack(itemShape));
			return new LabRecipeData(itemsOut, null, null, shapesOut, null);
		}
		return null;
	}

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
					ArrayList<LegacyShapeStack> shapesOut = new ArrayList<>();
					itemsOut.add(badFlask);
					shapesOut.add(new LegacyShapeStack(shape));
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
			LegacyShapeStack inShape = input.shapes.get(0);
			if (inShape.isValid() && inShape.getShape() == AspectShape.AIR) {
				ArrayList<LegacyShapeStack> shapesOut = new ArrayList<>();
				shapesOut.add(new LegacyShapeStack(AspectShape.FIRE));
				return new LabRecipeData(null, null, null, shapesOut, null);
			}
		}
		return null;
	}

	/** turns fire into earth */
	@Nullable
	public static LabRecipeData congelation(LabRecipeData input) {
		if (hasStacks(input.shapes)) {
			LegacyShapeStack inShape = input.shapes.get(0);
			if (inShape.isValid() && inShape.getShape() == AspectShape.FIRE) {
				ArrayList<LegacyShapeStack> shapesOut = new ArrayList<>();
				shapesOut.add(new LegacyShapeStack(AspectShape.EARTH));
				return new LabRecipeData(null, null, null, shapesOut, null);
			}
		}
		return null;
	}

	/** turns earth into water */
	@Nullable
	public static LabRecipeData ceration(LabRecipeData input) {
		if (hasStacks(input.shapes)) {
			LegacyShapeStack inShape = input.shapes.get(0);
			if (inShape.isValid() && inShape.getShape() == AspectShape.EARTH) {
				ArrayList<LegacyShapeStack> shapesOut = new ArrayList<>();
				shapesOut.add(new LegacyShapeStack(AspectShape.WATER));
				return new LabRecipeData(null, null, null, shapesOut, null);
			}
		}
		return null;
	}
	
	/** turns water into air */
	@Nullable
	public static LabRecipeData dehydration(LabRecipeData input) {
		if (hasStacks(input.shapes)) {
			LegacyShapeStack inShape = input.shapes.get(0);
			if (inShape.isValid() && inShape.getShape() == AspectShape.WATER) {
				ArrayList<LegacyShapeStack> shapesOut = new ArrayList<>();
				shapesOut.add(new LegacyShapeStack(AspectShape.AIR));
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
			for (LegacyShapeStack shape : input.shapes) {
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
				ArrayList<LegacyShapeStack> shapesOut = new ArrayList<>();
				shapesOut.add(new LegacyShapeStack(AspectShape.UNIVERSAL));
				return new LabRecipeData(null, null, null, shapesOut, null);
			}
		}
		return null;
	}
	
	/** takes 1 quintessence and turns it into 4 of any single basic shape, as decided by the focus */
	@Nullable
	public static LabRecipeData condemnation(LabRecipeData input) {
		if (hasStacks(input.shapes) && hasStacks(input.items)) {
			LegacyShapeStack inShape = input.shapes.get(0);
			AspectShape focusShape = AspectShape.AIR; // TODO: implement this properly with the mapper
			if (inShape.isValid() && inShape.getShape() == AspectShape.UNIVERSAL) {
				ArrayList<LegacyShapeStack> shapesOut = new ArrayList<>();
				shapesOut.add(new LegacyShapeStack(focusShape, 4));
				ArrayList<ItemStack> itemsOut = new ArrayList<>();
				itemsOut.add(input.items.get(0));
				return new LabRecipeData(itemsOut, null, null, shapesOut, null);
			}
		}
		return null;
	}
	
	// Form recipes
	
	/** extracts shape from an item */
	@Nullable
	public static LabRecipeData evaporation(LabRecipeData input) {
		if (hasStacks(input.items)) {
			AspectForm itemForm = FormTree.WITCHCRAFT.get(); // TODO: implement once mapper is finished
			ArrayList<ItemStack> itemsOut = new ArrayList<>();
			itemsOut.add(new ItemStack(ObjectInit.Items.SOOT.get()));
			ArrayList<LegacyFormStack> formsOut = new ArrayList<>();
			formsOut.add(new LegacyFormStack(itemForm));
			return new LabRecipeData(itemsOut, null, null, null, formsOut);
		}
		return null;
	}

	/** extracts form from a flask */
	@Nullable
	public static LabRecipeData formDistillation(LabRecipeData input) {
		if (hasStacks(input.items) && !hasStacks(input.forms)) {
			ItemStack item = input.items.get(0);
			if (item.getItem() instanceof FlaskItem flask && flask.hasStoredForm(item) && flask.canExtract(item)) {
				AspectForm form = flask.getStoredForm(item);
				if (form != null) {
					ItemStack badFlask = item.copy();
					if (badFlask.getItem() instanceof StorageFlaskItem sf) {
						sf.clearStored(badFlask);
					} else {
						flask.setContaminated(badFlask, true);
					}
					ArrayList<ItemStack> itemsOut = new ArrayList<>();
					ArrayList<LegacyFormStack> formsOut = new ArrayList<>();
					itemsOut.add(badFlask);
					formsOut.add(new LegacyFormStack(form));
					return new LabRecipeData(itemsOut, null, null, null, formsOut);
				}
			}
		}
		return null;
	}

	/** turns any non-leaf form into any leaf form */
	@Nullable
	public static LabRecipeData fixation(LabRecipeData input) {
		if (hasStacks(input.forms) && hasStacks(input.items)) {
			AspectForm inForm = input.forms.get(0).getForm();
			if (inForm.getChildren().length > 0) {
				ItemStack focus = input.items.get(0);
				AspectForm focusForm = FormTree.MARS.get();
				if (focusForm.getChildren().length <= 0) {
					ArrayList<ItemStack> itemsOut = new ArrayList<>();
					itemsOut.add(focus);
					ArrayList<LegacyFormStack> formsOut = new ArrayList<>();
					formsOut.add(new LegacyFormStack(focusForm));
					return new LabRecipeData(itemsOut, null, null, null, formsOut);
				}
			}
		}
		return null;
	}

	/** turns a form into another with the same parent */
	@Nullable
	public static LabRecipeData amalgamation(LabRecipeData input) {
		if (hasStacks(input.forms) && hasStacks(input.items)) {
			AspectForm inForm = input.forms.get(0).getForm();
			ItemStack focus = input.items.get(0);
			AspectForm focusForm = FormTree.SUN.get();
			if (inForm != focusForm) {
				AspectForm inPar = inForm.getParent(),
						focusPar = focusForm.getParent();
				if (inPar != null && inPar == focusPar) {
					ArrayList<ItemStack> itemsOut = new ArrayList<>();
					itemsOut.add(focus);
					ArrayList<LegacyFormStack> formsOut = new ArrayList<>();
					formsOut.add(new LegacyFormStack(focusForm));
					return new LabRecipeData(itemsOut, null, null, null, formsOut);
				}
			}
		}
		return null;
	}

	/** turns a form into its parent, at the cost of a universal */
	@Nullable
	public static LabRecipeData homogenization(LabRecipeData input) {
		if (hasStacks(input.forms)) {
			AspectForm inForm = input.forms.get(0).getForm(), inPar = inForm.getParent();
			if (inPar != null) {
				boolean canAfford = false;
				boolean eatForm = false;
				
				boolean hasShapes = hasStacks(input.shapes);
				boolean multiForms = input.forms.size() == 2;
				if (hasShapes) {
					canAfford = input.shapes.get(0).getShape() == AspectShape.UNIVERSAL;
				}
				if (!canAfford && multiForms) {
					canAfford = input.forms.get(1).getForm().getParent() == null;
					eatForm = canAfford;
				}
				
				if (canAfford) {
					ArrayList<LegacyFormStack> formsOut = new ArrayList<>();
					ArrayList<LegacyShapeStack> shapesOut = null;
					formsOut.add(new LegacyFormStack(inPar));
					if (eatForm && hasShapes) {
						shapesOut = new ArrayList<>();
						shapesOut.add(input.shapes.get(0));
					} else if (!eatForm && multiForms) {
						formsOut.add(input.forms.get(1));
					}
					return new LabRecipeData(null, null, null, shapesOut, formsOut);
				}
			}
		}
		return null;
	}
	
	// Misc
	
	/** extracts shape & form from a gold flask */
	@Nullable
	public static LabRecipeData cohobation(LabRecipeData input) {
		if ( hasStacks(input.items) && !(hasStacks(input.shapes) || hasStacks(input.forms)) ) {
			ItemStack item = input.items.get(0);
			if (item.is(Items.FLASK_GOLD.get()) && item.getItem() instanceof FlaskItem flask && flask.canExtract(item)) {
				AspectShape shape = flask.getStoredShape(item);
				AspectForm form = flask.getStoredForm(item);
				if (shape != null && form != null) {
					ItemStack badFlask = item.copy();
					flask.clearStored(badFlask);
					//((FlaskItem)badFlask.getItem()).setContaminated(badFlask, true);
					ArrayList<ItemStack> itemsOut = new ArrayList<>();
					ArrayList<LegacyShapeStack> shapesOut = new ArrayList<>();
					ArrayList<LegacyFormStack> formsOut = new ArrayList<>();
					itemsOut.add(badFlask);
					shapesOut.add(new LegacyShapeStack(shape));
					formsOut.add(new LegacyFormStack(form));
					return new LabRecipeData(itemsOut, null, null, shapesOut, formsOut);
				}
			}
		}
		return null;
	}
	
	/** special transmutation */
	@Nullable
	public static LabRecipeData projection(LabRecipeData input) {
		// TODO: implement
		return null;
	}
	
	/** fills flasks using alkahest <br><br>
	 * 	NOTE: sets flasks expiration date to -1, which tells the lab to change it once it finishes the recipe <br>
	 *  this is done because the lab function cannot know the world time, and thus cannot set an accurate expiration date
	 */
	@Nullable
	public static LabRecipeData solution(LabRecipeData input) {
		// TODO: currently uses water, implement alkahest
		if (hasStacks(input.items) && hasStacks(input.fluids) && hasStacks(input.shapes) && hasStacks(input.forms)) {
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
						boolean bad1 = sf.hasStored(inItem) || sf.isContaminated(inItem),
								bad2 = sf.hasStored(inItem2) || sf.isContaminated(inItem2);
						aether = !bad1 && !bad2;
					}
					invalid = !aether; // aetherglass must be put in 2 at a time
				} else if (inItem.getItem() instanceof FlaskItem flask) {
					invalid = flask.hasStored(inItem) || flask.isContaminated(inItem);
				}

				// we did flask-checking earlier, so we can get right to output processing
				if (!invalid) {
					ArrayList<ItemStack> itemsOut = new ArrayList<>();
					ItemStack f1, f2;
					if (aether) {
						StorageFlaskItem sFlask = ((StorageFlaskItem)inItem.getItem());
						sFlask.setStored(inItem, input.shapes.get(0).getShape(), null, 0);
						sFlask.setExpiry(inItem, -1);
						itemsOut.add(inItem);
						
						sFlask.setStored(inItem2, null, input.forms.get(0).getForm(), 0);
						sFlask.setExpiry(inItem2, -1);
						itemsOut.add(inItem2);
					} else {
						FlaskItem flask = ((FlaskItem)inItem.getItem());
						flask.setStored(inItem, input.shapes.get(0).getShape(), input.forms.get(0).getForm(), 0);
						flask.setExpiry(inItem, -1);
						itemsOut.add(inItem);
						if (multi) {
							// prevents voiding extra flasks
							itemsOut.add(inItem2);
						}
					}
					return new LabRecipeData(itemsOut, null, null, null, null);
				}
			}
		}
		return null;
	}
	
	/** turns a way, a shape, and a form into 1 aether */
	@Nullable
	public static LabRecipeData digestion(LabRecipeData input) {
		// TODO: this recipe should be sped up by higher way values. figure out a way to do this
		if (hasStacks(input.ways) && hasStacks(input.shapes) && hasStacks(input.forms)) {
			ArrayList<ItemStack> itemsOut = new ArrayList<>();
			itemsOut.add(new ItemStack(Items.AETHER.get()));
			return new LabRecipeData(itemsOut, null, null, null, null);
		}
		return null;
	}
	
	/** clones an item using 2x its raw aspects */
	@Nullable
	public static LabRecipeData multiplication(LabRecipeData input) {
		// TODO: implement, requires the mapper
		return null;
	}
	
}
