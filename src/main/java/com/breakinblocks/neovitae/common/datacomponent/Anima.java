package com.breakinblocks.neovitae.common.datacomponent;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import com.breakinblocks.neovitae.api.soul.IAnima;
import com.breakinblocks.neovitae.api.soul.AnimaTicket;
import com.breakinblocks.neovitae.api.soul.SyphonResult;
import com.breakinblocks.neovitae.util.helper.PlayerHelper;
import com.breakinblocks.neovitae.common.world.NVSavedData;
import com.breakinblocks.neovitae.common.damagesource.NVDamageSources;
import com.breakinblocks.neovitae.util.BooleanResult;

import java.util.UUID;

import net.minecraft.server.level.ServerLevel;
public class Anima implements IAnima {
    public static final Codec<Anima> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            UUIDUtil.CODEC.fieldOf("id").forGetter(Anima::getPlayerId),
            Codec.INT.fieldOf("current_ev").forGetter(Anima::getCurrentEV)
    ).apply(builder, Anima::new));

    private UUID playerId;
    private int currentEV;
    private NVSavedData parent;

    public static Anima newEmpty(UUID playerId, NVSavedData parent) {
        Anima anima = new Anima(playerId, 0);
        anima.parent = parent;
        return anima;
    }

    public void rebind(NVSavedData parent) {
        this.parent = parent;
    }

    protected Anima(UUID playerId, int ev) {
        this.playerId = playerId;
        this.currentEV = ev;
    }

    @Override
    public UUID getPlayerId() {
        return this.playerId;
    }

    @Override
    public int getCurrentEV() {
        return currentEV;
    }

    private void setCurrentEV(int currentEV) {
        this.currentEV = currentEV;
        markDirty();
    }

    private void markDirty() {
        if (parent != null)
            parent.setDirty();
    }

    public static Anima fromNBT(CompoundTag tag, NVSavedData parent) {
        Anima anima = CODEC.decode(NbtOps.INSTANCE, tag).getOrThrow().getFirst();

        anima.parent = parent;

        return anima;
    }

    public CompoundTag toNBT() {
        return (CompoundTag) CODEC.encodeStart(NbtOps.INSTANCE, this).getOrThrow();
    }

    @Override
    public int add(AnimaTicket ticket, int maximum) {
        int curr = getCurrentEV();
        if (curr >= maximum)
            return 0;

        int newEss = Math.min(maximum, curr + ticket.getAmount());
        setCurrentEV(newEss);

        return newEss - curr;
    }

    @Override
    public int set(AnimaTicket ticket, int maximum) {
        int val = Math.min(maximum, ticket.getAmount());
        setCurrentEV(val);
        return val;
    }

    @Override
    public int syphon(AnimaTicket ticket) {
        int amount = ticket.getAmount();
        if (amount <= 0) return 0;

        int curr = getCurrentEV();
        int toRemove = Math.min(curr, amount);
        setCurrentEV(curr - toRemove);
        return toRemove;
    }

    @Override
    public boolean hurtPlayer(Player user, float syphon) {
        if (user == null || syphon <= 0) return false;
        if (user.isCreative()) return true;
        if (PlayerHelper.isFakePlayer(user)) return false;

        int dmg = Math.ceilDiv((int) syphon, 100);
        user.invulnerableTime = 0;
        Level level = user.level();
        user.hurtServer((ServerLevel) user.level(), level.damageSources().source(NVDamageSources.SACRIFICE, user), dmg);
        return true;
    }

    @Override
    public SyphonResult syphonAndDamage(Player user, AnimaTicket ticket) {
        if (user.level().isClientSide()) {
            return SyphonResult.failure();
        }

        int amount = ticket.getAmount();
        int drainAmount = syphon(ticket);

        if (drainAmount >= amount) {
            return SyphonResult.of(true, drainAmount);
        }

        return SyphonResult.of(hurtPlayer(user, amount - drainAmount), drainAmount);
    }
}
