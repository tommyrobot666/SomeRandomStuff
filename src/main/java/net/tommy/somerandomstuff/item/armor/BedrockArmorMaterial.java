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

public class BedrockArmorMaterial implements ArmorMaterial {
    @Override
    public int getDurability(ArmorItem.Type type) {
        return (int) Math.pow(2,32); //32 int limit
    }

    @Override
    public int getProtection(ArmorItem.Type type) {
        return (int) Math.pow(2,32); //32 int limit
    }

    @Override
    public int getEnchantability() {
        return (int) Math.pow(2,32); //32 int limit
    }

    @Override
    public SoundEvent getEquipSound() {
        return SoundEvents.ITEM_ARMOR_EQUIP_TURTLE;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return Ingredient.fromTag(TagKey.of(RegistryKeys.ITEM, new Identifier("c","bedrocks")));
    }

    @Override
    public String getName() {
        return SomeRandomStuff.MOD_ID + ":bedrock";
    }

    @Override
    public float getToughness() {
        return (float) Math.pow(2,32); //32 int limit
    }

    @Override
    public float getKnockbackResistance() {
        return (float) Math.pow(2,32); //32 int limit
    }
}
