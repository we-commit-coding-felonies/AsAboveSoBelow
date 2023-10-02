package com.quartzshard.aasb.common.item.equipment.tool;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.Nullable;

import com.quartzshard.aasb.AsAboveSoBelow;
import com.quartzshard.aasb.api.alchemy.aspects.AspectShape;
import com.quartzshard.aasb.api.alchemy.aspects.stack.legacy.LegacyFormStack;
import com.quartzshard.aasb.api.alchemy.aspects.stack.legacy.LegacyShapeStack;
import com.quartzshard.aasb.api.alchemy.aspects.stack.legacy.LegacyWayStack;
import com.quartzshard.aasb.api.alchemy.lab.LabFunctions;
import com.quartzshard.aasb.api.alchemy.lab.LegacyLabRecipeData;
import com.quartzshard.aasb.api.item.IStaticSpeedBreaker;
import com.quartzshard.aasb.api.item.IShapeRuneItem.ShapeRune;
import com.quartzshard.aasb.api.item.bind.ICanHandleKeybind;
import com.quartzshard.aasb.client.AASBKeys;
import com.quartzshard.aasb.common.item.flask.FlaskItem;
import com.quartzshard.aasb.common.item.flask.StorageFlaskItem;
import com.quartzshard.aasb.common.network.server.KeyPressPacket.BindState;
import com.quartzshard.aasb.common.network.server.KeyPressPacket.PressContext;
import com.quartzshard.aasb.common.network.server.KeyPressPacket.ServerBind;
import com.quartzshard.aasb.data.AASBLang;
import com.quartzshard.aasb.init.AlchemyInit.FormTree;
import com.quartzshard.aasb.init.AlchemyInit.TrinketRunes;
import com.quartzshard.aasb.init.ObjectInit;
import com.quartzshard.aasb.util.ClientHelper;
import com.quartzshard.aasb.util.NBTHelper;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.loading.FMLEnvironment;

/**
 * tool that can break any block <br>
 * used internally by the shovel areablast, but is also a nice dev / test item
 * @author solunareclipse1
 */
public class InternalOmnitool extends DiggerItem implements IStaticSpeedBreaker, ICanHandleKeybind {
	public InternalOmnitool(float damage, float speed, Tier tier, TagKey<Block> breakableBlocks, Properties props) {
		super(damage, speed, tier, breakableBlocks, props);
	}
	
	@Override
	public int getBarWidth(ItemStack stack) {
		return NBTHelper.Item.getBoolean(stack, "Instamine", false) ? 13 : 0;
	}
	
	@Override
	public boolean isBarVisible(ItemStack stack) {
		return true;
	}
	
	@Override
	public int getBarColor(ItemStack stack) {
		return AsAboveSoBelow.RAND.nextInt(0, (0xffffff)+1);
	}
	
	@Override
	public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
		if (selected) {
			if (entity instanceof Player plr && plr.hasPermissions(4)) {
				if (!isFoil(stack)) {
					NBTHelper.Item.setBoolean(stack, "IsExtremelyOP", true);
				}
			} else {
				if (isFoil(stack)) {
					NBTHelper.Item.setBoolean(stack, "IsExtremelyOP", false);
				}
			}
		}
	}
	
	@Override
	public boolean isFoil(ItemStack stack) {
		return NBTHelper.Item.getBoolean(stack, "IsExtremelyOP", false);
	}
	
	@Override
	public boolean isCorrectToolForDrops(ItemStack stack, BlockState state) {
		return isFoil(stack) || net.minecraftforge.common.TierSortingRegistry.isCorrectTierForDrops(AASBToolTier.HERMETIC, state);
	}

	public boolean toggleInstamine(ItemStack stack) {
		boolean wasInstamine = NBTHelper.Item.getBoolean(stack, "Instamine", false);
		NBTHelper.Item.setBoolean(stack, "Instamine", !wasInstamine);
		//if (wasInstamine)
		//	AlchemyInit.TrinketRunes.FIRE.get().combatAbility(stack, player, level, BindState.PRESSED);
		//else
		//	AlchemyInit.TrinketRunes.WATER.get().combatAbility(stack, player, level, BindState.PRESSED);
		return true;
	}

	@Override
	public int blockBreakSpeedInTicks(ItemStack stack, BlockState state) {
		return NBTHelper.Item.getBoolean(stack, "Instamine", false) ? 1 : 2;
	}

	/**
	 * serverside code can be thrown in here to quickly test it
	 */
	@Override
	public boolean handle(PressContext ctx) {
		boolean debug = false && !FMLEnvironment.production;
		if (debug) {
			// debug & test code
			if (ctx.state() == BindState.PRESSED) {
				quickAndDirtyRuntimeCodeTests(ctx);
			}
			return false;
		}
		switch (ctx.bind()) {
		case ITEMMODE:
			ServerPlayer plr = ctx.player();
			if (ctx.state() == BindState.PRESSED) {
				if (!plr.isShiftKeyDown()) {
					plr.displayClientMessage(new TextComponent("GODMODE"), false);
					plr.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 4));
					plr.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, Integer.MAX_VALUE, 0));
					plr.addEffect(new MobEffectInstance(MobEffects.HEAL, Integer.MAX_VALUE, 99));
					plr.addEffect(new MobEffectInstance(MobEffects.SATURATION, Integer.MAX_VALUE, 99));
				} else {
					plr.displayClientMessage(new TextComponent("mortal mode"), false);
					plr.removeEffect(MobEffects.DAMAGE_RESISTANCE);
					plr.removeEffect(MobEffects.FIRE_RESISTANCE);
					plr.removeEffect(MobEffects.HEAL);
					plr.removeEffect(MobEffects.SATURATION);
				}
			}
			return true;
		case ITEMFUNC_1:
			return ctx.state() == BindState.PRESSED
			&& TrinketRunes.FIRE.get().combatAbility(ctx.stack(), ctx.player(), ctx.level(), BindState.PRESSED, true);
		case ITEMFUNC_2:
			return ctx.state() == BindState.PRESSED
			&& TrinketRunes.ETHEREAL.get().combatAbility(ctx.stack(), ctx.player(), ctx.level(), BindState.PRESSED, true);
		case EMPOWER:
			return ctx.state() == BindState.PRESSED
			&& toggleInstamine(ctx.stack());
		default:
			return false;
		}
	}
	
	private void quickAndDirtyRuntimeCodeTests(PressContext ctx) {
		// debug & test code
		BadTestCode t = new BadTestCode(ctx);
		boolean failed = false;
		switch (ctx.bind()) {
		case ITEMMODE:
			ArrayList<ItemStack> items = new ArrayList<>();
			items.add(ctx.player().getOffhandItem());
			LegacyLabRecipeData in = new LegacyLabRecipeData(items, null, null, null, null);
			LegacyLabRecipeData out = LabFunctions.shapeDistillation(in);
			ItemStack stack = out.items.get(0);
			FlaskItem flask = (FlaskItem) stack.getItem();
			System.out.println(out.shapes.get(0).toTag());
			ctx.player().drop(stack, false);
			break;
		case ITEMFUNC_1:
			ArrayList<LegacyWayStack> ways = new ArrayList<>();
			ways.add(new LegacyWayStack(10));
			ways.add(new LegacyWayStack(15));
			LegacyLabRecipeData dat = LabFunctions.conjunction(new LegacyLabRecipeData(null,null,ways,null,null));
			if (dat != null && dat.ways != null && dat.ways.size() == 1 && dat.ways.get(0) != null && dat.ways.get(0).getAmount() == 25) {
				System.out.println("conjunction 10 + 15 = " + dat.ways.get(0).getAmount());
			} else failed = true;
			break;
		case ITEMFUNC_2:
			ArrayList<LegacyWayStack> waysf = new ArrayList<>();
			waysf.add(new LegacyWayStack(40000));
			waysf.add(new LegacyWayStack(150));
			LegacyLabRecipeData datf = LabFunctions.conjunction(new LegacyLabRecipeData(null,null,waysf,null,null));
			if ( datf == null ) {
				System.out.println("conjunction 40k + 150 = " + datf);
			} else failed = true;
			break;
		case EMPOWER:
			failed = t.testLabFuncs();
			break;
		default:
			break;
		}
		ctx.player().displayClientMessage(new TextComponent(failed ? "failure" : "success"), false);
	}
	
	/**
	 * please abstract any quick & dirty runtime code tests into functions here <br>
	 * TODO: if possible & necessary, turn stuff in here into proper tests <br><br>
	 * please never remove the above todo marker, thank you! - Sol
	 */
	private class BadTestCode {
		/* something easy to copy, for your convenience

		// TODO
		if (!failed) {
			failed = false;
		}
		 */
		final PressContext ctx;
		BadTestCode(PressContext ctx) {
			this.ctx = ctx;
		}
		private LegacyLabRecipeData lrd(
			@Nullable ArrayList<ItemStack> items,
			@Nullable ArrayList<FluidStack> fluids,
			@Nullable ArrayList<LegacyWayStack> ways,
			@Nullable ArrayList<LegacyShapeStack> shapes,
			@Nullable ArrayList<LegacyFormStack> forms) {
			return new LegacyLabRecipeData(items,fluids,ways,shapes,forms);
		}
		
		boolean testLabFuncs() {
			LabWayTests w = new LabWayTests();
			LabShapeTests s = new LabShapeTests();
			LabFormTests f = new LabFormTests();
			LabMiscTests m = new LabMiscTests();
			boolean failed = w.testAll()
							|| s.testAll()
							|| f.testAll()
							|| m.testAll();
			
			return failed;
		}
		class LabWayTests {
			boolean testAll() {
				return testSublimation()
						|| testConjunction()
						|| testStagnation()
						|| testSeparation()
						|| testFiltration()
						|| testCondensation();
			}
			
			boolean testSublimation() {
				boolean failed = false;
				
				// valid
				if (!failed) {
					ArrayList<ItemStack> items = new ArrayList<>();
					items.add(new ItemStack(Items.BLAZE_POWDER));
					LegacyLabRecipeData dat = LabFunctions.sublimation(lrd(items,null,null,null,null));
					// fails if null, if items isnt null, or ways is null
					if (dat != null && dat.items != null && dat.ways != null) {
						System.out.println(dat.items);
						System.out.print("waystacks: { ");
						for (LegacyWayStack w : dat.ways) {
							System.out.print(w.getAmount() + ", ");
						}
						System.out.println(" }");
					} else {
						failed = true;
					}
				}

				// invalid
				if (false && !failed) { // TODO: re-enable when mapper
					ArrayList<ItemStack> items = new ArrayList<>();
					items.add(new ItemStack(Items.AIR));
					LegacyLabRecipeData dat = LabFunctions.sublimation(lrd(items,null,null,null,null));
					// fails if not null
					failed = dat != null;
				}
				
				return failed;
			}
			boolean testConjunction() {
				boolean failed = false;
				
				// valid
				if (!failed) {
					ArrayList<LegacyWayStack> ways = new ArrayList<>();
					ways.add(new LegacyWayStack(10));
					ways.add(new LegacyWayStack(15));
					LegacyLabRecipeData dat = LabFunctions.conjunction(lrd(null,null,ways,null,null));
					if (dat != null && dat.ways != null && dat.ways.size() == 1 && dat.ways.get(0) != null && dat.ways.get(0).getAmount() == 25) {
						System.out.println("conjunction 10 + 15 = " + dat.ways.get(0).getAmount());
					} else failed = true;
				}
				
				// invalid
				if (!failed) {
					ArrayList<LegacyWayStack> ways = new ArrayList<>();
					ways.add(new LegacyWayStack(10000));
					ways.add(new LegacyWayStack(10));
					LegacyLabRecipeData dat = LabFunctions.conjunction(lrd(null,null,ways,null,null));
					if ( dat == null ) {
						System.out.println("conjunction 40k + 150 = " + dat);
					} else failed = true;
				}
				
				// lower bound
				if (!failed) {
					ArrayList<LegacyWayStack> ways = new ArrayList<>();
					ways.add(new LegacyWayStack(64));
					ways.add(new LegacyWayStack(32));
					LegacyLabRecipeData dat = LabFunctions.conjunction(lrd(null,null,ways,null,null));
					if (dat != null && dat.ways != null && dat.ways.size() == 1 && dat.ways.get(0) != null && dat.ways.get(0).getAmount() == 96) {
						System.out.println("conjunction 64 + 32 = " + dat.ways.get(0).getAmount());
					} else failed = true;
				}
				
				// upper bound
				if (!failed) {
					ArrayList<LegacyWayStack> ways = new ArrayList<>();
					ways.add(new LegacyWayStack(64));
					ways.add(new LegacyWayStack(128));
					LegacyLabRecipeData dat = LabFunctions.conjunction(lrd(null,null,ways,null,null));
					if (dat != null && dat.ways != null && dat.ways.size() == 1 && dat.ways.get(0) != null && dat.ways.get(0).getAmount() == 192) {
						System.out.println("conjunction 64 + 128 = " + dat.ways.get(0).getAmount());
					} else failed = true;
				}
				
				return failed;
			}
			boolean testStagnation() {
				boolean failed = false;
				
				// valid
				if (!failed) {
					ArrayList<LegacyWayStack> ways = new ArrayList<>();
					long i = AsAboveSoBelow.RAND.nextLong(4, 1000);
					System.out.println(i);
					ways.add(new LegacyWayStack(i));
					LegacyLabRecipeData dat = LabFunctions.stagnation(lrd(null,null,ways,null,null));
					if (dat != null && dat.ways != null && dat.ways.size() > 0 && dat.ways.get(0).getAmount() == i-3) {
						System.out.print("waystacks: { ");
						for (LegacyWayStack w : dat.ways) {
							System.out.print(w.getAmount() + ", ");
						}
						System.out.println(" }");
					} else {
						failed = true;
					}
				}

				// invalid
				if (!failed) {
					ArrayList<LegacyWayStack> ways = new ArrayList<>();
					long i = AsAboveSoBelow.RAND.nextLong(0, 4);
					System.out.println(i);
					ways.add(new LegacyWayStack(i));
					LegacyLabRecipeData dat = LabFunctions.stagnation(lrd(null,null,ways,null,null));
					if (dat == null) {
					} else {
						failed = true;
					}
				}
				
				return failed;
			}
			boolean testSeparation() {
				boolean failed = false;
				
				// valid
				if (!failed) {
					ArrayList<LegacyWayStack> ways = new ArrayList<>();
					long i = 50;
					System.out.println(i);
					ways.add(new LegacyWayStack(i));
					LegacyLabRecipeData dat = LabFunctions.separation(lrd(null,null,ways,null,null));
					if (dat != null && dat.ways != null && dat.ways.size() == 2 && dat.ways.get(0).getAmount() == 25) {
						System.out.print("waystacks: { ");
						for (LegacyWayStack w : dat.ways) {
							System.out.print(w.getAmount() + ", ");
						}
						System.out.println(" }");
					} else {
						failed = true;
					}
				}
				
				// invalid
				if (!failed) {
					ArrayList<LegacyWayStack> ways = new ArrayList<>();
					long i = 49;
					System.out.println(i);
					ways.add(new LegacyWayStack(i));
					LegacyLabRecipeData dat = LabFunctions.separation(lrd(null,null,ways,null,null));
					if (dat == null) {
					} else {
						failed = true;
					}
				}
				
				return failed;
			}
			boolean testFiltration() {
				boolean failed = false;
				
				// valid
				if (!failed) {
					ArrayList<LegacyWayStack> ways = new ArrayList<>();
					long i = AsAboveSoBelow.RAND.nextLong(2,10);
					long j = i*11;
					System.out.println(i +" & "+ j);
					ways.add(new LegacyWayStack(j));
					ArrayList<ItemStack> items = new ArrayList<>();
					items.add(new ItemStack(Items.STONE_PICKAXE));
					LegacyLabRecipeData dat = LabFunctions.filtration(lrd(items,null,ways,null,null));
					//System.out.println(dat.ways.get(0).getAmount());
					if (dat != null && dat.items != null && dat.ways != null && dat.items.size() > 0 && dat.items.get(0).is(Items.STONE_PICKAXE) && dat.ways.size() == i && dat.ways.get(0).getAmount() == 11) {
						System.out.print("waystacks: { ");
						for (LegacyWayStack w : dat.ways) {
							System.out.print(w.getAmount() + ", ");
						}
						System.out.println(" }");
					} else {
						failed = true;
					}
				}
				
				// shenanigans
				if (!failed) {
					ArrayList<LegacyWayStack> ways = new ArrayList<>();
					long i = AsAboveSoBelow.RAND.nextLong(2,10);
					long k = AsAboveSoBelow.RAND.nextLong(1,11);
					long j = i*11 + k;
					System.out.println(i +" & "+ j);
					ways.add(new LegacyWayStack(j));
					ArrayList<ItemStack> items = new ArrayList<>();
					items.add(new ItemStack(Items.STONE_PICKAXE));
					LegacyLabRecipeData dat = LabFunctions.filtration(lrd(items,null,ways,null,null));
					//System.out.println(dat.ways.get(0).getAmount());
					if (dat != null && dat.ways != null && dat.ways.size() == i+1 && dat.ways.get(0).getAmount() == k) {
						System.out.print("waystacks: { ");
						for (LegacyWayStack w : dat.ways) {
							System.out.print(w.getAmount() + ", ");
						}
						System.out.println(" }");
					} else {
						failed = true;
					}
				}
				
				// invalid
				if (!failed) {
					ArrayList<LegacyWayStack> ways = new ArrayList<>();
					long i = AsAboveSoBelow.RAND.nextLong(2,10);
					System.out.println(i +" bad ");
					ways.add(new LegacyWayStack(i));
					ArrayList<ItemStack> items = new ArrayList<>();
					items.add(new ItemStack(Items.STONE_PICKAXE));
					LegacyLabRecipeData dat = LabFunctions.filtration(lrd(items,null,ways,null,null));
					//System.out.println(dat.ways.get(0).getAmount());
					if (dat == null) {
					} else {
						failed = true;
					}
				}
				
				return failed;
			}
			boolean testCondensation() {
				boolean failed = false;
				
				// TODO: NYI
				
				return failed;
			}
		}

		class LabShapeTests {
			boolean testAll() {
				return testDesiccation()
						|| testShapeDistillation()
						|| testOxidation()
						|| testCongelation()
						|| testCeration()
						|| testDehydration()
						|| testExaltation()
						|| testCondemnation();
			}
			
			boolean testDesiccation() {
				boolean failed = false;
				
				// valid
				if (!failed) {
					ArrayList<ItemStack> items = new ArrayList<>();
					items.add(new ItemStack(Items.BLAZE_POWDER));
					LegacyLabRecipeData dat = LabFunctions.desiccation(lrd(items,null,null,null,null));
					// fails if null, if items isnt null, or ways is null
					if (dat != null && dat.items != null && dat.shapes != null && dat.items.size() > 0 && dat.items.get(0).is(ObjectInit.Items.SALT.get()) && dat.shapes.size() > 0 && dat.shapes.get(0).getShape() == AspectShape.FIRE) {
						System.out.println(dat.items);
						for (LegacyShapeStack s : dat.shapes) {
							System.out.print(s.getShape()+", ");
						}
						System.out.println();
					} else {
						failed = true;
					}
				}

				// invalid
				if (false && !failed) { // TODO: re-enable when mapper
					ArrayList<ItemStack> items = new ArrayList<>();
					items.add(new ItemStack(Items.AIR));
					LegacyLabRecipeData dat = LabFunctions.desiccation(lrd(items,null,null,null,null));
					// fails if not null
					failed = dat != null;
				}
				
				return failed;
			}
			boolean testShapeDistillation() {
				boolean failed = false;
				
				// lead
				if (!failed) {
					ArrayList<ItemStack> items = new ArrayList<>();
					ItemStack item = new ItemStack(ObjectInit.Items.FLASK_LEAD.get());
					FlaskItem flask = (FlaskItem)item.getItem();
					flask.setStored(item, AspectShape.WATER, FormTree.ANIMAL.get(), ctx.level().getGameTime());
					items.add(item);
					LegacyLabRecipeData dat = LabFunctions.shapeDistillation(lrd(items,null,null,null,null));
					if ( dat != null && dat.items != null && dat.items.size() > 0 && flask.isContaminated(dat.items.get(0)) && dat.shapes != null && dat.shapes.size() > 0 && dat.shapes.get(0).getShape() == AspectShape.WATER ) {
						System.out.println(dat.items);
						for (LegacyShapeStack s : dat.shapes) {
							System.out.print(s.getShape()+", ");
						}
						System.out.println();
					} else {
						failed = true;
					}
				}
				
				// lead (bad)
				if (!failed) {
					ArrayList<ItemStack> items = new ArrayList<>();
					ItemStack item = new ItemStack(ObjectInit.Items.FLASK_LEAD.get());
					FlaskItem flask = (FlaskItem)item.getItem();
					flask.setStored(item, AspectShape.WATER, FormTree.ANIMAL.get(), ctx.level().getGameTime());
					flask.setContaminated(item, true);
					items.add(item);
					LegacyLabRecipeData dat = LabFunctions.shapeDistillation(lrd(items,null,null,null,null));
					if ( dat == null ) {
					} else {
						failed = true;
					}
				}
				
				// gold
				if (!failed) {
					ArrayList<ItemStack> items = new ArrayList<>();
					ItemStack item = new ItemStack(ObjectInit.Items.FLASK_GOLD.get());
					FlaskItem flask = (FlaskItem)item.getItem();
					flask.setStored(item, AspectShape.EARTH, FormTree.SUN.get(), ctx.level().getGameTime());
					items.add(item);
					LegacyLabRecipeData dat = LabFunctions.shapeDistillation(lrd(items,null,null,null,null));
					if ( dat != null && dat.items != null && dat.items.size() > 0 && flask.isContaminated(dat.items.get(0)) && dat.shapes != null && dat.shapes.size() > 0 && dat.shapes.get(0).getShape() == AspectShape.EARTH ) {
						System.out.println(dat.items);
						for (LegacyShapeStack s : dat.shapes) {
							System.out.print(s.getShape()+", ");
						}
						System.out.println();
					} else {
						failed = true;
					}
				}
				
				// gold (bad)
				if (!failed) {
					ArrayList<ItemStack> items = new ArrayList<>();
					ItemStack item = new ItemStack(ObjectInit.Items.FLASK_GOLD.get());
					FlaskItem flask = (FlaskItem)item.getItem();
					flask.setStored(item, AspectShape.EARTH, FormTree.SUN.get(), ctx.level().getGameTime());
					flask.setContaminated(item, true);
					items.add(item);
					LegacyLabRecipeData dat = LabFunctions.shapeDistillation(lrd(items,null,null,null,null));
					if ( dat == null ) {
					} else {
						failed = true;
					}
				}
				
				// aether
				if (!failed) {
					ArrayList<ItemStack> items = new ArrayList<>();
					ItemStack item = new ItemStack(ObjectInit.Items.FLASK_AETHER.get());
					FlaskItem flask = (FlaskItem)item.getItem();
					flask.setStored(item, AspectShape.UNIVERSAL, null, ctx.level().getGameTime());
					items.add(item);
					LegacyLabRecipeData dat = LabFunctions.shapeDistillation(lrd(items,null,null,null,null));
					if ( dat != null && dat.items != null && dat.items.size() > 0 && !flask.isContaminated(dat.items.get(0)) && !flask.hasStored(dat.items.get(0)) && dat.shapes != null && dat.shapes.size() > 0 && dat.shapes.get(0).getShape() == AspectShape.UNIVERSAL ) {
						System.out.println(dat.items);
						for (LegacyShapeStack s : dat.shapes) {
							System.out.print(s.getShape()+", ");
						}
						System.out.println();
					} else {
						failed = true;
					}
				}
				
				// aether (bad)
				if (!failed) {
					ArrayList<ItemStack> items = new ArrayList<>();
					ItemStack item = new ItemStack(ObjectInit.Items.FLASK_AETHER.get());
					FlaskItem flask = (FlaskItem)item.getItem();
					flask.setStored(item, AspectShape.UNIVERSAL, null, ctx.level().getGameTime());
					flask.setContaminated(item, true);
					items.add(item);
					LegacyLabRecipeData dat = LabFunctions.shapeDistillation(lrd(items,null,null,null,null));
					if ( dat == null ) {
					} else {
						failed = true;
					}
				}
				
				// nonsense
				if (!failed) {
					ArrayList<ItemStack> items = new ArrayList<>();
					ItemStack item = new ItemStack(Items.DIRT);
					items.add(item);
					LegacyLabRecipeData dat = LabFunctions.shapeDistillation(lrd(items,null,null,null,null));
					if ( dat == null ) {
					} else {
						failed = true;
					}
				}
				
				return failed;
			}
			boolean testOxidation() {
				boolean failed = false;
				
				// valid
				if (!failed) {
					ArrayList<LegacyShapeStack> shapes = new ArrayList<>();
					shapes.add(new LegacyShapeStack(AspectShape.AIR));
					LegacyLabRecipeData dat = LabFunctions.oxidation(lrd(null,null,null,shapes,null));
					if (dat != null && dat.shapes != null && dat.shapes.size() > 0 && dat.shapes.get(0).getShape() == AspectShape.FIRE) {
						for (LegacyShapeStack s : dat.shapes) {
							System.out.print(s.getShape()+", ");
						}
						System.out.println();
					} else failed = true;
				}
				
				// invalid
				if (!failed) {
					ArrayList<LegacyShapeStack> shapes = new ArrayList<>();
					shapes.add(new LegacyShapeStack(AspectShape.FIRE));
					LegacyLabRecipeData dat = LabFunctions.oxidation(lrd(null,null,null,shapes,null));
					if (dat == null) {
					} else failed = true;
				}
				
				return failed;
			}
			boolean testCongelation() {
				boolean failed = false;
				
				// valid
				if (!failed) {
					ArrayList<LegacyShapeStack> shapes = new ArrayList<>();
					shapes.add(new LegacyShapeStack(AspectShape.FIRE));
					LegacyLabRecipeData dat = LabFunctions.congelation(lrd(null,null,null,shapes,null));
					if (dat != null && dat.shapes != null && dat.shapes.size() > 0 && dat.shapes.get(0).getShape() == AspectShape.EARTH) {
						for (LegacyShapeStack s : dat.shapes) {
							System.out.print(s.getShape()+", ");
						}
						System.out.println();
					} else failed = true;
				}
				
				// invalid
				if (!failed) {
					ArrayList<LegacyShapeStack> shapes = new ArrayList<>();
					shapes.add(new LegacyShapeStack(AspectShape.EARTH));
					LegacyLabRecipeData dat = LabFunctions.congelation(lrd(null,null,null,shapes,null));
					if (dat == null) {
					} else failed = true;
				}
				
				return failed;
			}
			boolean testCeration() {
				boolean failed = false;
				
				// valid
				if (!failed) {
					ArrayList<LegacyShapeStack> shapes = new ArrayList<>();
					shapes.add(new LegacyShapeStack(AspectShape.EARTH));
					LegacyLabRecipeData dat = LabFunctions.ceration(lrd(null,null,null,shapes,null));
					if (dat != null && dat.shapes != null && dat.shapes.size() > 0 && dat.shapes.get(0).getShape() == AspectShape.WATER) {
						for (LegacyShapeStack s : dat.shapes) {
							System.out.print(s.getShape()+", ");
						}
						System.out.println();
					} else failed = true;
				}
				
				// invalid
				if (!failed) {
					ArrayList<LegacyShapeStack> shapes = new ArrayList<>();
					shapes.add(new LegacyShapeStack(AspectShape.WATER));
					LegacyLabRecipeData dat = LabFunctions.ceration(lrd(null,null,null,shapes,null));
					if (dat == null) {
					} else failed = true;
				}
				
				return failed;
			}
			boolean testDehydration() {
				boolean failed = false;
				
				// valid
				if (!failed) {
					ArrayList<LegacyShapeStack> shapes = new ArrayList<>();
					shapes.add(new LegacyShapeStack(AspectShape.WATER));
					LegacyLabRecipeData dat = LabFunctions.dehydration(lrd(null,null,null,shapes,null));
					if (dat != null && dat.shapes != null && dat.shapes.size() > 0 && dat.shapes.get(0).getShape() == AspectShape.AIR) {
						for (LegacyShapeStack s : dat.shapes) {
							System.out.print(s.getShape()+", ");
						}
						System.out.println();
					} else failed = true;
				}
				
				// invalid
				if (!failed) {
					ArrayList<LegacyShapeStack> shapes = new ArrayList<>();
					shapes.add(new LegacyShapeStack(AspectShape.AIR));
					LegacyLabRecipeData dat = LabFunctions.dehydration(lrd(null,null,null,shapes,null));
					if (dat == null) {
					} else failed = true;
				}
				
				return failed;
			}
			boolean testExaltation() {
				boolean failed = false;
				
				// valid
				if (!failed) {
					ArrayList<LegacyShapeStack> shapes = new ArrayList<>();
					shapes.add(new LegacyShapeStack(AspectShape.WATER));
					shapes.add(new LegacyShapeStack(AspectShape.EARTH));
					shapes.add(new LegacyShapeStack(AspectShape.FIRE));
					shapes.add(new LegacyShapeStack(AspectShape.AIR));
					LegacyLabRecipeData dat = LabFunctions.exaltation(lrd(null,null,null,shapes,null));
					if (dat != null && dat.shapes != null && dat.shapes.size() > 0 && dat.shapes.get(0).getShape() == AspectShape.UNIVERSAL) {
						for (LegacyShapeStack s : dat.shapes) {
							System.out.print(s.getShape()+", ");
						}
						System.out.println();
					} else failed = true;
				}
				
				// valid 2
				if (!failed) {
					ArrayList<LegacyShapeStack> shapes = new ArrayList<>();
					shapes.add(new LegacyShapeStack(AspectShape.AIR));
					shapes.add(new LegacyShapeStack(AspectShape.EARTH));
					shapes.add(new LegacyShapeStack(AspectShape.WATER));
					shapes.add(new LegacyShapeStack(AspectShape.FIRE));
					LegacyLabRecipeData dat = LabFunctions.exaltation(lrd(null,null,null,shapes,null));
					if (dat != null && dat.shapes != null && dat.shapes.size() > 0 && dat.shapes.get(0).getShape() == AspectShape.UNIVERSAL) {
						for (LegacyShapeStack s : dat.shapes) {
							System.out.print(s.getShape()+", ");
						}
						System.out.println();
					} else failed = true;
				}
				
				// invalid
				if (!failed) {
					ArrayList<LegacyShapeStack> shapes = new ArrayList<>();
					shapes.add(new LegacyShapeStack(AspectShape.AIR));
					shapes.add(new LegacyShapeStack(AspectShape.WATER));
					shapes.add(new LegacyShapeStack(AspectShape.WATER));
					shapes.add(new LegacyShapeStack(AspectShape.EARTH));
					LegacyLabRecipeData dat = LabFunctions.exaltation(lrd(null,null,null,shapes,null));
					if (dat == null) {
					} else failed = true;
				}
				
				return failed;
			}
			boolean testCondemnation() {
				boolean failed = false;
				
				// valid
				if (!failed) {
					ArrayList<ItemStack> items = new ArrayList<>();
					items.add(new ItemStack(Items.FEATHER));
					ArrayList<LegacyShapeStack> shapes = new ArrayList<>();
					shapes.add(new LegacyShapeStack(AspectShape.UNIVERSAL));
					LegacyLabRecipeData dat = LabFunctions.condemnation(lrd(items,null,null,shapes,null));
					if (dat != null && dat.shapes != null && dat.shapes.size() > 0 && dat.shapes.get(0).getShape() == AspectShape.AIR && dat.shapes.get(0).getAmount() == 4 && dat.items != null && dat.items.size() > 0 && dat.items.get(0).is(Items.FEATHER)) {
						System.out.println(dat.items);
						for (LegacyShapeStack s : dat.shapes) {
							System.out.print(s.getShape()+" "+ s.getAmount() +", ");
						}
						System.out.println();
					} else failed = true;
				}
				
				// invalid
				if (!failed) {
					ArrayList<ItemStack> items = new ArrayList<>();
					ArrayList<LegacyShapeStack> shapes = new ArrayList<>();
					shapes.add(new LegacyShapeStack(AspectShape.UNIVERSAL));
					LegacyLabRecipeData dat = LabFunctions.condemnation(lrd(items,null,null,shapes,null));
					if (dat == null) {
					} else failed = true;
				}
				
				return failed;
			}
		}

		class LabFormTests {
			boolean testAll() {
				return testEvaporation()
						|| testFormDistillation()
						|| testFixation()
						|| testAmalgamation()
						|| testHomogenization();
			}
			
			boolean testEvaporation() {
				boolean failed = false;
				
				// valid
				if (!failed) {
					ArrayList<ItemStack> items = new ArrayList<>();
					items.add(new ItemStack(Items.BLAZE_POWDER));
					LegacyLabRecipeData dat = LabFunctions.evaporation(lrd(items,null,null,null,null));
					// fails if null, if items isnt null, or ways is null
					if (dat != null && dat.items != null && dat.forms != null && dat.items.size() > 0 && dat.items.get(0).is(ObjectInit.Items.SOOT.get()) && dat.forms.size() > 0 && dat.forms.get(0).getForm() == FormTree.WITCHCRAFT.get()) {
						System.out.println(dat.items);
						for (LegacyFormStack s : dat.forms) {
							System.out.print(s.getForm().getName()+", ");
						}
						System.out.println();
					} else {
						failed = true;
					}
				}

				// invalid
				if (false && !failed) { // TODO: re-enable when mapper
					ArrayList<ItemStack> items = new ArrayList<>();
					items.add(new ItemStack(Items.AIR));
					LegacyLabRecipeData dat = LabFunctions.desiccation(lrd(items,null,null,null,null));
					// fails if not null
					failed = dat != null;
				}
				
				return failed;
			}
			boolean testFormDistillation() {
				boolean failed = false;
				
				// lead
				if (!failed) {
					ArrayList<ItemStack> items = new ArrayList<>();
					ItemStack item = new ItemStack(ObjectInit.Items.FLASK_LEAD.get());
					FlaskItem flask = (FlaskItem)item.getItem();
					flask.setStored(item, AspectShape.WATER, FormTree.ANIMAL.get(), ctx.level().getGameTime());
					items.add(item);
					LegacyLabRecipeData dat = LabFunctions.formDistillation(lrd(items,null,null,null,null));
					if ( dat != null && dat.items != null && dat.items.size() > 0 && flask.isContaminated(dat.items.get(0)) && dat.forms != null && dat.forms.size() > 0 && dat.forms.get(0).getForm() == FormTree.ANIMAL.get() ) {
						System.out.println(dat.items);
						for (LegacyFormStack s : dat.forms) {
							System.out.print(s.getForm().getName()+", ");
						}
						System.out.println();
					} else {
						failed = true;
					}
				}
				
				// lead (bad)
				if (!failed) {
					ArrayList<ItemStack> items = new ArrayList<>();
					ItemStack item = new ItemStack(ObjectInit.Items.FLASK_LEAD.get());
					FlaskItem flask = (FlaskItem)item.getItem();
					flask.setStored(item, AspectShape.WATER, FormTree.ANIMAL.get(), ctx.level().getGameTime());
					flask.setContaminated(item, true);
					items.add(item);
					LegacyLabRecipeData dat = LabFunctions.formDistillation(lrd(items,null,null,null,null));
					if ( dat == null ) {
					} else {
						failed = true;
					}
				}
				
				// gold
				if (!failed) {
					ArrayList<ItemStack> items = new ArrayList<>();
					ItemStack item = new ItemStack(ObjectInit.Items.FLASK_GOLD.get());
					FlaskItem flask = (FlaskItem)item.getItem();
					flask.setStored(item, AspectShape.EARTH, FormTree.SUN.get(), ctx.level().getGameTime());
					items.add(item);
					LegacyLabRecipeData dat = LabFunctions.formDistillation(lrd(items,null,null,null,null));
					if ( dat != null && dat.items != null && dat.items.size() > 0 && flask.isContaminated(dat.items.get(0)) && dat.forms != null && dat.forms.size() > 0 && dat.forms.get(0).getForm() == FormTree.SUN.get() ) {
						System.out.println(dat.items);
						for (LegacyFormStack s : dat.forms) {
							System.out.print(s.getForm().getName()+", ");
						}
						System.out.println();
					} else {
						failed = true;
					}
				}
				
				// gold (bad)
				if (!failed) {
					ArrayList<ItemStack> items = new ArrayList<>();
					ItemStack item = new ItemStack(ObjectInit.Items.FLASK_GOLD.get());
					FlaskItem flask = (FlaskItem)item.getItem();
					flask.setStored(item, AspectShape.EARTH, FormTree.SUN.get(), ctx.level().getGameTime());
					flask.setContaminated(item, true);
					items.add(item);
					LegacyLabRecipeData dat = LabFunctions.formDistillation(lrd(items,null,null,null,null));
					if ( dat == null ) {
					} else {
						failed = true;
					}
				}
				
				// aether
				if (!failed) {
					ArrayList<ItemStack> items = new ArrayList<>();
					ItemStack item = new ItemStack(ObjectInit.Items.FLASK_AETHER.get());
					FlaskItem flask = (FlaskItem)item.getItem();
					flask.setStored(item, null, FormTree.ETHEREAL.get(), ctx.level().getGameTime());
					items.add(item);
					LegacyLabRecipeData dat = LabFunctions.formDistillation(lrd(items,null,null,null,null));
					if ( dat != null && dat.items != null && dat.items.size() > 0 && !flask.isContaminated(dat.items.get(0)) && !flask.hasStored(dat.items.get(0)) && dat.forms != null && dat.forms.size() > 0 && dat.forms.get(0).getForm() == FormTree.ETHEREAL.get() ) {
						System.out.println(dat.items);
						for (LegacyFormStack s : dat.forms) {
							System.out.print(s.getForm().getName()+", ");
						}
						System.out.println();
					} else {
						failed = true;
					}
				}
				
				// aether (bad)
				if (!failed) {
					ArrayList<ItemStack> items = new ArrayList<>();
					ItemStack item = new ItemStack(ObjectInit.Items.FLASK_AETHER.get());
					FlaskItem flask = (FlaskItem)item.getItem();
					flask.setStored(item, null, FormTree.ETHEREAL.get(), ctx.level().getGameTime());
					flask.setContaminated(item, true);
					items.add(item);
					LegacyLabRecipeData dat = LabFunctions.formDistillation(lrd(items,null,null,null,null));
					if ( dat == null ) {
					} else {
						failed = true;
					}
				}
				
				// nonsense
				if (!failed) {
					ArrayList<ItemStack> items = new ArrayList<>();
					ItemStack item = new ItemStack(Items.DIRT);
					items.add(item);
					LegacyLabRecipeData dat = LabFunctions.formDistillation(lrd(items,null,null,null,null));
					if ( dat == null ) {
					} else {
						failed = true;
					}
				}
				
				return failed;
			}
			boolean testFixation() {
				boolean failed = false;
				
				// valid
				if (!failed) {
					ArrayList<ItemStack> items = new ArrayList<>();
					items.add(new ItemStack(Items.IRON_BLOCK));
					ArrayList<LegacyFormStack> forms = new ArrayList<>();
					forms.add(new LegacyFormStack(FormTree.PLANT.get()));
					LegacyLabRecipeData dat = LabFunctions.fixation(lrd(items,null,null,null,forms));
					if (dat != null && dat.items != null && dat.items.size() > 0 && dat.items.get(0).is(Items.IRON_BLOCK) && dat.forms != null && dat.forms.size() > 0 && dat.forms.get(0).getForm() == FormTree.MARS.get()) {
						System.out.println(dat.items);
						for (LegacyFormStack s : dat.forms) {
							System.out.print(s.getForm().getName()+", ");
						}
						System.out.println();
					} else failed = true;
				}
				
				// invalid
				if (!failed) {
					ArrayList<ItemStack> items = new ArrayList<>();
					items.add(new ItemStack(Items.IRON_BLOCK));
					ArrayList<LegacyFormStack> forms = new ArrayList<>();
					forms.add(new LegacyFormStack(FormTree.DULL.get()));
					LegacyLabRecipeData dat = LabFunctions.fixation(lrd(items,null,null,null,forms));
					if (dat == null) {
					} else failed = true;
				}
				
				return failed;
			}
			boolean testAmalgamation() {
				boolean failed = false;
				
				// valid
				if (!failed) {
					ArrayList<ItemStack> items = new ArrayList<>();
					items.add(new ItemStack(Items.GOLD_BLOCK));
					ArrayList<LegacyFormStack> forms = new ArrayList<>();
					forms.add(new LegacyFormStack(FormTree.MARS.get()));
					LegacyLabRecipeData dat = LabFunctions.amalgamation(lrd(items,null,null,null,forms));
					if (dat != null && dat.items != null && dat.items.size() > 0 && dat.items.get(0).is(Items.GOLD_BLOCK) && dat.forms != null && dat.forms.size() > 0 && dat.forms.get(0).getForm() == FormTree.SUN.get()) {
						System.out.println(dat.items);
						for (LegacyFormStack s : dat.forms) {
							System.out.print(s.getForm().getName()+", ");
						}
						System.out.println();
					} else failed = true;
				}
				
				// invalid
				if (!failed) {
					ArrayList<ItemStack> items = new ArrayList<>();
					items.add(new ItemStack(Items.GOLD_BLOCK));
					ArrayList<LegacyFormStack> forms = new ArrayList<>();
					forms.add(new LegacyFormStack(FormTree.MATERIA.get()));
					LegacyLabRecipeData dat = LabFunctions.amalgamation(lrd(items,null,null,null,forms));
					if (dat == null) {
					} else failed = true;
				}
				
				return failed;
			}
			boolean testHomogenization() {
				boolean failed = false;
				
				// valid with shape
				if (!failed) {
					ArrayList<LegacyShapeStack> shapes = new ArrayList<>();
					shapes.add(new LegacyShapeStack(AspectShape.UNIVERSAL));
					ArrayList<LegacyFormStack> forms = new ArrayList<>();
					forms.add(new LegacyFormStack(FormTree.PLANT.get()));
					LegacyLabRecipeData dat = LabFunctions.homogenization(lrd(null,null,null,shapes,forms));
					if (dat != null && dat.forms != null && dat.forms.size() > 0 && dat.forms.get(0).getForm() == FormTree.ALIVE.get() && dat.shapes == null) {
						for (LegacyFormStack s : dat.forms) {
							System.out.print(s.getForm().getName()+", ");
						}
						System.out.println();
					} else failed = true;
				}
				
				// valid with form
				if (!failed) {
					ArrayList<LegacyFormStack> forms = new ArrayList<>();
					forms.add(new LegacyFormStack(FormTree.IMMORTAL.get()));
					forms.add(new LegacyFormStack(FormTree.MATERIA.get()));
					LegacyLabRecipeData dat = LabFunctions.homogenization(lrd(null,null,null,null,forms));
					if (dat != null && dat.forms != null && dat.forms.size() == 1 && dat.forms.get(0).getForm() == FormTree.ORGANIC.get()) {
						for (LegacyFormStack s : dat.forms) {
							System.out.print(s.getForm().getName()+", ");
						}
						System.out.println();
					} else failed = true;
				}
				
				// valid with BOTH
				if (!failed) {
					ArrayList<LegacyShapeStack> shapes = new ArrayList<>();
					shapes.add(new LegacyShapeStack(AspectShape.UNIVERSAL));
					ArrayList<LegacyFormStack> forms = new ArrayList<>();
					forms.add(new LegacyFormStack(FormTree.SOIL.get()));
					forms.add(new LegacyFormStack(FormTree.MATERIA.get()));
					LegacyLabRecipeData dat = LabFunctions.homogenization(lrd(null,null,null,shapes,forms));
					if (dat != null && dat.forms != null && dat.forms.size() == 2 && dat.forms.get(0).getForm() == FormTree.TERRAIN.get() && dat.shapes == null) {
						for (LegacyFormStack s : dat.forms) {
							System.out.print(s.getForm().getName()+", ");
						}
						System.out.println();
					} else failed = true;
				}
				
				// invalid no catalyst
				if (!failed) {
					ArrayList<LegacyShapeStack> shapes = new ArrayList<>();
					//shapes.add(new ShapeStack(AspectShape.UNIVERSAL));
					ArrayList<LegacyFormStack> forms = new ArrayList<>();
					forms.add(new LegacyFormStack(FormTree.SOIL.get()));
					//forms.add(new FormStack(FormTree.MATERIA.get()));
					LegacyLabRecipeData dat = LabFunctions.homogenization(lrd(null,null,null,null,forms));
					if (dat == null) {
					} else failed = true;
				}
				
				// invalid wrong order
				if (!failed) {
					//ArrayList<ShapeStack> shapes = new ArrayList<>();
					//shapes.add(new ShapeStack(AspectShape.UNIVERSAL));
					ArrayList<LegacyFormStack> forms = new ArrayList<>();
					forms.add(new LegacyFormStack(FormTree.MATERIA.get()));
					forms.add(new LegacyFormStack(FormTree.MONSTER.get()));
					LegacyLabRecipeData dat = LabFunctions.homogenization(lrd(null,null,null,null,forms));
					if (dat == null) {
					} else failed = true;
				}
				
				// invalid no parent
				if (!failed) {
					ArrayList<LegacyShapeStack> shapes = new ArrayList<>();
					shapes.add(new LegacyShapeStack(AspectShape.UNIVERSAL));
					ArrayList<LegacyFormStack> forms = new ArrayList<>();
					forms.add(new LegacyFormStack(FormTree.MATERIA.get()));
					LegacyLabRecipeData dat = LabFunctions.homogenization(lrd(null,null,null,shapes,forms));
					if (dat == null) {
					} else failed = true;
				}
				
				return failed;
			}
		}

		class LabMiscTests {
			boolean testAll() {
				return testCohobation()
						|| testProjection()
						|| testSolution()
						|| testDigestion()
						|| testMultiplication();
			}

			boolean testCohobation() {
				boolean failed = false;
				
				// gold
				if (!failed) {
					ArrayList<ItemStack> items = new ArrayList<>();
					ItemStack item = new ItemStack(ObjectInit.Items.FLASK_GOLD.get());
					FlaskItem flask = (FlaskItem)item.getItem();
					flask.setStored(item, AspectShape.EARTH, FormTree.SUN.get(), ctx.level().getGameTime());
					items.add(item);
					LegacyLabRecipeData dat = LabFunctions.cohobation(lrd(items,null,null,null,null));
					if ( dat != null && dat.items != null && dat.items.size() > 0 && !flask.isContaminated(dat.items.get(0)) && dat.forms != null && dat.shapes != null && dat.forms.size() > 0 && dat.shapes.size() > 0 && dat.forms.get(0).getForm() == FormTree.SUN.get() && dat.shapes.get(0).getShape() == AspectShape.EARTH ) {
						System.out.println(dat.items);
						for (LegacyFormStack s : dat.forms) {
							System.out.print(s.getForm().getName()+", ");
						}
						System.out.println();
						for (LegacyShapeStack s : dat.shapes) {
							System.out.print(s.getShape().name()+", ");
						}
						System.out.println();
					} else {
						failed = true;
					}
				}
				
				// gold (bad)
				if (!failed) {
					ArrayList<ItemStack> items = new ArrayList<>();
					ItemStack item = new ItemStack(ObjectInit.Items.FLASK_GOLD.get());
					FlaskItem flask = (FlaskItem)item.getItem();
					flask.setStored(item, AspectShape.EARTH, FormTree.SUN.get(), ctx.level().getGameTime());
					flask.setContaminated(item, true);
					items.add(item);
					LegacyLabRecipeData dat = LabFunctions.cohobation(lrd(items,null,null,null,null));
					if ( dat == null ) {
					} else {
						failed = true;
					}
				}
				
				return failed;
			}
			boolean testProjection() {
				boolean failed = false;
				
				// TODO: implement
				
				return failed;
			}
			boolean testSolution() {
				boolean failed = false;

				// valid lead
				if (!failed) {
					ArrayList<ItemStack> items = new ArrayList<>();
					items.add(new ItemStack(ObjectInit.Items.FLASK_LEAD.get()));
					ArrayList<FluidStack> fluids = new ArrayList<>();
					fluids.add(new FluidStack(Fluids.WATER, 1000));
					ArrayList<LegacyShapeStack> shapes = new ArrayList<>();
					shapes.add(new LegacyShapeStack(AspectShape.UNIVERSAL));
					ArrayList<LegacyFormStack> forms = new ArrayList<>();
					forms.add(new LegacyFormStack(FormTree.MATERIA.get()));
					LegacyLabRecipeData dat = LabFunctions.solution(lrd(items,fluids,null,shapes,forms));
					if ( dat != null && dat.fluids == null
							&& dat.items != null && dat.items.size() == 1 && dat.items.get(0).is(ObjectInit.Items.FLASK_LEAD.get())
							&& dat.shapes == null && dat.forms == null ) {
						ItemStack stack = dat.items.get(0);
						System.out.print(stack);
						System.out.println(": "+stack.getTag());
					} else failed = true;
				}
				
				// valid aether
				if (!failed) {
					ArrayList<ItemStack> items = new ArrayList<>();
					items.add(new ItemStack(ObjectInit.Items.FLASK_AETHER.get()));
					items.add(new ItemStack(ObjectInit.Items.FLASK_AETHER.get()));
					ArrayList<FluidStack> fluids = new ArrayList<>();
					fluids.add(new FluidStack(Fluids.WATER, 1000));
					ArrayList<LegacyShapeStack> shapes = new ArrayList<>();
					shapes.add(new LegacyShapeStack(AspectShape.UNIVERSAL));
					ArrayList<LegacyFormStack> forms = new ArrayList<>();
					forms.add(new LegacyFormStack(FormTree.MATERIA.get()));
					LegacyLabRecipeData dat = LabFunctions.solution(lrd(items,fluids,null,shapes,forms));
					if ( dat != null && dat.fluids == null
							&& dat.items != null && dat.items.size() == 2 && dat.items.get(0).is(ObjectInit.Items.FLASK_AETHER.get()) && dat.items.get(1).is(ObjectInit.Items.FLASK_AETHER.get())
							&& dat.shapes == null && dat.forms == null ) {
						int i = 0;
						for (ItemStack stack : dat.items) {
							i++;
							System.out.print("("+ i +") "+ stack);
							System.out.println(": "+stack.getTag());
						}
					} else failed = true;
				}

				// valid (gold, leftover test)
				if (!failed) {
					ArrayList<ItemStack> items = new ArrayList<>();
					items.add(new ItemStack(ObjectInit.Items.FLASK_GOLD.get()));
					ItemStack fs = new ItemStack(ObjectInit.Items.FLASK_AETHER.get());
					StorageFlaskItem sFlask = (StorageFlaskItem)fs.getItem();
					FlaskItem flask = (FlaskItem)items.get(0).getItem();
					sFlask.setStored(fs, AspectShape.FIRE, null, ctx.level().getGameTime());
					items.add(fs);
					//items.add(new ItemStack(ObjectInit.Items.FLASK_AETHER.get()));
					ArrayList<FluidStack> fluids = new ArrayList<>();
					fluids.add(new FluidStack(Fluids.WATER, 1000));
					ArrayList<LegacyShapeStack> shapes = new ArrayList<>();
					shapes.add(new LegacyShapeStack(AspectShape.UNIVERSAL));
					ArrayList<LegacyFormStack> forms = new ArrayList<>();
					forms.add(new LegacyFormStack(FormTree.MATERIA.get()));
					LegacyLabRecipeData dat = LabFunctions.solution(lrd(items,fluids,null,shapes,forms));
					if ( dat != null && dat.items != null && dat.items.size() == 2
							&& dat.items.get(0).is(ObjectInit.Items.FLASK_GOLD.get()) && flask.hasStored(dat.items.get(0))
							&& dat.items.get(1).is(ObjectInit.Items.FLASK_AETHER.get()) && ItemStack.isSameItemSameTags(fs, dat.items.get(1))
							&& dat.fluids == null && dat.shapes == null && dat.forms == null) {
						int i = 0;
						for (ItemStack stack : dat.items) {
							i++;
							System.out.print("("+ i +") "+ stack);
							System.out.println(": "+stack.getTag());
						}
					} else failed = true;
				}

				// invalid (lead, fluid shortage)
				if (!failed) {
					ArrayList<ItemStack> items = new ArrayList<>();
					items.add(new ItemStack(ObjectInit.Items.FLASK_LEAD.get()));
					ArrayList<FluidStack> fluids = new ArrayList<>();
					fluids.add(new FluidStack(Fluids.WATER, 800));
					ArrayList<LegacyShapeStack> shapes = new ArrayList<>();
					shapes.add(new LegacyShapeStack(AspectShape.UNIVERSAL));
					ArrayList<LegacyFormStack> forms = new ArrayList<>();
					forms.add(new LegacyFormStack(FormTree.MATERIA.get()));
					LegacyLabRecipeData dat = LabFunctions.solution(lrd(items,fluids,null,shapes,forms));
					if ( dat == null) {
					} else failed = true;
				}

				// invalid (aether, bottle shortage)
				if (!failed) {
					ArrayList<ItemStack> items = new ArrayList<>();
					items.add(new ItemStack(ObjectInit.Items.FLASK_AETHER.get()));
					//items.add(new ItemStack(ObjectInit.Items.FLASK_AETHER.get()));
					ArrayList<FluidStack> fluids = new ArrayList<>();
					fluids.add(new FluidStack(Fluids.WATER, 1000));
					ArrayList<LegacyShapeStack> shapes = new ArrayList<>();
					shapes.add(new LegacyShapeStack(AspectShape.UNIVERSAL));
					ArrayList<LegacyFormStack> forms = new ArrayList<>();
					forms.add(new LegacyFormStack(FormTree.MATERIA.get()));
					LegacyLabRecipeData dat = LabFunctions.solution(lrd(items,fluids,null,shapes,forms));
					if ( dat == null) {
					} else failed = true;
				}

				// invalid (aether, single-bottle attempt)
				if (!failed) {
					ArrayList<ItemStack> items = new ArrayList<>();
					items.add(new ItemStack(ObjectInit.Items.FLASK_AETHER.get()));
					//items.add(new ItemStack(ObjectInit.Items.FLASK_AETHER.get()));
					ArrayList<FluidStack> fluids = new ArrayList<>();
					fluids.add(new FluidStack(Fluids.WATER, 1000));
					//ArrayList<ShapeStack> shapes = new ArrayList<>();
					//shapes.add(new ShapeStack(AspectShape.UNIVERSAL));
					ArrayList<LegacyFormStack> forms = new ArrayList<>();
					forms.add(new LegacyFormStack(FormTree.MATERIA.get()));
					LegacyLabRecipeData dat = LabFunctions.solution(lrd(items,fluids,null,null,forms));
					if ( dat == null) {
					} else failed = true;
				}

				// invalid (gold, full bottle)
				if (!failed) {
					ArrayList<ItemStack> items = new ArrayList<>();
					ItemStack fs = new ItemStack(ObjectInit.Items.FLASK_GOLD.get());
					((FlaskItem)fs.getItem()).setStored(fs, AspectShape.FIRE, FormTree.ALCHEMY.get(), ctx.level().getGameTime());
					items.add(fs);
					//items.add(new ItemStack(ObjectInit.Items.FLASK_AETHER.get()));
					ArrayList<FluidStack> fluids = new ArrayList<>();
					fluids.add(new FluidStack(Fluids.WATER, 1000));
					ArrayList<LegacyShapeStack> shapes = new ArrayList<>();
					shapes.add(new LegacyShapeStack(AspectShape.UNIVERSAL));
					ArrayList<LegacyFormStack> forms = new ArrayList<>();
					forms.add(new LegacyFormStack(FormTree.MATERIA.get()));
					LegacyLabRecipeData dat = LabFunctions.solution(lrd(items,fluids,null,shapes,forms));
					if ( dat == null) {
					} else failed = true;
				}
				
				return failed;
			}
			boolean testDigestion() {
				boolean failed = false;
				
				// valid
				if (!failed) {
					ArrayList<LegacyWayStack> ways = new ArrayList<>();
					ways.add(new LegacyWayStack(50));
					ArrayList<LegacyShapeStack> shapes = new ArrayList<>();
					shapes.add(new LegacyShapeStack(AspectShape.UNIVERSAL));
					ArrayList<LegacyFormStack> forms = new ArrayList<>();
					forms.add(new LegacyFormStack(FormTree.SOIL.get()));
					LegacyLabRecipeData dat = LabFunctions.digestion(lrd(null,null,ways,shapes,forms));
					if (dat != null && dat.items != null && dat.items.size() == 1 && dat.items.get(0).is(ObjectInit.Items.AETHER.get())
							&& dat.forms == null && dat.shapes == null && dat.ways == null) {
						System.out.println(dat.items);
					} else failed = true;
				}
				
				// invalid
				if (!failed) {
					ArrayList<LegacyWayStack> ways = new ArrayList<>();
					ways.add(new LegacyWayStack(50));
					ArrayList<LegacyShapeStack> shapes = new ArrayList<>();
					shapes.add(new LegacyShapeStack(AspectShape.UNIVERSAL));
					ArrayList<LegacyFormStack> forms = new ArrayList<>();
					forms.add(new LegacyFormStack(FormTree.SOIL.get()));
					LegacyLabRecipeData dat = LabFunctions.digestion(lrd(null,null,ways,null,forms));
					if (dat == null) {
					} else failed = true;
				}
				
				return failed;
			}
			boolean testMultiplication() {
				boolean failed = false;
				
				// TODO implement with mapper
				
				return failed;
			}
		}
	}

}
