package net.tommy.somerandomstuff;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.*;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.tommy.somerandomstuff.entitys.PetSlimeEntity;

public class SomeRandomStuff implements ModInitializer {
    public static final String MOD_ID = "somerandomstuff";

    public static final EntityType<PetSlimeEntity> PET_SLIME = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(MOD_ID,"pet_slime"),
            FabricEntityTypeBuilder.create(SpawnGroup.MISC, PetSlimeEntity::new).dimensions(EntityDimensions.fixed(0.75f, 0.75f)).build()
    );

    public static final Item PET_SLIME_SPAWN_EGG = new SpawnEggItem(PET_SLIME, 0x7BFFA3, 0x000000, new FabricItemSettings());

    private static final ItemGroup RANDOM_ITEM_GROUP = FabricItemGroup.builder()
            .icon(() -> new ItemStack(Items.ACACIA_CHEST_BOAT))
            .displayName(Text.translatable("itemGroup.somerandomstuff.random_group"))
            .entries((context, entries) -> {
                entries.add(PET_SLIME_SPAWN_EGG);
            })
            .build();

    public static final DefaultParticleType BABY_PET_SLIME = FabricParticleTypes.simple();

    @Override
    public void onInitialize() {
        FabricDefaultAttributeRegistry.register(PET_SLIME, PetSlimeEntity.createPetSlimeAttributes());

        Registry.register(Registries.ITEM, new Identifier(MOD_ID,"pet_slime_spawn_egg"), PET_SLIME_SPAWN_EGG);

        Registry.register(Registries.ITEM_GROUP, new Identifier(MOD_ID,"random_group"), RANDOM_ITEM_GROUP);

        Registry.register(Registries.PARTICLE_TYPE, new Identifier(MOD_ID,"baby_pet_slime"), BABY_PET_SLIME);
    }

}
