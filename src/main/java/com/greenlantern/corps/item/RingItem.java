package com.greenlantern.corps.item;

import com.greenlantern.corps.CorpsColor;
import com.greenlantern.corps.block.BatteryBlock;
import com.greenlantern.corps.event.RitualHandler;
import com.greenlantern.corps.network.Network;
import com.greenlantern.corps.util.GLCUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class RingItem extends Item implements GeoItem {
    public static final String TAG_CHARGE = "Charge";
    public static final String TAG_MODE = "Mode";
    public static final int MAX_CHARGE = 100;

    private final CorpsColor corps;
    private final AnimatableInstanceCache animationCache = GeckoLibUtil.createInstanceCache(this);

    public RingItem(CorpsColor corps, Properties properties) {
        super(properties);
        this.corps = corps;
    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private com.greenlantern.corps.client.RingRenderer renderer;

            @Override
            public net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer getGeoItemRenderer() {
                if (this.renderer == null) {
                    this.renderer = new com.greenlantern.corps.client.RingRenderer();
                }
                return this.renderer;
            }
        });
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "ring", 0,
                state -> state.setAndContinue(RawAnimation.begin().thenLoop("regular"))));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return animationCache;
    }

    public CorpsColor corps() {
        return corps;
    }

    public static int getCharge(ItemStack stack) {
        return stack.getOrCreateTag().getInt(TAG_CHARGE);
    }

    public static void setCharge(ItemStack stack, int value) {
        stack.getOrCreateTag().putInt(TAG_CHARGE, Mth.clamp(value, 0, MAX_CHARGE));
    }

    public static int getMode(ItemStack stack) {
        return stack.getOrCreateTag().getInt(TAG_MODE);
    }

    public static void setMode(ItemStack stack, int value) {
        stack.getOrCreateTag().putInt(TAG_MODE, Mth.clamp(value, 0, 10));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide) {
            return InteractionResultHolder.sidedSuccess(stack, true);
        }
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return InteractionResultHolder.pass(stack);
        }

        if (player.isShiftKeyDown()) {
            BlockPos battery = BatteryBlock.findNear(level, player.blockPosition(), 6);
            if (battery != null) {
                RitualHandler.start(serverPlayer, stack, battery);
            } else {
                Network.openMenu(serverPlayer);
            }
        } else {
            Constructs.cast(serverPlayer, stack, corps);
        }
        player.swing(hand, true);
        return InteractionResultHolder.success(stack);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
        if (!level.isClientSide && !stack.hasTag()) {
            setCharge(stack, MAX_CHARGE);
            setMode(stack, 0);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        int charge = getCharge(stack);
        Construct mode = Construct.byId(getMode(stack));
        tooltip.add(Component.literal(corps.zh() + "戒指").withStyle(corps.formatting()));
        tooltip.add(Component.literal("充能 " + charge + "%").withStyle(charge > 0 ? ChatFormatting.GREEN : ChatFormatting.RED));
        tooltip.add(Component.literal("构念：" + mode.display()).withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.literal("右键施放 · 潜行右键打开构念盘").withStyle(ChatFormatting.DARK_GRAY));
        tooltip.add(Component.literal("电池旁潜行右键：复诵誓词").withStyle(ChatFormatting.DARK_GRAY));
    }

    public static boolean isRing(ItemStack stack) {
        return stack.getItem() instanceof RingItem;
    }

    public static CorpsColor corpsOf(ItemStack stack) {
        return stack.getItem() instanceof RingItem ring ? ring.corps() : CorpsColor.GREEN;
    }

    public static void chargeNearby(Player player, ItemStack stack, int amount) {
        setCharge(stack, getCharge(stack) + amount);
        GLCUtil.msg(player, "充能 " + getCharge(stack) + "%", ChatFormatting.GREEN, true);
    }

    public static boolean isBattery(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        return state.getBlock() instanceof BatteryBlock;
    }
}
