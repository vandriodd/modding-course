package net.kaupenjoe.mccourse.event;

import net.kaupenjoe.mccourse.MCCourseMod;
import net.kaupenjoe.mccourse.command.ReturnHomeCommand;
import net.kaupenjoe.mccourse.command.SetHomeCommand;
import net.kaupenjoe.mccourse.enchantment.ModEnchantments;
import net.kaupenjoe.mccourse.item.ModItems;
import net.kaupenjoe.mccourse.item.custom.HammerItem;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.command.ConfigCommand;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Mod.EventBusSubscriber(modid = MCCourseMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ModEvents {

    // Done with the help of https://github.com/CoFH/CoFHCore/blob/1.19.x/src/main/java/cofh/core/event/AreaEffectEvents.java
    private static final Set<BlockPos> HARVESTED_BLOCKS = new HashSet<>();
    @SubscribeEvent
    public static void onHammerUsage(BlockEvent.BreakEvent event) {
        Player player = event.getPlayer();
        ItemStack mainHandItem = player.getMainHandItem();

        if(mainHandItem.getItem() instanceof HammerItem hammer && player instanceof ServerPlayer serverPlayer) {
            BlockPos initalBlockPos = event.getPos();
            if (HARVESTED_BLOCKS.contains(initalBlockPos)) {
                return;
            }

            for (BlockPos pos : HammerItem.getBlocksToBeDestroyed(1, initalBlockPos, serverPlayer)) {
                if(pos == initalBlockPos || !hammer.isCorrectToolForDrops(mainHandItem, event.getLevel().getBlockState(pos))) {
                    continue;
                }

                // Have to add them to a Set otherwise, the same code right here will get called for each block!
                HARVESTED_BLOCKS.add(pos);
                serverPlayer.gameMode.destroyBlock(pos);
                HARVESTED_BLOCKS.remove(pos);
            }
        }
    }

    @SubscribeEvent
    public static void onGreenThumbHoeUsage(BlockEvent.BreakEvent event) {
        Player player = event.getPlayer();
        if (player == null) {
            return;
        }

        ItemStack mainHandItem = player.getMainHandItem();
        if (!(mainHandItem.getItem() instanceof HoeItem)) {
            return;
        }

        if (EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.GREEN_THUMB.get(), mainHandItem) <= 0) {
            return;
        }

        if (!(event.getState().getBlock() instanceof CropBlock cropBlock) || !cropBlock.isMaxAge(event.getState())) {
            return;
        }

        if (!(event.getLevel() instanceof ServerLevel serverLevel)) {
            return;
        }

        BlockPos pos = event.getPos();

        // Prevents Minecraft from breaking the block normally (otherwise the replant below would get overwritten right after)
        event.setCanceled(true);

        // Since we canceled the event, we have to drop the loot ourselves
        List<ItemStack> drops = Block.getDrops(
                event.getState(),
                serverLevel,
                pos,
                serverLevel.getBlockEntity(pos),
                player,
                mainHandItem
        );

        // Charge one seed for the replant, same as manually harvesting and replanting by hand
        ItemStack seed = cropBlock.getCloneItemStack(serverLevel, pos, event.getState());
        for (ItemStack drop : drops) {
            if (ItemStack.isSameItem(drop, seed)) {
                drop.shrink(1);
                break;
            }
        }

        drops.forEach(stack -> {
            if (!stack.isEmpty()) {
                Block.popResource(serverLevel, pos, stack);
            }
        });

        // Replant the same crop at age 0
        serverLevel.setBlockAndUpdate(pos, cropBlock.getStateForAge(0));
    }

    @SubscribeEvent
    public static void onCommandsRegister(RegisterCommandsEvent event) {
        new SetHomeCommand(event.getDispatcher());
        new ReturnHomeCommand(event.getDispatcher());

        ConfigCommand.register(event.getDispatcher());
    }

    @SubscribeEvent
    public static void onPlayerCloned(PlayerEvent.Clone event) {
        event.getEntity().getPersistentData().putIntArray("mccourse.homepos",
                event.getOriginal().getPersistentData().getIntArray("mccourse.homepos"));
    }

    @SubscribeEvent
    public static void livingDamage(LivingDamageEvent event) {
        if (event.getEntity() instanceof Sheep) {
            if (event.getSource().getDirectEntity() instanceof Player player) {
                if (player.getItemInHand(InteractionHand.MAIN_HAND).getItem() == ModItems.ALEXANDRITE_AXE.get()) {
                    MCCourseMod.LOGGER.info("Sheep was hit with Alexandrite Axe by" + player.getName().getString());
                } else if (player.getItemInHand(InteractionHand.MAIN_HAND).getItem() == Items.DIAMOND) {
                    MCCourseMod.LOGGER.info("Sheep was hit with a diamond by" + player.getName().getString());
                } else {
                    MCCourseMod.LOGGER.info("Sheep was hit with something else by" + player.getName().getString());
                }
            }
        }
    }
}
