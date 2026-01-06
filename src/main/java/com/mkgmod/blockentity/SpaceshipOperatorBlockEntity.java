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

    public SpaceshipOperatorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SPACESHIP_OPERATOR_BlockEntity.get(), pos, state);
    }

// ------------- 逻辑：组装飞船 (识别铁块并保存) -------------
    public void assembleSpaceship() {
        if (this.level == null || this.level.isClientSide) return;
        ServerLevel serverLevel = (ServerLevel) this.level;
        StructureTemplateManager manager = serverLevel.getServer().getStructureManager();
        StructureTemplate template = manager.getOrCreate(ResourceLocation.fromNamespaceAndPath("mkgmod", "spaceship_data"));

        BlockPos center = this.worldPosition;
        BlockPos start = center.offset(-3, -3, -3);
        Vec3i size = new Vec3i(7, 7, 7);
        // 填充结构内容
        template.fillFromWorld(serverLevel, start, size, true, null);
        // ------------- 修复部分 -------------
        // 1. 将 template 的内容转换为 CompoundTag 并存入变量
        this.savedSpaceshipNbt = template.save(new CompoundTag());
        // 2. 必须调用 setChanged()，否则 Minecraft 不会触发 saveAdditional，重启后数据会丢失
        this.setChanged();
        // -----------------------------------
        // 可选：保存到文件（如果你想在其他地方也用这个 NBT）
        manager.save(ResourceLocation.fromNamespaceAndPath("mkgmod", "spaceship_data"));
        // 反馈给玩家
        System.out.println("飞船组装完成，NBT 已锁定到变量中！");
    }
    // -------------
    // ------------- 逻辑：发射 (恢复 NBT) -------------
// ------------- 逻辑：发射 (恢复 NBT 并在相同位置生成) -------------
    public void launchSpaceship(ServerPlayer player) {
        if (this.savedSpaceshipNbt == null) {
            player.sendSystemMessage(Component.literal("§c错误：飞船尚未组装！"));
            return;
        }

        ServerLevel spaceLevel = player.server.getLevel(SPACE_VOID);
        if (spaceLevel != null) {
            // ------------- 修复：使用当前操作方块的坐标 -------------
            BlockPos targetPos = this.worldPosition;
            // 如果你希望在太空维度稍微高一点（比如防止掉进虚空），可以使用：
            // BlockPos targetPos = new BlockPos(this.worldPosition.getX(), 120, this.worldPosition.getZ());
            // -----------------------------------------------------
            StructureTemplate template = new StructureTemplate();
            template.load(spaceLevel.holderLookup(Registries.BLOCK), savedSpaceshipNbt);
            // 注意：组装时我们用了 center.offset(-3, -3, -3)
            // 所以放置时也要偏移回去，否则飞船中心不会在 targetPos
            BlockPos placePos = targetPos.offset(-3, -3, -3);
            StructurePlaceSettings settings = new StructurePlaceSettings()
                    .setIgnoreEntities(false)
                    .setFinalizeEntities(true);
            // 在太空生成飞船
            template.placeInWorld(
                    spaceLevel,
                    placePos, // 放置的起始点
                    placePos,
                    settings,
                    spaceLevel.getRandom(),
                    Block.UPDATE_ALL
            );
            // ------------- 修复：传送玩家到操作方块的位置 -------------
            player.teleportTo(
                    spaceLevel,
                    targetPos.getX() + 0.5,
                    targetPos.getY() + 1.0, // 站在方块上面
                    targetPos.getZ() + 0.5,
                    Set.of(),
                    player.getYRot(),
                    player.getXRot()
            );
            // -----------------------------------------------------
            player.sendSystemMessage(Component.literal("§b§l飞船已跳跃至目标坐标。"));
        }
    }
    // -------------

    //-------------
    // 定义太空维度的 Key (对应你的 space_void_type.json)
    public static final ResourceKey<Level> SPACE_VOID = ResourceKey.create(
            Registries.DIMENSION,
            ResourceLocation.fromNamespaceAndPath("mkgmod", "space_void")
    );
    //-------------

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (savedSpaceshipNbt != null) tag.put("spaceship_data", savedSpaceshipNbt);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("spaceship_data")) this.savedSpaceshipNbt = tag.getCompound("spaceship_data");
    }
}