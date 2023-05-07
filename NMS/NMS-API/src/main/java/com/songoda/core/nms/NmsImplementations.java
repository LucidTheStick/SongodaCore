package com.songoda.core.nms;

import com.songoda.core.nms.anvil.AnvilCore;
import com.songoda.core.nms.entity.NMSPlayer;
import com.songoda.core.nms.nbt.NBTCore;
import com.songoda.core.nms.world.NmsWorldBorder;
import com.songoda.core.nms.world.WorldCore;
import org.jetbrains.annotations.NotNull;

public interface NmsImplementations {
    @NotNull NMSPlayer getPlayer();

    @NotNull WorldCore getWorld();

    @NotNull NmsWorldBorder getWorldBorder();

    @NotNull AnvilCore getAnvil();

    @NotNull NBTCore getNbt();
}
