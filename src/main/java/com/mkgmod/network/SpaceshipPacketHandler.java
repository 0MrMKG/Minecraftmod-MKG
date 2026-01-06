package com.mkgmod.network;

import com.mkgmod.blockentity.SpaceshipOperatorBlockEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class SpaceshipPacketHandler {

    public static void handleData(final SpaceshipActionPayload data, final IPayloadContext context) {
        // 确保逻辑在主线程执行，防止多线程崩溃
        context.enqueueWork(() -> {
            ServerPlayer player = (ServerPlayer) context.player();
            Level level = player.level();

            // 获取指定位置的方块实体并执行逻辑
            if (level.getBlockEntity(data.pos()) instanceof SpaceshipOperatorBlockEntity spaceshipBE) {
                if ("assemble".equals(data.action())) {
                    // 调用保存 NBT 的逻辑
                    spaceshipBE.assembleSpaceship(player);
                } else if ("launch".equals(data.action())) {
                    // 调用发射逻辑 (之后会用到)
                    spaceshipBE.launchSpaceship(player);
                } else if ("return".equals(data.action())) {
                    spaceshipBE.returnToOverworld(player);
                }
            }
        });
    }
}