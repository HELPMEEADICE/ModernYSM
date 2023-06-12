package com.elfmcys.yesstevemodel.client.gui;

import com.elfmcys.yesstevemodel.client.gui.button.ConfigCheckBox;
import com.elfmcys.yesstevemodel.client.gui.button.FlatColorButton;
import com.elfmcys.yesstevemodel.config.GeneralConfig;
import com.elfmcys.yesstevemodel.util.Keep;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ConfigScreen extends Screen {
    private final PlayerModelScreen parent;
    private int x;
    private int y;

    public ConfigScreen(PlayerModelScreen parent) {
        super(Component.literal("YSM Config GUI"));
        this.parent = parent;
    }

    @Override
    @Keep
    protected void init() {
        this.x = (width - 420) / 2;
        this.y = (height - 235) / 2;

        addRenderableWidget(new FlatColorButton(x + 5, y, 80, 18, Component.translatable("gui.yes_steve_model.model.return"), (b) -> this.getMinecraft().setScreen(parent)));

        addRenderableWidget(new ConfigCheckBox(x + 5, y + 25, "disable_self_model", GeneralConfig.DISABLE_SELF_MODEL));
        addRenderableWidget(new ConfigCheckBox(x + 5, y + 47, "disable_other_model", GeneralConfig.DISABLE_OTHER_MODEL));
        addRenderableWidget(new ConfigCheckBox(x + 5, y + 69, "print_animation_roulette_msg", GeneralConfig.PRINT_ANIMATION_ROULETTE_MSG));
    }

    @Override
    @Keep
    public void render(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
        renderBackground(graphics);
        super.render(graphics, pMouseX, pMouseY, pPartialTick);
    }
}
