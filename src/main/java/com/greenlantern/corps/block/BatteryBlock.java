package com.greenlantern.corps.block;

import com.greenlantern.corps.item.RingItem;
import com.greenlantern.corps.event.RitualHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.joml.Vector3f;

import javax.annotation.Nullable;
import java.util.List;

public class BatteryBlock extends Block {
    public BatteryBlock(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                 InteractionHand hand, BlockHitResult hit) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.getItem() instanceof RingItem && player.isShiftKeyDown() && player instanceof ServerPlayer serverPlayer) {
            RitualHandler.start(serverPlayer, stack, pos);
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return InteractionResult.PASS;
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        long time = level.getGameTime();
        double phase = (time % 160) / 160.0 * Math.PI * 2.0;
        for (int i = 0; i < 3; i++) {
            double angle = phase + i * (Math.PI * 2.0 / 3.0);
            level.addParticle(
                    new DustParticleOptions(new Vector3f(0.24F, 1.0F, 0.43F), 0.9F),
                    pos.getX() + 0.5D + Math.cos(angle) * 0.72D,
                    pos.getY() + 1.35D + Math.sin(time * 0.05D) * 0.05D,
                    pos.getZ() + 0.5D + Math.sin(angle) * 0.72D,
                    0.0D, 0.01D, 0.0D);
        }
        if (random.nextInt(6) == 0) {
            level.addParticle(
                    new DustParticleOptions(new Vector3f(0.7F, 1.0F, 0.8F), 0.6F),
                    pos.getX() + 0.5D + (random.nextDouble() - 0.5D) * 0.4D,
                    pos.getY() + 0.55D,
                    pos.getZ() + 0.5D + (random.nextDouble() - 0.5D) * 0.4D,
                    0.0D, 0.03D, 0.0D);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("站在 8 格内可缓慢充能").withStyle(net.minecraft.ChatFormatting.GREEN));
        tooltip.add(Component.literal("手持戒指潜行右键：复诵誓词，回满充能").withStyle(net.minecraft.ChatFormatting.DARK_GRAY));
    }

    public static BlockPos findNear(Level level, BlockPos center, int radius) {
        for (BlockPos pos : BlockPos.betweenClosed(center.offset(-radius, -3, -radius), center.offset(radius, 3, radius))) {
            if (level.getBlockState(pos).getBlock() instanceof BatteryBlock) {
                return pos.immutable();
            }
        }
        return null;
    }

    public static boolean hasBatteryNear(Level level, BlockPos center, int radius) {
        return findNear(level, center, radius) != null;
    }
}
