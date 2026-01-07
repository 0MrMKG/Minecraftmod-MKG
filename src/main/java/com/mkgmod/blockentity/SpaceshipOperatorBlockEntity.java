package com.mkgmod.blockentity;

import com.mkgmod.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
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
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.*;
import java.util.function.Supplier;


public class SpaceshipOperatorBlockEntity extends BlockEntity {

    private BlockPos sourcePos; // 记录出发点
    private ResourceKey<Level> sourceLevelKey; // 记录出发维度

    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, "mkgmod");
    public static final Supplier<AttachmentType<CompoundTag>> SHIP_DATA_ATTACHMENT =
            ATTACHMENT_TYPES.register("ship_home_data", () -> AttachmentType.builder(() -> new CompoundTag()).serialize(CompoundTag.CODEC).build());

    public SpaceshipOperatorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SPACESHIP_OPERATOR_BlockEntity.get(), pos, state);
    }

    private Vec3i shipSize = Vec3i.ZERO;
    private BlockPos minPos = BlockPos.ZERO;
    private CompoundTag savedSpaceshipNbt = null;

    public void assembleSpaceship(ServerPlayer player) {
        if (this.level == null || this.level.isClientSide) return;
        ServerLevel serverLevel = (ServerLevel) this.level;

        Set<BlockPos> shipBlocks = findShipBlocks(serverLevel, this.worldPosition);

        if (shipBlocks.isEmpty()) return;

        int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE, minZ = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE, maxZ = Integer.MIN_VALUE;

        for (BlockPos pos : shipBlocks) {
            minX = Math.min(minX, pos.getX());
            minY = Math.min(minY, pos.getY());
            minZ = Math.min(minZ, pos.getZ());
            maxX = Math.max(maxX, pos.getX());
            maxY = Math.max(maxY, pos.getY());
            maxZ = Math.max(maxZ, pos.getZ());
        }

        this.minPos = new BlockPos(minX, minY, minZ); // <- 修改：赋值给全局变量
        BlockPos start = new BlockPos(minX, minY, minZ);
        Vec3i size = new Vec3i(maxX - minX + 1, maxY - minY + 1, maxZ - minZ + 1);
        this.shipSize = size;

        StructureTemplateManager manager = serverLevel.getServer().getStructureManager();
        StructureTemplate template = manager.getOrCreate(ResourceLocation.fromNamespaceAndPath("mkgmod", "spaceship_data"));

        template.fillFromWorld(serverLevel, start, size, true, null);

        this.savedSpaceshipNbt = template.save(new CompoundTag());
        this.setChanged();
        manager.save(ResourceLocation.fromNamespaceAndPath("mkgmod", "spaceship_data"));

        player.displayClientMessage(Component.literal("飞船组装完成！范围: " + size.toShortString()), true);
    }

    /**
     * BFS 算法寻找连接的飞船方块
     */
    private Set<BlockPos> findShipBlocks(Level level, BlockPos startPos) {
        Set<BlockPos> visited = new HashSet<>();
        Queue<BlockPos> queue = new LinkedList<>();

        queue.add(startPos);
        visited.add(startPos);

        int maxBlocks = 1024; // 限制大小，防止卡死服务器

        while (!queue.isEmpty() && visited.size() < maxBlocks) {
            BlockPos current = queue.poll();

            for (Direction direction : Direction.values()) {
                BlockPos neighbor = current.relative(direction);

                if (!visited.contains(neighbor) && isShipMaterial(level.getBlockState(neighbor))) {
                    visited.add(neighbor);
                    queue.add(neighbor);
                }
            }
        }
        return visited;
    }

    /**
     * 定义哪些方块可以被视为飞船的一部分
     */
    private boolean isShipMaterial(BlockState state) {
        // 允许铁块、玻璃、或者铁栏杆等
        return state.is(Blocks.IRON_BLOCK)
                || state.is(Blocks.GLASS)
                || state.is(this.getBlockState().getBlock()); // 包含控制台本身
    }

    // ------------- 发射 (从主世界 -> 末地深空) -------------
    public void launchSpaceship(ServerPlayer player) {
        this.sourcePos = this.worldPosition;
        this.sourceLevelKey = this.level.dimension();

        CompoundTag playerTag = new CompoundTag();
        playerTag.putInt("x", this.sourcePos.getX());
        playerTag.putInt("y", this.sourcePos.getY());
        playerTag.putInt("z", this.sourcePos.getZ());
        playerTag.putString("dim", this.sourceLevelKey.location().toString());
        player.setData(SpaceshipOperatorBlockEntity.SHIP_DATA_ATTACHMENT, playerTag);


        this.setChanged();
        this.assembleSpaceship(player);

        if (this.savedSpaceshipNbt == null || this.level == null) {
            player.sendSystemMessage(Component.literal("§c错误：飞船尚未完成部件检测！"));
            return;
        }

        ServerLevel endLevel = player.server.getLevel(Level.END);
        BlockPos targetPos = new BlockPos(500000, 120, 500000);

        // 计算控制台相对于飞船起点的偏移
        BlockPos relativeOffset = this.worldPosition.subtract(this.minPos); // <- 新增：核心偏移逻辑
        BlockPos placePos = targetPos.subtract(relativeOffset);


        int clearRadius = Math.max(this.shipSize.getX(), Math.max(this.shipSize.getY(), Math.max(this.shipSize.getZ(),40)));
        for (BlockPos pos : BlockPos.betweenClosed(targetPos.offset(-clearRadius, -10, -clearRadius), targetPos.offset(clearRadius, clearRadius, clearRadius))) {
            if (!endLevel.isEmptyBlock(pos)) endLevel.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_CLIENTS);
        }

        if (endLevel != null) {
            StructureTemplate template = new StructureTemplate();
            template.load(endLevel.holderLookup(Registries.BLOCK), savedSpaceshipNbt);
            template.placeInWorld(endLevel, placePos, placePos, new StructurePlaceSettings().setIgnoreEntities(false).setFinalizeEntities(true), endLevel.getRandom(), Block.UPDATE_ALL); // <- 修改
            clearShipArea(this.level);
            player.teleportTo(endLevel, targetPos.getX() + 0.5, targetPos.getY(), targetPos.getZ() + 0.5, Set.of(), player.getYRot(), player.getXRot()); // --- 改动：加0.5防止卡墙 ---
            player.sendSystemMessage(Component.literal("§b§l已跃迁到深空。"));
        }
    }

    // ------------- 逻辑：返回 (末地深空 -> 原来世界的位置) -------------
    public void returnToOverworld(ServerPlayer player) {

        if (this.sourcePos == null || this.sourceLevelKey == null) {
            CompoundTag playerTag = player.getData(SpaceshipOperatorBlockEntity.SHIP_DATA_ATTACHMENT);
            if (playerTag.contains("dim")) {
                this.sourcePos = new BlockPos(playerTag.getInt("x"), playerTag.getInt("y"), playerTag.getInt("z"));
                this.sourceLevelKey = ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse(playerTag.getString("dim")));
            }
        }


        if (this.sourcePos == null || this.sourceLevelKey == null) {
            player.sendSystemMessage(Component.literal("§c错误：导航系统丢失原点坐标！"));
            return;
        }

        ServerLevel homeLevel = player.server.getLevel(this.sourceLevelKey);
        if (homeLevel == null) return;

        this.assembleSpaceship(player);

        BlockPos relativeOffset = this.worldPosition.subtract(this.minPos); // <- 新增
        BlockPos placePos = this.sourcePos.subtract(relativeOffset); // <- 修改：回到原控制台对应的起点

        clearShipArea(this.level);

        StructureTemplate template = new StructureTemplate();
        template.load(homeLevel.holderLookup(Registries.BLOCK), savedSpaceshipNbt);

        template.placeInWorld(homeLevel, placePos, placePos, new StructurePlaceSettings(), homeLevel.getRandom(), Block.UPDATE_ALL);

        player.teleportTo(homeLevel, sourcePos.getX() + 0.5, sourcePos.getY(), sourcePos.getZ() + 0.5, Set.of(), player.getYRot(), player.getXRot());
        player.sendSystemMessage(Component.literal("§a§l已回到原坐标。"));
    }

    private void clearShipArea(Level level) {
        BlockPos start =  this.minPos; // 改动
        BlockPos end = this.minPos.offset(this.shipSize);

        for (BlockPos pos : BlockPos.betweenClosed(start, end)) {
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
        }

        if (!level.isClientSide) {
            AABB area = new AABB(start).minmax(new AABB(end));
            List<ItemEntity> items = level.getEntitiesOfClass(ItemEntity.class, area);
            for (ItemEntity item : items) {
                item.discard();
            }
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);

        // 1. 保存原始坐标 (sourcePos)
        if (this.sourcePos != null) {
            tag.putInt("srcX", this.sourcePos.getX());
            tag.putInt("srcY", this.sourcePos.getY());
            tag.putInt("srcZ", this.sourcePos.getZ());
        }

        // 2. 保存原始维度 (sourceLevelKey)
        if (this.sourceLevelKey != null) {
            tag.putString("srcDim", this.sourceLevelKey.location().toString());
        }

        // 3. 保存飞船包围盒信息 (minPos 和 shipSize)
        if (this.minPos != null) {
            tag.putInt("minX", this.minPos.getX());
            tag.putInt("minY", this.minPos.getY());
            tag.putInt("minZ", this.minPos.getZ());
        }
        if (this.shipSize != null) {
            tag.putInt("sizeX", this.shipSize.getX());
            tag.putInt("sizeY", this.shipSize.getY());
            tag.putInt("sizeZ", this.shipSize.getZ());
        }

        // 4. 保存飞船结构数据 (savedSpaceshipNbt)
        if (this.savedSpaceshipNbt != null) {
            tag.put("shipNbt", this.savedSpaceshipNbt);
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);

        // 1. 读取原始坐标
        if (tag.contains("srcX")) {
            this.sourcePos = new BlockPos(tag.getInt("srcX"), tag.getInt("srcY"), tag.getInt("srcZ"));
        }

        // 2. 读取原始维度
        if (tag.contains("srcDim")) {
            ResourceLocation dimLoc = ResourceLocation.parse(tag.getString("srcDim"));
            this.sourceLevelKey = ResourceKey.create(Registries.DIMENSION, dimLoc);
        }

        // 3. 读取包围盒信息
        if (tag.contains("minX")) {
            this.minPos = new BlockPos(tag.getInt("minX"), tag.getInt("minY"), tag.getInt("minZ"));
        }
        if (tag.contains("sizeX")) {
            this.shipSize = new Vec3i(tag.getInt("sizeX"), tag.getInt("sizeY"), tag.getInt("sizeZ"));
        }

        // 4. 读取飞船结构数据
        if (tag.contains("shipNbt")) {
            this.savedSpaceshipNbt = tag.getCompound("shipNbt");
        }
    }


    // 当区块加载或方块更新时，将服务端数据同步给客户端
    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        saveAdditional(tag, registries); // 复用你的保存逻辑
        return tag;
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        // 创建一个标准的数据同步包，这样 /data get 就能读到实时数据
        return ClientboundBlockEntityDataPacket.create(this);
    }

}




//[暂时废弃]   定义太空维度的 Key (对应 space_void_type.json)
//    public static final ResourceKey<Level> SPACE_VOID = ResourceKey.create(
//            Registries.DIMENSION,
//            ResourceLocation.fromNamespaceAndPath("mkgmod", "space_void")
//    );
