package net.tommy.somerandomstuff;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.ExperienceDroppingBlock;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.*;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.tommy.somerandomstuff.entitys.PetSlimeEntity;
import net.tommy.somerandomstuff.item.armor.SilverArmorMaterial;
import net.tommy.somerandomstuff.item.tool.SilverToolMaterial;

public class SomeRandomStuff implements ModInitializer {
    public static final String MOD_ID = "somerandomstuff";

    public static final Block SILVER_ORE = new ExperienceDroppingBlock(UniformIntProvider.create(7, 13),FabricBlockSettings.copyOf(Blocks.ANCIENT_DEBRIS).sounds(BlockSoundGroup.DEEPSLATE).mapColor(DyeColor.LIGHT_GRAY));
    public static final Block SILVER_BLOCK = new Block(FabricBlockSettings.copyOf(Blocks.NETHERITE_BLOCK).mapColor(DyeColor.LIGHT_GRAY));
    public static final Block RAW_SILVER_BLOCK = new Block(FabricBlockSettings.copyOf(Blocks.ANCIENT_DEBRIS).mapColor(DyeColor.LIGHT_GRAY));


    public static final Item RAW_SILVER = new Item(new FabricItemSettings().rarity(Rarity.UNCOMMON));
    public static final Item SILVER_INGOT = new Item(new FabricItemSettings().rarity(Rarity.UNCOMMON));
    public static final Item SILVER_NUGGET = new Item(new FabricItemSettings().rarity(Rarity.UNCOMMON));

    //https://misode.github.io/worldgen/feature/
    //https://misode.github.io/worldgen/placed-feature/
    public static final RegistryKey<PlacedFeature> SILVER_ORE_PLACED_KEY = RegistryKey.of(RegistryKeys.PLACED_FEATURE, new Identifier(MOD_ID,"silver_ore"));

    public static final ArmorMaterial SILVER_ARMOR_MATERIAL = new SilverArmorMaterial();

    public static final Item SILVER_HELMET = new ArmorItem(SILVER_ARMOR_MATERIAL, ArmorItem.Type.HELMET, new FabricItemSettings().rarity(Rarity.UNCOMMON));
    public static final Item SILVER_CHESTPLATE = new ArmorItem(SILVER_ARMOR_MATERIAL, ArmorItem.Type.CHESTPLATE, new FabricItemSettings().rarity(Rarity.UNCOMMON));
    public static final Item SILVER_LEGGINGS = new ArmorItem(SILVER_ARMOR_MATERIAL, ArmorItem.Type.LEGGINGS, new FabricItemSettings().rarity(Rarity.UNCOMMON));
    public static final Item SILVER_BOOTS = new ArmorItem(SILVER_ARMOR_MATERIAL, ArmorItem.Type.BOOTS, new FabricItemSettings().rarity(Rarity.UNCOMMON));

    public static final ToolMaterial SILVER_TOOL_MATERIAL = new SilverToolMaterial();

    public static final Item SILVER_SWORD = new SwordItem(SILVER_TOOL_MATERIAL, 14, 7.8f, new FabricItemSettings().rarity(Rarity.UNCOMMON));
    public static final Item SILVER_SHOVEL = new ShovelItem(SILVER_TOOL_MATERIAL, 0, -1.8f, new FabricItemSettings().rarity(Rarity.UNCOMMON));
    public static final Item SILVER_PICKAXE = new PickaxeItem(SILVER_TOOL_MATERIAL, 1, -2.8f, new FabricItemSettings().rarity(Rarity.UNCOMMON));
    public static final Item SILVER_AXE = new AxeItem(SILVER_TOOL_MATERIAL, 17, -3.2f, new FabricItemSettings().rarity(Rarity.UNCOMMON));
    public static final Item SILVER_HOE = new HoeItem(SILVER_TOOL_MATERIAL, 0, -1.8f, new FabricItemSettings().rarity(Rarity.UNCOMMON));

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
                entries.add(SILVER_ORE);
                entries.add(RAW_SILVER);
                entries.add(SILVER_NUGGET);
                entries.add(SILVER_INGOT);
                entries.add(SILVER_BLOCK);
                entries.add(RAW_SILVER_BLOCK);
                entries.add(SILVER_HELMET);
                entries.add(SILVER_CHESTPLATE);
                entries.add(SILVER_LEGGINGS);
                entries.add(SILVER_BOOTS);
                entries.add(SILVER_SWORD);
                entries.add(SILVER_SHOVEL);
                entries.add(SILVER_PICKAXE);
                entries.add(SILVER_AXE);
                entries.add(SILVER_HOE);
                entries.add(PET_SLIME_SPAWN_EGG);
            })
            .build();

    public static final DefaultParticleType BABY_PET_SLIME = FabricParticleTypes.simple();

    @Override
    public void onInitialize() {
        Registry.register(Registries.BLOCK, new Identifier(MOD_ID, "silver_ore"), SILVER_ORE);
        Registry.register(Registries.BLOCK, new Identifier(MOD_ID, "silver_block"), SILVER_BLOCK);
        Registry.register(Registries.BLOCK, new Identifier(MOD_ID, "raw_silver_block"), RAW_SILVER_BLOCK);

        Registry.register(Registries.ITEM, new Identifier(MOD_ID, "silver_ore"), new BlockItem(SILVER_ORE,new FabricItemSettings().rarity(Rarity.UNCOMMON)));
        Registry.register(Registries.ITEM, new Identifier(MOD_ID,"silver_block"), new BlockItem(SILVER_BLOCK,new FabricItemSettings().rarity(Rarity.UNCOMMON)));
        Registry.register(Registries.ITEM, new Identifier(MOD_ID,"raw_silver_block"), new BlockItem(RAW_SILVER_BLOCK,new FabricItemSettings().rarity(Rarity.UNCOMMON)));

        Registry.register(Registries.ITEM, new Identifier(MOD_ID, "raw_silver"), RAW_SILVER);
        Registry.register(Registries.ITEM, new Identifier(MOD_ID, "silver_ingot"), SILVER_INGOT);
        Registry.register(Registries.ITEM, new Identifier(MOD_ID, "silver_nugget"), SILVER_NUGGET);

        Registry.register(Registries.ITEM, new Identifier(MOD_ID, "silver_sword"), SILVER_SWORD);
        Registry.register(Registries.ITEM, new Identifier(MOD_ID, "silver_shovel"), SILVER_SHOVEL);
        Registry.register(Registries.ITEM, new Identifier(MOD_ID, "silver_pickaxe"), SILVER_PICKAXE);
        Registry.register(Registries.ITEM, new Identifier(MOD_ID, "silver_axe"), SILVER_AXE);
        Registry.register(Registries.ITEM, new Identifier(MOD_ID, "silver_hoe"), SILVER_HOE);

        Registry.register(Registries.ITEM, new Identifier(MOD_ID, "silver_helmet"), SILVER_HELMET);
        Registry.register(Registries.ITEM, new Identifier(MOD_ID, "silver_chestplate"), SILVER_CHESTPLATE);
        Registry.register(Registries.ITEM, new Identifier(MOD_ID, "silver_leggings"), SILVER_LEGGINGS);
        Registry.register(Registries.ITEM, new Identifier(MOD_ID, "silver_boots"), SILVER_BOOTS);

        FabricDefaultAttributeRegistry.register(PET_SLIME, PetSlimeEntity.createPetSlimeAttributes());

        Registry.register(Registries.ITEM, new Identifier(MOD_ID,"pet_slime_spawn_egg"), PET_SLIME_SPAWN_EGG);

        Registry.register(Registries.ITEM_GROUP, new Identifier(MOD_ID,"random_group"), RANDOM_ITEM_GROUP);

        Registry.register(Registries.PARTICLE_TYPE, new Identifier(MOD_ID,"baby_pet_slime"), BABY_PET_SLIME);

        BiomeModifications.addFeature(BiomeSelectors.foundInTheEnd(), GenerationStep.Feature.UNDERGROUND_ORES, SILVER_ORE_PLACED_KEY);
    }

}
