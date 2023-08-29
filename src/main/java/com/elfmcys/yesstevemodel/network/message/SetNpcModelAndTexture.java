package com.elfmcys.yesstevemodel.network.message;

import com.elfmcys.yesstevemodel.capability.ModelInfoCapabilityProvider;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SetNpcModelAndTexture {
    private final ResourceLocation modelId;
    private final ResourceLocation selectTexture;
    private final int entityId;

    public SetNpcModelAndTexture(ResourceLocation modelId, ResourceLocation selectTexture, int entityId) {
        this.modelId = modelId;
        this.selectTexture = selectTexture;
        this.entityId = entityId;
    }

    public static void encode(SetNpcModelAndTexture message, FriendlyByteBuf buf) {
        buf.writeResourceLocation(message.modelId);
        buf.writeResourceLocation(message.selectTexture);
        buf.writeVarInt(message.entityId);
    }

    public static SetNpcModelAndTexture decode(FriendlyByteBuf buf) {
        return new SetNpcModelAndTexture(buf.readResourceLocation(), buf.readResourceLocation(), buf.readVarInt());
    }

    public static void handle(SetNpcModelAndTexture message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        if (context.getDirection().getReceptionSide().isServer()) {
            context.enqueueWork(() -> {
                ServerPlayer sender = context.getSender();
                if (sender == null) {
                    return;
                }
                Entity entity = sender.level().getEntity(message.entityId);
                if (entity instanceof ServerPlayer player && sender.hasPermissions(2)) {
                    handleCapability(message, player);
                }
            });
        }
        context.setPacketHandled(true);
    }

    private static void handleCapability(SetNpcModelAndTexture message, ServerPlayer sender) {
        sender.getCapability(ModelInfoCapabilityProvider.MODEL_INFO_CAP).ifPresent(modelIdCap -> modelIdCap.setModelAndTexture(message.modelId, message.selectTexture));
    }
}
