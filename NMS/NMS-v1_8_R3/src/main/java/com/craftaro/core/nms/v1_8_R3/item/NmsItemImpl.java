package com.craftaro.core.nms.v1_8_R3.item;

import com.craftaro.core.nms.item.NmsItem;
import net.minecraft.server.v1_8_R3.EnchantmentManager;
import net.minecraft.server.v1_8_R3.ItemStack;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;

import java.util.concurrent.ThreadLocalRandom;

public class NmsItemImpl implements NmsItem {
    @Override
    public org.bukkit.inventory.ItemStack copyAndApplyRandomEnchantment(org.bukkit.inventory.ItemStack item, int level) {
        ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
        EnchantmentManager.a(ThreadLocalRandom.current(), nmsItem, level);
        return CraftItemStack.asBukkitCopy(nmsItem);
    }
}
