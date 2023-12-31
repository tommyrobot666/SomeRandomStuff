package net.tommy.somerandomstuff;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.tommy.somerandomstuff.entitys.PetSlimeEntity;

public class SomeRandomStuff implements ModInitializer {
    public static final String MOD_ID = "somerandomstuff";

    public static final EntityType<PetSlimeEntity> PET_SLIME = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(MOD_ID,"pet_slime"),
            FabricEntityTypeBuilder.create(SpawnGroup.MISC, PetSlimeEntity::new).dimensions(EntityDimensions.fixed(0.75f, 0.75f)).build()
    );
    @Override
    public void onInitialize() {
        FabricDefaultAttributeRegistry.register(PET_SLIME, PetSlimeEntity.createPetSlimeAttributes());
    }

}
