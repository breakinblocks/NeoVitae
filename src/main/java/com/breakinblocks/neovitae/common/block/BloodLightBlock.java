package com.breakinblocks.neovitae.common.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import com.breakinblocks.neovitae.client.particle.ColoredParticleOptions;
import com.breakinblocks.neovitae.common.blockentity.BloodLightBlockEntity;
import com.breakinblocks.neovitae.common.particle.NVParticles;
import com.breakinblocks.neovitae.util.helper.ColorHelper;
import com.breakinblocks.neovitae.util.helper.BloodLightHelper;
import org.jetbrains.annotations.Nullable;

public class BloodLightBlock extends BaseEntityBlock implements SimpleWaterloggedBlock {

    public static final MapCodec<BloodLightBlock> CODEC = simpleCodec(p -> new BloodLightBlock());

    public static final IntegerProperty BRIGHTNESS = IntegerProperty.create("brightness", 1, 15);
    public static final BooleanProperty POWERED = BooleanProperty.create("powered");
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    protected static final VoxelShape BODY = Block.box(5, 5, 5, 11, 11, 11);

    public static final int DEFAULT_BRIGHTNESS = 15;

    public BloodLightBlock() {
        super(Properties.of()
                .noCollission()
                .noOcclusion()
                .instabreak()
                .lightLevel(BloodLightBlock::getLightLevel)
                .replaceable()
                .noLootTable());
        registerDefaultState(stateDefinition.any()
                .setValue(BRIGHTNESS, DEFAULT_BRIGHTNESS)
                .setValue(POWERED, false)
                .setValue(WATERLOGGED, false));
    }

    private static int getLightLevel(BlockState state) {
        if (state.getValue(POWERED)) {
            return state.getValue(BRIGHTNESS);
        }
        return 0;
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(BRIGHTNESS, POWERED, WATERLOGGED);
    }

    @Override
    protected FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
                                     LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (state.getValue(WATERLOGGED)) {
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return BODY;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, net.minecraft.world.InteractionHand hand, BlockHitResult hitResult) {
        if (stack.is(Items.REDSTONE)) {
            if (!level.isClientSide) {
                BlockEntity be = level.getBlockEntity(pos);
                if (be instanceof BloodLightBlockEntity ble) {
                    boolean newState = !ble.isRedstoneControlled();
                    ble.setRedstoneControlled(newState);
                    updatePoweredState(level, pos, state, ble);
                    String key = newState ? "message.neovitae.blood_light.redstone_on" : "message.neovitae.blood_light.redstone_off";
                    player.displayClientMessage(Component.translatable(key), true);
                }
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        int current = state.getValue(BRIGHTNESS);
        boolean sneaking = player.isShiftKeyDown();
        int newBrightness = sneaking ? Math.max(1, current - 1) : Math.min(15, current + 1);
        if (current != newBrightness && !level.isClientSide) {
            level.setBlock(pos, state.setValue(BRIGHTNESS, newBrightness), Block.UPDATE_ALL);
            BloodLightHelper.playSound(level, pos);
        }
        player.displayClientMessage(Component.translatable("message.neovitae.blood_light.brightness", newBrightness), true);
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
        if (!level.isClientSide) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof BloodLightBlockEntity ble && ble.isRedstoneControlled()) {
                updatePoweredState(level, pos, state, ble);
            }
        }
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        if (!level.isClientSide && !oldState.is(this)) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof BloodLightBlockEntity ble) {
                updatePoweredState(level, pos, state, ble);
            }
        }
    }

    private void updatePoweredState(Level level, BlockPos pos, BlockState state, BloodLightBlockEntity ble) {
        boolean shouldBeOn;
        if (ble.isRedstoneControlled()) {
            shouldBeOn = level.hasNeighborSignal(pos);
        } else {
            shouldBeOn = true;
        }
        if (state.getValue(POWERED) != shouldBeOn) {
            level.setBlock(pos, state.setValue(POWERED, shouldBeOn), Block.UPDATE_ALL);
        }
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
        return true;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BloodLightBlockEntity(pos, state);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (!state.getValue(POWERED)) return;

        int color = ColorHelper.fromDye(net.minecraft.world.item.DyeColor.RED);
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof BloodLightBlockEntity ble) {
            color = ColorHelper.fromDye(ble.getColor());
        }

        double cx = pos.getX() + 0.5;
        double cy = pos.getY() + 0.5;
        double cz = pos.getZ() + 0.5;

        for (int i = 0; i < 8; i++) {
            double ox = (random.nextDouble() - 0.5) * 0.2;
            double oy = (random.nextDouble() - 0.5) * 0.2;
            double oz = (random.nextDouble() - 0.5) * 0.2;

            double vx = ox * 0.3;
            double vy = oy * 0.3 + 0.005;
            double vz = oz * 0.3;

            level.addParticle(new ColoredParticleOptions(NVParticles.BLOOD_FLAME.get(), color),
                    cx + ox, cy + oy, cz + oz, vx, vy, vz);
        }

        level.addParticle(new ColoredParticleOptions(NVParticles.BLOOD_GLOW.get(), color),
                cx, cy, cz, 0, 0, 0);
    }
}
