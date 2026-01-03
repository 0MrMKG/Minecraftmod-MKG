package com.mkgmod.item;


import com.mkgmod.item.food.ModFoods;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("mkgmod");

    public static final DeferredItem<Item> TEST_FOOD = ITEMS.register("test_food",
            () -> new Item(new Item.Properties().food(ModFoods.TEST_FOOD)));

}
