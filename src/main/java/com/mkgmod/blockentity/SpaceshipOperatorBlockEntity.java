package com.mkgmod.blockentity;

import com.mkgmod.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

import java.util.Optional;
import java.util.Set;

public class SpaceshipOperatorBlockEntity extends BlockEntity {
    private CompoundTag savedSpaceshipNbt = null;
    private BlockPos sourcePos; // 记录出发点
    private ResourceKey<Level> sourceLevelKey; // 记录出发维度

    public SpaceshipOperatorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SPACESHIP_OPERATOR_BlockEntity.get(), pos, state);
    }

// ------------- 逻辑：组装飞船 (识别铁块并保存) -------------
    public void assembleSpaceship(ServerPlayer player) {
        if (this.level == null || this.level.isClientSide) return;

        ServerLevel serverLevel = (ServerLevel) this.level;
        StructureTemplateManager manager = serverLevel.getServer().getStructureManager();
        StructureTemplate template = manager.getOrCreate(ResourceLocation.fromNamespaceAndPath("mkgmod", "spaceship_data"));

        BlockPos center = this.worldPosition;
        BlockPos start = center.offset(-3, -3, -3);
        Vec3i size = new Vec3i(7, 7, 7);
        template.fillFromWorld(serverLevel, start, size, true, null);
        this.savedSpaceshipNbt = template.save(new CompoundTag());
        this.setChanged();
        manager.save(ResourceLocation.fromNamespaceAndPath("mkgmod", "spaceship_data"));
        player.sendSystemMessage(Component.literal("§a§l完成飞船检测。"));
    }

    // ------------- 逻辑：发射 (恢复 NBT) -------------
    // --- 工具方法：清除飞船区域 ---
    private void clearShipArea(Level level, BlockPos center) {
        // 飞船是 7x7x7，中心偏移是 -3, -3, -3
        BlockPos start = center.offset(-3, -3, -3);
        BlockPos end = center.offset(3, 3, 3);

        for (BlockPos pos : BlockPos.betweenClosed(start, end)) {
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
        }
    }

    // ------------- 逻辑：发射 (主世界 -> 末地深空) -------------
    public void launchSpaceship(ServerPlayer player) {
        if (this.savedSpaceshipNbt == null || this.level == null) {
            player.sendSystemMessage(Component.literal("§c错误：飞船尚未完成部件检测！"));
            return;
        }

        // 记录出发数据
        this.sourcePos = this.worldPosition;
        this.sourceLevelKey = this.level.dimension();
        this.setChanged(); // 标记需要保存数据

        ServerLevel endLevel = player.server.getLevel(Level.END);
        // 2. 定义末地坐标 (500,000 坐标点)
        BlockPos targetPos = new BlockPos(500000, 120, 500000);

        // 3. 清除末地目标区 (防止撞上之前的残留或其他方块)
        int clearRadius = 15; // 实际上飞船只有 7x7，给 15 绰绰有余
        for (BlockPos pos : BlockPos.betweenClosed(targetPos.offset(-clearRadius, -10, -clearRadius), targetPos.offset(clearRadius, clearRadius, clearRadius))) {
            if (!endLevel.isEmptyBlock(pos)) endLevel.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_CLIENTS);
        }

        if (endLevel != null) {
            // 4. 在新维度放置飞船
            StructureTemplate template = new StructureTemplate();
            template.load(endLevel.holderLookup(Registries.BLOCK), savedSpaceshipNbt);
            BlockPos placePos = targetPos.offset(-3, -3, -3);
            template.placeInWorld(endLevel, placePos, placePos, new StructurePlaceSettings().setIgnoreEntities(false).setFinalizeEntities(true), endLevel.getRandom(), Block.UPDATE_ALL);

            BlockEntity newBE = endLevel.getBlockEntity(targetPos);
            if (newBE instanceof SpaceshipOperatorBlockEntity spaceshipBE) {
                spaceshipBE.sourcePos = this.sourcePos;
                spaceshipBE.sourceLevelKey = this.sourceLevelKey;
                spaceshipBE.savedSpaceshipNbt = this.savedSpaceshipNbt; // 同步 NBT 状态
                spaceshipBE.setChanged();
            }

            clearShipArea(this.level, this.worldPosition);

            player.teleportTo(endLevel, targetPos.getX(), targetPos.getY(), targetPos.getZ(), Set.of(), player.getYRot(), player.getXRot());

            player.sendSystemMessage(Component.literal("§b§l超空间引擎已启动。目标：深空。"));
        }
    }

    // ------------- 逻辑：返回 (末地深空 -> 原来世界的位置) -------------
    public void returnToOverworld(ServerPlayer player) {
        if (this.sourcePos == null) {
            player.sendSystemMessage(Component.literal("§c错误：找不到返回坐标！"));
            return;
        }

        ServerLevel targetLevel = player.server.getLevel(this.sourceLevelKey);
        if (targetLevel != null && this.level != null) {
            // 1. 清除当前末地位置的飞船 (避免留在太空)
            clearShipArea(this.level, this.worldPosition);

            // 2. 在原世界目标位置放置飞船
            StructureTemplate template = new StructureTemplate();
            template.load(targetLevel.holderLookup(Registries.BLOCK), savedSpaceshipNbt);
            BlockPos placePos = this.sourcePos.offset(-3, -3, -3);

            template.placeInWorld(targetLevel, placePos, placePos, new StructurePlaceSettings().setIgnoreEntities(false).setFinalizeEntities(true), targetLevel.getRandom(), Block.UPDATE_ALL);

            // 3. 传送玩家回主世界
            player.teleportTo(targetLevel, this.sourcePos.getX() + 0.5, this.sourcePos.getY() + 1.0, this.sourcePos.getZ() + 0.5, Set.of(), player.getYRot(), player.getXRot());
            player.sendSystemMessage(Component.literal("§a§l重返主世界。已着陆至出发点。"));
        }
    }
    // -------------

    //-------------
    //暂时废弃
    //定义太空维度的 Key (对应 space_void_type.json)
//    public static final ResourceKey<Level> SPACE_VOID = ResourceKey.create(
//            Registries.DIMENSION,
//            ResourceLocation.fromNamespaceAndPath("mkgmod", "space_void")
//    );
    //-------------

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (savedSpaceshipNbt != null) tag.put("spaceship_data", savedSpaceshipNbt);

        // 保存返航坐标
        if (sourcePos != null) {
            tag.putLong("source_pos", sourcePos.asLong());
        }
        // 保存维度信息
        if (sourceLevelKey != null) {
            tag.putString("source_dimension", sourceLevelKey.location().toString());
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("spaceship_data")) this.savedSpaceshipNbt = tag.getCompound("spaceship_data");

        // 读取返航坐标
        if (tag.contains("source_pos")) {
            this.sourcePos = BlockPos.of(tag.getLong("source_pos"));
        }
        // 读取维度信息
        if (tag.contains("source_dimension")) {
            this.sourceLevelKey = ResourceKey.create(Registries.DIMENSION,
                    ResourceLocation.parse(tag.getString("source_dimension")));
        }
    }
}