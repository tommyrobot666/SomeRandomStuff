package net.tommy.somerandomstuff.item.tool;

import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class SilverToolMaterial implements ToolMaterial {

    @Override
    public int getDurability() {
        return 3000;
    }

    @Override
    public float getMiningSpeedMultiplier() {
        return 11f;
    }

    @Override
    public float getAttackDamage() {
        return 5f;
    }

    @Override
    public int getMiningLevel() {
        return 5;
    }

    @Override
    public int getEnchantability() {
        return 35;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return Ingredient.fromTag(TagKey.of(RegistryKeys.ITEM, new Identifier("c","silver_ingots")));
    }
}
