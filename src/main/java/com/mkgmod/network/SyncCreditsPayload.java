package com.mkgmod.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

// 定义数据包载荷
public record SyncCreditsPayload(int credits) implements CustomPacketPayload {
    public static final Type<SyncCreditsPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath("mkgmod", "sync_credits"));

    // 使用 StreamCodec 进行高效序列化
    public static final StreamCodec<FriendlyByteBuf, SyncCreditsPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.VAR_INT, SyncCreditsPayload::credits,
                    SyncCreditsPayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}