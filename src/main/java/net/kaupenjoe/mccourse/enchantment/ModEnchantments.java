package net.kaupenjoe.mccourse.enchantment;

import net.kaupenjoe.mccourse.MCCourseMod;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

// For more 'complicated' enchantments we might have to use events
public class ModEnchantments {
    public static final EnchantmentCategory HOE =
            EnchantmentCategory.create("hoe", item -> item instanceof HoeItem);

    public static final DeferredRegister<Enchantment> ENCHANTMENTS =
            DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, MCCourseMod.MOD_ID);

    // No need to add it anywhere, books are added automatically
    public static final RegistryObject<Enchantment> LIGHTNING_STRIKER =
            ENCHANTMENTS.register("lightning_striker",
                    () -> new LightningStrikerEnchantment(Enchantment.Rarity.COMMON,
                            EnchantmentCategory.WEAPON, EquipmentSlot.MAINHAND));

    public static final RegistryObject<Enchantment> GREEN_THUMB =
            ENCHANTMENTS.register("green_thumb",
                    () -> new GreenThumbEnchantment(Enchantment.Rarity.COMMON,
                            HOE, EquipmentSlot.MAINHAND));

    public static void register(IEventBus eventBus) {
        ENCHANTMENTS.register(eventBus);
    }
}
