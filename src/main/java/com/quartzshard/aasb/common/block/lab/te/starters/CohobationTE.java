package com.quartzshard.aasb.common.block.lab.te.starters;

import java.util.LinkedHashMap;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import com.quartzshard.aasb.api.alchemy.aspects.stack.FormStack;
import com.quartzshard.aasb.api.alchemy.aspects.stack.ShapeStack;
import com.quartzshard.aasb.api.alchemy.aspects.stack.chamber.FormChamber;
import com.quartzshard.aasb.api.alchemy.aspects.stack.chamber.ShapeChamber;
import com.quartzshard.aasb.api.alchemy.aspects.stack.chamber.IAspectChamber.AspectAction;
import com.quartzshard.aasb.api.alchemy.lab.LabRecipeData;
import com.quartzshard.aasb.api.capability.AASBCapabilities;
import com.quartzshard.aasb.api.capability.aspect.form.IHandleForm;
import com.quartzshard.aasb.api.capability.aspect.shape.IHandleShape;
import com.quartzshard.aasb.api.capability.aspect.way.IHandleWay;
import com.quartzshard.aasb.api.misc.Executor;
import com.quartzshard.aasb.common.block.lab.te.LabTE;
import com.quartzshard.aasb.common.item.flask.FlaskItem;
import com.quartzshard.aasb.common.item.flask.StorageFlaskItem;
import com.quartzshard.aasb.util.LogHelper;
import com.quartzshard.aasb.util.NBTHelper.TagKeys;
import com.quartzshard.aasb.util.WorldHelper.Side;

import software.bernie.geckolib3.core.AnimationState;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.Animation;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.builder.ILoopType.EDefaultLoopTypes;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

import static com.quartzshard.aasb.api.alchemy.lab.LabProcess.*;
import static com.quartzshard.aasb.init.ObjectInit.TileEntities./*Labs.*/*;

public class CohobationTE extends LabTE implements IAnimatable {

	public CohobationTE(BlockPos pos, BlockState state) {
		super(DISTILLATION_TE.get(), pos, state, 500, DISTILLATION);
	}

	// Item INPUT
	private final ItemStackHandler itemIn = new ItemStackHandler(1) {
		@Override
		public void onContentsChanged(int slot) {
			setChangedInput();
		} 
		
		@Override
		public boolean isItemValid(int slot, @NotNull ItemStack stack) {
			return stack.getItem() instanceof FlaskItem flask && flask.hasStored(stack);
		}
	};
	private final LazyOptional<IItemHandler> xItemIn = LazyOptional.of(() -> itemIn);
	
	// Shape OUTPUT
	private final ShapeChamber shapeOut = new ShapeChamber(10) {
		@Override
		public void onChanged() {
			setChanged();
		}
	};
	
	// Form OUTPUT
	private final FormChamber formOut = new FormChamber(10) {
		@Override
		public void onChanged() {
			setChanged();
		}
	};
	
	// Item OUTPUT
	private final ItemStackHandler itemOut = new ItemStackHandler(1) {
		@Override
		public void onContentsChanged(int slot) {
			setChanged();
		}
	};

	@Override
	@Nullable
	protected LabRecipeData packInputs() {
		// FLASK PACKING
		NonNullList<ItemStack> items = LabRecipeData.il(1);
		ItemStack flask1 = itemIn.extractItem(0, 1, true);
		if (flask1.getItem() instanceof FlaskItem flaskItem) {
			if (flaskItem.isExpired(flask1, level.getGameTime())) {
				flaskItem.setContaminated(flask1, true);
			} else {
				items.set(0, flask1);
				return new LabRecipeData(items, null, null, null, null);
			}
		}
		
		return null;
	}

	@Override
	protected boolean unpackOutputs(LabRecipeData dat) {
		Executor xc = () -> LogHelper.debug("DistillationTE.unpackOutputs().xc()", "ExecutorStarted");
		boolean doItems = LabRecipeData.hasItemStacks(dat.items),
				doShapes = LabRecipeData.hasAspectStacks(dat.shapes),
				doForms = LabRecipeData.hasAspectStacks(dat.forms);
		if (doItems) {
			ItemStack outStack = dat.items.get(0);
			ItemStack estLeft = itemOut.insertItem(0, outStack, true);
			if (!estLeft.isEmpty())
				return false;
			xc = xc.also(() -> itemOut.insertItem(0, outStack, false));
		}
		if (doShapes) {
			ShapeStack outStack = dat.shapes.get(0);
			int estLeft = shapeOut.insert(outStack, AspectAction.SIMULATE);
			if (estLeft != 0)
				return false;
			xc = xc.also(() -> shapeOut.insert(outStack, AspectAction.EXECUTE));
		}
		if (doForms) {
			FormStack outStack = dat.forms.get(0);
			int estLeft = formOut.insert(outStack, AspectAction.SIMULATE);
			if (estLeft != 0)
				return false;
			xc = xc.also(() -> formOut.insert(outStack, AspectAction.EXECUTE));
		}
		xc.execute();
		return doItems || doShapes || doForms;
	}

	@Override
	protected void consumeInputs(LabRecipeData toConsume) {
		if (LabRecipeData.hasItemStacks(toConsume.items)) {
			itemIn.extractItem(0, 1, false);
		}
	}

	@Override
	protected boolean tryPushItem(IItemHandler target, Direction dir) {
		ItemStack toPush = itemOut.extractItem(0, 1, true);
		if (!toPush.isEmpty()) {
			for (int i = 0; i < target.getSlots(); i++) {
				ItemStack rem = target.insertItem(i, toPush, true);
				if (rem.isEmpty()) {
					target.insertItem(i, itemOut.extractItem(0, 1, false), false);
					return true;
				}
			}
		}
		return false;
	}

	@Override
	protected boolean tryPushFluid(IFluidHandler target, Direction dir) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected boolean tryPushWay(IHandleWay target, Direction dir) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected boolean tryPushShape(IHandleShape target, Direction dir) {
		ShapeStack toPush = shapeOut.extract(1, AspectAction.SIMULATE);
		if (!toPush.isEmpty()) {
			int left = target.receiveFrom(toPush, dir.getOpposite());
			if (left == 0) {
				shapeOut.extract(1, AspectAction.EXECUTE);
				return true;
			}
		}
		return false;
	}

	@Override
	protected boolean tryPushForm(IHandleForm target, Direction dir) {
		FormStack toPush = formOut.extract(1, AspectAction.SIMULATE);
		if (!toPush.isEmpty()) {
			int left = target.receiveFrom(toPush, dir.getOpposite());
			if (left == 0) {
				formOut.extract(1, AspectAction.EXECUTE);
				return true;
			}
		}
		return false;
	}

	@Override
	protected Side[] getPushingSides(PushType type) {
		switch (type) {
		case ITEM:
			return new Side[] {Side.BOTTOM};
		case SHAPE:
		case FORM:
			return new Side[] {Side.BACK};
		default:
			break;
		}
		return new Side[] {};
	}

	@NotNull
	@Override
	public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction dir) {
		if (dir != null) {
			switch (Side.rel(dir, getFacing())) {
			case FRONT:
				if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
					return xItemIn.cast();
				break;
			default:
				break;
			}
		}
		return super.getCapability(cap, dir);
	}

	@Override
	protected LinkedHashMap<String, String> getDebugInfoSpecific(LinkedHashMap<String, String> info) {
		return info;
	}


	public static final String
		TK_TE = "SolutionTE",
			TK_INV = TagKeys.MULTIINV,
				TK_ITEMIN = "ItemInput",
				TK_SHAPEOUT = "ShapeOutput",
				TK_FORMOUT = "FormOutput",
				TK_ITEMOUT = "ItemOutput";
	@Override
	@NotNull
	protected CompoundTag saveLabData(@NotNull CompoundTag labData) {
		CompoundTag dat = new CompoundTag();
		CompoundTag invDat = new CompoundTag();
		
		if (!itemIn.getStackInSlot(0).isEmpty()) {
			invDat.put(TK_ITEMIN, itemIn.serializeNBT());
		}
		@Nullable CompoundTag tag = shapeOut.serialize();
		if (tag != null) {
			invDat.put(TK_SHAPEOUT, tag);
		}
		tag = formOut.serialize();
		if (tag != null) {
			invDat.put(TK_FORMOUT, tag);
		}
		if (!itemOut.getStackInSlot(0).isEmpty()) {
			invDat.put(TK_ITEMOUT, itemIn.serializeNBT());
		}
		
		if (!invDat.isEmpty()) {
			dat.put(TK_INV, invDat);
		}
		
		if (!dat.isEmpty())
			labData.put(TK_TE, dat);
		return super.saveLabData(labData);
	}

	@Override
	public void load(CompoundTag teData) {
		if (teData.contains(TK_DATA)) {
			CompoundTag labData = teData.getCompound(TK_DATA);
			if (labData.contains(TK_TE)) {
				CompoundTag dat = labData.getCompound(TK_TE);
				if (dat.contains(TK_INV)) {
					CompoundTag invDat = labData.getCompound(TK_INV);
					if (invDat.contains(TK_ITEMIN)) {
						itemIn.deserializeNBT(invDat.getCompound(TK_ITEMIN));
					}
					if (invDat.contains(TK_SHAPEOUT)) {
						shapeOut.deserialize(invDat.getCompound(TK_SHAPEOUT));
					}
					if (invDat.contains(TK_FORMOUT)) {
						formOut.deserialize(invDat.getCompound(TK_FORMOUT));
					}
					if (invDat.contains(TK_ITEMOUT)) {
						itemOut.deserializeNBT(invDat.getCompound(TK_ITEMOUT));
					}
				}
				
			}
		}
		super.load(teData);
	}

	//protected static final AnimationBuilder IDLE = new AnimationBuilder().addAnimation("idle", EDefaultLoopTypes.LOOP);
	protected static final AnimationBuilder CONNECT_DOWN = new AnimationBuilder().addAnimation("connect-down", EDefaultLoopTypes.PLAY_ONCE).addAnimation("extended-down", EDefaultLoopTypes.LOOP);
	//protected static final AnimationBuilder EXTENDED_DOWN = new AnimationBuilder().addAnimation("extended-down", EDefaultLoopTypes.LOOP);
	protected static final AnimationBuilder DETACH_DOWN = new AnimationBuilder().addAnimation("detach-down", EDefaultLoopTypes.HOLD_ON_LAST_FRAME);
	
	protected static final AnimationBuilder CONNECT_BACK = new AnimationBuilder().addAnimation("connect-back", EDefaultLoopTypes.PLAY_ONCE).addAnimation("extended-back", EDefaultLoopTypes.LOOP);
	//protected static final AnimationBuilder EXTENDED_BACK = new AnimationBuilder().addAnimation("extended-back", EDefaultLoopTypes.LOOP);
	protected static final AnimationBuilder DETACH_BACK = new AnimationBuilder().addAnimation("detach-back", EDefaultLoopTypes.HOLD_ON_LAST_FRAME);
	private final AnimationFactory factory = GeckoLibUtil.createFactory(this);

	@Override
	public void registerControllers(final AnimationData data) {
		// TODO Auto-generated method stub
		//data.addAnimationController(new AnimationController<>(this, "main", 0, this::predicate));
		data.addAnimationController(new AnimationController<>(this, "io-down", 0, this::ioDown));
		data.addAnimationController(new AnimationController<>(this, "io-back", 0, this::ioBack));
	}

	private PlayState ioDown(AnimationEvent<CohobationTE> event) {
		AnimationController<CohobationTE> ctrl = event.getController();
		CohobationTE te = event.getAnimatable();
		Level level = te.getLevel();
		if (level != null) {
			Direction dir = Direction.DOWN;
			@Nullable BlockEntity adjTE = level.getBlockEntity(te.getBlockPos().relative(dir));
			@Nullable Animation curAnim = ctrl.getCurrentAnimation();
			if (curAnim != null) {
				if (adjTE != null) {
					// TODO: find a way to only check capabilities when absolutely necessary
					if (adjTE.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, dir.getOpposite()).isPresent()) {
						ctrl.setAnimation(CONNECT_DOWN);
						return PlayState.CONTINUE;
					}
				}
			}
			ctrl.setAnimation(DETACH_DOWN);
			return PlayState.CONTINUE;
		}
		// why is the level null!?
		// i dunno.
		return PlayState.STOP;
		// probably wont ever be, but just in case
	}
	
	private PlayState ioBack(AnimationEvent<CohobationTE> event) {
		AnimationController<CohobationTE> ctrl = event.getController();
		CohobationTE te = event.getAnimatable();
		Level level = te.getLevel();
		if (level != null) {
			Direction dir = Side.BACK.abs(te.getFacing());
			@Nullable BlockEntity adjTE = level.getBlockEntity(te.getBlockPos().relative(dir));
			@Nullable Animation curAnim = ctrl.getCurrentAnimation();
			if (curAnim != null) {
				if (adjTE != null) {
					// we only check capabilities if were currently in the detach animation to save time
					if (!adjTE.getCapability(AASBCapabilities.SHAPE_HANDLER, dir.getOpposite()).isPresent()
						|| !adjTE.getCapability(AASBCapabilities.FORM_HANDLER, dir.getOpposite()).isPresent()) {
						ctrl.setAnimation(CONNECT_BACK);
						return PlayState.CONTINUE;
					}
				}
			}
			ctrl.setAnimation(DETACH_BACK);
			return PlayState.CONTINUE;
		}
		// why is the level null!?
		// i dunno.
		return PlayState.STOP;
		// probably wont ever be, but just in case
	}
	
	@Override
	public AnimationFactory getFactory() {
		// TODO Auto-generated method stub
		return factory;
	}

}
