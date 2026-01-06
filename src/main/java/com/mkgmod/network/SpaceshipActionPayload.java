package com.mkgmod.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record SpaceshipActionPayload(BlockPos pos, String action) implements CustomPacketPayload {

    // 定义 Payload 的唯一 ID (确保 mkgmod 是你的模组 ID)
    public static final Type<SpaceshipActionPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath("mkgmod", "spaceship_action"));

    // 定义编解码器：如何将数据转为二进制流
    public static final StreamCodec<RegistryFriendlyByteBuf, SpaceshipActionPayload> CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, SpaceshipActionPayload::pos,
            ByteBufCodecs.STRING_UTF8, SpaceshipActionPayload::action,
            SpaceshipActionPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}