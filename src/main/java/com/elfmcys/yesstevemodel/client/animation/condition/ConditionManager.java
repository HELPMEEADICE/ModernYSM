package com.elfmcys.yesstevemodel.client.animation.condition;

import com.google.common.collect.Maps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;

import java.util.Map;

public class ConditionManager {
    public static Map<ResourceLocation, ConditionalSwing> SWING = Maps.newHashMap();
    public static Map<ResourceLocation, ConditionalUse> USE_MAINHAND = Maps.newHashMap();
    public static Map<ResourceLocation, ConditionalUse> USE_OFFHAND = Maps.newHashMap();

    public static void addTest(ResourceLocation id, String name) {
        SWING.putIfAbsent(id, new ConditionalSwing());
        USE_MAINHAND.putIfAbsent(id, new ConditionalUse(InteractionHand.MAIN_HAND));
        USE_OFFHAND.putIfAbsent(id, new ConditionalUse(InteractionHand.OFF_HAND));
        ConditionalSwing conditionalSwing = SWING.get(id);
        ConditionalUse conditionalUseMainhand = USE_MAINHAND.get(id);
        ConditionalUse conditionalUseOffhand = USE_OFFHAND.get(id);
        conditionalSwing.addTest(name);
        conditionalUseMainhand.addTest(name);
        conditionalUseOffhand.addTest(name);
    }

    public static void clear() {
        SWING.clear();
        USE_MAINHAND.clear();
        USE_OFFHAND.clear();
    }

    public static ConditionalSwing getSwing(ResourceLocation id) {
        return SWING.get(id);
    }

    public static ConditionalUse getUseMainhand(ResourceLocation id) {
        return USE_MAINHAND.get(id);
    }

    public static ConditionalUse getUseOffhand(ResourceLocation id) {
        return USE_OFFHAND.get(id);
    }
}
