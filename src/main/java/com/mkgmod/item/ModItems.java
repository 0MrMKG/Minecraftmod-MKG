package com.mkgmod.item;

import com.mkgmod.item.food.ModFoods;
import com.mkgmod.item.galaxycredit.*;
import com.mkgmod.item.tool.CreditCollector;
import com.mkgmod.item.tool.VoyagerCompassItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {

    // mod总体的Item列表注册
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("mkgmod");

    public static final DeferredItem<Item> TEST_FOOD = ITEMS.register("test_food",
            () -> new Item(new Item.Properties().food(ModFoods.TEST_FOOD)));

    /*
     *   传送世界相关item
     * */
    public static final DeferredItem<VoyagerCompassItem> VOYAGER_COMPASS = ITEMS.register("voyager_compass",
            () -> new VoyagerCompassItem(new Item.Properties().stacksTo(1)));



    /*
    *   货币相关item
    * */
    // 1面值信用点
    public static final DeferredItem<GalaxyCredit_1> GALAXY_CREDIT_1 = ITEMS.register("galaxy_credit_1",
            () -> new GalaxyCredit_1(new Item.Properties()));
    // 10面值信用点
    public static final DeferredItem<GalaxyCredit_10> GALAXY_CREDIT_10 = ITEMS.register("galaxy_credit_10",
            () -> new GalaxyCredit_10(new Item.Properties()));
    // 100面值信用点
    public static final DeferredItem<GalaxyCredit_100> GALAXY_CREDIT_100 = ITEMS.register("galaxy_credit_100",
            () -> new GalaxyCredit_100(new Item.Properties()));
    // 1000面值信用点
    public static final DeferredItem<GalaxyCredit_1000> GALAXY_CREDIT_1000 = ITEMS.register("galaxy_credit_1000",
            () -> new GalaxyCredit_1000(new Item.Properties()));
    // 10000面值信用点 (假设您也创建了 GalaxyCredit_10000 类)
    public static final DeferredItem<GalaxyCredit_10000> GALAXY_CREDIT_10000 = ITEMS.register("galaxy_credit_10000",
            () -> new GalaxyCredit_10000(new Item.Properties()));

    //  信用点钱包
    public static final DeferredItem<Item> CREDIT_COLLECTOR = ITEMS.register("credit_collector",
            () -> new CreditCollector(new Item.Properties()));


}
