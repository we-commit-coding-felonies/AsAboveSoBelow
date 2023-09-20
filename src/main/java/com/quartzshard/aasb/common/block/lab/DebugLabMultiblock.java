package com.quartzshard.aasb.common.block.lab;

import java.util.function.Consumer;

import org.jetbrains.annotations.Nullable;

import com.quartzshard.aasb.api.alchemy.aspects.stack.chamber.ShapeChamber;
import com.quartzshard.aasb.api.alchemy.aspects.AspectShape;
import com.quartzshard.aasb.api.alchemy.aspects.stack.ShapeStack;
import com.quartzshard.aasb.api.alchemy.aspects.stack.chamber.IAspectChamber.AspectAction;
import com.quartzshard.aasb.api.capability.AASBCapabilities;
import com.quartzshard.aasb.common.block.lab.te.debug.capability.LabDebugCapabilityRecieveTE;
import com.quartzshard.aasb.common.block.lab.te.debug.capability.LabDebugCapabilitySendTE;
import com.quartzshard.aasb.common.item.flask.FlaskItem;
import com.quartzshard.aasb.common.item.flask.StorageFlaskItem;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class DebugLabMultiblock extends Block implements EntityBlock {
	final boolean is_sender;
	public DebugLabMultiblock(Properties props, boolean isSender) {
		super(props);
		is_sender = isSender;
	}

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult trace) {
        if (!level.isClientSide) {
        	ItemStack held = player.getItemInHand(hand);
        	if (held.getItem() instanceof StorageFlaskItem flask) {
        		if (level.getBlockEntity(pos) instanceof LabDebugCapabilitySendTE te) {
        			if (flask.hasStored(held) && flask.hasStoredShape(held)) {
        				AspectShape as = flask.getStoredShape(held);
        				if (as != null) {
        					ShapeStack s = new ShapeStack(as);
                			te.getCapability(AASBCapabilities.SHAPE_HANDLER).ifPresent((h) -> {
                				if (h instanceof ShapeChamber sc) {
                					int r = sc.insert(s, AspectAction.EXECUTE);
                					System.out.println(r);
                					if (r == 0) {
                						flask.clearStored(held);
                					}
                				}
                			});
        				}
        			}
        		} else if (level.getBlockEntity(pos) instanceof LabDebugCapabilityRecieveTE te) {
        			if (!flask.hasStored(held)) {
            			te.getCapability(AASBCapabilities.SHAPE_HANDLER).ifPresent((h) -> {
            				if (h instanceof ShapeChamber sc) {
            					ShapeStack s = sc.extract(1, AspectAction.EXECUTE);
            					if (!s.isEmpty()) {
            						flask.setStored(held, s.getAspect(), null, level.getGameTime());
            					}
            				}
            			});
        			}
        		}
        	} else  {
        		CompoundTag t = new CompoundTag();
        		Consumer<ShapeStack> f = (s) -> t.put("StoredShape", s.serialize());
        		if (level.getBlockEntity(pos) instanceof LabDebugCapabilityRecieveTE te) {
        			te.getCapability(AASBCapabilities.SHAPE_HANDLER).ifPresent((h) -> {
        				if (h instanceof ShapeChamber sc) {
        					ShapeStack s = sc.extract(10000, AspectAction.SIMULATE);
        					if (!s.isEmpty()) {
        						f.accept(s);
        					}
        				}
        			});
        		} else if (level.getBlockEntity(pos) instanceof LabDebugCapabilitySendTE te) {
        			te.getCapability(AASBCapabilities.SHAPE_HANDLER).ifPresent((h) -> {
        				if (h instanceof ShapeChamber sc) {
        					ShapeStack s = sc.extract(10000, AspectAction.SIMULATE);
        					if (!s.isEmpty()) {
        						f.accept(s);
        					}
        				}
        			});
        		}
        		player.displayClientMessage(new TextComponent(t.toString()), false);
        	}
        }
        return InteractionResult.SUCCESS;
    }

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		System.out.println("created isSender: " + is_sender);
		if (is_sender)
			return new LabDebugCapabilitySendTE(pos, state);
		else return new LabDebugCapabilityRecieveTE(pos, state);
	}

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide()) {
            return null;
        }
        return (lvl, pos, bState, te) -> {
            if (te instanceof LabDebugCapabilitySendTE lab) {
                lab.tickServer();
            }
        };
    }

}
