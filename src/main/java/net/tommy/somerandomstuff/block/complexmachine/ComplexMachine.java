package net.tommy.somerandomstuff.block.complexmachine;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.*;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.tommy.somerandomstuff.SomeRandomStuff;
import net.tommy.somerandomstuff.block.complexmachine.machineparts.HopperMachinePart;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ComplexMachine extends BlockWithEntity{
    public static final BooleanProperty TOUCHING_NORTH = BooleanProperty.of("touching_north");
    public static final BooleanProperty TOUCHING_EAST = BooleanProperty.of("touching_east");
    public static final BooleanProperty TOUCHING_SOUTH = BooleanProperty.of("touching_south");
    public static final BooleanProperty TOUCHING_WEST = BooleanProperty.of("touching_west");
    public static final BooleanProperty TOUCHING_UP = BooleanProperty.of("touching_up");
    public static final BooleanProperty TOUCHING_DOWN = BooleanProperty.of("touching_down");

    public ComplexMachine(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(TOUCHING_NORTH, false).with(TOUCHING_EAST, false).with(TOUCHING_SOUTH, false).with(TOUCHING_WEST, false).with(TOUCHING_UP, false).with(TOUCHING_DOWN, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(TOUCHING_NORTH);
        builder.add(TOUCHING_EAST);
        builder.add(TOUCHING_SOUTH);
        builder.add(TOUCHING_WEST);
        builder.add(TOUCHING_UP);
        builder.add(TOUCHING_DOWN);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        updateNearbyStates(this,world,pos,true);
    }

    @Override
    public void onBroken(WorldAccess world, BlockPos pos, BlockState state) {
        updateNearbyStates(this, (World) world,pos,false);
    }

    public void updateNearbyStates(ComplexMachine complexMachine, World world, BlockPos pos, boolean andSelf){
        for (int j = -1; j < 2; j++){
            for (int k = -1; k < 2; k++){
                for (int l = -1; l < 2; l++) {
                    if (!(j == 0 && k == 0 && l == 0) || andSelf){ //((j==0&&k==0&&l==0)&&andSelf)||!(j==0&&k==0&&l==0)
                        BlockPos current_pos = new BlockPos(j,k,l).add(pos);
                        if (world.getBlockState(current_pos).isOf(complexMachine)){
                            world.setBlockState(current_pos,updateState(complexMachine,world,current_pos));
                        }
                    }
                }
            }
        }
    }

    public BlockState updateState(ComplexMachine complexMachine,World world,BlockPos pos){
        return complexMachine.getDefaultState()
                .with(TOUCHING_NORTH,world.getBlockState(pos.north()).isOf(complexMachine))
                .with(TOUCHING_EAST,world.getBlockState(pos.east()).isOf(complexMachine))
                .with(TOUCHING_SOUTH,world.getBlockState(pos.south()).isOf(complexMachine))
                .with(TOUCHING_WEST,world.getBlockState(pos.west()).isOf(complexMachine))
                .with(TOUCHING_UP,world.getBlockState(pos.up()).isOf(complexMachine))
                .with(TOUCHING_DOWN,world.getBlockState(pos.down()).isOf(complexMachine));
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        // With inheriting from BlockWithEntity this defaults to INVISIBLE, so we need to change that!
        return BlockRenderType.MODEL; //HAHAHHAH i didn't change it!!!! HAHHAHAHAHHA //HAHAHAH NOW I DID
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return validateTicker(type, SomeRandomStuff.COMPLEX_MACHINE_ENTITY, ComplexMachineEntity::tick);
    }

    @Override
    public MapCodec<? extends BlockWithEntity> getCodec() {
        return null;
    }

    @Override
    public BlockState getAppearance(BlockState state, BlockRenderView renderView, BlockPos pos, Direction side, @Nullable BlockState sourceState, @Nullable BlockPos sourcePos) {
        return super.getAppearance(state, renderView, pos, side, sourceState, sourcePos);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ComplexMachineEntity(pos,state);
    }

    @SuppressWarnings("deprecation")
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient()){
            ItemStack stack = player.getStackInHand(hand);
            NbtCompound partNbt;
            try {
                assert stack.getNbt() != null;
                partNbt = stack.getNbt().getCompound(ComplexMachineEntity.MACHINE_PART_KEY);
            } catch (NullPointerException e){
                return ActionResult.PASS;
            }
            try {
                ComplexMachineEntity CME = (ComplexMachineEntity) world.getBlockEntity(pos);
                assert CME != null;
                CME.setPart(MachinePart.fromNbt(partNbt),stack.copy() ,0);
                world.updateListeners(pos,state,state, Block.NOTIFY_LISTENERS);
            } catch (ClassCastException e) {
                return ActionResult.FAIL;
            }
    }
        return ActionResult.PASS;
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        ItemScatterer.onStateReplaced(state, newState, world, pos);
        super.onStateReplaced(state, world, pos, newState, moved);
    }
}
