package com.quartzshard.aasb.api.alchemy.rune.form;

import java.util.UUID;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.quartzshard.aasb.AASB;
import com.quartzshard.aasb.api.item.IRuneable;
import com.quartzshard.aasb.net.server.KeybindPacket.BindState;
import com.quartzshard.aasb.util.NBTUtil;
import com.quartzshard.aasb.util.PlayerUtil;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.BlockHitResult;
import top.theillusivec4.curios.api.SlotContext;

public class OrganicRune extends FormRune {
	public static final String
		TK_PHOTOPOWER = "PhotosynthesisStrength",
		TK_PHOTOSTRONG = "PhotosynthesisEmpowered";
	public static final UUID
		UUID_DAMAGE = UUID.fromString("31ff182e-27b1-461e-b7b5-abdb79002b44"),
		UUID_ATKSPEED = UUID.fromString("ecf6e85e-113b-4f41-a909-028e2ea092df"),
		UUID_MOVESPEED = UUID.fromString("d46dc487-3162-4faf-a4a4-a5fdda9bb6b4"),
		UUID_HEALTH = UUID.fromString("c98999cc-7a96-4519-b4e6-7cfc8302b65c"),
		UUID_ARMOR = UUID.fromString("7701e118-021d-48be-8bd9-adfddb6dd2c5"),
		UUID_TOUGHNESS = UUID.fromString("67860e25-29a4-4f75-8f0f-475dd6a66bf6"),
		UUID_LUCK = UUID.fromString("7e5b5682-68ce-4e8b-9259-67f630eb19be");

	public OrganicRune() {
		super(AASB.rl("organic"));
	}

	/**
	 * normal: super poison touch <br>
	 * strong: wither vine
	 */
	@Override
	public boolean combatAbility(ItemStack stack, ServerPlayer player, ServerLevel level, BindState state, boolean strong, String slot) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * normal: bonemeal <br>
	 * strong: area bonemeal
	 */
	@Override
	public boolean utilityAbility(ItemStack stack, ServerPlayer player, @NotNull ServerLevel level, BindState state, boolean strong, String slot) {
		if (state == BindState.PRESSED) {
			// TODO cost based on way of bone meal
			ItemStack meal = new ItemStack(Items.BONE_MEAL, 64);
			if (strong) {
				
			}
			BlockHitResult hitRes = PlayerUtil.getTargetedBlockGrass(player, 16);
			UseOnContext mealCtx = new UseOnContext(level, player, InteractionHand.MAIN_HAND, meal, hitRes);
			if (Items.BONE_MEAL.useOn(mealCtx) != InteractionResult.PASS) {
				for (int i = 0; i < 63; i++) {
					// it worked once, do it 63 more times
					Items.BONE_MEAL.useOn(mealCtx);
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * normal: solar stat boost <br>
	 * strong: stronger boost and +10 hearts
	 */
	//@Override
	//public boolean passiveAbility(ItemStack stack, ServerPlayer player, ServerLevel level, BindState state, boolean strong, String slot) {
	//	return false;
	//}

	@Override
	public void tickPassive(ItemStack stack, ServerPlayer player, ServerLevel level, boolean strong, boolean unequipped) {
		int blockLight = level.getMaxLocalRawBrightness(player.blockPosition().above());
		int skyLight = level.getBrightness(LightLayer.SKY, player.blockPosition().above());
		int photoPower = strong ?
				skyLight > 7 ? 15 : Math.max(skyLight, blockLight):
				skyLight;
		NBTUtil.setInt(stack, TK_PHOTOPOWER, photoPower);
		NBTUtil.setBoolean(stack, TK_PHOTOSTRONG, strong && skyLight > 7); // FIXME make max health update properly clientside
	}
	
	@Override @Nullable
	public Multimap<Attribute, AttributeModifier> getAttributeModifiers(SlotContext ctx, UUID uuid, ItemStack stack) {
		if (stack.getItem() instanceof IRuneable item) {
			boolean strong = NBTUtil.getBoolean(stack, TK_PHOTOSTRONG, false);
			float potency = (float)NBTUtil.getInt(stack, TK_PHOTOPOWER, 0) / 15f;
			if (this.passiveEnabled(stack) && potency > 0) {
				ImmutableMultimap.Builder<Attribute,AttributeModifier> attribs = ImmutableMultimap.builder();
				attribs.put(Attributes.ATTACK_DAMAGE,
						new AttributeModifier(UUID_DAMAGE, "photosynthesis - attack damage", potency * (strong ? 10 : 2), AttributeModifier.Operation.ADDITION));
				attribs.put(Attributes.ATTACK_SPEED,
						new AttributeModifier(UUID_ATKSPEED, "photosynthesis - attack speed", potency * (strong ? 2 : 1), AttributeModifier.Operation.ADDITION));
				attribs.put(Attributes.MOVEMENT_SPEED,
						new AttributeModifier(UUID_MOVESPEED, "photosynthesis - movement speed", potency * (strong ? 0.1 : 0.05), AttributeModifier.Operation.ADDITION));
				if (strong) {
					attribs.put(Attributes.MAX_HEALTH,
							new AttributeModifier(UUID_HEALTH, "photosynthesis - max health", 20, AttributeModifier.Operation.ADDITION));
				}
				attribs.put(Attributes.ARMOR,
						new AttributeModifier(UUID_ARMOR, "photosynthesis - armor", potency * (strong ? 8 : 4), AttributeModifier.Operation.ADDITION));
				attribs.put(Attributes.ARMOR_TOUGHNESS,
						new AttributeModifier(UUID_TOUGHNESS, "photosynthesis - armor toughness", potency * (strong ? 4 : 2), AttributeModifier.Operation.ADDITION));
				attribs.put(Attributes.LUCK,
						new AttributeModifier(UUID_LUCK, "photosynthesis - luck", potency * (strong ? 10 : 1), AttributeModifier.Operation.ADDITION));
				return attribs.build();
			}
		}
		return null;
	}

}
