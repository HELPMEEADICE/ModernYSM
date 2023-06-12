package com.elfmcys.yesstevemodel.client.gui.button;

import com.elfmcys.yesstevemodel.YesSteveModel;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class FlatIconButton extends FlatColorButton {
    private final static ResourceLocation ICON = new ResourceLocation(YesSteveModel.MOD_ID, "texture/icon.png");
    private final int textureX;
    private final int textureY;
    private Component tooltips;

    public FlatIconButton(int x, int y, int width, int height, int textureX, int textureY, OnPress onPress) {
        super(x, y, width, height, Component.empty(), onPress);
        this.textureX = textureX;
        this.textureY = textureY;
    }

    public FlatIconButton setTooltips(String key) {
        tooltips = Component.translatable(key);
        return this;
    }

    public void renderToolTip(GuiGraphics graphics, Screen screen, int pMouseX, int pMouseY) {
        if (this.isHoveredOrFocused() && tooltips != null) {
            graphics.renderTooltip(screen.getMinecraft().font, tooltips, pMouseX, pMouseY);
        }
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float pPartialTick) {
        super.renderWidget(graphics, mouseX, mouseY, pPartialTick);
        int startX = (this.width - 16) / 2;
        int startY = (this.height - 16) / 2;
        graphics.blit(ICON, this.getX() + startX, this.getY() + startY, 16, 16, textureX, textureY, 16, 16, 256, 256);
    }
}
