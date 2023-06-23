package com.elfmcys.yesstevemodel.client.model;

import com.elfmcys.yesstevemodel.YesSteveModel;
import com.elfmcys.yesstevemodel.client.animation.AnimationRegister;
import com.elfmcys.yesstevemodel.client.entity.CustomPlayerEntity;
import com.elfmcys.yesstevemodel.geckolib3.core.IAnimatable;
import com.elfmcys.yesstevemodel.geckolib3.core.event.predicate.AnimationEvent;
import com.elfmcys.yesstevemodel.geckolib3.core.molang.MolangParser;
import com.elfmcys.yesstevemodel.geckolib3.core.processor.IBone;
import com.elfmcys.yesstevemodel.geckolib3.model.AnimatedGeoModel;
import com.elfmcys.yesstevemodel.geckolib3.model.provider.data.EntityModelData;
import com.elfmcys.yesstevemodel.geckolib3.resource.GeckoLibCache;
import com.elfmcys.yesstevemodel.util.Keep;
import com.elfmcys.yesstevemodel.util.ModelIdUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;
import java.util.List;

@SuppressWarnings("all")
public class CustomPlayerModel extends AnimatedGeoModel {
    public static final ResourceLocation DEFAULT_MAIN_MODEL = ModelIdUtil.getMainId(new ResourceLocation(YesSteveModel.MOD_ID, "default"));
    public static final ResourceLocation DEFAULT_MAIN_ANIMATION = ModelIdUtil.getMainId(new ResourceLocation(YesSteveModel.MOD_ID, "default"));
    public static final ResourceLocation DEFAULT_TEXTURE = new ResourceLocation(YesSteveModel.MOD_ID, "default/default.png");

    @Override
    @Keep
    public ResourceLocation getModelLocation(Object object) {
        if (object instanceof CustomPlayerEntity customPlayer) {
            return customPlayer.getMainModel();
        }
        return DEFAULT_MAIN_MODEL;
    }

    @Override
    @Keep
    public ResourceLocation getTextureLocation(Object object) {
        if (object instanceof CustomPlayerEntity customPlayer) {
            return customPlayer.getTexture();
        }
        return DEFAULT_TEXTURE;
    }

    @Override
    @Keep
    public ResourceLocation getAnimationFileLocation(Object object) {
        if (object instanceof CustomPlayerEntity customPlayer) {
            return customPlayer.getAnimation();
        }
        return DEFAULT_MAIN_ANIMATION;
    }

    @Override
    @Keep
    public void setCustomAnimations(IAnimatable animatable, int instanceId, AnimationEvent animationEvent) {
        super.setCustomAnimations(animatable, instanceId, animationEvent);
        List extraData = animationEvent.getExtraData();
        MolangParser parser = GeckoLibCache.getInstance().parser;
        if (!Minecraft.getInstance().isPaused() && extraData.size() == 1 && extraData.get(0) instanceof EntityModelData data && animatable instanceof CustomPlayerEntity customPlayer) {
            Player player = customPlayer.getPlayer();
            if (player == null) {
                return;
            }
            AnimationRegister.setParserValue(animationEvent, parser, data, player);
            this.codeAnimation(animationEvent, data, player);
        }
    }

    private void codeAnimation(AnimationEvent animationEvent, EntityModelData data, Player player) {
        // FIXME: 2023/6/21 这一块设计应该改成 molang 的，而且这个寻找效率低下
        IBone head = getBone("Head");
        if (head != null) {
            head.setRotationX(head.getRotationX() + (float) Math.toRadians(data.headPitch));
            head.setRotationY(head.getRotationY() + (float) Math.toRadians(data.netHeadYaw));
        }
    }

    @Override
    @Keep
    @Nullable
    public IBone getBone(String boneName) {
        return getAnimationProcessor().getBone(boneName);
    }

    @Override
    @Keep
    public void setMolangQueries(IAnimatable animatable, double seekTime) {
    }
}
