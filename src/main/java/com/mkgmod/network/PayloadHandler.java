package com.mkgmod.network;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class PayloadHandler {

    public static void handleTeleport(final TeleportPayload payload, final IPayloadContext context) {
        // 1.21 使用 enqueueWork 确保在主线程执行
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player) {
                MinecraftServer server = player.getServer();
                if (server == null) return;

                // 1.21 解析 ResourceLocation 的方式
                ResourceLocation dimLoc = ResourceLocation.parse(payload.dimensionId());
                ResourceKey<Level> destKey = ResourceKey.create(Registries.DIMENSION, dimLoc);
                ServerLevel destLevel = server.getLevel(destKey);

                if (destLevel != null) {
                    player.teleportTo(destLevel, player.getX(), player.getY(), player.getZ(), player.getYRot(), player.getXRot());
                }
            }
        });
    }
}