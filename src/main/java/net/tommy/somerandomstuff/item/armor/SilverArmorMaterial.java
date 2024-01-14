package net.tommy.somerandomstuff.item.armor;

import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.tommy.somerandomstuff.SomeRandomStuff;

public class SilverArmorMaterial implements ArmorMaterial {
    private static final int[] BASE_DURABILITY = new int[] {13, 15, 16, 11};
    private static final int[] PROTECTION_VALUES = new int[] {5, 10, 13, 5};

    @Override
    public int getDurability(ArmorItem.Type type) {
        return BASE_DURABILITY[type.ordinal()] * 67;
    }

    @Override
    public int getProtection(ArmorItem.Type type) {
        return PROTECTION_VALUES[type.ordinal()];
    }

    @Override
    public int getEnchantability() {
        return 35;
    }

    @Override
    public SoundEvent getEquipSound() {
        return SoundEvents.ITEM_ARMOR_EQUIP_CHAIN;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return Ingredient.fromTag(TagKey.of(RegistryKeys.ITEM, new Identifier("c","silver_ingots")));
    }

    @Override
    public String getName() {
        // Must be all lowercase
        return SomeRandomStuff.MOD_ID + ":silver";
    }

    @Override
    public float getToughness() {
        return 5.0F;
    }

    @Override
    public float getKnockbackResistance() {
        return 0.3F;
    }
}
