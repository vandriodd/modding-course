package net.kaupenjoe.mccourse.block.custom;

import net.kaupenjoe.mccourse.MCCourseMod;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Interaction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class SoundBlock extends Block {
    public SoundBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
//        if (pLevel.isClientSide()) {
//            if (pHand == InteractionHand.MAIN_HAND) {
//                MCCourseMod.LOGGER.info("Main Hand, Client");
//            } else {
//                MCCourseMod.LOGGER.info("Off Hand, Client");
//            }
//        } else {
//            if (pHand == InteractionHand.MAIN_HAND) {
//                MCCourseMod.LOGGER.info("Main Hand, Server");
//            } else {
//                MCCourseMod.LOGGER.info("Off Hand, Server");
//            }
//        }

        // It's going to be called once
        // And it won't play any sound because if we use 'playSound', we are actually consuming that on the server,
        // players gets ignored (see playSound method desc)
        if (!pLevel.isClientSide() && pHand == InteractionHand.MAIN_HAND) {
            if (pPlayer.isCrouching()) {
//                pLevel.playSound(pPlayer, pPos, SoundEvents.NOTE_BLOCK_BANJO.get(), SoundSource.BLOCKS, 1f, 1f);
                pLevel.playSound(null, pPos, SoundEvents.NOTE_BLOCK_BANJO.get(), SoundSource.BLOCKS, 1f, 1f);
                return InteractionResult.SUCCESS;
            } else {
//                pLevel.playSound(pPlayer, pPos, SoundEvents.NOTE_BLOCK_COW_BELL.get(), SoundSource.BLOCKS, 1f, 1f);
                pLevel.playSound(null, pPos, SoundEvents.NOTE_BLOCK_COW_BELL.get(), SoundSource.BLOCKS, 1f, 1f);
                // CONSUME means --> every other interaction while not crouching will be ignored, cuz the block is consuming the entire action
                return InteractionResult.CONSUME;
            }
        }

        return super.use(pState, pLevel, pPos, pPlayer, pHand, pHit);
    }

    // Plays sound when player steps on block, either server or client, doesn't matter
    @Override
    public void stepOn(Level pLevel, BlockPos pPos, BlockState pState, Entity pEntity) {
        pLevel.playSound(pEntity, pPos, SoundEvents.NOTE_BLOCK_BIT.get(), SoundSource.BLOCKS, 1f, 1f);
        super.stepOn(pLevel, pPos, pState, pEntity);
    }
}
