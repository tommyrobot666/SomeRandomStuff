package net.tommy.somerandomstuff.datagenerators;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.*;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.data.client.*;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.RecipeProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.ApplyBonusLootFunction;
import net.minecraft.loot.function.ExplosionDecayLootFunction;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.tommy.somerandomstuff.SomeRandomStuff;
import net.tommy.somerandomstuff.block.complexmachine.ComplexMachine;

import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class DataGenerator implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();

        pack.addProvider(BlockLootTables::new);
        pack.addProvider(ModelGenerator::new);
        pack.addProvider(ItemTagGenerator::new);
        pack.addProvider(BlockTagGenerator::new);
        pack.addProvider(RecipeGenerator::new);
        pack.addProvider(EnglishLangProvider::new);
    }

    private static class BlockLootTables extends FabricBlockLootTableProvider {
        public BlockLootTables(FabricDataOutput dataOutput) {
            super(dataOutput);
        }

        @Override
        public void generate() {
            addDrop(SomeRandomStuff.SILVER_ORE, LootTable.builder()
             .pool(LootPool.builder()
                    .rolls(ConstantLootNumberProvider.create(1.0F))
                    .with(ItemEntry.builder(SomeRandomStuff.SILVER_ORE.asItem())
                            .conditionally(WITH_SILK_TOUCH)
                            .alternatively(ItemEntry.builder(SomeRandomStuff.RAW_SILVER)
                                    .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 3.0F)))
                                    .apply(ApplyBonusLootFunction.uniformBonusCount(Enchantments.FORTUNE))
                                    .apply(ExplosionDecayLootFunction.builder())))));

            addDrop(SomeRandomStuff.SILVER_BLOCK, drops(SomeRandomStuff.SILVER_BLOCK));
            addDrop(SomeRandomStuff.RAW_SILVER_BLOCK, drops(SomeRandomStuff.RAW_SILVER_BLOCK));
            addDrop(SomeRandomStuff.COMPLEX_MACHINE, drops(SomeRandomStuff.COMPLEX_MACHINE));
        }
    }

    private static class ModelGenerator extends FabricModelProvider {
        private ModelGenerator(FabricDataOutput generator) {
            super(generator);
        }

        public final void registerBlockItemModel(Block block, ItemModelGenerator itemModelGenerator) {
            new Model(Optional.of(new Identifier(SomeRandomStuff.MOD_ID, "block/" + Registries.BLOCK.getId(block).getPath())), Optional.empty()).upload(ModelIds.getItemModelId(block.asItem()), TextureMap.all(block), itemModelGenerator.writer);
        }

        public MultipartBlockStateSupplier complexMachineStateSupplier(Block block){
            MultipartBlockStateSupplier stateSupplier = MultipartBlockStateSupplier.create(block);
            for (int N = 0; N < 2; N++){
                for (int E = 0; E < 2; E++){
                    for (int S = 0; S < 2; S++){
                        for (int W = 0; W < 2; W++){
                            for (int U = 0; U < 2; U++){
                                for (int D = 0; D < 2; D++){
                                    stateSupplier.with(When.create().
                                                    set(ComplexMachine.TOUCHING_NORTH, N != 0)
                                                    .set(ComplexMachine.TOUCHING_EAST, E != 0)
                                                    .set(ComplexMachine.TOUCHING_SOUTH, S != 0)
                                                    .set(ComplexMachine.TOUCHING_WEST, W != 0)
                                                    .set(ComplexMachine.TOUCHING_UP, U != 0)
                                                    .set(ComplexMachine.TOUCHING_DOWN, D != 0),
                                            BlockStateVariant.create().put(VariantSettings.MODEL, Registries.BLOCK.getId(block).withSuffixedPath("_"+N+""+E+""+S+""+W+""+U+""+D).withPrefixedPath("block/"+Registries.BLOCK.getId(block).getPath()+"/")));
                                }
                            }
                        }
                    }
                }
            }
            return stateSupplier;
        }

        public void complexMachineModelCreator(Block block, BlockStateModelGenerator blockStateModelGenerator){
            for (int N = 0; N < 2; N++){
                for (int E = 0; E < 2; E++){
                    for (int S = 0; S < 2; S++){
                        for (int W = 0; W < 2; W++){
                            for (int U = 0; U < 2; U++){
                                for (int D = 0; D < 2; D++){
                                    Identifier modelName = new Identifier(SomeRandomStuff.MOD_ID, "block/"+Registries.BLOCK.getId(block).getPath()+"/"+ Registries.BLOCK.getId(block).getPath()+"_"+N+""+E+""+S+""+W+""+U+""+D);
                                    TextureMap textures = determineTexturesOfComplexMachineModel(N, E, S, W, U, D,block);
                                    Model model = new Model(Optional.of(new Identifier("minecraft","block/cube_all")),Optional.empty(),TextureKey.NORTH,TextureKey.EAST,TextureKey.SOUTH,TextureKey.WEST,TextureKey.UP,TextureKey.DOWN,TextureKey.PARTICLE);
                                    model.upload(modelName,textures, blockStateModelGenerator.modelCollector);
                                }
                            }
                        }
                    }
                }
            }
        }

        private static TextureMap determineTexturesOfComplexMachineModel(int N, int E, int S, int W, int U, int D,Block block) {
            return new TextureMap()
                    .put(TextureKey.NORTH,determineTextureOfComplexMachineFaceOrAir(N,U,D,E,W,block))
                    .put(TextureKey.EAST,determineTextureOfComplexMachineFaceOrAir(E,U,D,S,N,block))
                    .put(TextureKey.SOUTH,determineTextureOfComplexMachineFaceOrAir(S,U,D,W,E,block))
                    .put(TextureKey.WEST,determineTextureOfComplexMachineFaceOrAir(W,U,D,N,S,block))
                    .put(TextureKey.UP,determineTextureOfComplexMachineFaceOrAir(U,N,S,W,E,block))
                    .put(TextureKey.DOWN,determineTextureOfComplexMachineFaceOrAir(D,N,S,W,E,block))
                    .put(TextureKey.PARTICLE,new Identifier(SomeRandomStuff.MOD_ID,"block/complex_machine/complex_machine_0000"));
        }

        private static Identifier determineTextureOfComplexMachineFaceOrAir(int side,int U, int D, int L, int R,Block block) {
            return side != 0?new Identifier(SomeRandomStuff.MOD_ID, "block/complex_machine/complex_machine_1111"):determineTextureOfComplexMachineFace(U,D,L,R,block);
        }

        private static Identifier determineTextureOfComplexMachineFace(int U, int D, int L, int R,Block block) {
            return new Identifier(SomeRandomStuff.MOD_ID,"block/"+Registries.BLOCK.getId(block).getPath()+"/"+Registries.BLOCK.getId(block).getPath()+"_"+U+""+D+""+L+""+R);
        }

        @Override
        public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
            blockStateModelGenerator.registerSimpleCubeAll(SomeRandomStuff.SILVER_ORE);
            blockStateModelGenerator.registerSimpleCubeAll(SomeRandomStuff.SILVER_BLOCK);
            blockStateModelGenerator.registerSimpleCubeAll(SomeRandomStuff.RAW_SILVER_BLOCK);
            blockStateModelGenerator.blockStateCollector.accept(complexMachineStateSupplier(SomeRandomStuff.COMPLEX_MACHINE));
            complexMachineModelCreator(SomeRandomStuff.COMPLEX_MACHINE, blockStateModelGenerator);
        }

        @Override
        public void generateItemModels(ItemModelGenerator itemModelGenerator) {
            registerBlockItemModel(SomeRandomStuff.SILVER_ORE, itemModelGenerator);
            registerBlockItemModel(SomeRandomStuff.SILVER_BLOCK, itemModelGenerator);
            registerBlockItemModel(SomeRandomStuff.RAW_SILVER_BLOCK, itemModelGenerator);

            itemModelGenerator.register(SomeRandomStuff.RAW_SILVER, Models.GENERATED);
            itemModelGenerator.register(SomeRandomStuff.SILVER_INGOT, Models.GENERATED);
            itemModelGenerator.register(SomeRandomStuff.SILVER_NUGGET, Models.GENERATED);

            itemModelGenerator.register(SomeRandomStuff.SILVER_SWORD, Models.HANDHELD);
            itemModelGenerator.register(SomeRandomStuff.SILVER_SHOVEL, Models.HANDHELD);
            itemModelGenerator.register(SomeRandomStuff.SILVER_PICKAXE, Models.GENERATED);
            itemModelGenerator.register(SomeRandomStuff.SILVER_AXE, Models.GENERATED);
            itemModelGenerator.register(SomeRandomStuff.SILVER_HOE, Models.GENERATED);

            itemModelGenerator.registerArmor(((ArmorItem) SomeRandomStuff.SILVER_HELMET));
            itemModelGenerator.registerArmor(((ArmorItem) SomeRandomStuff.SILVER_CHESTPLATE));
            itemModelGenerator.registerArmor(((ArmorItem) SomeRandomStuff.SILVER_LEGGINGS));
            itemModelGenerator.registerArmor(((ArmorItem) SomeRandomStuff.SILVER_BOOTS));
            itemModelGenerator.registerArmor(((ArmorItem) SomeRandomStuff.BEDROCK_HELMET));
            itemModelGenerator.registerArmor(((ArmorItem) SomeRandomStuff.BEDROCK_CHESTPLATE));
            itemModelGenerator.registerArmor(((ArmorItem) SomeRandomStuff.BEDROCK_LEGGINGS));
            itemModelGenerator.registerArmor(((ArmorItem) SomeRandomStuff.BEDROCK_BOOTS));
        }
    }

    private static class ItemTagGenerator extends FabricTagProvider.ItemTagProvider {

        public void commonTagFromName(ItemConvertible item, String end){
            getOrCreateTagBuilder(TagKey.of(RegistryKeys.ITEM, new Identifier("c",Registries.ITEM.getId(item.asItem()).getPath()+end)))
                    .add(item.asItem());
        }

        private static final TagKey<Item> ORES = TagKey.of(RegistryKeys.ITEM, new Identifier("c", "ores"));
        private static final TagKey<Item> RAW_ORES = TagKey.of(RegistryKeys.ITEM, new Identifier("c", "raw_ores"));
        private static final TagKey<Item> INGOTS = TagKey.of(RegistryKeys.ITEM, new Identifier("c", "ingots"));
        private static final TagKey<Item> NUGGETS = TagKey.of(RegistryKeys.ITEM, new Identifier("c", "ingots"));
        private static final TagKey<Item> WOOD_STICKS = TagKey.of(RegistryKeys.ITEM, new Identifier("c", "wood_sticks"));

        private static final TagKey<Item> TRIMMABLE_ARMOR = TagKey.of(RegistryKeys.ITEM, new Identifier("minecraft","trimmable_armor"));

        private static final TagKey<Item> SWORDS = TagKey.of(RegistryKeys.ITEM, new Identifier("fabric","swords"));
        private static final TagKey<Item> SHOVELS = TagKey.of(RegistryKeys.ITEM, new Identifier("fabric","shovels"));
        private static final TagKey<Item> PICKAXES = TagKey.of(RegistryKeys.ITEM, new Identifier("fabric","pickaxes"));
        private static final TagKey<Item> AXES = TagKey.of(RegistryKeys.ITEM, new Identifier("fabric","axes"));
        private static final TagKey<Item> HOES = TagKey.of(RegistryKeys.ITEM, new Identifier("fabric","hoes"));

        public ItemTagGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture) {
            super(output, completableFuture);
        }

        @Override
        protected void configure(RegistryWrapper.WrapperLookup arg) {
            getOrCreateTagBuilder(WOOD_STICKS)
                    .add(Items.STICK);
            getOrCreateTagBuilder(ORES)
                    .add(SomeRandomStuff.SILVER_ORE.asItem());
            getOrCreateTagBuilder(RAW_ORES)
                    .add(SomeRandomStuff.RAW_SILVER);
            getOrCreateTagBuilder(INGOTS)
                    .add(SomeRandomStuff.SILVER_INGOT);
            getOrCreateTagBuilder(NUGGETS)
                    .add(SomeRandomStuff.SILVER_NUGGET);
            commonTagFromName(SomeRandomStuff.RAW_SILVER,"s");
            commonTagFromName(SomeRandomStuff.SILVER_ORE.asItem(),"s");
            commonTagFromName(SomeRandomStuff.SILVER_INGOT,"s");
            commonTagFromName(SomeRandomStuff.SILVER_NUGGET,"s");
            commonTagFromName(SomeRandomStuff.SILVER_BLOCK,"s");
            commonTagFromName(SomeRandomStuff.RAW_SILVER_BLOCK.asItem(),"s");
            commonTagFromName(SomeRandomStuff.SILVER_HELMET,"s");
            commonTagFromName(SomeRandomStuff.SILVER_CHESTPLATE,"s");
            commonTagFromName(SomeRandomStuff.SILVER_LEGGINGS,"");
            commonTagFromName(SomeRandomStuff.SILVER_BOOTS,"");
            commonTagFromName(SomeRandomStuff.SILVER_SWORD,"s");
            commonTagFromName(SomeRandomStuff.SILVER_SHOVEL,"s");
            commonTagFromName(SomeRandomStuff.SILVER_PICKAXE,"s");
            commonTagFromName(SomeRandomStuff.SILVER_AXE,"s");
            commonTagFromName(SomeRandomStuff.SILVER_HOE,"s");
            commonTagFromName(Items.BEDROCK,"s");
            commonTagFromName(SomeRandomStuff.BEDROCK_HELMET,"s");
            commonTagFromName(SomeRandomStuff.BEDROCK_CHESTPLATE,"s");
            commonTagFromName(SomeRandomStuff.BEDROCK_LEGGINGS,"");
            commonTagFromName(SomeRandomStuff.BEDROCK_BOOTS,"");

            getOrCreateTagBuilder(TRIMMABLE_ARMOR)
                    .add(SomeRandomStuff.SILVER_HELMET)
                    .add(SomeRandomStuff.SILVER_CHESTPLATE)
                    .add(SomeRandomStuff.SILVER_LEGGINGS)
                    .add(SomeRandomStuff.SILVER_BOOTS)
                    .add(SomeRandomStuff.BEDROCK_HELMET)
                    .add(SomeRandomStuff.BEDROCK_CHESTPLATE)
                    .add(SomeRandomStuff.BEDROCK_LEGGINGS)
                    .add(SomeRandomStuff.BEDROCK_BOOTS);

            getOrCreateTagBuilder(SWORDS)
                    .add(SomeRandomStuff.SILVER_SWORD);
            getOrCreateTagBuilder(SHOVELS)
                    .add(SomeRandomStuff.SILVER_SHOVEL);
            getOrCreateTagBuilder(PICKAXES)
                    .add(SomeRandomStuff.SILVER_PICKAXE);
            getOrCreateTagBuilder(AXES)
                    .add(SomeRandomStuff.SILVER_AXE);
            getOrCreateTagBuilder(HOES)
                    .add(SomeRandomStuff.SILVER_HOE);
        }
    }

    private static class BlockTagGenerator extends FabricTagProvider.BlockTagProvider {

        public void commonTagFromName(Block block, String end){
            getOrCreateTagBuilder(TagKey.of(RegistryKeys.BLOCK, new Identifier("c",Registries.BLOCK.getId(block).getPath()+end)))
                    .add(block);
        }

        private static final TagKey<Block> MINEABLE_WITH_PICKAXE = TagKey.of(RegistryKeys.BLOCK, new Identifier("minecraft","mineable/pickaxe"));
        private static final TagKey<Block> NEEDS_DIAMOND_TOOL = TagKey.of(RegistryKeys.BLOCK, new Identifier("minecraft","needs_diamond_tool"));
        private static final TagKey<Block> NEEDS_TOOL_LEVEL_5 = TagKey.of(RegistryKeys.BLOCK, new Identifier("fabric","needs_tool_level_5"));
        private static final TagKey<Block> BEACON_BASE_BLOCKS = TagKey.of(RegistryKeys.BLOCK, new Identifier("minecraft","beacon_base_blocks"));
        private static final TagKey<Block> ORES = TagKey.of(RegistryKeys.BLOCK, new Identifier("c","ores"));



        public BlockTagGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture) {
            super(output, completableFuture);
        }

        @Override
        protected void configure(RegistryWrapper.WrapperLookup arg) {
            getOrCreateTagBuilder(MINEABLE_WITH_PICKAXE)
                    .add(SomeRandomStuff.SILVER_ORE)
                    .add(SomeRandomStuff.SILVER_BLOCK)
                    .add(SomeRandomStuff.RAW_SILVER_BLOCK)
                    .add(Blocks.BEDROCK);

            getOrCreateTagBuilder(NEEDS_DIAMOND_TOOL)
                    .add(SomeRandomStuff.SILVER_ORE)
                    .add(SomeRandomStuff.SILVER_BLOCK)
                    .add(SomeRandomStuff.RAW_SILVER_BLOCK);
            getOrCreateTagBuilder(NEEDS_TOOL_LEVEL_5)
                    .add(Blocks.BEDROCK);

            getOrCreateTagBuilder(BEACON_BASE_BLOCKS)
                    .add(SomeRandomStuff.SILVER_BLOCK);

            getOrCreateTagBuilder(ORES)
                    .add(SomeRandomStuff.SILVER_ORE);
            commonTagFromName(SomeRandomStuff.SILVER_ORE,"s");
            commonTagFromName(SomeRandomStuff.SILVER_BLOCK,"s");
            commonTagFromName(SomeRandomStuff.RAW_SILVER_BLOCK,"s");
        }
    }

    private static class RecipeGenerator extends FabricRecipeProvider {
        private static final TagKey<Item> SILVER_INGOTS = TagKey.of(RegistryKeys.ITEM, new Identifier("c","silver_ingots"));
        private static final TagKey<Item> SILVER_NUGGETS = TagKey.of(RegistryKeys.ITEM, new Identifier("c","silver_nuggets"));
        private static final TagKey<Item> SILVER_BLOCKS = TagKey.of(RegistryKeys.ITEM, new Identifier("c","silver_blocks"));
        private static final TagKey<Item> RAW_SILVER_BLOCKS = TagKey.of(RegistryKeys.ITEM, new Identifier("c","raw_silver_blocks"));
        private static final TagKey<Item> RAW_SILVERS = TagKey.of(RegistryKeys.ITEM, new Identifier("c","raw_silvers"));
        private static final TagKey<Item> WOOD_STICKS = TagKey.of(RegistryKeys.ITEM, new Identifier("c","wood_sticks"));
        private static final TagKey<Item> BEDROCK = TagKey.of(RegistryKeys.ITEM, new Identifier("c","bedrocks"));


        private RecipeGenerator(FabricDataOutput generator) {
            super(generator);
        }

        @Override
        public void generate(RecipeExporter recipeExporter) {
            ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, SomeRandomStuff.SILVER_BLOCK)
                            .pattern("###")
                            .pattern("###")
                            .pattern("###")
                            .input('#',SILVER_INGOTS)
                            .criterion(FabricRecipeProvider.hasItem(SomeRandomStuff.SILVER_INGOT),
                                    FabricRecipeProvider.conditionsFromItem(SomeRandomStuff.SILVER_INGOT))
                            .offerTo(recipeExporter, "block_of_silver_from_silver_ingots");

            ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, SomeRandomStuff.SILVER_INGOT)
                    .pattern("###")
                    .pattern("###")
                    .pattern("###")
                    .input('#',SILVER_NUGGETS)
                    .criterion(FabricRecipeProvider.hasItem(SomeRandomStuff.SILVER_NUGGET),
                            FabricRecipeProvider.conditionsFromItem(SomeRandomStuff.SILVER_NUGGET))
                    .offerTo(recipeExporter, "silver_ingot_from_silver_nuggets");

            ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, SomeRandomStuff.RAW_SILVER_BLOCK)
                    .pattern("###")
                    .pattern("###")
                    .pattern("###")
                    .input('#',RAW_SILVERS)
                    .criterion(FabricRecipeProvider.hasItem(SomeRandomStuff.RAW_SILVER),
                            FabricRecipeProvider.conditionsFromItem(SomeRandomStuff.RAW_SILVER))
                    .offerTo(recipeExporter, "raw_silver_block_from_raw_silvers");

            ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, SomeRandomStuff.SILVER_HELMET)
                    .pattern("###")
                    .pattern("# #")
                    .input('#',SILVER_INGOTS)
                    .criterion(FabricRecipeProvider.hasItem(SomeRandomStuff.SILVER_INGOT),
                            FabricRecipeProvider.conditionsFromItem(SomeRandomStuff.SILVER_INGOT))
                    .offerTo(recipeExporter, "silver_helmet");

            ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, SomeRandomStuff.SILVER_CHESTPLATE)
                    .pattern("# #")
                    .pattern("###")
                    .pattern("###")
                    .input('#',SILVER_INGOTS)
                    .criterion(FabricRecipeProvider.hasItem(SomeRandomStuff.SILVER_INGOT),
                            FabricRecipeProvider.conditionsFromItem(SomeRandomStuff.SILVER_INGOT))
                    .offerTo(recipeExporter, "silver_chestplate");

            ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, SomeRandomStuff.SILVER_LEGGINGS)
                    .pattern("###")
                    .pattern("# #")
                    .pattern("# #")
                    .input('#',SILVER_INGOTS)
                    .criterion(FabricRecipeProvider.hasItem(SomeRandomStuff.SILVER_INGOT),
                            FabricRecipeProvider.conditionsFromItem(SomeRandomStuff.SILVER_INGOT))
                    .offerTo(recipeExporter, "silver_leggings");

            ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, SomeRandomStuff.SILVER_HELMET)
                    .pattern("###")
                    .pattern("# #")
                    .input('#',BEDROCK)
                    .criterion(FabricRecipeProvider.hasItem(Items.BEDROCK),
                            FabricRecipeProvider.conditionsFromItem(Items.BEDROCK))
                    .offerTo(recipeExporter, "bedrock_helmet");

            ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, SomeRandomStuff.SILVER_CHESTPLATE)
                    .pattern("# #")
                    .pattern("###")
                    .pattern("###")
                    .input('#',BEDROCK)
                    .criterion(FabricRecipeProvider.hasItem(Items.BEDROCK),
                            FabricRecipeProvider.conditionsFromItem(Items.BEDROCK))
                    .offerTo(recipeExporter, "bedrock_chestplate");

            ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, SomeRandomStuff.SILVER_LEGGINGS)
                    .pattern("###")
                    .pattern("# #")
                    .pattern("# #")
                    .input('#',BEDROCK)
                    .criterion(FabricRecipeProvider.hasItem(Items.BEDROCK),
                            FabricRecipeProvider.conditionsFromItem(Items.BEDROCK))
                    .offerTo(recipeExporter, "bedrock_leggings");

            ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, SomeRandomStuff.SILVER_BOOTS)
                    .pattern("# #")
                    .pattern("# #")
                    .input('#',BEDROCK)
                    .criterion(FabricRecipeProvider.hasItem(Items.BEDROCK),
                            FabricRecipeProvider.conditionsFromItem(Items.BEDROCK))
                    .offerTo(recipeExporter, "bedrock_boots");

            ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, SomeRandomStuff.SILVER_BOOTS)
                    .pattern("# #")
                    .pattern("# #")
                    .input('#',BEDROCK)
                    .criterion(FabricRecipeProvider.hasItem(SomeRandomStuff.SILVER_INGOT),
                            FabricRecipeProvider.conditionsFromItem(SomeRandomStuff.SILVER_INGOT))
                    .offerTo(recipeExporter, "silver_boots");

            ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, SomeRandomStuff.SILVER_SWORD)
                    .pattern(" # ")
                    .pattern(" # ")
                    .pattern(" S ")
                    .input('#',SILVER_INGOTS)
                    .input('S',WOOD_STICKS)
                    .criterion(FabricRecipeProvider.hasItem(SomeRandomStuff.SILVER_INGOT),
                            FabricRecipeProvider.conditionsFromItem(SomeRandomStuff.SILVER_INGOT))
                    .criterion(FabricRecipeProvider.hasItem(Items.STICK),
                            FabricRecipeProvider.conditionsFromItem(Items.STICK))
                    .offerTo(recipeExporter, "silver_sword");

            ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, SomeRandomStuff.SILVER_SHOVEL)
                    .pattern(" # ")
                    .pattern(" S ")
                    .pattern(" S ")
                    .input('#',SILVER_INGOTS)
                    .input('S',WOOD_STICKS)
                    .criterion(FabricRecipeProvider.hasItem(SomeRandomStuff.SILVER_INGOT),
                            FabricRecipeProvider.conditionsFromItem(SomeRandomStuff.SILVER_INGOT))
                    .criterion(FabricRecipeProvider.hasItem(Items.STICK),
                            FabricRecipeProvider.conditionsFromItem(Items.STICK))
                    .offerTo(recipeExporter, "silver_shovel");

            ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, SomeRandomStuff.SILVER_PICKAXE)
                    .pattern("###")
                    .pattern(" S ")
                    .pattern(" S ")
                    .input('#',SILVER_INGOTS)
                    .input('S',WOOD_STICKS)
                    .criterion(FabricRecipeProvider.hasItem(SomeRandomStuff.SILVER_INGOT),
                            FabricRecipeProvider.conditionsFromItem(SomeRandomStuff.SILVER_INGOT))
                    .criterion(FabricRecipeProvider.hasItem(Items.STICK),
                            FabricRecipeProvider.conditionsFromItem(Items.STICK))
                    .offerTo(recipeExporter, "silver_pickaxe");

            ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, SomeRandomStuff.SILVER_AXE)
                    .pattern("## ")
                    .pattern("#S ")
                    .pattern(" S ")
                    .input('#',SILVER_INGOTS)
                    .input('S',WOOD_STICKS)
                    .criterion(FabricRecipeProvider.hasItem(SomeRandomStuff.SILVER_INGOT),
                            FabricRecipeProvider.conditionsFromItem(SomeRandomStuff.SILVER_INGOT))
                    .criterion(FabricRecipeProvider.hasItem(Items.STICK),
                            FabricRecipeProvider.conditionsFromItem(Items.STICK))
                    .offerTo(recipeExporter, "silver_axe");

            ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, SomeRandomStuff.SILVER_AXE)
                    .pattern(" ##")
                    .pattern(" S#")
                    .pattern(" S ")
                    .input('#',SILVER_INGOTS)
                    .input('S',WOOD_STICKS)
                    .criterion(FabricRecipeProvider.hasItem(SomeRandomStuff.SILVER_INGOT),
                            FabricRecipeProvider.conditionsFromItem(SomeRandomStuff.SILVER_INGOT))
                    .criterion(FabricRecipeProvider.hasItem(Items.STICK),
                            FabricRecipeProvider.conditionsFromItem(Items.STICK))
                    .offerTo(recipeExporter, "silver_axe_mirrored");

            ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, SomeRandomStuff.SILVER_HOE)
                    .pattern(" ##")
                    .pattern(" S ")
                    .pattern(" S ")
                    .input('#',SILVER_INGOTS)
                    .input('S',WOOD_STICKS)
                    .criterion(FabricRecipeProvider.hasItem(SomeRandomStuff.SILVER_INGOT),
                            FabricRecipeProvider.conditionsFromItem(SomeRandomStuff.SILVER_INGOT))
                    .criterion(FabricRecipeProvider.hasItem(Items.STICK),
                            FabricRecipeProvider.conditionsFromItem(Items.STICK))
                    .offerTo(recipeExporter, "silver_hoe_mirrored");

            ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, SomeRandomStuff.SILVER_HOE)
                    .pattern("## ")
                    .pattern(" S ")
                    .pattern(" S ")
                    .input('#',SILVER_INGOTS)
                    .input('S',WOOD_STICKS)
                    .criterion(FabricRecipeProvider.hasItem(SomeRandomStuff.SILVER_INGOT),
                            FabricRecipeProvider.conditionsFromItem(SomeRandomStuff.SILVER_INGOT))
                    .criterion(FabricRecipeProvider.hasItem(Items.STICK),
                            FabricRecipeProvider.conditionsFromItem(Items.STICK))
                    .offerTo(recipeExporter, "silver_hoe");

            ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, SomeRandomStuff.SILVER_INGOT, 9)
                            .input(SILVER_BLOCKS).criterion(FabricRecipeProvider.hasItem(SomeRandomStuff.SILVER_BLOCK),
                            FabricRecipeProvider.conditionsFromItem(SomeRandomStuff.SILVER_BLOCK))
                            .offerTo(recipeExporter, "silver_ingots_from_block_of_silver");

            ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, SomeRandomStuff.SILVER_NUGGET, 9)
                    .input(SILVER_INGOTS).criterion(FabricRecipeProvider.hasItem(SomeRandomStuff.SILVER_INGOT),
                            FabricRecipeProvider.conditionsFromItem(SomeRandomStuff.SILVER_INGOT))
                    .offerTo(recipeExporter, "silver_nuggets_from_silver_ingot");

            ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, SomeRandomStuff.RAW_SILVER, 9)
                    .input(RAW_SILVER_BLOCKS).criterion(FabricRecipeProvider.hasItem(SomeRandomStuff.RAW_SILVER_BLOCK),
                            FabricRecipeProvider.conditionsFromItem(SomeRandomStuff.RAW_SILVER_BLOCK))
                    .offerTo(recipeExporter, "raw_silvers_from_raw_silver_block");

            RecipeProvider.offerSmelting(recipeExporter, List.of(SomeRandomStuff.SILVER_ORE,SomeRandomStuff.RAW_SILVER), RecipeCategory.MISC ,SomeRandomStuff.SILVER_INGOT, 0.45f, 200, "silver_ingot_from_smelting_ores");
            RecipeProvider.offerSmelting(recipeExporter, List.of(SomeRandomStuff.SILVER_CHESTPLATE,SomeRandomStuff.SILVER_BOOTS,SomeRandomStuff.SILVER_HELMET,SomeRandomStuff.SILVER_LEGGINGS), RecipeCategory.MISC, SomeRandomStuff.SILVER_NUGGET, 3.4f, 200, "silver_nugget_from_smelting_tools_and_armor");
        }
    }

    private static class EnglishLangProvider extends FabricLanguageProvider {
        private EnglishLangProvider(FabricDataOutput dataGenerator) {
            // Specifying en_us is optional, by default is is en_us.
            super(dataGenerator, "en_us");
        }

        @Override
        public void generateTranslations(TranslationBuilder translationBuilder) {
            translationBuilder.add(SomeRandomStuff.SILVER_BLOCK,"Block Of Silver");
            translationBuilder.add(SomeRandomStuff.SILVER_INGOT,"Silver Ingot");
            translationBuilder.add(SomeRandomStuff.SILVER_NUGGET,"Silver Nugget");
            translationBuilder.add(SomeRandomStuff.RAW_SILVER, "Raw Silver");
            translationBuilder.add(SomeRandomStuff.SILVER_ORE, "Silver Ore");
            translationBuilder.add(SomeRandomStuff.RAW_SILVER_BLOCK, "Block Of Raw Silver");
            translationBuilder.add(SomeRandomStuff.SILVER_HELMET, "Silver Helmet");
            translationBuilder.add(SomeRandomStuff.SILVER_CHESTPLATE, "Silver Chestplate");
            translationBuilder.add(SomeRandomStuff.SILVER_LEGGINGS, "Silver Leggings");
            translationBuilder.add(SomeRandomStuff.SILVER_BOOTS, "Silver Boots");
            translationBuilder.add(SomeRandomStuff.SILVER_SWORD, "Silver Sword");
            translationBuilder.add(SomeRandomStuff.SILVER_SHOVEL, "Silver Shovel");
            translationBuilder.add(SomeRandomStuff.SILVER_PICKAXE, "Silver Pickaxe");
            translationBuilder.add(SomeRandomStuff.SILVER_AXE, "Silver Axe");
            translationBuilder.add(SomeRandomStuff.SILVER_HOE, "Silver Hoe");
            translationBuilder.add(SomeRandomStuff.BEDROCK_HELMET,"Bedrock Helmet");
            translationBuilder.add(SomeRandomStuff.BEDROCK_CHESTPLATE,"Bedrock Chestplate");
            translationBuilder.add(SomeRandomStuff.BEDROCK_LEGGINGS,"Bedrock Leggings");
            translationBuilder.add(SomeRandomStuff.BEDROCK_BOOTS,"Bedrock Boots");
            translationBuilder.add(SomeRandomStuff.COMPLEX_MACHINE, "Complex Machine");

            // Load an existing language file.
            try {
                Path existingFilePath = this.dataOutput.getModContainer().findPath("assets/" + SomeRandomStuff.MOD_ID + "/lang/en_us.existing.json").get();
                translationBuilder.add(existingFilePath);
            } catch (Exception e) {
                throw new RuntimeException("Failed to add existing language file!", e);
            }
        }
    }
}
