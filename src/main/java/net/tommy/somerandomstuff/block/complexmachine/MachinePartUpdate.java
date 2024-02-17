package net.tommy.somerandomstuff.block.complexmachine;

import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.util.math.Direction;

import java.util.HashMap;
import java.util.Map;

public class MachinePartUpdate {
    public Map<String, Integer> intUpdates = new HashMap<>();
    public Map<String, Storage> itemStorageUpdates = new HashMap<>();
    public Direction direction;

    public MachinePartUpdate(){}

    public MachinePartUpdate(Map<String,Integer> intUpdates, Map<String,Storage> itemStorageUpdates, Direction direction){
        this.itemStorageUpdates = itemStorageUpdates;
        this.intUpdates = intUpdates;
        this.direction = direction;
    }

    @Override
    public String toString() {
        return "MachinePartUpdate{" +
                "intUpdates=" + intUpdates +
                ", itemStorageUpdates=" + itemStorageUpdates +
                ", direction=" + direction +
                '}';
    }
}
