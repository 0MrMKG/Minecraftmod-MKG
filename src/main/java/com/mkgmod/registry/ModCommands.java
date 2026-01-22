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
                        // 获取玩家对象并检查是否为创造模式
                        return source.getPlayerOrException().isCreative();
                    } catch (Exception e) {
                        // 如果是控制台执行，通常允许或根据需求决定（这里返回 false 表示仅限创造玩家）
                        return false;
                    }
                })
                .then(Commands.literal("add")
                        .then(Commands.argument("amount", IntegerArgumentType.integer(0))
                                .executes(context -> {
                                    var player = context.getSource().getPlayerOrException();
                                    int amount = IntegerArgumentType.getInteger(context, "amount");
                                    PlayerCredits credits = player.getData(ModAttachments.PLAYER_CREDITS);
                                    credits.addCredits(amount);
                                    context.getSource().sendSuccess(() -> Component.literal("已增加 " + amount), true);
                                    return 1;
                                })))
                .then(Commands.literal("query")
                        .executes(context -> {
                            var player = context.getSource().getPlayerOrException();
                            int current = player.getData(ModAttachments.PLAYER_CREDITS).getCredits();
                            context.getSource().sendSuccess(() -> Component.literal("当前余额: " + current), false);
                            return 1;
                        })));
    }
}