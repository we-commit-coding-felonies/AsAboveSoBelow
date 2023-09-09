package com.quartzshard.aasb.common.block.lab.te;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import com.quartzshard.aasb.api.alchemy.lab.LabRecipeData;
import com.quartzshard.aasb.api.misc.SemiNullableFunctions.MixedNullableReturnFunction;

/**
 * Base for all the lab blocks in the mod, contains code common to all of them <br>
 * For more specialized classes to extend, see the templates section
 */
public abstract class LabTE extends BlockEntity {
	/** the default maximum for aspect chamber size (stack limit) */
	public static final int CHAMBERSIZE = 12;
	public static final int WORK_IDLE = -1; // Timer value signifying idle (waiting for input)
	public static final int WORK_DONE = 0; // Timer value signifying finished (trying to create output)
	public LabTE(BlockEntityType<? extends LabTE> type,
			BlockPos pos, BlockState state,
			int workTime,
			MixedNullableReturnFunction<LabRecipeData, LabRecipeData> process) {
		super(type, pos, state);
		this.workTime = workTime;
		this.process = process;
	}
	
	public final MixedNullableReturnFunction<LabRecipeData, LabRecipeData> process; // The recipe logic
	public final int workTime; // Amount of ticks for a recipe to finish
	private int work = -1; // The internal timer used for crafting
	private boolean stalled = false; // flag for if the TE is trying to finish a recipe, but cant
	
	@Override
	public void setChanged() {
		super.setChanged();
		MixedNullableReturnFunction<LabRecipeData, LabRecipeData> fn = (in) -> {
			return in;
		};
		if (work >= 0 && !validateRecipe()) {
			work = -1;
		}
	}
	
	/**
	 * Validates that the TE's current inventory is a proper recipe
	 * @return
	 */
	public boolean validateRecipe() {
		return process.apply(packInputs()) != null;
	}
	
	/**
	 * Actually does the crafting operation, consuming inputs and creating outputs
	 */
	protected void finalizeCrafting() {
		LabRecipeData dat = process.apply(packInputs());
		if (dat != null) {
			if (unpackOutputs(dat)) {
				this.setChanged();
			}
		}
	}
	
	/**
	 * Creates a LabRecipeData for sending to the recipe function <br>
	 * The output of this function will also be used to define what items get consumed
	 * @apiNote this should not be linked to the TE's inventory, please make clones of stacks
	 * @return the LabRecipeData
	 */
	protected abstract LabRecipeData packInputs();
	
	/**
	 * Takes the LabRecipeData and updates the TE's inventory accordingly
	 * @return true if the unpacking was successful 
	 */
	protected abstract boolean unpackOutputs(LabRecipeData dat);
	
	/**
	 * Is run server-side every tick
	 */
	public void tickServer() {
	}
}
