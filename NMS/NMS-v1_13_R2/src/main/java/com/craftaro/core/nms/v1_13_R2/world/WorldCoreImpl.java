package com.craftaro.core.nms.v1_13_R2.world;

import com.craftaro.core.nms.ReflectionUtils;
import com.craftaro.core.nms.v1_13_R2.world.spawner.BBaseSpawnerImpl;
import com.craftaro.core.nms.world.BBaseSpawner;
import com.craftaro.core.nms.world.SItemStack;
import com.craftaro.core.nms.world.SSpawner;
import com.craftaro.core.nms.world.SWorld;
import com.craftaro.core.nms.world.WorldCore;
import net.minecraft.server.v1_13_R2.BlockLever;
import net.minecraft.server.v1_13_R2.BlockPosition;
import net.minecraft.server.v1_13_R2.Chunk;
import net.minecraft.server.v1_13_R2.ChunkSection;
import net.minecraft.server.v1_13_R2.Fluid;
import net.minecraft.server.v1_13_R2.IBlockData;
import net.minecraft.server.v1_13_R2.MobSpawnerAbstract;
import net.minecraft.server.v1_13_R2.WorldServer;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.craftbukkit.v1_13_R2.CraftChunk;
import org.bukkit.craftbukkit.v1_13_R2.block.CraftBlock;
import org.bukkit.craftbukkit.v1_13_R2.block.data.CraftBlockData;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class WorldCoreImpl implements WorldCore {
    @Override
    public SSpawner getSpawner(CreatureSpawner spawner) {
        return new SSpawnerImpl(spawner.getLocation());
    }

    @Override
    public SSpawner getSpawner(Location location) {
        return new SSpawnerImpl(location);
    }

    @Override
    public SItemStack getItemStack(ItemStack item) {
        return new SItemStackImpl(item);
    }

    @Override
    public SWorld getWorld(World world) {
        return new SWorldImpl(world);
    }

    @Override
    public BBaseSpawner getBaseSpawner(CreatureSpawner spawner) throws NoSuchFieldException, IllegalAccessException {
        Object cTileEntity = ReflectionUtils.getFieldValue(spawner, "tileEntity");

        return new BBaseSpawnerImpl((MobSpawnerAbstract) ReflectionUtils.getFieldValue(cTileEntity, "a"));
    }

    /**
     * Method is based on {@link WorldServer#n_()}.
     */
    @SuppressWarnings("JavadocReference")
    @Override
    public void randomTickChunk(org.bukkit.Chunk bukkitChunk, int tickAmount) throws NoSuchFieldException, IllegalAccessException {
        Chunk chunk = ((CraftChunk) bukkitChunk).getHandle();

        if (tickAmount <= 0) {
            return;
        }

        int j = chunk.locX * 16;
        int k = chunk.locZ * 16;

        chunk.world.methodProfiler.enter("tickBlocks");
        for (ChunkSection chunksection : chunk.getSections()) {
            if (chunksection != net.minecraft.server.v1_13_R2.Chunk.a && chunksection.b()) {
                for (int k1 = 0; k1 < tickAmount; ++k1) {
                    int worldM = (int) ReflectionUtils.getFieldValue(chunk.world, "m");
                    worldM = worldM * 3 + 1013904223;
                    ReflectionUtils.setFieldValue(chunk.world, "m", worldM);

                    int l1 = worldM >> 2;
                    int i2 = l1 & 15;
                    int j2 = l1 >> 8 & 15;
                    int k2 = l1 >> 16 & 15;

                    IBlockData iblockdata = chunksection.getType(i2, k2, j2);
                    Fluid fluid = chunksection.b(i2, k2, j2);

                    chunk.world.methodProfiler.enter("randomTick");

                    if (iblockdata.t()) {
                        iblockdata.b(chunk.world, new BlockPosition(i2 + j, k2 + chunksection.getYPosition(), j2 + k), chunk.world.random);
                    }

                    if (fluid.h()) {
                        fluid.b(chunk.world, new BlockPosition(i2 + j, k2 + chunksection.getYPosition(), j2 + k), chunk.world.random);
                    }

                    chunk.world.methodProfiler.exit();
                }
            }
        }

        chunk.world.methodProfiler.exit();
    }

    @Override
    public void updateAdjacentComparators(@NotNull Block bukkitBlock) {
        CraftBlock craftBlock = (CraftBlock) bukkitBlock;
        WorldServer serverLevel = craftBlock.getCraftWorld().getHandle();

        serverLevel.updateAdjacentComparators(craftBlock.getPosition(), craftBlock.getNMS().getBlock());
    }

    @Override
    public void toggleLever(@NotNull Block bukkitBlock) {
        CraftBlock craftBlock = (CraftBlock) bukkitBlock;

        IBlockData iBlockData = ((CraftBlockData) craftBlock.getBlockData()).getState();
        BlockPosition blockposition = craftBlock.getPosition();
        WorldServer world = craftBlock.getCraftWorld().getHandle();

        ((BlockLever) craftBlock.getNMS().getBlock()).interact(iBlockData, world, blockposition, null, null, null, 0, 0, 0);
    }
}
