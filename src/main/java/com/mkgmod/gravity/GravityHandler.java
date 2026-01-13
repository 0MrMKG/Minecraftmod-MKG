package com.mkgmod.gravity;

import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

@EventBusSubscriber(modid = "mkgmod")
public class GravityHandler {

    // 使用 WeakHashMap 记录玩家静止的 Tick 数 (1秒 = 20ticks)
    private static final Map<UUID, Integer> STILL_TICKS_MAP = new WeakHashMap<>();

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        Level level = player.level();

        if (level.dimension() == Level.END) {
            Vec3 delta = player.getDeltaMovement();
            UUID uuid = player.getUUID();

            // 1. 检测是否处于静止/极慢速状态 (阈值设为 0.001)
            // .horizontalDistanceSqr() 只检测水平移动，如果你希望上下不动也算静止，可以用 .lengthSqr()
            boolean isStill = delta.horizontalDistanceSqr() < 0.001 && Math.abs(delta.y) < 0.01;

            if (isStill) {
                // 累加静止计时
                STILL_TICKS_MAP.put(uuid, STILL_TICKS_MAP.getOrDefault(uuid, 0) + 1);
            } else {
                // 移动时重置计时
                STILL_TICKS_MAP.put(uuid, 0);
            }

            int ticksStill = STILL_TICKS_MAP.getOrDefault(uuid, 0);

            // 2. 根据停留时间判断状态
            if (ticksStill < 40) { // 停留少于 40 ticks (2秒)
                // 强制横向姿态
                player.setPose(Pose.SWIMMING);
                player.setSwimming(true);

                // 执行低重力飞行逻辑
                handleDirectionalMovement(player);
            } else {
                // 停留超过 2 秒，不再强制姿势，恢复自然站立
                player.setSwimming(false);
                // 这里不需要 setPose(STANDING)，原版逻辑会自动处理

                // 停留时如果不在地面，依然需要微弱重力处理，防止猛然掉落
                if (!player.onGround()) {
                    applyLowGravity(player);
                }
            }
        }
    }

    private static void handleDirectionalMovement(Player player) {
        Vec3 delta = player.getDeltaMovement();
        Vec3 look = player.getLookAngle();

        // --- 参数调节 ---
        double speedMultiplier = 0.05;
        double friction = 0.95;
        double hoverGravity = 0.002;   // 【修改点】重力改得更小了 (原先是 0.01)

        if (player.zza > 0) {
            delta = delta.add(
                    look.x * speedMultiplier,
                    look.y * speedMultiplier,
                    look.z * speedMultiplier
            );
        }

        // 抵消原生 0.08 重力，并应用极小的自定义重力
        double nextX = delta.x * friction;
        double nextY = (delta.y + 0.08) * friction - hoverGravity;
        double nextZ = delta.z * friction;

        player.setDeltaMovement(nextX, nextY, nextZ);

        if (player.fallDistance > 0) player.fallDistance = 0;
    }

    // 当玩家“站立”悬浮时调用的简易重力逻辑
    private static void applyLowGravity(Player player) {
        Vec3 delta = player.getDeltaMovement();
        // 仅仅是让玩家在空中不至于飘走，但依然有极轻微的下坠
        player.setDeltaMovement(delta.x * 0.9, (delta.y + 0.08) * 0.9 - 0.005, delta.z * 0.9);
        if (player.fallDistance > 0) player.fallDistance = 0;
    }
}