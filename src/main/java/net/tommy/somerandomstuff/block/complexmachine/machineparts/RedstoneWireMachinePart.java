package net.tommy.somerandomstuff.block.complexmachine.machineparts;

import net.fabricmc.fabric.api.transfer.v1.storage.base.FilteringStorage;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtInt;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.tommy.somerandomstuff.SomeRandomStuff;
import net.tommy.somerandomstuff.block.complexmachine.MachinePart;
import net.tommy.somerandomstuff.block.complexmachine.MachinePartUpdate;

import java.util.HashMap;
import java.util.Map;

public class RedstoneWireMachinePart extends MachinePart {
    Item item_that_looks_similar = Items.REDSTONE;

    int power = 0;

    public RedstoneWireMachinePart() {
        super(new Identifier(SomeRandomStuff.MOD_ID,"redstone_wire"), Items.REDSTONE);

    }

    @Override
    public BakedModel getModel(){
        return null;
    }

    @Override
    public NbtCompound toNbt(){
        NbtCompound nbtCompound = super.toNbt();
        nbtCompound.put("power", NbtInt.of(power));
        return nbtCompound;
    }

    @Override
    public MachinePart fromNbtNS(NbtCompound nbt){
        RedstoneWireMachinePart machinePart = new RedstoneWireMachinePart();
        machinePart.power = nbt.getInt("power");
        return machinePart;
    }

    @Override
    public Map<Direction, MachinePartUpdate> tick(Map<Direction, MachinePart> nearbyParts, World world, BlockPos pos) {
        SomeRandomStuff.LOGGER.info(nearbyParts.toString());
        int highestNearbyPower = 0;
        for (Direction direction : Direction.values()){
            if (nearbyParts.get(direction) != null){
                MachinePartUpdate data = nearbyParts.get(direction).constructUpdateWithOwnData();
                if (data.intUpdates.containsKey("redstone_power")){
                    highestNearbyPower = Math.max(highestNearbyPower, data.intUpdates.get("redstone_power"));
                }
            }
            highestNearbyPower = Math.max(highestNearbyPower,world.getEmittedRedstonePower(pos.add(direction.getVector()),direction));
        }
        power = Math.max(0,highestNearbyPower-1);
        return super.tick(nearbyParts, world, pos);
    }

    @Override
    public MachinePartUpdate interact(Direction direction, MachinePart part, World world, BlockPos pos) {
        if (!world.getBlockState(pos).isOf(SomeRandomStuff.COMPLEX_MACHINE)){
            return null;
        }
        return new MachinePartUpdate(Map.of("redstone_power",power),Map.of(),direction);
    }

    @Override
    public MachinePartUpdate constructUpdateWithOwnData() {
        return new MachinePartUpdate(Map.of("redstone_power",power),Map.of(),(Direction) null);
    }

    @Override
    public String toString() {
        return "RedstoneWireMachinePart{" +
                "power=" + power +
                '}';
    }
}
