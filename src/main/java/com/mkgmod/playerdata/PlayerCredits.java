package com.mkgmod.playerdata;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;

public class PlayerCredits implements INBTSerializable<CompoundTag> {
    private int credits;

    public int getCredits() { return credits; }
    public void setCredits(int credits) { this.credits = credits; }
    public void addCredits(int amount) { this.credits += amount; }

    // 注意：在 1.20.6+ 中，方法必须带上 Provider 参数
    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag nbt = new CompoundTag();
        nbt.putInt("mkg_credits", credits);
        return nbt;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
        credits = nbt.getInt("mkg_credits");
    }
}