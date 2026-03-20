package com.breakinblocks.neovitae.ritual;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import com.breakinblocks.neovitae.api.ritual.AreaDescriptor;
import com.breakinblocks.neovitae.api.soul.SoulTicket;
import com.breakinblocks.neovitae.common.datacomponent.EnumWillType;
import com.breakinblocks.neovitae.common.datacomponent.SoulNetwork;
import com.breakinblocks.neovitae.util.helper.SoulNetworkHelper;

import java.util.Map;
import java.util.UUID;

public interface IMasterRitualStone {

    Level getLevel();

    default Level getWorldObj() {
        return getLevel();
    }

    BlockPos getBlockPos();

    default BlockPos getMasterBlockPos() {
        return getBlockPos();
    }

    UUID getOwner();

    void setOwner(UUID owner);

    Ritual getCurrentRitual();

    boolean isActive();

    Direction getDirection();

    boolean isInverted();

    int getCooldown();

    void setCooldown(int cooldown);

    long getRunningTime();

    boolean activateRitual(Ritual ritual, Player player, int crystalLevel);

    void performRitual();

    void stopRitual(Ritual.BreakType breakType);

    boolean checkStructure(Ritual ritual);

    AreaDescriptor getBlockRange(String key);

    Map<String, AreaDescriptor> getBlockRanges();

    void setBlockRange(String key, AreaDescriptor descriptor);

    void setBlockRanges(Map<String, AreaDescriptor> ranges);

    EnumWillType getActiveWillConfig();

    void setActiveWillConfig(EnumWillType type);

    void provideInformationOfRitualToPlayer(Player player);

    void provideInformationOfRangeToPlayer(Player player, String key);

    void provideInformationOfOffsetToPlayer(Player player, AreaDescriptor.Rectangle descriptor);

    default SoulTicket ticket() {
        return SoulTicket.create(0);
    }

    default SoulTicket ticket(int amount) {
        return SoulTicket.create(amount);
    }

    default SoulNetwork getOwnerNetwork() {
        UUID owner = getOwner();
        return owner != null ? SoulNetworkHelper.getSoulNetwork(owner) : null;
    }

    void notifyOwner(Component message);
}
