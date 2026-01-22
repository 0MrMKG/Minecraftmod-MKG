package com.mkgmod;

import com.mkgmod.blockentity.SpaceshipOperatorBlockEntity;
import com.mkgmod.init.ModBlockEntities;
import com.mkgmod.item.ModItems;
import com.mkgmod.network.SpaceshipActionPayload;
import com.mkgmod.network.SpaceshipPacketHandler;
import com.mkgmod.network.TeleportPayloadHandler;
import com.mkgmod.network.TeleportPayload;
import com.mkgmod.registry.ModAttachments;
import com.mkgmod.registry.ModBlockItems;
import com.mkgmod.registry.ModBlocks;
import com.mkgmod.registry.ModCommands;
import com.mkgmod.worldgen.MKGRegion;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

import static com.mkgmod.registry.ModBlockItems.TEST_BLOCK2_ITEM;
import static com.mkgmod.registry.ModBlockItems.TEST_BLOCK_ITEM;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(MKGMOD.MODID)
public class MKGMOD {
    public static final String MODID = "mkgmod";
    public static final Logger LOGGER = LogUtils.getLogger();


    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public MKGMOD(IEventBus modEventBus, ModContainer modContainer) {
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::registerNetworking);
        //=========================================
        //Blocks
        ModBlocks.register(modEventBus);
        ModBlockItems.register(modEventBus);
        //Entities
        ModBlockEntities.BLOCK_ENTITIES.register(modEventBus);
        //Items
        ModItems.ITEMS.register(modEventBus);
        //CreativeTabs
        MKGModCreativeTabs.register(modEventBus);
        //OTHER
        SpaceshipOperatorBlockEntity.ATTACHMENT_TYPES.register(modEventBus);

        //=========================================
        NeoForge.EVENT_BUS.register(this);

        // 【核心操作】将附件注册表挂载到总线上
        ModAttachments.ATTACHMENT_TYPES.register(modEventBus);

        // Register the item to a creative tab
        modEventBus.addListener(this::addCreative);
        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void registerNetworking(final RegisterPayloadHandlersEvent event) {
        // "1" 是协议版本号，如果以后更新了包结构，可以改掉它
        final PayloadRegistrar registrar = event.registrar("1");

        // 注册从 客户端 发送到 服务器 的数据包
        registrar.playToServer(
                TeleportPayload.TYPE,
                TeleportPayload.STREAM_CODEC,
                TeleportPayloadHandler::handleTeleport
        );

        //-------------
        // 2. 注册飞船动作包（必须添加这一段，否则会报错）
        registrar.playToServer(
                SpaceshipActionPayload.TYPE,
                SpaceshipActionPayload.CODEC,
                SpaceshipPacketHandler::handleData
        );
        //-------------
    }

    @SubscribeEvent
    public void onCommandsRegister(RegisterCommandsEvent event) {
        // 在这里调用你写的 register 方法
        ModCommands.register(event.getDispatcher());

    }


    private void commonSetup(FMLCommonSetupEvent event) {
        // Some common setup code
        LOGGER.info("HELLO FROM COMMON SETUP");
        if (Config.LOG_DIRT_BLOCK.getAsBoolean()) {
            LOGGER.info("DIRT BLOCK >> {}", BuiltInRegistries.BLOCK.getKey(Blocks.DIRT));
        }
        LOGGER.info("{}{}", Config.MAGIC_NUMBER_INTRODUCTION.get(), Config.MAGIC_NUMBER.getAsInt());

        event.enqueueWork(() -> {
            terrablender.api.Regions.register(new MKGRegion(
                    ResourceLocation.fromNamespaceAndPath(MODID, "overworld_region"), 100));
        });

        Config.ITEM_STRINGS.get().forEach((item) -> LOGGER.info("ITEM >> {}", item));
    }

    // Add the example block item to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
            event.accept(TEST_BLOCK_ITEM);
        }
        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
            event.accept(TEST_BLOCK2_ITEM);
        }
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Do something when the server starts
        LOGGER.info("HELLO from server starting");
    }
}


