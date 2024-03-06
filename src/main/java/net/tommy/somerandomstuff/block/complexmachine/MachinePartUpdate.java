package net.tommy.somerandomstuff.block.complexmachine;

import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.util.math.Direction;

import java.util.HashMap;
import java.util.Map;

public class MachinePartUpdate {
    public Map<String, Integer> intUpdates = new HashMap<>();
    public Map<String, Storage> itemStorageUpdates = new HashMap<>();

    public Map<String, Object> anyUpdates = new HashMap<>();
    public Direction direction;

    public MachinePartUpdate(){}

    public MachinePartUpdate(Map<String,Integer> intUpdates, Direction direction){
        this(intUpdates, Map.of(), Map.of(),direction);
    }

    public MachinePartUpdate(Map<String,Integer> intUpdates, Map<String,Storage> itemStorageUpdates, Direction direction){
        this(intUpdates,itemStorageUpdates, Map.of(),direction);
    }

    public MachinePartUpdate(Map<String,Integer> intUpdates, Map<String,Storage> itemStorageUpdates, Map<String, Object> anyUpdates, Direction direction){
        this.itemStorageUpdates = itemStorageUpdates;
        this.intUpdates = intUpdates;
        this.direction = direction;
        this.anyUpdates = anyUpdates;
    }

    @Override
    public String toString() {
        return "MachinePartUpdate{" +
                "intUpdates=" + intUpdates +
                ", itemStorageUpdates=" + itemStorageUpdates +
                ",anyUpdates=" + anyUpdates +
                ", direction=" + direction +
                '}';
    }
}
