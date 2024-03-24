package com.quartzshard.aasb.common.block;

import com.quartzshard.aasb.common.gui.menu.TransmutationMenu;
import com.quartzshard.aasb.data.LangData;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

public class TransmutationBlock extends Block {
	public static final Component MENUNAME = LangData.tc(LangData.CIRCLE_5_GREAT);
	public TransmutationBlock(Properties props) {
		super(props);
	}

	@NotNull
	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitRes) {
		if (level.isClientSide) {
			return InteractionResult.SUCCESS;
		} else {
			player.openMenu(state.getMenuProvider(level, pos));
			player.awardStat(Stats.INTERACT_WITH_CRAFTING_TABLE);
			return InteractionResult.CONSUME;
		}
	}

	@Override
	public MenuProvider getMenuProvider(BlockState state, Level level, BlockPos pos) {
		return new SimpleMenuProvider((id, inv, plr) -> new TransmutationMenu(id, inv, ContainerLevelAccess.create(level, pos)), MENUNAME);
		//return new SimpleMenuProvider((p_52229_, p_52230_, p_52231_) -> new CraftingMenu(p_52229_, p_52230_, ContainerLevelAccess.create(level, pos)), MENUNAME);
	}
}
