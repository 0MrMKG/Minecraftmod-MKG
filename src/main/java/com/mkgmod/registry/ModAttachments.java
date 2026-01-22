package com.mkgmod.registry;

import com.mkgmod.playerdata.PlayerCredits;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class ModAttachments {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, "mkgmod");

    // 只要你的 PlayerCredits 正确实现了新的 INBTSerializable，这里就不需要改动
    public static final Supplier<AttachmentType<PlayerCredits>> PLAYER_CREDITS =
            ATTACHMENT_TYPES.register("credits", () -> AttachmentType.serializable(PlayerCredits::new)
                    .copyOnDeath() // 推荐开启：确保玩家重生后信用点不归零
                    .build());

    public static void register(IEventBus eventBus) {
        ATTACHMENT_TYPES.register(eventBus);
    }
}