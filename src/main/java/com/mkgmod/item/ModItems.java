package com.mkgmod.item;


import com.mkgmod.item.food.ModFoods;
import com.mkgmod.item.tool.VoyagerCompassItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("mkgmod");

    public static final DeferredItem<Item> TEST_FOOD = ITEMS.register("test_food",
            () -> new Item(new Item.Properties().food(ModFoods.TEST_FOOD)));

    public static final DeferredItem<VoyagerCompassItem> VOYAGER_COMPASS = ITEMS.register("voyager_compass",
            () -> new VoyagerCompassItem(new Item.Properties().stacksTo(1)));
}
