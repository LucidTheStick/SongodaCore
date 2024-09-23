package com.craftaro.core.nms.v1_19_R3.world;

import com.craftaro.core.nms.ReflectionUtils;
import com.craftaro.core.nms.v1_19_R3.world.spawner.BBaseSpawnerImpl;
import com.craftaro.core.nms.world.BBaseSpawner;
import com.craftaro.core.nms.world.SItemStack;
import com.craftaro.core.nms.world.SSpawner;
import com.craftaro.core.nms.world.SWorld;
import com.craftaro.core.nms.world.WorldCore;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.material.FluidState;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.craftbukkit.v1_19_R3.CraftChunk;
import org.bukkit.craftbukkit.v1_19_R3.block.CraftBlock;
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
    public BBaseSpawner getBaseSpawner(CreatureSpawner spawner) throws NoSuchFieldException, NoSuchMethodException, IllegalAccessException {
        Object cTileEntity = ReflectionUtils.getFieldValue(spawner, "tileEntity");

        return new BBaseSpawnerImpl(spawner, (BaseSpawner) ReflectionUtils.getFieldValue(cTileEntity, "a"));
    }

    /**
     * Method is based on {@link ServerLevel#tickChunk(LevelChunk, int)}.
     */
    @Override
    public void randomTickChunk(org.bukkit.Chunk bukkitChunk, int tickAmount) {
        LevelChunk chunk = (LevelChunk) ((CraftChunk) bukkitChunk).getHandle(ChunkStatus.FULL);
        ServerLevel world = chunk.q;
        ProfilerFiller gameprofilerfiller = world.getProfiler();

        ChunkPos chunkCoordIntPair = chunk.getPos();
        int j = chunkCoordIntPair.getMinBlockX();
        int k = chunkCoordIntPair.getMinBlockZ();

        gameprofilerfiller.push("tickBlocks");
        if (tickAmount > 0) {
            LevelChunkSection[] aChunkSection = chunk.getSections();
            for (LevelChunkSection chunkSection : aChunkSection) {
                if (chunkSection.isRandomlyTicking()) {
                    int l1 = chunkSection.bottomBlockY();

                    for (int l = 0; l < tickAmount; ++l) {
                        BlockPos blockposition2 = world.getBlockRandomPos(j, l1, k, 15);
                        gameprofilerfiller.push("randomTick");
                        BlockState iBlockData3 = chunkSection.getBlockState(blockposition2.getX() - j, blockposition2.getY() - l1, blockposition2.getZ() - k);
                        if (iBlockData3.isRandomlyTicking()) {
                            iBlockData3.randomTick(world, blockposition2, world.random);
                        }

                        FluidState fluid = iBlockData3.getFluidState();
                        if (fluid.isRandomlyTicking()) {
                            fluid.randomTick(world, blockposition2, world.random);
                        }

                        gameprofilerfiller.pop();
                    }
                }
            }
        }
        gameprofilerfiller.pop();
    }

    @Override
    public void updateAdjacentComparators(@NotNull Block bukkitBlock) {
        CraftBlock craftBlock = (CraftBlock) bukkitBlock;
        ServerLevel serverLevel = craftBlock.getCraftWorld().getHandle();

        serverLevel.updateNeighbourForOutputSignal(craftBlock.getPosition(), craftBlock.getNMS().getBlock());
    }
}
