package com.mkgmod.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record TeleportPayload(String dimensionId) implements CustomPacketPayload {

    // 1.21 必须使用 ResourceLocation.fromNamespaceAndPath 或 parse
    public static final Type<TeleportPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("mkgmod", "teleport"));

    // 1.21 引入了 StreamCodec 来代替旧的 read/write 逻辑手动调用
    public static final StreamCodec<FriendlyByteBuf, TeleportPayload> STREAM_CODEC = CustomPacketPayload.codec(
            TeleportPayload::write,
            TeleportPayload::new
    );

    public TeleportPayload(FriendlyByteBuf buffer) {
        this(buffer.readUtf());
    }

    public void write(FriendlyByteBuf buffer) {
        buffer.writeUtf(this.dimensionId);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}