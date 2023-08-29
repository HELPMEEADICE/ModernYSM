package com.elfmcys.yesstevemodel.client.event;

import com.elfmcys.yesstevemodel.YesSteveModel;
import com.elfmcys.yesstevemodel.client.gui.PlayerModelScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = YesSteveModel.MOD_ID)
public class NpcClickEvent {
    private static final Component TAG = Component.literal("943");

    @SubscribeEvent
    public static void onKeyboardInput(PlayerInteractEvent.EntityInteract event) {
        Entity target = event.getTarget();
        Player player = event.getEntity();
        if (player.hasPermissions(2) && target instanceof Player playerTarget && player.level().isClientSide() && isClickItem(player.getMainHandItem())) {
            Minecraft.getInstance().setScreen(new PlayerModelScreen(playerTarget));
        }
    }

    private static boolean isClickItem(ItemStack stack) {
        if (stack.getItem() != Items.STICK) {
            return false;
        }
        return stack.getHoverName().equals(TAG);
    }
}
