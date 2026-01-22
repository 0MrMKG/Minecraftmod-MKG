package com.mkgmod.item;


import com.mkgmod.item.food.ModFoods;
import com.mkgmod.item.tool.CreditCollector;
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

    public static final DeferredItem<Item> CREDIT_COLLECTOR = ITEMS.register("credit_collector",
            () -> new CreditCollector(new Item.Properties()));

    // 银河信用点面值注册
    public static final DeferredItem<Item> GALAXY_CREDIT_1 = ITEMS.register("galaxy_credit_1",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> GALAXY_CREDIT_10 = ITEMS.register("galaxy_credit_10",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> GALAXY_CREDIT_100 = ITEMS.register("galaxy_credit_100",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> GALAXY_CREDIT_1000 = ITEMS.register("galaxy_credit_1000",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> GALAXY_CREDIT_10000 = ITEMS.register("galaxy_credit_10000",
            () -> new Item(new Item.Properties()));

}
