package com.breakinblocks.neovitae.common.datacomponent;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import com.breakinblocks.neovitae.api.soul.ISoulNetwork;
import com.breakinblocks.neovitae.api.soul.SoulTicket;
import com.breakinblocks.neovitae.api.soul.SyphonResult;
import com.breakinblocks.neovitae.common.world.BMSavedData;
import com.breakinblocks.neovitae.common.damagesource.BMDamageSources;
import com.breakinblocks.neovitae.util.BooleanResult;

import java.util.UUID;

public class SoulNetwork implements ISoulNetwork {
    public static final Codec<SoulNetwork> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            UUIDUtil.CODEC.fieldOf("id").forGetter(SoulNetwork::getPlayerId),
            Codec.INT.fieldOf("current_essence").forGetter(SoulNetwork::getCurrentEssence)
    ).apply(builder, SoulNetwork::new));

    private UUID playerId;
    private int currentEssence;
    private BMSavedData parent;

    public static SoulNetwork newEmpty(UUID playerId, BMSavedData parent) {
        SoulNetwork soulNetwork = new SoulNetwork(playerId, 0);
        soulNetwork.parent = parent;
        return soulNetwork;
    }

    protected SoulNetwork(UUID playerId, int essence) {
        this.playerId = playerId;
        this.currentEssence = essence;
    }

    @Override
    public UUID getPlayerId() {
        return this.playerId;
    }

    @Override
    public int getCurrentEssence() {
        return currentEssence;
    }

    private void setCurrentEssence(int currentEssence) {
        this.currentEssence = currentEssence;
        markDirty();
    }

    private void markDirty() {
        if (parent != null)
            parent.setDirty();
    }

    public static SoulNetwork fromNBT(CompoundTag tag, BMSavedData parent) {
        SoulNetwork soulNetwork = CODEC.decode(NbtOps.INSTANCE, tag).getOrThrow().getFirst();

        soulNetwork.parent = parent;

        return soulNetwork;
    }

    public CompoundTag toNBT() {
        return (CompoundTag) CODEC.encodeStart(NbtOps.INSTANCE, this).getOrThrow();
    }

    @Override
    public int add(SoulTicket ticket, int maximum) {
        int curr = getCurrentEssence();
        if (curr >= maximum)
            return 0;

        int newEss = Math.min(maximum, curr + ticket.getAmount());
        setCurrentEssence(newEss);

        return newEss - curr;
    }

    /**
     * Backwards compatibility method for deprecated SoulTicket.
     * @deprecated Use the API SoulTicket version instead
     */
    @Deprecated
    public int add(com.breakinblocks.neovitae.util.SoulTicket ticket, int maximum) {
        return add(SoulTicket.create(ticket.getAmount()), maximum);
    }

    @Override
    public int set(SoulTicket ticket, int maximum) {
        int val = Math.min(maximum, ticket.getAmount());
        setCurrentEssence(val);
        return val;
    }

    /**
     * Backwards compatibility method for deprecated SoulTicket.
     * @deprecated Use the API SoulTicket version instead
     */
    @Deprecated
    public int set(com.breakinblocks.neovitae.util.SoulTicket ticket, int maximum) {
        return set(SoulTicket.create(ticket.getAmount()), maximum);
    }

    @Override
    public int syphon(SoulTicket ticket) {
        int amount = ticket.getAmount();
        if (amount <= 0) return 0;

        int curr = getCurrentEssence();
        int toRemove = Math.min(curr, amount);
        setCurrentEssence(curr - toRemove);
        return toRemove;
    }

    /**
     * Backwards compatibility method for deprecated SoulTicket.
     * @deprecated Use the API SoulTicket version instead
     */
    @Deprecated
    public int syphon(com.breakinblocks.neovitae.util.SoulTicket ticket) {
        return syphon(SoulTicket.create(ticket.getAmount()));
    }

    @Override
    public void hurtPlayer(Player user, float syphon) {
        if (user != null) {
            if (syphon > 0) {
                if (!user.isCreative()) {
                    int dmg = Math.ceilDiv((int) syphon, 100);
                    user.invulnerableTime = 0;
                    Level level = user.level();
                    user.hurt(level.damageSources().source(BMDamageSources.SACRIFICE, user), dmg);
                }
            }
        }
    }

    @Override
    public SyphonResult syphonAndDamage(Player user, SoulTicket ticket) {
        if (user.level().isClientSide) {
            return SyphonResult.failure();
        }

        int amount = ticket.getAmount();
        int drainAmount = syphon(ticket);

        // If we couldn't syphon enough, damage the player
        if (drainAmount < amount) {
            hurtPlayer(user, amount - drainAmount);
        }

        return SyphonResult.of(true, amount);
    }

    /**
     * Backwards compatibility method for deprecated SoulTicket.
     * @deprecated Use the API SoulTicket version instead
     */
    @Deprecated
    public BooleanResult<Integer> syphonAndDamage(Player user, com.breakinblocks.neovitae.util.SoulTicket ticket) {
        SyphonResult result = syphonAndDamage(user, SoulTicket.create(ticket.getAmount()));
        return BooleanResult.newResult(result.success(), result.amount());
    }
}
