package com.mkgmod.registry;
import com.mkgmod.playerdata.PlayerCredits;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class ModCommands {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("credits")
                .requires(source -> {
                    try {
                        return source.getPlayerOrException().isCreative();
                    } catch (Exception e) {
                        return false;
                    }
                })
                // --- 查询余额 ---
                .then(Commands.literal("query")
                        .executes(context -> {
                            var player = context.getSource().getPlayerOrException();
                            int current = player.getData(ModAttachments.PLAYER_CREDITS).getCredits();
                            context.getSource().sendSuccess(() -> Component.literal("当前余额: " + current), false);
                            return 1;
                        }))
                // --- 增加信用点 ---
                .then(Commands.literal("add")
                        .then(Commands.argument("amount", IntegerArgumentType.integer(0))
                                .executes(context -> {
                                    var player = context.getSource().getPlayerOrException();
                                    int amount = IntegerArgumentType.getInteger(context, "amount");
                                    PlayerCredits credits = player.getData(ModAttachments.PLAYER_CREDITS);
                                    credits.addCredits(amount);
                                    context.getSource().sendSuccess(() -> Component.literal("已成功增加 " + amount + " 信用点"), true);
                                    return 1;
                                })))
                // --- 减少信用点 ---
                .then(Commands.literal("delete")
                        .then(Commands.argument("amount", IntegerArgumentType.integer(0))
                                .executes(context -> {
                                    var player = context.getSource().getPlayerOrException();
                                    int amount = IntegerArgumentType.getInteger(context, "amount");
                                    PlayerCredits credits = player.getData(ModAttachments.PLAYER_CREDITS);

                                    // 逻辑处理：确保不会扣成负数（可选）
                                    int current = credits.getCredits();
                                    int toRemove = Math.min(current, amount);
                                    credits.addCredits(-toRemove);

                                    context.getSource().sendSuccess(() -> Component.literal("已成功扣除 " + toRemove + " 信用点"), true);
                                    return 1;
                                })))
                // --- 设置信用点 ---
                .then(Commands.literal("set")
                        .then(Commands.argument("amount", IntegerArgumentType.integer(0))
                                .executes(context -> {
                                    var player = context.getSource().getPlayerOrException();
                                    int amount = IntegerArgumentType.getInteger(context, "amount");
                                    PlayerCredits credits = player.getData(ModAttachments.PLAYER_CREDITS);

                                    // 假设 PlayerCredits 类中有 setCredits 方法，如果没有，可以用下面的逻辑：
                                    // int current = credits.getCredits();
                                    // credits.addCredits(amount - current);
                                    credits.setCredits(amount);

                                    context.getSource().sendSuccess(() -> Component.literal("信用点已设置为 " + amount), true);
                                    return 1;
                                }))));
    }
}