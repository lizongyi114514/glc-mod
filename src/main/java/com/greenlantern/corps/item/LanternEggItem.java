package com.greenlantern.corps.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * 刷怪蛋：生成一个带名字、装备和标签的真实生物。
 * 实体本身是原版生物，但由本模组配置，配合决斗脚本即可获得 AI 行为。
 */
public class LanternEggItem extends Item {
    private final String entityId;
    private final String displayName;
    private final String tag;
    private final ChatFormatting color;

    public LanternEggItem(Properties properties, String entityId, String displayName, String tag, ChatFormatting color) {
        super(properties);
        this.entityId = entityId;
        this.displayName = displayName;
        this.tag = tag;
        this.color = color;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide) {
            EntityType<?> type = ForgeRegistries.ENTITY_TYPES.getValue(new ResourceLocation(entityId));
            if (type != null) {
                Entity entity = type.create(level);
                if (entity != null) {
                    entity.moveTo(player.getX(), player.getY(), player.getZ(), player.getYRot(), 0.0F);
                    entity.setCustomName(Component.literal(displayName).withStyle(color));
                    entity.setCustomNameVisible(true);
                    entity.getTags().add("glc_spawned");
                    entity.getTags().add(tag);
                    if (entity instanceof Mob mob) {
                        mob.setPersistenceRequired();
                        mob.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SWORD));
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
