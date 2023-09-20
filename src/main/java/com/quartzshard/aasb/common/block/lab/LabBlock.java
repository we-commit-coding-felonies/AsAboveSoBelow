package com.quartzshard.aasb.common.block.lab;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Tuple;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import com.quartzshard.aasb.api.alchemy.aspects.AspectShape;
import com.quartzshard.aasb.api.alchemy.aspects.stack.ShapeStack;
import com.quartzshard.aasb.api.alchemy.aspects.stack.chamber.ShapeChamber;
import com.quartzshard.aasb.api.alchemy.aspects.stack.chamber.IAspectChamber.AspectAction;
import com.quartzshard.aasb.api.alchemy.lab.LabProcess;
import com.quartzshard.aasb.api.capability.AASBCapabilities;
import com.quartzshard.aasb.common.block.lab.te.LabTE;
import com.quartzshard.aasb.common.block.lab.te.LabTE.LabState;
import com.quartzshard.aasb.common.block.lab.te.debug.LabDebugEndTE;
import com.quartzshard.aasb.common.block.lab.te.debug.LabDebugStartTE;
import com.quartzshard.aasb.common.block.lab.te.debug.capability.LabDebugCapabilityRecieveTE;
import com.quartzshard.aasb.common.block.lab.te.debug.capability.LabDebugCapabilitySendTE;
import com.quartzshard.aasb.common.item.flask.StorageFlaskItem;
import com.quartzshard.aasb.common.network.AASBNet;
import com.quartzshard.aasb.common.network.client.DrawParticleAABBPacket;
import com.quartzshard.aasb.common.network.client.DrawParticleAABBPacket.AABBParticlePreset;
import com.quartzshard.aasb.util.WorldHelper;

public class LabBlock extends HorizontalDirectionalBlock implements EntityBlock {

	public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING; // Cardinal corresponding to the back face
	public static final BooleanProperty LIT = BlockStateProperties.LIT; // Used by fuel-consuming lab blocks in a similar way to the furnace
	public static final EnumProperty<LabState> STATE = EnumProperty.create("lab_state", LabState.class); // Used for visual feedback for the lab recipe
	public static final EnumProperty<LabProcess> PROCESS = EnumProperty.create("lab_process", LabProcess.class); // Used to determine recipe & for visual feedback
	public LabBlock(LabProcess process, Properties props) {
		this(process, false, props);
	}
	public LabBlock(LabProcess process, boolean usesFuel, Properties props) {
		super(props);
		this.process = process;
		this.usesFuel = usesFuel;
	}
	private final LabProcess process;
	public final boolean usesFuel;
	
	
	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult trace) {
		if (!level.isClientSide) {
			//ItemStack held = player.getItemInHand(hand);
			BlockEntity blockEnt = level.getBlockEntity(pos);
			if (blockEnt instanceof LabTE te) {
				Tuple<LinkedHashMap<String,String>,LinkedHashMap<String,String>> debug = te.getDebugInfo();
				player.displayClientMessage(new TextComponent("=== GENERAL ==="), false);
				for (Map.Entry<String,String> entry : debug.getA().entrySet()) {
					player.displayClientMessage(new TextComponent(entry.getKey() +" : "+ entry.getValue()), false);
				}
				player.displayClientMessage(new TextComponent("=== SPECIFIC TO "+ te.getType().getRegistryName() +" ==="), false);
				for (Map.Entry<String,String> entry : debug.getB().entrySet()) {
					player.displayClientMessage(new TextComponent(entry.getKey() +" : "+ entry.getValue()), false);
				}
				
				BlockPos facePos = pos.relative(te.getFacing());
				AASBNet.toClient(new DrawParticleAABBPacket(Vec3.atLowerCornerOf(facePos), Vec3.atLowerCornerOf(facePos.above().east().south()), AABBParticlePreset.DEBUG), (ServerPlayer) player);
			}
			
		}
		return InteractionResult.SUCCESS;
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		System.out.println("creation");
		// AbstractFurnaceBlockEntity
		return process == LabProcess.DISTILLATION ? new LabDebugStartTE(pos, state) : new LabDebugEndTE(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		if (level.isClientSide())
			return null;
		return (lvl, pos, bState, te) -> {
			if (te instanceof LabTE lab) {
				lab.tickServer();
			}
		};
	}


	@Override
	public BlockState getStateForPlacement(BlockPlaceContext ctx) {
		BlockState state = this.defaultBlockState()
				.setValue(PROCESS, process)
				.setValue(BlockStateProperties.HORIZONTAL_FACING, WorldHelper.getHorizontalFacing(ctx.getPlayer()))
				.setValue(STATE, LabState.IDLE);
		if (usesFuel)
			state.setValue(BlockStateProperties.LIT, false);
		return state;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(PROCESS);
		builder.add(BlockStateProperties.HORIZONTAL_FACING);
		builder.add(STATE);
		if (usesFuel)
			builder.add(BlockStateProperties.LIT);
	}

}
