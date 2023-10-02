package com.quartzshard.aasb.common.block.lab.te;

import java.util.LinkedHashMap;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.registries.RegistryObject;

import com.quartzshard.aasb.api.alchemy.aspects.stack.FormStack;
import com.quartzshard.aasb.api.alchemy.aspects.stack.ShapeStack;
import com.quartzshard.aasb.api.alchemy.aspects.stack.WayStack;
import com.quartzshard.aasb.api.alchemy.aspects.stack.chamber.IAspectChamber.AspectAction;
import com.quartzshard.aasb.api.alchemy.lab.LabFunction;
import com.quartzshard.aasb.api.alchemy.lab.LabProcess;
import com.quartzshard.aasb.api.alchemy.lab.LabRecipeData;
import com.quartzshard.aasb.api.capability.AASBCapabilities;
import com.quartzshard.aasb.api.capability.aspect.form.IHandleForm;
import com.quartzshard.aasb.api.capability.aspect.shape.IHandleShape;
import com.quartzshard.aasb.api.capability.aspect.way.IHandleWay;
import com.quartzshard.aasb.api.misc.Thing;
import com.quartzshard.aasb.common.block.lab.LabBlock;
import com.quartzshard.aasb.common.block.lab.te.debug.LabDebugEndTE;
import com.quartzshard.aasb.common.block.lab.te.starters.DistillationTE;
import com.quartzshard.aasb.util.WorldHelper.Side;

/**
 * Base for all the lab blocks in the mod, contains code common to all of them <br>
 * For more specialized classes to extend, see the templates section
 */
public abstract class LabTE extends BlockEntity {
	public enum PushType {
		ITEM, FLUID, WAY, SHAPE, FORM;
		
		public Class<?> getClazz() {
			switch (this) {
			case ITEM: return ItemStack.class;
			case FLUID: return FluidStack.class;
			case WAY: return WayStack.class;
			case SHAPE: return ShapeStack.class;
			case FORM: return FormStack.class;
			}
			return null;
		}
		
		public Capability<?> getCap() {
			switch (this) {
			case ITEM: return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;
			case FLUID: return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY;
			case WAY: return AASBCapabilities.WAY_HANDLER;
			case SHAPE: return AASBCapabilities.SHAPE_HANDLER;
			case FORM: return AASBCapabilities.FORM_HANDLER;
			}
			return null;
		}
	}
	/**
	 * represents discreet states for a lab
	 * <p>
	 * IDLE means not currently doing anything (awaiting input) <br>
	 * ACTIVE means a recipe is in progress (ticking the timer) <br>
	 * STALLED means a recipe is in progress, but the timer has been paused (timer paused) <br>
	 * FINISHED means recipe is finished, and is currently trying to consume inputs and create outputs (attempting output) <br>
	 * <p>
	 * This is not actually stored anywhere, it only exists to make things a bit more readable
	 */
	public enum LabState implements StringRepresentable {
		IDLE, ACTIVE, STALLED, FINISHED;

		@Override
		public String getSerializedName() {
			return this.toString().toLowerCase();
		}
		
		public boolean idle() {
			return this == IDLE;
		}
		
		public boolean active() {
			return this == ACTIVE;
		}
		
		public boolean stalled() {
			return this == STALLED;
		}
		
		public boolean finished() {
			return this == FINISHED;
		}
	}
	
	
	
	public static final String
		TK_DATA = "LabData",
			TK_TE = "LabTE",
				TK_WORK = "WorkLeft",
				TK_CACHE = "RecipeCache";
	public LabTE(BlockEntityType<? extends LabTE> type, BlockPos pos, BlockState state,
			int workTime, LabProcess process) {
		this(type, pos, state, workTime, process.getFunc());
	}
	public LabTE(BlockEntityType<? extends LabTE> type, BlockPos pos, BlockState state,
			int workTime, LabFunction func) {
		super(type, pos, state);
		this.workTime = workTime;
		this.func = func;
	}
	private final LabFunction func;
	private final int workTime;
	private int work = -1;
	private @Nullable LabRecipeData recipeCache = null;
	
	public void tickServer() {
		// tick inventory -> push -> tick work -> update state
		// AbstractFurnaceBlockEntity
		attemptPush();
		
		LabState beforeState = getState();
		if (!beforeState.stalled()) {
			switch (beforeState) {
			case FINISHED:
				// attempt to finish crafting
				// if finalization fails it breaks
				// otherwise the case rolls over and ticks the timer down to -1 (idle)
				if (!finalizeCrafting())
					break;
				//this.setChanged();
			case ACTIVE:
				// decrement timer
				// check for >0 is a failsafe
				if (work >= 0) {
					work--;
					this.setChanged();
				}
			default:
				break;
			}
		}
	}
	
	/**
	 * Causes the lab to check its inputs for a valid recipe, and change state if necessary. <br>
	 * Should be used in place of setChanged() whenever the input inventory changes.
	 */
	protected void setChangedInput() {
		// TODO recipe cache
		LabState oldState = getState();
		@Nullable LabRecipeData in = packInputs();
		if (in != null) {
			@Nullable LabRecipeData out = transform(in);
			if (out != null) {
				// Recipe valid
				if (oldState.idle()) {
					// start crafting
					work = workTime;
					recipeCache = out; // TODO recipe cache
				}
			} else if (!oldState.idle()) becomeIdle(); // recipe function output invalid
		} else if (!oldState.idle()) becomeIdle(); // no inputs
		
		// probably not necessary but here just in case
		this.setChanged();
	}
	
	/**
	 * transforms the input LabRecipeData according to this labs process function
	 * @param in the LabRecipeData input
	 * @return new LabRecipeData with transformations applied, or null if the input was invalid
	 */
	@Nullable
	public LabRecipeData transform(@NotNull LabRecipeData in) {
		LabRecipeData out = func.apply(in);
		return out;
	}
	
	/**
	 * Packs a copy of inputs into a LabRecipeData,
	 * which will be sent to the recipe function for processing <br>
	 * @apiNote By default, this will also be used to determine what items get consumed when the recipe finishes!
	 * @return packed inputs, or null if no inputs
	 */
	@Nullable
	protected abstract LabRecipeData packInputs();
	
	/**
	 * Unpacks the given LabRecipeData and places it in the outputs of the TE
	 * @return true if the unpacking fully succeeded (partial unpacks should never happen)
	 */
	protected abstract boolean unpackOutputs(LabRecipeData dat);
	
	/**
	 * tells the TE to consume the given inputs. specifics on how consumption is handled may vary wildly from block to block,
	 * the passed in LabRecipeData is only there in case its necessary and may be ignored by the block.<br>
	 * @apiNote Checks done here probably don't need to be very robust. Most of the verification is done before this ever gets called.
	 * If your recipes conform to a very rigid ruleset, it may be possible to skip checks entirely and just consume. Some of the blocks in base AASB do just that.
	 * @param toConsume the LabRecipeData of inputs to consume
	 */
	protected abstract void consumeInputs(LabRecipeData toConsume);
	
	
	/**
	 * attempts to finish a recipe
	 * @return true if the recipe was actually finished, false if it couldnt finish (such as full outputs)
	 */
	protected boolean finalizeCrafting() {
		// TODO recipe cache
		@Nullable LabRecipeData in = packInputs();
		if (in != null) {
			@Nullable LabRecipeData out = transform(in);
			if (out != null) {
				if (unpackOutputs(out)) {
					consumeInputs(in);
					this.setChangedInput();
					return true;
				}
				return false;
			}
		}
		// since either inputs were empty, or were invalid, we cancel and become idle
		becomeIdle();
		return false;
	}
	
	/**
	 * cancels any active recipe and reverts to idle state
	 */
	protected void becomeIdle() {
		recipeCache = null;
		work = -1;
		this.setChanged();
	}
	
	/**
	 * causes the lab to perform a push attempt <br>
	 * this code is awful, because i genericized it beyond what is reasonable <br>
	 * makes tweaking it somewhat easier, at the very least
	 */
	protected void attemptPush() {
		for (PushType type : PushType.values()) {
			Direction[] pushDirs = getPushingDirs(type);
			for (Direction dir : pushDirs) {
				//System.out.println(type.name() +" "+ dir.name());
				Capability<?> cap = type.getCap();
				if (cap != null) {
					BlockEntity target = level.getBlockEntity(worldPosition.relative(dir));
					if (target != null) {
						LazyOptional<?> lo = target.getCapability(cap, dir.getOpposite());
						lo.map(c -> {
							if (c instanceof IItemHandler h) {
								tryPushItem(h, dir);
							} else if (c instanceof IFluidHandler h) {
								tryPushFluid(h, dir);
							} else if (c instanceof IHandleWay h) {
								tryPushWay(h, dir);
							} else if (c instanceof IHandleShape h) {
								tryPushShape(h, dir);
							} else if (c instanceof IHandleForm h) {
								tryPushForm(h, dir);
							}
							return false;
						}).orElse(false);
					}
				}
			}
		}
	}
	
	/**
	 * Attempts to push an itemstack OUT of the given direction, into the target handler. <br>
	 * @param target the target handler for this push operation
	 * @param dir the direction we are pushing FROM. to get the side being pushed INTO, use dir.opposite()
	 * @return true if the lab pushed. False if the lab cannot push this, or failed to push
	 */
	protected abstract boolean tryPushItem(IItemHandler target, Direction dir);

	/**
	 * Attempts to push an fluidstack OUT of the given direction, into the target handler. <br>
	 * @param target the target handler for this push operation
	 * @param dir the direction we are pushing FROM. to get the side being pushed INTO, use dir.opposite()
	 * @return true if the lab pushed. False if the lab cannot push this, or failed to push
	 */
	protected abstract boolean tryPushFluid(IFluidHandler target, Direction dir);

	/**
	 * Attempts to push an waystack OUT of the given direction, into the target handler. <br>
	 * @param target the target handler for this push operation
	 * @param dir the direction we are pushing FROM. to get the side being pushed INTO, use dir.opposite()
	 * @return true if the lab pushed. False if the lab cannot push this, or failed to push
	 */
	protected abstract boolean tryPushWay(IHandleWay target, Direction dir);

	/**
	 * Attempts to push an shapestack OUT of the given direction, into the target handler. <br>
	 * @param target the target handler for this push operation
	 * @param dir the direction we are pushing FROM. to get the side being pushed INTO, use dir.opposite()
	 * @return true if the lab pushed. False if the lab cannot push this, or failed to push
	 */
	protected abstract boolean tryPushShape(IHandleShape target, Direction dir);

	/**
	 * Attempts to push an formstack OUT of the given direction, into the target handler. <br>
	 * @param target the target handler for this push operation
	 * @param dir the direction we are pushing FROM. to get the side being pushed INTO, use dir.opposite()
	 * @return true if the lab pushed. False if the lab cannot push this, or failed to push
	 */
	protected abstract boolean tryPushForm(IHandleForm target, Direction dir);
	
	/**
	 * Gets the directions that this will push stacks to. Stack type corresponds with flag
	 * @param pf Flag of the stack type
	 * @return direcitons that this will try to push to when possible
	 */
	protected Direction[] getPushingDirs(PushType type) {
		Side[] sides = getPushingSides(type);
		Direction[] dirs = new Direction[sides.length];
		Direction facing = getFacing();
		for (int i = 0; i < sides.length; i++) {
			//System.out.println(type.name() +" "+ sides[i].name());
			dirs[i] = sides[i].abs(facing);
			//System.out.println(type.name() +" "+ dirs[i].name());
		}
		return dirs;
	}
	
	/**
	 * Gets the sides that this will attempt to push the given type from <br>
	 * Use this to define sidedness
	 * @param type the type in question
	 * @return array of sides to attempt pushing to
	 */
	protected abstract Side[] getPushingSides(PushType type);
	
	public int getWork() {
		return work;
	}
	
	public float getWorkPercent() {
		if (work == -1) return 0;
		return 1f - ( ((float)work) / ((float)workTime) );
	}
	
	/**
	 * gets the actual state of this TE. Agnostic to the BlockState
	 * @return
	 */
	public LabState getState() {
		if (!level.hasNeighborSignal(worldPosition)) {
			if (work < 0)
				return LabState.IDLE;
			if (work == 0)
				return LabState.FINISHED;
			return LabState.ACTIVE;
		}
		return LabState.STALLED;
	}
	
	public Direction getFacing() {
		return this.getBlockState().getValue(LabBlock.FACING);
	}
	
	public Tuple<LinkedHashMap<String,String>,LinkedHashMap<String,String>> getDebugInfo() {
		LinkedHashMap<String,String> infoG = new LinkedHashMap<>(),
									infoS = getDebugInfoSpecific(new LinkedHashMap<>());
		infoG.put("Facing", getFacing().name());
		//infoG.put("LabState", getLabState().name());
		infoG.put("LabState", getState().name());
		infoG.put("Work", work+" / "+workTime+", "+getWorkPercent()+"%");
		return new Tuple<LinkedHashMap<String,String>,LinkedHashMap<String,String>>(infoG, infoS);
		
	}
	
	protected abstract LinkedHashMap<String,String> getDebugInfoSpecific(LinkedHashMap<String,String> info);

	@Override
	public void load(CompoundTag teData) {
		if (teData.contains(TK_DATA)) {
			CompoundTag labData = teData.getCompound(TK_DATA);
			if (labData.contains(TK_TE)) {
				
				CompoundTag dat = labData.getCompound(TK_TE);
				if (dat.contains(TK_WORK)) {
					work = dat.getInt(TK_WORK);
				}
				if (dat.contains(TK_CACHE)) {
					LabRecipeData loadedCache = LabRecipeData.deserialize(dat.getCompound(TK_CACHE));
					recipeCache = loadedCache.isEmpty() ? null : loadedCache;
				}
				
			}
		}
		super.load(teData);
	}

	@Override
	public void saveAdditional(CompoundTag teData) {
		CompoundTag labData = saveLabData(new CompoundTag());
		if (!labData.isEmpty()) {
			teData.put(TK_DATA, labData);
		}
	}
	
	/**
	 * Saves TE data to a tag. You should override this if you want to save additional data specific to your class. <br>
	 * Each class should keep its data in an appropriately named subtag, so that things stay nicely organized.
	 * <p>
	 * Example: <br>
	 * The base LabTE class saves all its data in the "LabTE" CompoundTag, which is a subtag of "LabData".
	 * The more specific FueledLabTE saves all its data in the "FueledLabTE" CompoundTag, which is also a subtag of "LabData" (and NOT a subtag of LabTE).
	 * 
	 * @apiNote <b><i><u>YOU MUST CALL super.saveLabData(labData)</b></i></u>, as that allows any parent classes to save their data as well. Failing to do so will almost certainly cause issues.
	 * @param labData The parent "LabData" tag.
	 * @return Always {@code super.saveLabData(labData)}, eventually ending up as a tag containing all serialized data for the lab
	 */
	@NotNull
	protected CompoundTag saveLabData(@NotNull CompoundTag labData) {
		CompoundTag dat = new CompoundTag();
		if (work >= 0) {
			dat.putInt(TK_WORK, work);
		}
		if (recipeCache != null) {
			dat.put(TK_CACHE, recipeCache.serialize());
		}
		if (!dat.isEmpty())
			labData.put(TK_TE, dat);
		return labData;
	}
}
