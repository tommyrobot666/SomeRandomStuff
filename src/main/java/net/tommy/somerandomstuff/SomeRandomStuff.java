package net.tommy.somerandomstuff;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.ExperienceDroppingBlock;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.registry.*;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.tommy.somerandomstuff.block.complexmachine.ComplexMachine;
import net.tommy.somerandomstuff.block.complexmachine.ComplexMachineEntity;
import net.tommy.somerandomstuff.block.complexmachine.MachinePart;
import net.tommy.somerandomstuff.block.complexmachine.machineparts.HopperMachinePart;
import net.tommy.somerandomstuff.block.complexmachine.machineparts.RedstoneWireMachinePart;
import net.tommy.somerandomstuff.entitys.PetSlimeEntity;
import net.tommy.somerandomstuff.item.armor.BedrockArmorMaterial;
import net.tommy.somerandomstuff.item.armor.SilverArmorMaterial;
import net.tommy.somerandomstuff.item.tool.SilverToolMaterial;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class SomeRandomStuff implements ModInitializer {
    public static final String MOD_ID = "somerandomstuff";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static RegistryKey<Registry<MachinePart>> COMPLEX_MACHINE_PART_TYPE_KEY = RegistryKey.ofRegistry(new Identifier(MOD_ID,"machine_part_type"));
    //public static Registry<MachinePart> COMPLEX_MACHINE_PART_TYPE = FabricRegistryBuilder.createDefaulted(COMPLEX_MACHINE_PART_TYPE_KEY,new Identifier("","")).buildAndRegister();
    public static Registry<MachinePart> COMPLEX_MACHINE_PART_TYPE = FabricRegistryBuilder.createSimple(COMPLEX_MACHINE_PART_TYPE_KEY).buildAndRegister();

    //RegistriesAccessor.setDefaultEntries(RegistriesAccessor.getDefaultEntries().add(default machine part))

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
    public static final ArmorMaterial BEDROCK_ARMOR_MATERIAL = new BedrockArmorMaterial();

    public static final Item SILVER_HELMET = new ArmorItem(SILVER_ARMOR_MATERIAL, ArmorItem.Type.HELMET, new FabricItemSettings().rarity(Rarity.UNCOMMON));
    public static final Item SILVER_CHESTPLATE = new ArmorItem(SILVER_ARMOR_MATERIAL, ArmorItem.Type.CHESTPLATE, new FabricItemSettings().rarity(Rarity.UNCOMMON));
    public static final Item SILVER_LEGGINGS = new ArmorItem(SILVER_ARMOR_MATERIAL, ArmorItem.Type.LEGGINGS, new FabricItemSettings().rarity(Rarity.UNCOMMON));
    public static final Item SILVER_BOOTS = new ArmorItem(SILVER_ARMOR_MATERIAL, ArmorItem.Type.BOOTS, new FabricItemSettings().rarity(Rarity.UNCOMMON));
    public static final Item BEDROCK_HELMET = new ArmorItem(BEDROCK_ARMOR_MATERIAL, ArmorItem.Type.HELMET, new FabricItemSettings().rarity(Rarity.EPIC).fireproof());
    public static final Item BEDROCK_CHESTPLATE= new ArmorItem(BEDROCK_ARMOR_MATERIAL, ArmorItem.Type.CHESTPLATE, new FabricItemSettings().rarity(Rarity.EPIC).fireproof());
    public static final Item BEDROCK_LEGGINGS = new ArmorItem(BEDROCK_ARMOR_MATERIAL, ArmorItem.Type.LEGGINGS, new FabricItemSettings().rarity(Rarity.EPIC).fireproof());
    public static final Item BEDROCK_BOOTS = new ArmorItem(BEDROCK_ARMOR_MATERIAL, ArmorItem.Type.BOOTS, new FabricItemSettings().rarity(Rarity.EPIC).fireproof());

    public static final ToolMaterial SILVER_TOOL_MATERIAL = new SilverToolMaterial();

    public static final Item SILVER_SWORD = new SwordItem(SILVER_TOOL_MATERIAL, 14, 7.8f, new FabricItemSettings().rarity(Rarity.UNCOMMON));
    public static final Item SILVER_SHOVEL = new ShovelItem(SILVER_TOOL_MATERIAL, 0, -1.8f, new FabricItemSettings().rarity(Rarity.UNCOMMON));
    public static final Item SILVER_PICKAXE = new PickaxeItem(SILVER_TOOL_MATERIAL, 1, -2.8f, new FabricItemSettings().rarity(Rarity.UNCOMMON));
    public static final Item SILVER_AXE = new AxeItem(SILVER_TOOL_MATERIAL, 17, -3.2f, new FabricItemSettings().rarity(Rarity.UNCOMMON));
    public static final Item SILVER_HOE = new HoeItem(SILVER_TOOL_MATERIAL, 0, -1.8f, new FabricItemSettings().rarity(Rarity.UNCOMMON));

    public static final Block COMPLEX_MACHINE = new ComplexMachine(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK).nonOpaque());

    public static final BlockEntityType<ComplexMachineEntity> COMPLEX_MACHINE_ENTITY = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            new Identifier(MOD_ID, "complex_machine_entity"),
            FabricBlockEntityTypeBuilder.create(ComplexMachineEntity::new, COMPLEX_MACHINE).build()
    );

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
                entries.add(BEDROCK_HELMET);
                entries.add(BEDROCK_CHESTPLATE);
                entries.add(BEDROCK_LEGGINGS);
                entries.add(BEDROCK_BOOTS);
                entries.add(PET_SLIME_SPAWN_EGG);
                entries.add(COMPLEX_MACHINE);
                NbtCompound redstoneMachinePartStackNBT = new NbtCompound();
                redstoneMachinePartStackNBT.put(ComplexMachineEntity.MACHINE_PART_KEY,new RedstoneWireMachinePart().toNbt());
                ItemStack redstoneMachinePartStack = new ItemStack(Registries.ITEM.getEntry(Items.REDSTONE),1, Optional.of(redstoneMachinePartStackNBT));
                entries.add(redstoneMachinePartStack);
                NbtCompound hopperMachinePartStackNBT = new NbtCompound();
                hopperMachinePartStackNBT.put(ComplexMachineEntity.MACHINE_PART_KEY,new HopperMachinePart().toNbt());
                ItemStack hopperMachinePartStack = new ItemStack(Registries.ITEM.getEntry(Items.HOPPER),1, Optional.of(hopperMachinePartStackNBT));
                entries.add(hopperMachinePartStack);
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
        Registry.register(Registries.ITEM, new Identifier(MOD_ID, "bedrock_helmet"), BEDROCK_HELMET);
        Registry.register(Registries.ITEM, new Identifier(MOD_ID, "bedrock_chestplate"), BEDROCK_CHESTPLATE);
        Registry.register(Registries.ITEM, new Identifier(MOD_ID, "bedrock_leggings"), BEDROCK_LEGGINGS);
        Registry.register(Registries.ITEM, new Identifier(MOD_ID, "bedrock_boots"), BEDROCK_BOOTS);

        Registry.register(Registries.BLOCK, new Identifier(MOD_ID,"complex_machine"), COMPLEX_MACHINE);

        Registry.register(Registries.ITEM, new Identifier(MOD_ID, "complex_machine"), new BlockItem(COMPLEX_MACHINE, new FabricItemSettings()));

        FabricDefaultAttributeRegistry.register(PET_SLIME, PetSlimeEntity.createPetSlimeAttributes());

        Registry.register(Registries.ITEM, new Identifier(MOD_ID,"pet_slime_spawn_egg"), PET_SLIME_SPAWN_EGG);

        Registry.register(Registries.ITEM_GROUP, new Identifier(MOD_ID,"random_group"), RANDOM_ITEM_GROUP);

        Registry.register(Registries.PARTICLE_TYPE, new Identifier(MOD_ID,"baby_pet_slime"), BABY_PET_SLIME);

        BiomeModifications.addFeature(BiomeSelectors.foundInTheEnd(), GenerationStep.Feature.UNDERGROUND_ORES, SILVER_ORE_PLACED_KEY);

        AttackBlockCallback.EVENT.register((player,world,hand,pos,direction)->{
            if (world.getBlockState(pos).isOf(Blocks.BEDROCK)||world.getBlockState(pos).isOf(Blocks.DEAD_BRAIN_CORAL_BLOCK)) {
                //player.sendMessage(Text.literal("YOU FOUND THE BEDROCK GEN"));
                try {
                    MiningToolItem players_mining_tool = (MiningToolItem) player.getStackInHand(hand).getItem();
                    if (players_mining_tool.isSuitableFor(world.getBlockState(pos))) {
                        BlockPos item_spawn_pos = pos.add(direction.getVector());
                        world.spawnEntity(new ItemEntity(world,item_spawn_pos.getX()-0.5,item_spawn_pos.getY()-0.5,item_spawn_pos.getZ()-0.5,new ItemStack(Items.BEDROCK),direction.getVector().getX()*0.1,direction.getVector().getY()*0.1,direction.getVector().getZ()*0.1));
                        return ActionResult.SUCCESS;
                    }
                }
                catch (ClassCastException e) {
                    //player.sendMessage(Text.literal("YOU NEED A PICKAXE"));
                    return ActionResult.SUCCESS;
                }
            }
            return ActionResult.PASS;
        });

        Registry.register(COMPLEX_MACHINE_PART_TYPE,new Identifier("",""),new MachinePart());
        Registry.register(COMPLEX_MACHINE_PART_TYPE,new Identifier(MOD_ID,"redstone_wire"),new RedstoneWireMachinePart());
        Registry.register(COMPLEX_MACHINE_PART_TYPE,new Identifier(MOD_ID, "hopper"), new HopperMachinePart());
        LOGGER.error(COMPLEX_MACHINE_PART_TYPE.getIds().toString());
        COMPLEX_MACHINE_PART_TYPE.streamEntries().forEach(machinePartReference -> {LOGGER.error(machinePartReference.value().toString());});
    }
}
