package com.quartzshard.aasb.common.item.equipment.armor.jewellery;

import java.util.UUID;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.quartzshard.aasb.AASB;
import com.quartzshard.aasb.api.item.bind.ICanLegsMode;
import com.quartzshard.aasb.util.NBTUtil;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;

@Mod.EventBusSubscriber(modid = AASB.MODID)
public class PocketwatchItem extends JewelleryArmorItem implements ICanLegsMode {
	public PocketwatchItem(Properties props) {
		super(Type.LEGGINGS, props);
	}
	
	public static final UUID SPEED_UUID = UUID.fromString("27311d20-ecc5-427c-bf3d-02d019eb4688");
	
	public static final String TAG_SPEED = "WatchMobility";
	
	@Override
	public void onArmorTick(ItemStack stack, Level level, Player player) {
		// TODO: COST
		if (speedEnabled(stack)) {
			boolean flying = player.getAbilities().flying || player.isFallFlying();
			if (!flying && player.isShiftKeyDown()) {
				fastDescend(player, level);
			}
		}
	}
	
	@Override
	public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, @NotNull ItemStack stack) {
		Multimap<Attribute, AttributeModifier> supMods = super.getAttributeModifiers(slot, stack);
		if (slot != this.getEquipmentSlot() || !speedEnabled(stack))
			return supMods;
		
		ImmutableMultimap.Builder<Attribute,AttributeModifier> attribs = ImmutableMultimap.builder();
		AttributeModifier
			walk = new AttributeModifier(SPEED_UUID, "Watch of the Astrologer - Movement Speed", 0.35, AttributeModifier.Operation.MULTIPLY_TOTAL),
			swim = new AttributeModifier(SPEED_UUID, "Watch of the Astrologer - Swim Speed", 0.35, AttributeModifier.Operation.MULTIPLY_TOTAL),
			step = new AttributeModifier(SPEED_UUID, "Watch of the Astrologer - Step Height", 0.55, AttributeModifier.Operation.ADDITION);
		attribs.put(Attributes.MOVEMENT_SPEED, walk);
		attribs.put(net.minecraftforge.common.ForgeMod.SWIM_SPEED.get(), swim);
		attribs.put(net.minecraftforge.common.ForgeMod.STEP_HEIGHT_ADDITION.get(), step);
		attribs.putAll(supMods);
		return attribs.build();
	}
	
	@SubscribeEvent
	public static void checkJumpBonus(LivingEvent.LivingJumpEvent event) {
		if (event.getEntity() instanceof Player plr) {
			ItemStack watchStack = plr.getItemBySlot(EquipmentSlot.LEGS);
			
			if (watchStack != null && !watchStack.isEmpty()
					&& watchStack.getItem() instanceof PocketwatchItem watch && watch.speedEnabled(watchStack)) {
				double mx,my,mz;
				my = 0.288939;
				mx = mz = 0;
				if (plr.isSprinting()) {
					float horizMod = plr.getYRot() * ((float)Math.PI / 180f);
					mx = -Mth.sin(horizMod) * 0.3;
					mz = Mth.cos(horizMod) * 0.3;
				}
				plr.setDeltaMovement(plr.getDeltaMovement().add(mx, my, mz));
			}
		}
	}
	
	private static void fastDescend(Player player, @NotNull Level level) {
		if (level.isClientSide) {
			Vec3 vel = player.getDeltaMovement();
			if (!player.onGround() && vel.y() > -8) {
				player.setDeltaMovement(vel.add(0, -0.32f, 0));
			}
		}
	}
	
	@Override
	public boolean onPressedLegsMode(ItemStack stack, ServerPlayer player, ServerLevel level) {
		toggleSpeed(stack);
		return true;
	}
	
	public boolean speedEnabled(ItemStack stack) {
		return NBTUtil.getBoolean(stack, TAG_SPEED, true);
	}
	
	public void toggleSpeed(ItemStack stack) {
		setSpeedy(stack, !speedEnabled(stack));
	}
	
	public void setSpeedy(ItemStack stack, boolean state) {
		NBTUtil.setBoolean(stack, TAG_SPEED, state);
	}

}
