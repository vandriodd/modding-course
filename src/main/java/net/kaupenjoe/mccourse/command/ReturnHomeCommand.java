package net.kaupenjoe.mccourse.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

public class ReturnHomeCommand {
    public ReturnHomeCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("home").then(Commands.literal("return").executes(this::execute)));
    }

    private int execute(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        boolean hasHomepos = player.getPersistentData().getIntArray("mccourse.homepos").length != 0;

        if(hasHomepos) {
            int[] playerPos = player.getPersistentData().getIntArray("mccourse.homepos");

            ResourceLocation dimLocation = new ResourceLocation(
                    player.getPersistentData().getString("mccourse.homedim"));

            ResourceKey<Level> dimKey = ResourceKey.create(Registries.DIMENSION, dimLocation);
            ServerLevel targetLevel = player.getServer().getLevel(dimKey);

            if (targetLevel == null) {
                context.getSource().sendFailure(Component.literal("Home dimension no longer exists!"));
                return -1;
            }

            player.teleportTo(targetLevel, playerPos[0] + 0.5, playerPos[1], playerPos[2] + 0.5,
                    player.getYRot(), player.getXRot());

            context.getSource().sendSuccess(() -> Component.literal("Player returned Home!"), false);
            return 1;
        } else {
            context.getSource().sendFailure(Component.literal("No Home Position has been set!"));
            return -1;
        }
    }
}