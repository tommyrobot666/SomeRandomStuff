package net.tommy.somerandomstuff.block.complexmachine;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.tommy.somerandomstuff.SomeRandomStuff;
import net.tommy.somerandomstuff.mixin.DirectionAccessor;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class ComplexMachineEntity extends BlockEntity implements Inventory {
    public static final String MACHINE_PART_KEY = "ComplexMachinePart";
    public static final int PARTS_IN_EACH_BLOCK = 2;
    public ArrayList<MachinePart> whole_machine_parts = new ArrayList<>();

    public ArrayList<ItemStack> partItems = new ArrayList<>();
    public ArrayList<MachinePart> parts;
    public ArrayList<Byte> empty = new ArrayList<>();
    public Box size = new Box(0,0,0,0,0,0);

    public ComplexMachineEntity(BlockPos pos, BlockState state) {
        super(SomeRandomStuff.COMPLEX_MACHINE_ENTITY, pos, state);
        parts = new ArrayList<>(); // Initialize the parts list
        for (int i = 0; i < Math.pow(PARTS_IN_EACH_BLOCK,3);i++){
            parts.add(new MachinePart());
            partItems.add(ItemStack.EMPTY);
        }
    }

    public static void tick(World world, BlockPos pos, BlockState state, ComplexMachineEntity be) {
        if (world.isClient()){
            return;
        }
        if (be.parts.size() != be.partItems.size()){
            throw new IllegalStateException("Size of parts list is unequal to size of part's items list (every part must have a corresponding item)");
        }
        updateSizeBoxAndPartsArray(world, pos, state.getBlock(),be,128);
        tickParts(be,world);
        updatePartItems(be);
    }

    static void updatePartItems(ComplexMachineEntity entity){
        for (int i = 0; i < entity.partItems.size(); i++){
            ItemStack itemStack = entity.partItems.get(i);
            if (itemStack.getNbt() != null) {
                itemStack.getNbt().remove(MACHINE_PART_KEY);
                itemStack.getNbt().put(MACHINE_PART_KEY, entity.parts.get(i).toNbt());
            }
        }
    }

    static void tickParts(ComplexMachineEntity entity, World world){
        for (MachinePart part : entity.parts){
            Map<Direction,MachinePart> nearbyParts = new HashMap<>();
            for (Direction direction : Direction.values()){
                if (onEdgeOfList(entity.parts.indexOf(part), PARTS_IN_EACH_BLOCK, PARTS_IN_EACH_BLOCK, direction)){
                    if (world.getBlockState(entity.pos.add(direction.getVector())).isOf(SomeRandomStuff.COMPLEX_MACHINE)){
                        nearbyParts.put(direction,((ComplexMachineEntity)world.getBlockEntity(entity.pos.add(direction.getVector()))).parts.get(calculateIndexOfMachinePartOfOtherMachine(listIndexToPos(entity.parts.indexOf(part),PARTS_IN_EACH_BLOCK,PARTS_IN_EACH_BLOCK),direction)));
                    } else {
                        nearbyParts.put(direction,null);
                    }
                } else {
                    nearbyParts.put(direction,entity.parts.get(blockPosToListIndex(listIndexToPos(entity.parts.indexOf(part),PARTS_IN_EACH_BLOCK,PARTS_IN_EACH_BLOCK).add(direction.getVector()),PARTS_IN_EACH_BLOCK,PARTS_IN_EACH_BLOCK)));
                }
            }
            Map<Direction,MachinePartUpdate> toUpdate = part.tick(nearbyParts,world,entity.getPos());
            for (Direction direction : toUpdate.keySet()){
                if (onEdgeOfList(entity.parts.indexOf(part), PARTS_IN_EACH_BLOCK, PARTS_IN_EACH_BLOCK, direction)){
                    if (world.getBlockState(entity.pos.add(direction.getVector())).isOf(SomeRandomStuff.COMPLEX_MACHINE)){
                        ((ComplexMachineEntity)world.getBlockEntity(entity.pos.add(direction.getVector()))).parts.get(calculateIndexOfMachinePartOfOtherMachine(listIndexToPos(entity.parts.indexOf(part),PARTS_IN_EACH_BLOCK,PARTS_IN_EACH_BLOCK),direction)).update(toUpdate.get(direction));
                    }
                } else {
                    entity.parts.get(blockPosToListIndex(listIndexToPos(entity.parts.indexOf(part),PARTS_IN_EACH_BLOCK,PARTS_IN_EACH_BLOCK).add(direction.getVector()),PARTS_IN_EACH_BLOCK,PARTS_IN_EACH_BLOCK)).update(toUpdate.get(direction));
                }
            }
        }
    }

    public void setPart(MachinePart part,ItemStack itemStack ,int index){
        parts.set(index, part);
        partItems.set(index, itemStack);
    }

    // make private later
    private static void updateSizeBoxAndPartsArray(World world, BlockPos pos, Block block, ComplexMachineEntity entity, int max_size) {
        ArrayList<BlockPos> connected_machines = getConnectedBlocks(world, pos, block, max_size);
        Box box_to_return = updateBox(connected_machines);
        ArrayList<MachinePart> list_to_return = getAllParts(entity, connected_machines, world, pos);
        //NbtList parts_from_list_to_return = machinesOwnParts(new BlockPos(0,0,0),list_to_return,box_to_return);

        entity.size = box_to_return;
        entity.whole_machine_parts = list_to_return;
        entity.empty = resizeEmpty(entity,connected_machines,box_to_return);
        entity.markDirty();
        //world.getPlayers().forEach((player -> player.sendMessage(Text.literal("_______________________________"))));
        //world.getPlayers().forEach((player -> player.sendMessage(Text.literal(entity.size.toString()))));
        //world.getPlayers().forEach((player -> player.sendMessage(Text.literal(entity.whole_machine_parts.toString()))));
        //world.getPlayers().forEach((player -> player.sendMessage(Text.literal(connected_machines.toString()))));
        //world.getPlayers().forEach(player -> player.sendMessage(Text.literal(entity.parts.toString())));
    }

    private static ArrayList<MachinePart> getAllParts(ComplexMachineEntity entity, ArrayList<BlockPos> connected_machines, World world, BlockPos pos) {
     //   ArrayList<NbtCompound> list_to_return = new ArrayList<>(List.of(resizeEmpty(entity, connected_machines,entity.size).toArray(NbtCompound[]::new)));
        ArrayList<MachinePart> list_to_return = new ArrayList<>();
        Box box = entity.size;
        int boxLength = (int) (box.maxX-box.minX);
        int boxWidth = (int) (box.maxZ-box.minZ);
        int boxHeight = (int) (box.maxY-box.minY);
        for (int j = 0;j <= (boxLength)*PARTS_IN_EACH_BLOCK;j++){
            for (int k = 0;k <= (boxHeight)*PARTS_IN_EACH_BLOCK;k++){
                for (int l = 0;l <= (boxWidth)*PARTS_IN_EACH_BLOCK;l++) {
                    BlockPos current_point = new BlockPos(Math.floorDiv(j, PARTS_IN_EACH_BLOCK), Math.floorDiv(k, PARTS_IN_EACH_BLOCK), Math.floorDiv(l, PARTS_IN_EACH_BLOCK));
                    list_to_return.add(new MachinePart());
                    if (connected_machines.contains(current_point)) {
                        ComplexMachineEntity connected_entity = (ComplexMachineEntity) world.getBlockEntity(pos.add(current_point));
                        int part_of_whole_machine_parts_Z = l % PARTS_IN_EACH_BLOCK;
                        int part_of_whole_machine_parts_Y = k % PARTS_IN_EACH_BLOCK;
                        int part_of_whole_machine_parts_X = j % PARTS_IN_EACH_BLOCK;
                        assert connected_entity != null;
                        //list_to_return.set(l+boxWidth*k+boxWidth*boxHeight*j, connected_entity.parts.get(current_point.getZ()+part_of_whole_machine_parts_Z+current_point.getY()+part_of_whole_machine_parts_Y*boxWidth+ current_point.getX()+part_of_whole_machine_parts_X*boxWidth*boxHeight));
                        try {
                            list_to_return.set(l + boxWidth * k + boxWidth * boxHeight * j, connected_entity.parts.get(blockPosToListIndex(new BlockPos(part_of_whole_machine_parts_X,part_of_whole_machine_parts_Y,part_of_whole_machine_parts_Z),boxWidth,boxHeight)));
                        } catch (IndexOutOfBoundsException e){
                            SomeRandomStuff.LOGGER.error("Complex Machine had trouble getting parts of other Machine");
                        }
                    }
                }
            }
        }
        return list_to_return;
      //  NbtList MAKE_THE_SECOND_CONSTRUCTOR_PUBLIC_RIGHT_NOW_OR_ELSE = new NbtList(); // #programmingThreats
       // for (NbtCompound compound : list_to_return){
       //     MAKE_THE_SECOND_CONSTRUCTOR_PUBLIC_RIGHT_NOW_OR_ELSE.add(compound); // ADD ALL?? HOW ABOUT NO!
       // }
     //   return MAKE_THE_SECOND_CONSTRUCTOR_PUBLIC_RIGHT_NOW_OR_ELSE;
    }

    private static ArrayList<Byte> resizeEmpty(ComplexMachineEntity entity, ArrayList<BlockPos> connected_machines, Box box) {
        ArrayList<Byte> list_to_return = new ArrayList<>(List.of());
        // box.max - box.min = length/width/height
        int boxLength = (int) (box.maxX-box.minX);
        int boxWidth = (int) (box.maxZ-box.minZ);
        int boxHeight = (int) (box.maxY-box.minY);
        //int oldBoxLength = (int) (entity.size.maxX-entity.size.minX);
        //int oldBoxWidth = (int) (entity.size.maxZ-entity.size.minZ);
        //int oldBoxHeight = (int) (entity.size.maxY-entity.size.minY);
        for (int j = 0;j <= (boxLength)*PARTS_IN_EACH_BLOCK;j++){
            for (int k = 0;k <= (boxHeight)*PARTS_IN_EACH_BLOCK;k++){
                for (int l = 0;l <= (boxWidth)*PARTS_IN_EACH_BLOCK;l++){
                    NbtByte aByte = NbtByte.ONE;
                    BlockPos current_point = new BlockPos(Math.floorDiv(j,PARTS_IN_EACH_BLOCK),Math.floorDiv(k,PARTS_IN_EACH_BLOCK),Math.floorDiv(l,PARTS_IN_EACH_BLOCK));
                    //we don't do that anymore // if a pos already has data, puts data in its new location in the list. (a pos at (x,y,z) can go from being at (n) to being at (v) with out changing the (x,y,z))
                    //if (entity.size.contains(current_point.getX()+box.minX,current_point.getY()+box.minY,current_point.getZ()+box.minZ)) { // adding the min re-applys offsets
                    //    aByte = NbtByte.of(entity.empty.get(l + oldBoxWidth * k + oldBoxWidth * oldBoxHeight * j));
                    //}
                    if (listOfPointsHasPoint(connected_machines,current_point.subtract(new Vec3i((int) box.minX, (int) box.minY, (int) box.minZ)))){
                        aByte = NbtByte.ZERO;
                    }
                    list_to_return.add(aByte.byteValue());
                }
            }
        }
        return list_to_return;
    }

    private static ArrayList<BlockPos> getConnectedBlocks(World world,BlockPos pos,Block block, int max_size){
        ArrayList<Direction> directions = new ArrayList<>(List.of(DirectionAccessor.getAllDirections()));
        ArrayList<BlockPos> connected_machines = new ArrayList<>(List.of(new BlockPos(0,0,0)));
        ArrayList<BlockPos> poses_to_check = new ArrayList<>(List.of(pos));
        int times_checked_poses = 0;
        while (poses_to_check.size() > 0 && times_checked_poses < max_size+1){
            directions.forEach((dir) -> {
                BlockPos checking = poses_to_check.get(0).add(dir.getVector());
                if (world.getBlockState(checking).isOf(block) && !listOfPointsHasPoint(connected_machines, checking.subtract(pos))){
                    connected_machines.add(checking.subtract(pos));
                    poses_to_check.add(checking);
                }
            });
            poses_to_check.remove(0);
            times_checked_poses++;
        }
        if (connected_machines.size() >= max_size){
            SomeRandomStuff.LOGGER.error("MACHINE REACHED MAX SIZE!!! ANY MORE SIZE WILL CORRUPT MACHINE");
        }
        return connected_machines;
    }

    private static Box updateBox(ArrayList<BlockPos> connected_machines){
        Box box_to_return = new Box(0,0,0,0,0,0);
        for (BlockPos current_pos : connected_machines) {
            if (current_pos.getX() > box_to_return.maxX) {
                box_to_return = box_to_return.withMaxX(current_pos.getX());
            } else if (current_pos.getX() < box_to_return.minX) {
                box_to_return = box_to_return.withMinX(current_pos.getX());
            }
            if (current_pos.getY() > box_to_return.maxY) {
                box_to_return = box_to_return.withMaxY(current_pos.getY());
            } else if (current_pos.getY() < box_to_return.minY) {
                box_to_return = box_to_return.withMinY(current_pos.getY());
            }
            if (current_pos.getZ() > box_to_return.maxZ) {
                box_to_return = box_to_return.withMaxZ(current_pos.getZ());
            } else if (current_pos.getZ() < box_to_return.minZ) {
                box_to_return = box_to_return.withMinZ(current_pos.getZ());
            }
        }
        return box_to_return;
    }

    private static <P extends Vec3i> boolean listOfPointsHasPoint(ArrayList<P> list, P point){
        for (P poi : list){
            if (poi.getX()==point.getX()&&poi.getY()==point.getY()&&poi.getZ()==point.getZ()){
                return true;
            }
        }
        return false;
    }

    private static boolean onEdgeOfList(int index, int width, int height, Direction direction){
        BlockPos listPosition = listIndexToPos(index,width,height);
        if (listPosition.add(direction.getVector()).getZ()<0){
            return true;
        }
        if (listPosition.add(direction.getVector()).getY()<0){
            return true;
        }
        if (listPosition.add(direction.getVector()).getX()<0){
            return true;
        }
        return false;
    }

    //private static NbtList machinesOwnParts(BlockPos pos, NbtList whole_machine_parts, Box size){
    //    NbtList parts_from_list_to_return = new NbtList();
    //    int boxLength = (int) (size.maxX-size.minX);
    //    int boxWidth = (int) (size.maxZ-size.minZ);
    //    int boxHeight = (int) (size.maxY-size.minY);
    //    for (int j = 0;j <= (boxLength)*PARTS_IN_EACH_BLOCK;j++){
    //        for (int k = 0;k <= (boxHeight)*PARTS_IN_EACH_BLOCK;k++){
    //            for (int l = 0;l <= (boxWidth)*PARTS_IN_EACH_BLOCK;l++){
    //                BlockPos current_point = new BlockPos(Math.floorDiv(j,PARTS_IN_EACH_BLOCK),Math.floorDiv(k,PARTS_IN_EACH_BLOCK),Math.floorDiv(l,PARTS_IN_EACH_BLOCK));
    //                if (current_point.equals(pos)){
    //                    parts_from_list_to_return.add(whole_machine_parts.get(l+boxWidth*k+boxWidth*boxHeight*j));
    //                }
    //            }
    //        }
    //    }
    //    return parts_from_list_to_return;
    //}

    @Override
    public void writeNbt(NbtCompound nbt) {
        nbt.put("whole_machine_parts", MachinePart.listToNbtList(whole_machine_parts));
        nbt.put("parts",MachinePart.listToNbtList(parts));
        nbt.putByteArray("empty",empty);
        nbt.putIntArray("box", List.of((int)size.maxX,(int)size.maxY,(int)size.maxZ,(int)size.minX,(int)size.minY,(int)size.minZ));
        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        whole_machine_parts = MachinePart.listFromNbtList(nbt.getList("whole_machine_parts",NbtCompound.COMPOUND_TYPE));
        parts = MachinePart.listFromNbtList(nbt.getList("parts",NbtCompound.COMPOUND_TYPE));
        byte[] emptyArray = nbt.getByteArray("empty");
        ArrayList<Byte> newEmpty = new ArrayList<>();
        for (byte b : emptyArray){
            newEmpty.add(b);
        }
        empty = newEmpty;
        int[] box_array = nbt.getIntArray("box");
        size = new Box(box_array[0],box_array[1],box_array[2],box_array[3],box_array[4],box_array[5]);
    }

    private static BlockPos listIndexToPos(int idx, int width, int height){
        int a = width * height;
        int b = idx - a * (idx/a);
        return new BlockPos(idx/a,b/width,b%width);
    }

    private static int blockPosToListIndex(BlockPos pos, int width, int height){
        return pos.getZ() + pos.getY() * width + pos.getX() * width * height;
    }

    private static int calculateIndexOfMachinePartOfOtherMachine(BlockPos machinePartInThisMachine, Direction direction){
        int to_return = 0;
        switch (direction.getId()){
            case 0: case 1: {
                to_return = blockPosToListIndex(machinePartInThisMachine.withY(PARTS_IN_EACH_BLOCK-machinePartInThisMachine.getY()),PARTS_IN_EACH_BLOCK,PARTS_IN_EACH_BLOCK);
                break;
            }
            case 2: case 3: {
                to_return = blockPosToListIndex(machinePartInThisMachine.north(machinePartInThisMachine.getZ())
                        .south(PARTS_IN_EACH_BLOCK-machinePartInThisMachine.getZ()),PARTS_IN_EACH_BLOCK,PARTS_IN_EACH_BLOCK);
                break;
            }
            case 4: case 5: {
                to_return = blockPosToListIndex(machinePartInThisMachine.west(machinePartInThisMachine.getX())
                        .east(PARTS_IN_EACH_BLOCK-machinePartInThisMachine.getX()),PARTS_IN_EACH_BLOCK,PARTS_IN_EACH_BLOCK);
            }
        }
        return to_return;
    }

    //Warning: Need to call world.updateListeners(pos, state, state, Block.NOTIFY_LISTENERS); to trigger the update, otherwise the client does not know that the block entity has been changed.
    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }

    // methods for stack scatterer
    @Override
    public int size() {
        return partItems.size();
    }

    @Override
    public boolean isEmpty() {
        return partItems.isEmpty();
    }

    @Override
    public ItemStack getStack(int slot) {
        return partItems.get(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        throw new IllegalStateException("Don't Remove stacks");
    }

    @Override
    public ItemStack removeStack(int slot) {
        throw new IllegalStateException("Don't Remove stacks");
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        partItems.set(slot, stack);
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return true;
    }

    @Override
    public void clear() {
        throw new IllegalStateException("Don't clear");
    }
}
