package com.quartzshard.aasb.client.gui.screen;

import com.quartzshard.aasb.AASB;
import com.quartzshard.aasb.api.alchemy.AlchData;
import com.quartzshard.aasb.api.alchemy.Phil;
import com.quartzshard.aasb.common.gui.container.TransmutationResultContainer;
import com.quartzshard.aasb.common.gui.menu.TransmutationMenu;
import com.quartzshard.aasb.util.Colors;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;

public class TransmutationScreen extends AbstractContainerScreen<TransmutationMenu> {
	//private static final ResourceLocation CRAFTING_TABLE_LOCATION = new ResourceLocation("textures/gui/container/crafting_table.png");
	private static final ResourceLocation CRAFTING_TABLE_LOCATION = AASB.rl("textures/gui/transmutation/5.png");
	//private static final ResourceLocation RECIPE_BUTTON_LOCATION = new ResourceLocation("textures/gui/recipe_button.png");
	//private final RecipeBookComponent recipeBookComponent = new RecipeBookComponent();
	private boolean widthTooNarrow;

	public TransmutationScreen(TransmutationMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
		super(pMenu, pPlayerInventory, pTitle);
		this.imageWidth = 230;
		this.imageHeight = 193;
		this.inventoryLabelX = 35;
		this.inventoryLabelY = this.imageHeight - 94;
	}

	@Override
	protected void init() {
		super.init();
		this.widthTooNarrow = this.width < 379;
		//this.recipeBookComponent.init(this.width, this.height, this.minecraft, this.widthTooNarrow, this.menu);
		this.leftPos = (this.width - this.imageWidth) / 2;
		//this.addRenderableWidget(new ImageButton(this.leftPos + 5, this.height / 2 - 49, 20, 18, 0, 0, 19, RECIPE_BUTTON_LOCATION, (p_289630_) -> {
		//	this.recipeBookComponent.toggleVisibility();
		//	this.leftPos = this.recipeBookComponent.updateScreenPosition(this.width, this.imageWidth);
		//	p_289630_.setPosition(this.leftPos + 5, this.height / 2 - 49);
		//}));
		//this.addWidget(this.recipeBookComponent);
		//this.setInitialFocus(this.recipeBookComponent);
		this.titleLabelX = 8;//29;
	}

	@Override
	public void containerTick() {
		super.containerTick();
		//this.recipeBookComponent.tick();
	}

	/**
	 * Renders the graphical user interface (GUI) element.
	 * @param pGuiGraphics the GuiGraphics object used for rendering.
	 * @param pMouseX the x-coordinate of the mouse cursor.
	 * @param pMouseY the y-coordinate of the mouse cursor.
	 * @param pPartialTick the partial tick time.
	 */
	@Override
	public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
		this.renderBackground(pGuiGraphics);
		if (this.widthTooNarrow) {
			this.renderBg(pGuiGraphics, pPartialTick, pMouseX, pMouseY);
			//this.recipeBookComponent.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
		} else {
			//this.recipeBookComponent.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
			super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
			//this.recipeBookComponent.renderGhostRecipe(pGuiGraphics, this.leftPos, this.topPos, true, pPartialTick);
		}

		this.renderTooltip(pGuiGraphics, pMouseX, pMouseY);
		//this.recipeBookComponent.renderTooltip(pGuiGraphics, this.leftPos, this.topPos, pMouseX, pMouseY);
	}

	@Override
	protected void renderBg(GuiGraphics gfx, float tick, int mouseX, int mouseY) {
		int leftPos = this.leftPos;
		int h = (this.height - this.imageHeight) / 2;
		gfx.blit(CRAFTING_TABLE_LOCATION, leftPos, h, 0, 0, this.imageWidth, this.imageHeight);
		gfx.blit(CRAFTING_TABLE_LOCATION, leftPos+104, h+44, 0, 240, 22, 16);
		long ms = System.currentTimeMillis();
		if (this.hoveredSlot != null && hoveredSlot.hasItem() && hoveredSlot.container instanceof TransmutationResultContainer con) {
			AlchData in = menu.resolveInputs();
			AlchData out = Phil.getAspects(hoveredSlot.getItem());
			float vio = Float.POSITIVE_INFINITY;// = menu.resolveInputs().violationTo(Phil.getAspects(hoveredSlot.getItem()));
			if (!in.complexity().allowsNull() && !out.complexity().allowsNull()) {
				assert in.shape() != null && in.form() != null;
				vio = in.shape().violationTo(out.shape()) + in.form().violationTo(out.form());
			}
			//System.out.println(vio);
			int[] rgb = Colors.rgbFromInt(Colors.materiaGradient(1f-vio));
			for (int i = 0; i < 22; i++) {
				gfx.setColor(rgb[0]/255f,rgb[1]/255f,rgb[2]/255f, Colors.loopFade(ms, 6304, i*287, 0.5f, 1f));
				//gfx.blit(CRAFTING_TABLE_LOCATION, leftPos+104, h+44, 0, 240, 22, 16);
				gfx.blit(CRAFTING_TABLE_LOCATION, leftPos+104+21-i, h+44, 21-i, 240, 1, 16);
			}
			//System.out.println(System.currentTimeMillis()-ms+"ms");
			gfx.setColor(1,1,1,1);
		}
	}

	@Override
	protected boolean isHovering(int pX, int pY, int pWidth, int pHeight, double pMouseX, double pMouseY) {
		return (!this.widthTooNarrow) && super.isHovering(pX, pY, pWidth, pHeight, pMouseX, pMouseY);
	}

	/**
	 * Called when a mouse button is clicked within the GUI element.
	 * <p>
	 * @return {@code true} if the event is consumed, {@code false} otherwise.
	 * @param pMouseX the X coordinate of the mouse.
	 * @param pMouseY the Y coordinate of the mouse.
	 * @param pButton the button that was clicked.
	 */
	@Override
	public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
		//if (this.recipeBookComponent.mouseClicked(pMouseX, pMouseY, pButton)) {
		//	this.setFocused(this.recipeBookComponent);
		//	return true;
		//} else {
			return this.widthTooNarrow || super.mouseClicked(pMouseX, pMouseY, pButton);
		//}
	}

	@Override
	protected boolean hasClickedOutside(double pMouseX, double pMouseY, int pGuiLeft, int pGuiTop, int pMouseButton) {
		boolean flag = pMouseX < (double)pGuiLeft || pMouseY < (double)pGuiTop || pMouseX >= (double)(pGuiLeft + this.imageWidth) || pMouseY >= (double)(pGuiTop + this.imageHeight);
		return flag;
		//return this.recipeBookComponent.hasClickedOutside(pMouseX, pMouseY, this.leftPos, this.topPos, this.imageWidth, this.imageHeight, pMouseButton) && flag;
	}

	/**
	 * Called when the mouse is clicked over a slot or outside the gui.
	 */
	@Override
	protected void slotClicked(Slot pSlot, int pSlotId, int pMouseButton, ClickType pType) {
		super.slotClicked(pSlot, pSlotId, pMouseButton, pType);
		//this.recipeBookComponent.slotClicked(pSlot);
	}
}