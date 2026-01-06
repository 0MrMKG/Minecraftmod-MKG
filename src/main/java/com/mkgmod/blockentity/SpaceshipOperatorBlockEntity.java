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
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.phys.AABB;

import java.util.List;
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
    }


    // ------------- 发射 (从主世界 -> 末地深空) -------------
    public void launchSpaceship(ServerPlayer player) {
        // --- 改动：先记录坐标和维度，再进行组装，这样快照里才会包含这些数据 ---
        this.sourcePos = this.worldPosition;
        this.sourceLevelKey = this.level.dimension();
        this.setChanged();
        this.assembleSpaceship(player);
        // -----------------------------------------------------------------

        if (this.savedSpaceshipNbt == null || this.level == null) {
            player.sendSystemMessage(Component.literal("§c错误：飞船尚未完成部件检测！"));
            return;
        }

        // --- 删除了手动 putLong/putString 到 savedSpaceshipNbt 的代码，因为 assemble 会自动抓取 ---

        ServerLevel endLevel = player.server.getLevel(Level.END);
        BlockPos targetPos = new BlockPos(500000, 120, 500000);
        int clearRadius = 15;
        for (BlockPos pos : BlockPos.betweenClosed(targetPos.offset(-clearRadius, -10, -clearRadius), targetPos.offset(clearRadius, clearRadius, clearRadius))) {
            if (!endLevel.isEmptyBlock(pos)) endLevel.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_CLIENTS);
        }

        if (endLevel != null) {
            StructureTemplate template = new StructureTemplate();
            template.load(endLevel.holderLookup(Registries.BLOCK), savedSpaceshipNbt);
            BlockPos placePos = targetPos.offset(-3, -3, -3);
            template.placeInWorld(endLevel, placePos, placePos, new StructurePlaceSettings().setIgnoreEntities(false).setFinalizeEntities(true), endLevel.getRandom(), Block.UPDATE_ALL);

            clearShipArea(this.level, this.worldPosition);
            player.teleportTo(endLevel, targetPos.getX() + 0.5, targetPos.getY(), targetPos.getZ() + 0.5, Set.of(), player.getYRot(), player.getXRot()); // --- 改动：加0.5防止卡墙 ---
            player.sendSystemMessage(Component.literal("§b§l已跃迁到深空。"));
        }
    }


    // ------------- 逻辑：返回 (末地深空 -> 原来世界的位置) -------------
    public void returnToOverworld(ServerPlayer player) {
        if (this.sourcePos == null || this.sourceLevelKey == null) {
            player.sendSystemMessage(Component.literal("§c错误：导航系统丢失原点坐标！"));
            return;
        }

        ServerLevel homeLevel = player.server.getLevel(this.sourceLevelKey);
        if (homeLevel == null) return;

        this.assembleSpaceship(player);
        clearShipArea(this.level, this.worldPosition);

        StructureTemplate template = new StructureTemplate();
        template.load(homeLevel.holderLookup(Registries.BLOCK), savedSpaceshipNbt);

        // --- 改动：offset 必须与发射时的 -3 保持一致，否则飞船位置会偏移 ---
        BlockPos placePos = this.sourcePos.offset(-3, -3, -3);
        // -------------------------------------------------------------

        template.placeInWorld(homeLevel, placePos, placePos, new StructurePlaceSettings(), homeLevel.getRandom(), Block.UPDATE_ALL);

        player.teleportTo(homeLevel, sourcePos.getX() + 0.5, sourcePos.getY(), sourcePos.getZ() + 0.5, Set.of(), player.getYRot(), player.getXRot());
        player.sendSystemMessage(Component.literal("§a§l已回到原坐标。"));
    }


    //[暂时废弃]   定义太空维度的 Key (对应 space_void_type.json)
//    public static final ResourceKey<Level> SPACE_VOID = ResourceKey.create(
//            Registries.DIMENSION,
//            ResourceLocation.fromNamespaceAndPath("mkgmod", "space_void")
//    );


    //-------------
    private void clearShipArea(Level level, BlockPos center) {
        BlockPos start = center.offset(-3, -3, -3);
        BlockPos end = center.offset(3, 3, 3);

        // 1. 清除方块
        for (BlockPos pos : BlockPos.betweenClosed(start, end)) {
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
        }

        // --- 改动：清除区域内的掉落物实体 ---
        if (!level.isClientSide) {
            // 创建一个包围盒（AABB），对应你飞船的 7x7x7 区域
            AABB area = new AABB(start).minmax(new AABB(end));

            // 获取区域内所有的掉落物实体 (ItemEntity)
            List<ItemEntity> items = level.getEntitiesOfClass(ItemEntity.class, area);

            // 遍历并移除它们
            for (ItemEntity item : items) {
                item.discard(); // 或者使用 item.remove(Entity.RemovalReason.DISCARDED);
            }
        }
        // ---------------------------------
    }


    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (this.sourcePos != null) {
            tag.putLong("SourcePos", this.sourcePos.asLong());
        }
        if (this.sourceLevelKey != null) {
            tag.putString("SourceDim", this.sourceLevelKey.location().toString());
        }
        if (this.savedSpaceshipNbt != null) {
            tag.put("SavedSpaceship", this.savedSpaceshipNbt);
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("SourcePos")) {
            this.sourcePos = BlockPos.of(tag.getLong("SourcePos"));
        }
        if (tag.contains("SourceDim")) {
            this.sourceLevelKey = ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse(tag.getString("SourceDim")));
        }
        if (tag.contains("SavedSpaceship")) {
            this.savedSpaceshipNbt = tag.getCompound("SavedSpaceship");
        }
    }
}