package net.kaupenjoe.mccourse.item;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;

public class ModFoodProperties {
    // Nutrition -> how much nutrition does this food have
    // Effect -> happens 10% of the time when you're consuming this particular food
    // If that happens, you get movement speed effect, with a duration of 10s (in ticks)
    public static final FoodProperties KOHLRABI = new FoodProperties.Builder().nutrition(3).saturationMod(0.25f)
            .effect(() -> new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 200), 0.1f).build();

    public static final FoodProperties CANNABIS = new FoodProperties.Builder().nutrition(3).saturationMod(0.25f)
            .effect(() -> new MobEffectInstance(MobEffects.CONFUSION, 300), 1.0f)
            .effect(() -> new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 300), 1.0f)
            .effect(() -> new MobEffectInstance(MobEffects.HUNGER, 400), 1.0f)
            .build();
}
