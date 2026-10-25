package com.greenlantern.corps.command;

import com.greenlantern.corps.CorpsColor;
import com.greenlantern.corps.block.BatteryBlock;
import com.greenlantern.corps.event.SinestroManager;
import com.greenlantern.corps.item.Construct;
import com.greenlantern.corps.item.RingItem;
import com.greenlantern.corps.registry.ModBlocks;
import com.greenlantern.corps.registry.ModItems;
import com.greenlantern.corps.util.Will;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.concurrent.CompletableFuture;

@Mod.EventBusSubscriber(modid = "glc")
public final class ModCommands {
    private ModCommands() {
    }

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("glc")
                .then(Commands.literal("ring")
                        .requires(source -> source.hasPermission(2))
                        .then(Commands.argument("color", StringArgumentType.word())
                                .suggests(ModCommands::suggestColors)
                                .executes(ModCommands::giveRing)))
                .then(Commands.literal("boss")
                        .requires(source -> source.hasPermission(2))
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayerOrException();
                            SinestroManager.spawn(player);
                            return 1;
                        }))
                .then(Commands.literal("battery")
                        .requires(source -> source.hasPermission(2))
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayerOrException();
                            var pos = player.blockPosition().relative(player.getDirection(), 2);
                            player.serverLevel().setBlockAndUpdate(pos, ModBlocks.BATTERY.get().defaultBlockState());
                            context.getSource().sendSuccess(() -> Component.literal("电池已就位。"), true);
                            return 1;
                        }))
                .then(Commands.literal("status")
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayerOrException();
                            ItemStack ring = player.getMainHandItem();
                            int charge = RingItem.isRing(ring) ? RingItem.getCharge(ring) : -1;
                            int mode = RingItem.isRing(ring) ? RingItem.getMode(ring) : 0;
                            context.getSource().sendSuccess(() -> Component.literal(
                                    "充能 " + charge + " / 意志 " + Will.get(player)
                                            + " / 构念 " + Construct.byId(mode).display()
                                            + " / 电池附近 " + BatteryBlock.hasBatteryNear(player.level(), player.blockPosition(), 8)),
                                    false);
                            return 1;
                        }))
                .then(Commands.literal("help")
                        .executes(context -> {
                            context.getSource().sendSuccess(() -> Component.literal(
                                    "绿灯军团：/glc ring <green|yellow|red|blue|indigo|violet|orange>，/glc boss，/glc battery，/glc status")
                                    .withStyle(ChatFormatting.GREEN), false);
                            return 1;
                        })));
    }

    private static CompletableFuture<Suggestions> suggestColors(CommandContext<CommandSourceStack> context,
                                                                SuggestionsBuilder builder) {
        for (CorpsColor color : CorpsColor.values()) {
            builder.suggest(color.id());
        }
        return builder.buildFuture();
    }

    private static int giveRing(CommandContext<CommandSourceStack> context) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        String id = StringArgumentType.getString(context, "color");
        CorpsColor color = CorpsColor.byId(id);
        ItemStack stack = new ItemStack(ModItems.ring(color));
        if (!player.getInventory().add(stack)) {
            player.drop(stack, false);
        }
        context.getSource().sendSuccess(() -> Component.literal("已发放 " + color.zh() + "戒指。")
                .withStyle(color.formatting()), false);
        return 1;
    }
}
