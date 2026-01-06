package com.mkgmod.network;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class TeleportPayloadHandler {

    public static void handleTeleport(final TeleportPayload payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player) {
                MinecraftServer server = player.getServer();
                if (server == null) return;

                String currentDimId = player.level().dimension().location().toString();
                savePlayerLocation(player, currentDimId);

                String targetDimId = payload.dimensionId();
                ResourceLocation destLoc = ResourceLocation.parse(targetDimId);
                ResourceKey<Level> destKey = ResourceKey.create(Registries.DIMENSION, destLoc);
                ServerLevel destLevel = server.getLevel(destKey);

                if (destLevel != null) {
                    BlockPos targetPos = loadPlayerLocation(player, targetDimId);

                    if (targetPos == null) {
                        // 第一次进入，使用加强版的安全寻找逻辑
                        BlockPos initialSpawn = destLevel.getSharedSpawnPos();
                        BlockPos safePos = findSafePosition(destLevel, initialSpawn);

                        player.teleportTo(destLevel, safePos.getX() + 0.5, safePos.getY() + 0.2, safePos.getZ() + 0.5, player.getYRot(), player.getXRot());
                    } else {
                        player.teleportTo(destLevel, targetPos.getX() + 0.5, targetPos.getY() + 0.2, targetPos.getZ() + 0.5, player.getYRot(), player.getXRot());
                    }
                }
            }
        });
    }

    private static BlockPos findSafePosition(ServerLevel level, BlockPos startPos) {
        String dimId = level.dimension().location().toString();

        // 1. 下界处理逻辑
        if (dimId.equals("minecraft:the_nether")) {
            // 下界通常在 Y=31 以下是巨大的岩浆海，所以我们从 120 扫到 32
            for (int y = 120; y > 32; y--) {
                BlockPos checkPos = new BlockPos(startPos.getX(), y, startPos.getZ());
                if (isPosSafe(level, checkPos)) {
                    return checkPos;
                }
            }
            // 如果垂直扫描失败，尝试在附近 10 格范围内寻找
            return startPos.atY(64);
        }

        // 2. 末地处理逻辑
        if (dimId.equals("minecraft:the_end")) {
            return new BlockPos(100, 50, 0);
        }

        // 3. 自定义星球/主世界处理逻辑 (针对 high_sakura_peaks 所在维度)
        // 先尝试使用高度图获取地表
        int surfaceY = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, startPos.getX(), startPos.getZ());

        // 核心修复：如果高度图返回的值在世界底部（说明区块没加载好），则强制手动扫描
        if (surfaceY <= level.getMinBuildHeight() + 1) {
            // 从天空向下扫描到地壳
            for (int y = level.getMaxBuildHeight(); y > level.getMinBuildHeight(); y--) {
                BlockPos checkPos = new BlockPos(startPos.getX(), y, startPos.getZ());
                // 如果当前是空气且下方是固体方块
                if (level.getBlockState(checkPos).isAir() && level.getBlockState(checkPos.below()).isSolid()) {
                    return checkPos;
                }
            }
            // 如果全扫完都没找到固体方块（比如出生在虚空海上），返回一个合理的默认高度
            return startPos.atY(level.getSeaLevel() + 10);
        }

        return startPos.atY(surfaceY);
    }

    private static void savePlayerLocation(ServerPlayer player, String dimId) {
        CompoundTag persistentData = player.getPersistentData();
        if (!persistentData.contains("mkgmod_travel_data")) {
            persistentData.put("mkgmod_travel_data", new CompoundTag());
        }
        CompoundTag travelData = persistentData.getCompound("mkgmod_travel_data");

        CompoundTag posTag = new CompoundTag();
        posTag.putDouble("x", player.getX());
        posTag.putDouble("y", player.getY());
        posTag.putDouble("z", player.getZ());

        travelData.put(dimId, posTag);
    }

    private static BlockPos loadPlayerLocation(ServerPlayer player, String dimId) {
        CompoundTag persistentData = player.getPersistentData();
        if (persistentData.contains("mkgmod_travel_data")) {
            CompoundTag travelData = persistentData.getCompound("mkgmod_travel_data");
            if (travelData.contains(dimId)) {
                CompoundTag posTag = travelData.getCompound(dimId);
                return new BlockPos(
                        (int) posTag.getDouble("x"),
                        (int) posTag.getDouble("y"),
                        (int) posTag.getDouble("z")
                );
            }
        }
        return null;
    }


    private static boolean isPosSafe(ServerLevel level, BlockPos pos) {
        BlockPos below = pos.below();

        // 检查脚下：必须是固体方块，且不能是岩浆、火、或者岩浆块
        boolean solidFloor = level.getBlockState(below).isSolid();
        boolean isLava = level.getFluidState(below).is(net.minecraft.tags.FluidTags.LAVA);
        boolean isMagma = level.getBlockState(below).is(net.minecraft.world.level.block.Blocks.MAGMA_BLOCK);
        boolean isFire = level.getBlockState(pos).is(net.minecraft.world.level.block.Blocks.FIRE);

        // 检查空间：脚下是固体且非危险品，身体和头部是空气（或者是可替换的方块如草）
        return solidFloor && !isLava && !isMagma && !isFire &&
                level.getBlockState(pos).isAir() &&
                level.getBlockState(pos.above()).isAir();
    }
}