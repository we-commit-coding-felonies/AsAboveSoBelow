package com.quartzshard.aasb.common.item;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.apache.commons.lang3.mutable.MutableBoolean;

import com.quartzshard.aasb.AsAboveSoBelow;
import com.quartzshard.aasb.init.ObjectInit;
import com.quartzshard.aasb.util.ColorsHelper;
import com.quartzshard.aasb.util.LogHelper;

import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.util.Tuple;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.BundleTooltip;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * an item for containing huge amounts of items <br>
 * helps prevent FPS / TPS lag <br>
 * based off the code for bundles / BundleItem
 * @author solunareclipse1
 */
@Mod.EventBusSubscriber(modid = AsAboveSoBelow.MODID)
public class LootBallItem extends Item {
	private static final Supplier<ItemStack> EMPTY_BALL_SUPPLIER = () -> {
		ItemStack ball = new ItemStack(ObjectInit.Items.LOOT_BALL.get());
		CompoundTag tag = ball.getOrCreateTag();
		if (!tag.contains("LootBallStorage")) {
			tag.put("LootBallStorage", new ListTag());
		}
		return ball;
	};
	private static final String TAG_ITEMS = "LootBallStorage";
	public static final int MAX_ITEMS = 4096;

	public LootBallItem(Item.Properties props) {
		super(props);
	}

	public static float getFullnessDisplay(ItemStack stack) {
		return (float)storedItemCount(stack) / (float)MAX_ITEMS;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level pLevel, Player player, InteractionHand pUsedHand) {
		ItemStack itemstack = player.getItemInHand(pUsedHand);
		if (dropContents(itemstack, player)) {
			LootBallItem.playDropContentsSound(player);
			player.awardStat(Stats.ITEM_USED.get(this));
			itemstack.shrink(1);
			return InteractionResultHolder.sidedSuccess(itemstack, pLevel.isClientSide());
		}
		return InteractionResultHolder.fail(itemstack);
	}

	@Override
	public boolean isBarVisible(ItemStack stack) {
		return storedItemCount(stack) > 0;
	}

	@Override
	public int getBarWidth(ItemStack stack) {
		return Math.min(1 + 12 * storedItemCount(stack) / MAX_ITEMS, 13);
	}

	@Override
	public int getBarColor(ItemStack stack) {
		return ColorsHelper.covalenceGradient((float)storedItemCount(stack)/(float)MAX_ITEMS);
	}

	private static int storedItemCount(ItemStack stack) {
		return getContents(stack).mapToInt((qStack) -> {
			return qStack.getCount();
		}).sum();
	}

	private static boolean dropContents(ItemStack stack, Player player) {
		CompoundTag tag = stack.getOrCreateTag();
		if (!tag.contains(TAG_ITEMS)) {
			return false;
		}
		if (player instanceof ServerPlayer) {
			ListTag itemTags = tag.getList(TAG_ITEMS, 10);

			for (int i = 0; i < itemTags.size(); ++i) {
				CompoundTag tagItem = itemTags.getCompound(i);
				player.spawnAtLocation(stackFromTag(tagItem));
			}
		}

		stack.removeTagKey(TAG_ITEMS);
		return true;
	}

	private static Stream<ItemStack> getContents(ItemStack stack) {
		CompoundTag compoundtag = stack.getTag();
		if (compoundtag == null) {
			return Stream.empty();
		}
		ListTag items = compoundtag.getList(TAG_ITEMS, 10);
		return items.stream().map(CompoundTag.class::cast).map(LootBallItem::stackFromTag);
	}

	@Override
	public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
		NonNullList<ItemStack> itemList = NonNullList.create();
		getContents(stack).forEach(itemList::add);
		return Optional.of(new BundleTooltip(itemList, storedItemCount(stack)));
	}

	@Override
	public void appendHoverText(ItemStack stack, Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
		pTooltipComponents.add(new TextComponent(String.format("%s/%s", storedItemCount(stack), MAX_ITEMS)).withStyle(ChatFormatting.GRAY));
	}

	@Override
	public void onDestroyed(ItemEntity pItemEntity) {
		ItemUtils.onContainerDestroyed(pItemEntity, getContents(pItemEntity.getItem()));
	}

	private static void playDropContentsSound(Entity pEntity) {
		pEntity.playSound(SoundEvents.BUNDLE_DROP_CONTENTS, 0.8F, 0.8F + pEntity.getLevel().getRandom().nextFloat() * 0.4F);
	}

	@SubscribeEvent
	public static void checkCombineBalls(EntityItemPickupEvent event) {
		ItemEntity itemEnt = event.getItem();
		ItemStack stack = itemEnt.getItem();
		ItemStack oldStack = stack.copy();
		Item item = stack.getItem();
		if (item == ObjectInit.Items.LOOT_BALL.get()) {
			Player player = event.getPlayer();
			boolean didMerge = false;
			Iterator<ItemStack> playerInv = player.getInventory().items.iterator();
			while (playerInv.hasNext() && !stack.isEmpty()) {
				ItemStack heldStack = playerInv.next();
				if (heldStack.getItem() == ObjectInit.Items.LOOT_BALL.get() && storedItemCount(heldStack) < MAX_ITEMS) {
					stack = combineLoot(stack, heldStack);
					didMerge = didMerge || !ItemStack.isSameItemSameTags(oldStack, stack);
				}
			}
			if (didMerge) {
				if (!stack.isEmpty()) {
					// there was leftovers, so lets spawn a new ball
					ItemEntity newEnt = itemEnt.spawnAtLocation(stack);
					if (newEnt != null)
						newEnt.setNoPickUpDelay();
				}
				itemEnt.kill();
				event.setCanceled(true);
				player.level.playSound((Player)null, player, SoundEvents.ITEM_PICKUP, SoundSource.HOSTILE, 1, player.getRandom().nextFloat(0.5f, 0.7f));
			}
		}
	}

	/**
	 * attempts to insert an itemstack into the loot ball
	 * @param receiverStack
	 * @param sentStack the stack to try inserting
	 * @return tuple with the leftover sentStack that couldnt be inserted (or EMPTY if all were inserted), and a boolean of if the receiverStack is full
	 */
	private static Tuple<ItemStack,Boolean> add(ItemStack receiverStack, ItemStack sentStack) {
		if (canBeStoredInLootBall(sentStack)) {
			CompoundTag receiverTag = receiverStack.getOrCreateTag();
			if (!receiverTag.contains(TAG_ITEMS)) {
				receiverTag.put(TAG_ITEMS, new ListTag());
			}

			int receiverStoredCount = storedItemCount(receiverStack);
			int sentCount = sentStack.getCount();
			int numToInsert = Math.min(sentCount, MAX_ITEMS - receiverStoredCount);
			if (numToInsert <= 0) {
				return new Tuple<>(sentStack, sentCount > 0);
			}
			ListTag receiverInv = receiverTag.getList(TAG_ITEMS, 10);
			Optional<CompoundTag> match = getMatchingItem(sentStack, receiverInv);
			if (match.isPresent()) {
				CompoundTag matchingStackTag = match.get();
				ItemStack matchingStack = stackFromTag(matchingStackTag);
				matchingStack.grow(numToInsert);
				tagFromStack(matchingStack, matchingStackTag);
				//matchingStack.save(matchingStackTag);
				receiverInv.remove(matchingStackTag);
				receiverInv.add(0, matchingStackTag);
			} else {
				ItemStack stackToInsert = sentStack.copy();
				stackToInsert.setCount(numToInsert);
				CompoundTag stackTag = new CompoundTag();
				tagFromStack(stackToInsert, stackTag);
				//stackToInsert.save(compoundtag2);
				receiverInv.add(0, stackTag);
			}
			ItemStack leftover = sentStack.copy();
			leftover.setCount(sentCount-numToInsert);
			Tuple<ItemStack,Boolean> tup = new Tuple<>(leftover.isEmpty() ? ItemStack.EMPTY : leftover, !leftover.isEmpty());
			return tup;
		}
		return new Tuple<>(sentStack, false);
	}
	private static Optional<CompoundTag> getMatchingItem(ItemStack pStack, ListTag pList) {
		return pStack.is(Items.BUNDLE) ? Optional.empty() : pList.stream().filter(CompoundTag.class::isInstance).map(CompoundTag.class::cast).filter((tag) -> {
			return ItemStack.isSameItemSameTags(stackFromTag(tag), pStack);
		}).findFirst();
	}

	/**
	 * combines 2 loot balls <br>
	 * @param donor
	 * @param receiver
	 * @return loot ball containing leftovers that couldnt be merged, or EMPTY if all was merged
	 */
	public static ItemStack combineLoot(ItemStack donor, ItemStack receiver) {
		if (!donor.is(ObjectInit.Items.LOOT_BALL.get()) || !receiver.is(ObjectInit.Items.LOOT_BALL.get()))
			return donor;
		List<ItemStack> leftoverInv = new ArrayList<>();
		MutableBoolean full = new MutableBoolean(false);
		getContents(donor).forEach((insertStack) -> {
			if (insertStack.isEmpty()) return; // filters out any weird empty stacks
			if (full.isTrue() || storedItemCount(receiver) >= MAX_ITEMS) {
				full.setTrue();
				leftoverInv.add(insertStack);
				return;
			}
			Tuple<ItemStack,Boolean> info = add(receiver, insertStack);
			ItemStack leftover = info.getA();
			if (!leftover.isEmpty()) {
				leftoverInv.add(leftover);
				if (info.getB()) {
					full.setTrue();
				}
			}
		});
		if (!leftoverInv.isEmpty()) {
			return makeLootBall(leftoverInv);
		}
		return ItemStack.EMPTY;
	}
	
	/**
	 * makes a loot ball with the given contents <br>
	 * note: this doesnt do any checks, please use makeValidLootBalls if possible
	 * @param contents
	 * @return
	 */
	public static ItemStack makeLootBall(List<ItemStack> contents) {
		ItemStack newBall = EMPTY_BALL_SUPPLIER.get();
		if (!contents.isEmpty()) {
			ListTag receiverInv = newBall.getOrCreateTag().getList(TAG_ITEMS, 10);
			for (ItemStack stackToInsert : contents) {
				Optional<CompoundTag> match = getMatchingItem(stackToInsert, receiverInv);
				if (match.isPresent()) {
					CompoundTag matchingStackTag = match.get();
					ItemStack matchingStack = stackFromTag(matchingStackTag);
					matchingStack.grow(stackToInsert.getCount());
					tagFromStack(matchingStack, matchingStackTag);
					//matchingStack.save(matchingStackTag);
					receiverInv.remove(matchingStackTag);
					receiverInv.add(0, matchingStackTag);
				} else {
					CompoundTag itemTag = new CompoundTag();
					tagFromStack(stackToInsert, itemTag);
					//stackToInsert.save(itemTag);
					receiverInv.add(0, itemTag);
				}
			}
		}
		return newBall;
	}
	
	public static List<ItemStack> makeValidLootBalls(List<ItemStack> contents) {
		List<ItemStack> balls = new ArrayList<>();
		if (!contents.isEmpty()) {
			List<ItemStack> toInsert = new ArrayList<>();
			int tally = 0;
			for (ItemStack s : contents) {
				// anything that cant be stored gets sent to the shadow realm
				if (!canBeStoredInLootBall(s)) continue;
				
				//Optional<ItemStack>
				tally += s.getCount();
				toInsert.add(s); // making a duplicate of contents that we will mess with
			}
			
			if (tally <= MAX_ITEMS) {
				// contents can fit in 1 ball, so we skip the extra processing
				balls.add(makeLootBall(contents));
			} else {
				int expectedBallCount = Mth.ceil((float)tally/(float)MAX_ITEMS);
				int loops = 0;
				// as a safety measure, we stop looping if we loop too many times
				// double the expected loops is a decent number, leaving some room for error
				while (!toInsert.isEmpty() && loops < expectedBallCount*2) {
					// get an empty loot ball, we will store items in it
					ItemStack newBall = EMPTY_BALL_SUPPLIER.get();
					int inserted = 0;
					List<ItemStack> remainingItems = new ArrayList<>();
					for (ItemStack s : toInsert) {
						toInsert.add(s); // another duplicate so the for loop doesnt do weird shit
					}
					for (ItemStack stack : toInsert) {
						if (inserted + stack.getCount() > MAX_ITEMS) {
							remainingItems.add(stack);
							continue;
						}
						inserted += stack.getCount();
						add(newBall, stack);
					}
					balls.add(newBall);
					toInsert = remainingItems;
					loops++;
				}
			}
		}
		return balls;
	}
	
	public static List<ItemEntity> dropBalls(Player player, List<ItemStack> contents) {
		List<ItemEntity> ballEnts = new ArrayList<>();
		if (!contents.isEmpty()) {
			List<ItemStack> lootBalls = makeValidLootBalls(contents);
			for (ItemStack ball : lootBalls) {
				ItemEntity item = player.spawnAtLocation(ball);
				if (item != null) {
					item.setNoPickUpDelay();
					ballEnts.add(item);
				}
			}	
		}
		return ballEnts;
	}
	
	public static boolean canBeStoredInLootBall(ItemStack stack) {
		return !(stack.isEmpty()
				|| !stack.getItem().canFitInsideContainerItems()
				|| stack.getMaxStackSize() <= 1);
	}
	
	@Override
	public boolean canFitInsideContainerItems() {
		return false;
	}
	public static CompoundTag tagFromStack(ItemStack stack, CompoundTag pCompoundTag) {
		ResourceLocation resourcelocation = stack.getItem().getRegistryName();
		pCompoundTag.putString("id", resourcelocation == null ? "minecraft:air" : resourcelocation.toString());
		pCompoundTag.putInt("Count", stack.getCount());
		if (stack.hasTag()) {
			pCompoundTag.put("tag", stack.getTag().copy());
		}
		/* TODO: save caps to loot balls instead of voiding them
		CompoundTag cnbt = stack.serializeCaps();
		if (cnbt != null && !cnbt.isEmpty()) {
			pCompoundTag.put("ForgeCaps", cnbt);
		} */
		return pCompoundTag;
	}
	public static ItemStack stackFromTag(CompoundTag tag) {
		try {
			Item item = ForgeRegistries.ITEMS.getValue(ResourceLocation.tryParse(tag.getString("id")));
			int amount = tag.getInt("Count");
			CompoundTag nbt = tag.contains("tag", CompoundTag.TAG_COMPOUND) ? tag.getCompound("tag") : null;
			ItemStack tagStack = new ItemStack(item, amount);
			if (nbt != null) {
				tagStack.setTag(nbt);
			}
			return tagStack;
		} catch (RuntimeException e) {
			LogHelper.LOGGER.debug("Tried to load invalid item: {}", tag, e);
			return ItemStack.EMPTY;
		}
	}
}
