package com.craftaro.core.nms.v1_16_R2.item;

import com.craftaro.core.nms.item.NmsItem;
import net.minecraft.server.v1_16_R2.EnchantmentManager;
import net.minecraft.server.v1_16_R2.ItemStack;
import org.bukkit.craftbukkit.v1_16_R2.inventory.CraftItemStack;

import java.util.concurrent.ThreadLocalRandom;

public class NmsItemImpl implements NmsItem {
    @Override
    public org.bukkit.inventory.ItemStack copyAndApplyRandomEnchantment(org.bukkit.inventory.ItemStack item, int level) {
        ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
        EnchantmentManager.a(ThreadLocalRandom.current(), nmsItem, level, false);
        return CraftItemStack.asBukkitCopy(nmsItem);
    }
}
