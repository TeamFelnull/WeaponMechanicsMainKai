package me.deecaad.compatibility.nbt;

import net.minecraft.server.v1_16_R3.NBTTagCompound;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_16_R3.persistence.CraftPersistentDataContainer;
import org.bukkit.craftbukkit.v1_16_R3.persistence.CraftPersistentDataTypeRegistry;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;

import javax.annotation.Nonnull;

public class NBT_1_16_R3 implements NBTCompatibility {

    @Nonnull
    @Override
    public net.minecraft.server.v1_16_R3.ItemStack getNMSStack(@Nonnull ItemStack bukkitStack) {
        return CraftItemStack.asNMSCopy(bukkitStack);
    }

    @Nonnull
    @Override
    public String getNBTDebug(@Nonnull ItemStack bukkitStack) {
        NBTTagCompound nbt = getNMSStack(bukkitStack).getTag();
        return nbt == null ? "null" : nbt.toString();
    }

    @Override
    public PersistentDataContainer createContainer() {
        return new CraftPersistentDataContainer(new CraftPersistentDataTypeRegistry());
    }
}