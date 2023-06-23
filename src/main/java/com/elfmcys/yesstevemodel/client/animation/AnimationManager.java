package com.elfmcys.yesstevemodel.client.animation;

import com.elfmcys.yesstevemodel.capability.ModelInfoCapabilityProvider;
import com.elfmcys.yesstevemodel.client.animation.condition.ConditionManager;
import com.elfmcys.yesstevemodel.client.animation.condition.ConditionalSwing;
import com.elfmcys.yesstevemodel.client.animation.condition.ConditionalUse;
import com.elfmcys.yesstevemodel.client.entity.CustomPlayerEntity;
import com.elfmcys.yesstevemodel.geckolib3.core.IAnimatable;
import com.elfmcys.yesstevemodel.geckolib3.core.PlayState;
import com.elfmcys.yesstevemodel.geckolib3.core.builder.AnimationBuilder;
import com.elfmcys.yesstevemodel.geckolib3.core.builder.ILoopType;
import com.elfmcys.yesstevemodel.geckolib3.core.event.predicate.AnimationEvent;
import com.elfmcys.yesstevemodel.util.ModelIdUtil;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;

public final class AnimationManager {
    private static AnimationManager MANAGER;
    private final Int2ObjectOpenHashMap<LinkedList<AnimationState>> data = new Int2ObjectOpenHashMap<>();

    public static AnimationManager getInstance() {
        if (MANAGER == null) {
            MANAGER = new AnimationManager();
        }
        return MANAGER;
    }

    @NotNull
    private static <P extends IAnimatable> PlayState playLoopAnimation(AnimationEvent<P> event, String animationName) {
        return playAnimation(event, animationName, ILoopType.EDefaultLoopTypes.LOOP);
    }

    @NotNull
    private static <P extends IAnimatable> PlayState playAnimation(AnimationEvent<P> event, String animationName, ILoopType loopType) {
        event.getController().setAnimation(new AnimationBuilder().addAnimation(animationName, loopType));
        return PlayState.CONTINUE;
    }

    @NotNull
    private static <P extends IAnimatable> PlayState playAnimation(AnimationEvent<P> event, String animationName) {
        event.getController().setAnimation(new AnimationBuilder().addAnimation(animationName));
        return PlayState.CONTINUE;
    }

    public void register(AnimationState state) {
        if (data.containsKey(state.getPriority())) {
            data.get(state.getPriority()).add(state);
        } else {
            LinkedList<AnimationState> states = Lists.newLinkedList();
            states.add(state);
            data.put(state.getPriority(), states);
        }
    }

    public PlayState predicateCap(AnimationEvent<CustomPlayerEntity> event) {
        CustomPlayerEntity animatable = event.getAnimatable();
        Player player = animatable.getPlayer();
        if (player == null) {
            if (animatable.hasPreviewAnimation()) {
                return playLoopAnimation(event, animatable.getPreviewAnimation());
            }
            return PlayState.STOP;
        }

        return player.getCapability(ModelInfoCapabilityProvider.MODEL_INFO_CAP).map(cap -> {
            if (cap.isPlayAnimation()) {
                return playAnimation(event, cap.getAnimation());
            }
            return PlayState.STOP;
        }).orElse(PlayState.STOP);
    }

    @NotNull
    public PlayState predicateMain(AnimationEvent<CustomPlayerEntity> event) {
        Player player = event.getAnimatable().getPlayer();
        if (player == null) {
            return PlayState.STOP;
        }
        for (int i = Priority.HIGHEST; i <= Priority.LOWEST; i++) {
            if (!data.containsKey(i)) {
                continue;
            }
            LinkedList<AnimationState> states = data.get(i);
            for (AnimationState state : states) {
                if (state.getPredicate().test(player, event)) {
                    String animationName = state.getAnimationName();
                    ILoopType loopType = state.getLoopType();
                    return playAnimation(event, animationName, loopType);
                }
            }
        }
        return PlayState.STOP;
    }

    public PlayState predicateSwing(AnimationEvent<CustomPlayerEntity> event) {
        Player player = event.getAnimatable().getPlayer();
        if (player == null) {
            return PlayState.STOP;
        }
        if (player.swinging && !player.isSleeping()) {
            if (player.swingTime == 0) {
                event.getController().shouldResetTick = true;
                event.getController().adjustTick(0);
            }
            return player.getCapability(ModelInfoCapabilityProvider.MODEL_INFO_CAP).map(cap -> {
                ResourceLocation id = ModelIdUtil.getMainId(cap.getModelId());
                ConditionalSwing conditionalSwing = ConditionManager.getSwing(id);
                if (conditionalSwing != null) {
                    String name = conditionalSwing.doTest(player, player.swingingArm);
                    if (StringUtils.isNoneBlank(name)) {
                        return playAnimation(event, name, ILoopType.EDefaultLoopTypes.LOOP);
                    }
                }
                return playAnimation(event, "swing_hand", ILoopType.EDefaultLoopTypes.LOOP);
            }).orElse(PlayState.STOP);
        }
        return PlayState.STOP;
    }

    public PlayState predicateUse(AnimationEvent<CustomPlayerEntity> event) {
        Player player = event.getAnimatable().getPlayer();
        if (player == null) {
            return PlayState.STOP;
        }
        if (player.isUsingItem() && !player.isSleeping()) {
            if (player.getTicksUsingItem() == 0) {
                event.getController().shouldResetTick = true;
                event.getController().adjustTick(0);
            }
            if (player.getUsedItemHand() == InteractionHand.MAIN_HAND) {
                return player.getCapability(ModelInfoCapabilityProvider.MODEL_INFO_CAP).map(cap -> {
                    ResourceLocation id = ModelIdUtil.getMainId(cap.getModelId());
                    ConditionalUse conditionalUse = ConditionManager.getUseMainhand(id);
                    if (conditionalUse != null) {
                        String name = conditionalUse.doTest(player, InteractionHand.MAIN_HAND);
                        if (StringUtils.isNoneBlank(name)) {
                            return playAnimation(event, name, ILoopType.EDefaultLoopTypes.LOOP);
                        }
                    }
                    return playAnimation(event, "use_mainhand", ILoopType.EDefaultLoopTypes.LOOP);
                }).orElse(PlayState.STOP);
            } else {
                return player.getCapability(ModelInfoCapabilityProvider.MODEL_INFO_CAP).map(cap -> {
                    ResourceLocation id = ModelIdUtil.getMainId(cap.getModelId());
                    ConditionalUse conditionalUse = ConditionManager.getUseOffhand(id);
                    if (conditionalUse != null) {
                        String name = conditionalUse.doTest(player, InteractionHand.OFF_HAND);
                        if (StringUtils.isNoneBlank(name)) {
                            return playAnimation(event, name, ILoopType.EDefaultLoopTypes.LOOP);
                        }
                    }
                    return playAnimation(event, "use_offhand", ILoopType.EDefaultLoopTypes.LOOP);
                }).orElse(PlayState.STOP);
            }
        }
        return PlayState.STOP;
    }
}