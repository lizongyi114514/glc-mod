package com.greenlantern.corps.item;

import com.greenlantern.corps.entity.LanternScoutEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * 刷怪蛋：生成本模组的灯侠实体（按颜色变体），不是原版生物换皮。
 */
public class LanternEggItem extends Item {
    private static final String ENTITY_ID = "glc:lantern_scout";

    private final String variant;
    private final String displayName;
    private final String tag;
    private final ChatFormatting color;

    public LanternEggItem(Properties properties, String variant, String displayName, String tag, ChatFormatting color) {
        super(properties);
        this.variant = variant;
        this.displayName = displayName;
        this.tag = tag;
        this.color = color;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide) {
            EntityType<?> type = ForgeRegistries.ENTITY_TYPES.getValue(new ResourceLocation(ENTITY_ID));
            if (type != null) {
                Entity entity = type.create(level);
                if (entity != null) {
                    entity.moveTo(player.getX(), player.getY(), player.getZ(), player.getYRot(), 0.0F);
                    entity.setCustomName(Component.literal(displayName).withStyle(color));
                    entity.setCustomNameVisible(true);
                    entity.getTags().add("glc_spawned");
                    entity.getTags().add(tag);
                    if (entity instanceof LanternScoutEntity scout) {
                        scout.setVariant(variant);
                    }
                    level.addFreshEntity(entity);
                }
            }
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
        }
        return InteractionResultHolder.success(stack);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }
}
