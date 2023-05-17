package com.songoda.core.nms.v1_19_R3.world;

import com.songoda.core.compatibility.ServerVersion;
import com.songoda.core.nms.world.SWorld;
import io.papermc.paper.threadedregions.scheduler.EntityScheduler;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.entity.LevelEntityGetter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_19_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_19_R3.block.data.CraftBlockData;
import org.bukkit.entity.LivingEntity;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SWorldImpl implements SWorld {
    private final World world;

    public SWorldImpl(World world) {
        this.world = world;
    }

    @Override
    public List<LivingEntity> getLivingEntities() {
        List<LivingEntity> result = new ArrayList<>();

        ServerLevel worldServer = ((CraftWorld) this.world).getHandle();
        LevelEntityGetter<Entity> entities = worldServer.getEntities();

        entities.getAll().forEach((mcEnt) -> {
            org.bukkit.entity.Entity bukkitEntity = mcEnt.getBukkitEntity();

            if (bukkitEntity instanceof LivingEntity && bukkitEntity.isValid()) {
                result.add((LivingEntity) bukkitEntity);
            }
        });

        return result;
    }

    @Override
    public void setBlockFast(int x, int y, int z, Material material) {
        ServerLevel serverLevel = ((CraftWorld) this.world).getHandle();
        LevelChunk levelChunk = serverLevel.getChunk(x >> 4, z >> 4);
        BlockState blockState = ((CraftBlockData) material.createBlockData()).getState();

        levelChunk.setBlockState(new BlockPos(x & 0xF, y, z & 0xF), blockState, true);
    }

    @Override
    public Map<EntityScheduler, List<LivingEntity>> getRegionizedEntities() {
        if (!ServerVersion.isFolia()) {
            SWorld.super.getRegionizedEntities();
        }
        Map<EntityScheduler, List<LivingEntity>> result = new HashMap<>();

        for (LivingEntity entity : getLivingEntities()) {
            EntityScheduler scheduler = entity.getScheduler();

            if (!result.containsKey(scheduler)) {
                result.computeIfAbsent(scheduler, k -> new ArrayList<>(List.of(entity)));
            }

            result.get(scheduler).add(entity);
        }

        return result;
    }
}
