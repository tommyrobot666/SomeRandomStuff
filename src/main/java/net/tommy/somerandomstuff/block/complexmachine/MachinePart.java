package net.tommy.somerandomstuff.block.complexmachine;

import net.minecraft.client.render.model.BakedModel;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.nbt.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.tommy.somerandomstuff.SomeRandomStuff;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MachinePart {
        Identifier id;
        int multipart_index = 0;
        int multipart_total = 0;
        Item item_that_looks_similar = Items.AIR;

    public MachinePart(){
        this(new Identifier("",""));
    }
    public MachinePart(Identifier id){
        this(id,Items.AIR);
    }
    public MachinePart(Identifier id, Item item_that_looks_similar){
        this(id,item_that_looks_similar,0,0);
    }
    public MachinePart(Identifier id ,Item item_that_looks_similar ,int index, int total){
        this.id = id;
        multipart_index = index;
        multipart_total = total;
        this.item_that_looks_similar = item_that_looks_similar;
    }

    public NbtCompound toNbt(){
        NbtCompound to_return = new NbtCompound();
        to_return.put("path", NbtString.of(this.id.getPath()));
        to_return.put("namespace", NbtString.of(this.id.getNamespace()));
        to_return.put("index", NbtInt.of(this.multipart_index));
        to_return.put("total", NbtInt.of(this.multipart_total));
        //to_return.put("item_that_looks_similar/path", NbtString.of(Registries.ITEM.getId(this.item_that_looks_similar).getPath()));
        //to_return.put("item_that_looks_similar/namespace", NbtString.of(Registries.ITEM.getId(this.item_that_looks_similar).getNamespace()));
        return to_return;
    }

    public static MachinePart fromNbt(NbtCompound nbt){
        MachinePart to_return = SomeRandomStuff.COMPLEX_MACHINE_PART_TYPE.get(new Identifier(nbt.getString("namespace"),nbt.getString("path")));
        return to_return.fromNbtNS(nbt);
    }

    public MachinePart fromNbtNS(NbtCompound nbt){
        MachinePart to_return = new MachinePart();
        to_return.multipart_index = nbt.getInt("index");
        to_return.multipart_total = nbt.getInt("total");
        return to_return;
    }

    MachinePart otherDataFromNbt(MachinePart the_part, NbtCompound nbt){
            return the_part;
        }

    boolean sameType(MachinePart machinePart){
            return this.id.equals(machinePart.id);
        }

    public static ArrayList<NbtCompound> listToNbt(ArrayList<MachinePart> list){
        ArrayList<NbtCompound> to_return = new ArrayList<>(List.of());
        for (MachinePart machinePart : list){
            to_return.add(machinePart.toNbt());
        }
        return to_return;
    }

    public static NbtList listToNbtList(ArrayList<MachinePart> list){
        NbtList to_return = new NbtList();
        for (MachinePart machinePart : list) {
            to_return.add(machinePart.toNbt());
        }
        return to_return;
    }

    public static ArrayList<MachinePart> listFromNbt(ArrayList<NbtCompound> list){
        ArrayList<MachinePart> to_return = new ArrayList<>(List.of());
        for (NbtCompound compound : list){
            to_return.add(MachinePart.fromNbt(compound));
        }
        return to_return;
    }

    public static ArrayList<MachinePart> listFromNbtList(NbtList list){
        ArrayList<MachinePart> to_return = new ArrayList<>(List.of());
        for (NbtElement element : list){
            to_return.add(MachinePart.fromNbt((NbtCompound) element));
        }
        return to_return;
    }

    public BakedModel getModel(){
        return null;
    }

    public void update(MachinePartUpdate update){}

    public boolean willAcceptUpdate(MachinePartUpdate update){
        return false;
    }

    @NotNull
    public MachinePartUpdate constructUpdateWithOwnData(){
        return new MachinePartUpdate();
    }

    public Map<Direction,MachinePartUpdate> tick(Map<Direction,MachinePart> nearbyParts, World world, BlockPos pos){
        Direction[] directions = Direction.values();
        Map<Direction,MachinePartUpdate> machinePartUpdates = new HashMap<>();
        for(Direction direction : directions) {
            if (nearbyParts.get(direction) != null) {
                    machinePartUpdates.put(direction, interact(direction, nearbyParts.get(direction), world, pos));
                if (machinePartUpdates.get(direction) == null) {
                    machinePartUpdates.remove(direction);
                }
            }
        }
        return machinePartUpdates;
    }

    public MachinePartUpdate interact(Direction direction, MachinePart part, World world, BlockPos pos){
        return null;
    }

    @Override
    public String toString() {
        return "MachinePart{" +
                "id=" + id +
                ", multipart_index=" + multipart_index +
                ", multipart_total=" + multipart_total +
                '}';
    }
}

