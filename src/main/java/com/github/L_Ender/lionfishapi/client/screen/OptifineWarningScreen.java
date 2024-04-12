package com.github.L_Ender.lionfishapi.client.screen;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.*;
import org.jetbrains.annotations.Nullable;

public class OptifineWarningScreen extends Screen {

	private final Screen lastScreen;
	private int ticksUntilEnable = 20 * 8;
	private MultiLineLabel message = MultiLineLabel.EMPTY;
	private MultiLineLabel embeddiumsuggestions = MultiLineLabel.EMPTY;
	private MultiLineLabel oculussuggestions = MultiLineLabel.EMPTY;
	private static final Component text = Component.translatable("gui.lionfishapi.optifine.message");
	private static final MutableComponent embeddiumurl = Component.translatable("gui.lionfishapi.optifine.embeddiumsuggestions").withStyle(style -> style.withColor(ChatFormatting.GREEN).applyFormat(ChatFormatting.UNDERLINE).withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://modrinth.com/mod/embeddium")));
	private static final MutableComponent oculusurl = Component.translatable("gui.lionfishapi.optifine.oculussuggestions").withStyle(style -> style.withColor(ChatFormatting.GREEN).applyFormat(ChatFormatting.UNDERLINE).withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://modrinth.com/mod/oculus")));
	private Button exitButton;

	public OptifineWarningScreen(Screen screen) {
		super(Component.translatable("gui.lionfishapi.optifine.title"));
		this.lastScreen = screen;
	}

	@Override
	public Component getNarrationMessage() {
		return CommonComponents.joinForNarration(super.getNarrationMessage(), text);
	}

	@Override
	protected void init() {
		super.init();
		this.exitButton = this.addRenderableWidget(Button.builder(CommonComponents.GUI_PROCEED, (pressed) -> Minecraft.getInstance().setScreen(this.lastScreen)).bounds(this.width / 2 - 75, this.height * 3 / 4, 150, 20).build());
		this.exitButton.active = true;
		this.message = MultiLineLabel.create(this.font, text, this.width - 25);
		this.embeddiumsuggestions = MultiLineLabel.create(this.font, embeddiumurl, this.width - 35);
		this.oculussuggestions = MultiLineLabel.create(this.font, oculusurl, this.width - 45);
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
		this.renderBackground(graphics);
		graphics.drawCenteredString(this.font, this.title, this.width / 2, 30, 16777215);
		this.message.renderCentered(graphics, this.width / 2, 70);
		this.embeddiumsuggestions.renderCentered(graphics, this.width / 2, 120);
		this.oculussuggestions.renderCentered(graphics, this.width / 2, 160);
		super.render(graphics, mouseX, mouseY, partialTicks);

		this.exitButton.render(graphics, mouseX, mouseY, partialTicks);
	}

	@Override
	public void tick() {
		super.tick();
		if (--this.ticksUntilEnable <= 0) {
			this.exitButton.active = true;
		}
	}

	@Override
	public boolean shouldCloseOnEsc() {
		return this.ticksUntilEnable <= 0;
	}

	@Override
	public void onClose() {
		Minecraft.getInstance().setScreen(this.lastScreen);
	}

	@Override
	public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
		if (pMouseY > 120 && pMouseY < 130) {
			Style style = this.getClickedComponentStyleAt((int) pMouseX);
			if (style != null && style.getClickEvent() != null && style.getClickEvent().getAction() == ClickEvent.Action.OPEN_URL) {
				this.handleComponentClicked(style);
				return false;
			}
		}

		if (pMouseY > 160 && pMouseY < 170) {
			Style style = this.getClickedComponentStyleAt2((int) pMouseX);
			if (style != null && style.getClickEvent() != null && style.getClickEvent().getAction() == ClickEvent.Action.OPEN_URL) {
				this.handleComponentClicked(style);
				return false;
			}
		}

		return super.mouseClicked(pMouseX, pMouseY, pButton);
	}

	@Nullable
	private Style getClickedComponentStyleAt(int xPos) {
		int wid = Minecraft.getInstance().font.width(embeddiumurl);
		int left = this.width / 2 - wid / 2;
		int right = this.width / 2 + wid / 2;
		return xPos >= left && xPos <= right ? Minecraft.getInstance().font.getSplitter().componentStyleAtWidth(embeddiumurl, xPos - left) : null;
	}

	@Nullable
	private Style getClickedComponentStyleAt2(int xPos) {
		int wid = Minecraft.getInstance().font.width(oculusurl);
		int left = this.width / 2 - wid / 2;
		int right = this.width / 2 + wid / 2;
		return xPos >= left && xPos <= right ? Minecraft.getInstance().font.getSplitter().componentStyleAtWidth(oculusurl, xPos - left) : null;
	}
}
