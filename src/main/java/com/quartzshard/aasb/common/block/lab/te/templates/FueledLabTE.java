package com.quartzshard.aasb.common.block.lab.te.templates;

import java.util.LinkedHashMap;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import com.quartzshard.aasb.api.alchemy.lab.LabFunction;
import com.quartzshard.aasb.api.alchemy.lab.LabRecipeData;
import com.quartzshard.aasb.common.block.lab.te.LabTE;
import com.quartzshard.aasb.util.WorldHelper.Side;

/**
 * Variant of LabTE that requires burnable fuel, much like a furnace
 * TODO actually implement this
 */
public abstract class FueledLabTE extends LabTE {

	public static final BooleanProperty LIT = BlockStateProperties.LIT; // Used by fuel-consuming lab blocks in a similar way to the furnace
	public FueledLabTE(BlockEntityType<? extends LabTE> type, BlockPos pos, BlockState state,
			int workTime, LabFunction func) {
		super(type, pos, state, workTime, func);
		// TODO Auto-generated constructor stub
	}
	
	// Item OUTPUT
	protected final ItemStackHandler fuelInv = new ItemStackHandler(1) {
		@Override
		public boolean isItemValid(int slot, @NotNull ItemStack query) {
			return ForgeHooks.getBurnTime(query, RecipeType.BLASTING) > 0;
		}
		
		@Override
		public void onContentsChanged(int slot) {
			setChanged();
		}
	};
	private final LazyOptional<IItemHandler> xFuelInv = LazyOptional.of(() -> fuelInv);
	private int burner = 0;
	
	protected abstract Side[] getFuelInputSides();

	@Override
	protected LinkedHashMap<String, String> getDebugInfoSpecific(LinkedHashMap<String, String> info) {
		info.put("Lit", fuelInv.getStackInSlot(0).serializeNBT()+"");
		info.put("FuelStack", fuelInv.getStackInSlot(0).serializeNBT()+"");
		info.put("BurnTime", burner+" ticks");
		return null;
	}

}
