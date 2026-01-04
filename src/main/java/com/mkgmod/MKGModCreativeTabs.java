package com.mkgmod;

import com.mkgmod.item.ModItems;
import com.mkgmod.registry.ModBlockItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class MKGModCreativeTabs {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MKGMOD.MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MKG_TAB =
            CREATIVE_MODE_TABS.register("mkg_tab", () -> CreativeModeTab.builder()
                    .title(Component.translatable("creativetab.mkgmod.mkg_tab"))
                    // ✅ 修改这里：使用 Items.GOLDEN_APPLE 替代之前的方块物品
                    .icon(() -> new ItemStack(Items.GOLDEN_APPLE))
                    .displayItems((parameters, output) -> {
                        // 这里依然保留你的方块，因为这是要把方块“放进”这个物品栏
                        output.accept(ModBlockItems.TEST_BLOCK_ITEM.get());
                        output.accept(ModBlockItems.TEST_BLOCK2_ITEM.get());
                        output.accept(ModItems.TEST_FOOD.get());
                    })
                    .build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
