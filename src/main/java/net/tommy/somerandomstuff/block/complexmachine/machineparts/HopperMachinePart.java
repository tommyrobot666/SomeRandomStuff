package net.tommy.somerandomstuff.block.complexmachine.machineparts;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.impl.transfer.item.InventoryStorageImpl;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.tommy.somerandomstuff.SomeRandomStuff;
import net.tommy.somerandomstuff.block.complexmachine.MachinePart;
import net.tommy.somerandomstuff.block.complexmachine.MachinePartUpdate;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class HopperMachinePart extends MachinePart {
    Item item_that_looks_similar = Items.HOPPER;

    Direction direction = Direction.NORTH;
    DefaultedList<ItemStack> inventory = DefaultedList.ofSize(3,ItemStack.EMPTY);
    int speed = 1;
    boolean locked = false;

    public HopperMachinePart(){
        super(new Identifier(SomeRandomStuff.MOD_ID,"hopper"), Items.HOPPER);
    }

    @Override
    public BakedModel getModel() {
        return null;
    }

    @Override
    public Map<Direction, MachinePartUpdate> tick(Map<Direction, MachinePart> nearbyParts, World world, BlockPos pos) {
        int highestNearbyPower = 0;
        for (Direction direction : Direction.values()){
            if (nearbyParts.get(direction) != null){
                MachinePartUpdate data = nearbyParts.get(direction).constructUpdateWithOwnData();
                if (data.intUpdates.containsKey("redstone_power")){
                    highestNearbyPower = Math.max(highestNearbyPower, data.intUpdates.get("redstone_power"));
                }
            } else {
                highestNearbyPower = Math.max(highestNearbyPower,world.getEmittedRedstonePower(pos.add(direction.getVector()),direction));
            }
        }
        locked = highestNearbyPower > 0;
        List<Entity> collectableItems = world.getOtherEntities(null,new Box(pos.up())).stream().filter((entity -> entity instanceof ItemEntity)).toList();
        if (!collectableItems.isEmpty()){
            for (Entity entity : collectableItems){
                ItemEntity itemEntity = (ItemEntity) entity;
                ItemStack itemEntityStack = itemEntity.getStack();
                SomeRandomStuff.LOGGER.error(itemEntityStack+"%");
                if (new SimpleInventory(inventory.get(0),inventory.get(1),inventory.get(2)).canInsert((itemEntityStack))){
                    for(int item = 0; item < inventory.size(); item++) {
                        ItemStack itemStack = inventory.get(item);
                        if (ItemStack.canCombine(itemStack, itemEntityStack) && itemStack.getCount() < itemStack.getMaxCount()) {
                            if (itemStack.getCount() + itemEntityStack.getCount() > itemStack.getMaxCount()) {
                                inventory.get(item).increment(itemEntityStack.getCount() - Math.abs(itemStack.getCount() - itemEntityStack.getCount()));
                                itemEntityStack.decrement(Math.abs(itemStack.getCount() - itemEntityStack.getCount()));
                            } else {
                                inventory.get(item).increment(itemEntityStack.getCount());
                                itemEntityStack.setCount(0);
                            }
                        } else if (itemStack.isEmpty()) {
                            inventory.set(item,itemEntityStack.copy());
                            itemEntityStack.setCount(0);
                        }
                        if (itemEntityStack.getCount() < 1){
                            itemEntity.setDespawnImmediately();
                            break;
                        }
                    }
                }
            }
        }
        return super.tick(nearbyParts, world, pos);
    }

    @Override
    public MachinePartUpdate interact(Direction direction, MachinePart part, World world, BlockPos pos) {
        if (direction.equals(this.direction)){
            MachinePartUpdate update = new MachinePartUpdate(Map.of("item_transfer_amount",speed,"item_transfer_exact",0),Map.of("any_item_transfer_inventory", InventoryStorageImpl.of(new SimpleInventory(inventory.get(0),inventory.get(1),inventory.get(2)),null)),direction);
            //SomeRandomStuff.LOGGER.error(update.toString());
            if (part.willAcceptUpdate(update)){
                int items_to_remove = speed;
                while (items_to_remove > 0 && !inventory.isEmpty()){
                    for (int index : List.of(0,1,2)) {
                        if (!inventory.get(index).isEmpty()) {
                            if (inventory.get(index).getCount() < items_to_remove) {
                                items_to_remove -= inventory.get(index).getCount();
                                inventory.set(index, ItemStack.EMPTY);
                            } else {
                                inventory.get(index).decrement(items_to_remove);
                                items_to_remove = 0;
                            }
                            if (items_to_remove < 1){
                                break;
                            }
                        }
                    }
                }
                return update;
            }
        }
        return null;
    }

    @Override
    public boolean willAcceptUpdate(MachinePartUpdate update) {
        if (update.itemStorageUpdates.containsKey("any_item_transfer_inventory") && update.intUpdates.containsKey("item_transfer_amount")  && update.intUpdates.containsKey("item_transfer_exact")) {
            int amount_transferable = 0;
            InventoryStorageImpl inventoryStorage = (InventoryStorageImpl) update.itemStorageUpdates.get("any_item_transfer_inventory");
            for (SingleSlotStorage stackStorage : inventoryStorage.getSlots()){
                if (new SimpleInventory(inventory.get(0),inventory.get(1),inventory.get(2)).canInsert(((ItemVariant) stackStorage.getResource()).toStack(stackStorage.getSlotCount()))){
                    Iterator iterator = inventory.iterator();
                    while(iterator.hasNext()) {
                        ItemStack itemStack = (ItemStack)iterator.next();
                        if (itemStack.isEmpty() || ItemStack.canCombine(itemStack, ((ItemVariant) stackStorage.getResource()).toStack(stackStorage.getSlotCount())) && itemStack.getCount() < itemStack.getMaxCount()) {
                            amount_transferable += itemStack.getCount() - stackStorage.getSlotCount();
                            break;
                        }
                    }
                }
            }
            if (update.intUpdates.getOrDefault("item_transfer_exact",2)!=0){
                return update.intUpdates.getOrDefault("item_transfer_amount", 0) <= amount_transferable;
            } else {
                return amount_transferable > 0;
            }
        }
        return false;
    }

    @Override
    public void update(MachinePartUpdate update) {
        if (!willAcceptUpdate(update)){
            return;
        }
        InventoryStorageImpl inventoryStorage = (InventoryStorageImpl) update.itemStorageUpdates.get("any_item_transfer_inventory");
        int amountToTransfer = update.intUpdates.get("item_transfer_amount");
        for (SingleSlotStorage stackStorage : inventoryStorage.getSlots()){
            if (new SimpleInventory(inventory.get(0),inventory.get(1),inventory.get(2)).canInsert(((ItemVariant) stackStorage.getResource()).toStack(stackStorage.getSlotCount()))){
                for(int item = 0; item < inventory.size(); item++) {
                    ItemStack itemStack = inventory.get(item);
                    if (itemStack.isEmpty() || ItemStack.canCombine(itemStack, ((ItemVariant) stackStorage.getResource()).toStack(stackStorage.getSlotCount())) && itemStack.getCount() < itemStack.getMaxCount()) {
                        if (itemStack.getCount() + stackStorage.getSlotCount() > itemStack.getMaxCount()) {
                            inventory.get(item).increment(amountToTransfer - Math.abs(itemStack.getCount() - stackStorage.getSlotCount()));
                            amountToTransfer -= Math.abs(itemStack.getCount() - stackStorage.getSlotCount());
                        } else {
                            inventory.get(item).increment(amountToTransfer);
                            amountToTransfer = 0;
                        }
                    }
                    if (amountToTransfer < 1){
                        break;
                    }
                }
            }
            if (amountToTransfer < 1){
                break;
            }
        }
    }

    @Override
    public NbtCompound toNbt() {
        NbtCompound nbtCompound = super.toNbt();
        nbtCompound.put("inventory",new SimpleInventory(inventory.get(0),inventory.get(1),inventory.get(2)).toNbtList());
        nbtCompound.putInt("direction",direction.getId());
        nbtCompound.putInt("speed",speed);
        return nbtCompound;
    }

    @Override
    public MachinePart fromNbtNS(NbtCompound nbt) {
        HopperMachinePart machinePart = new HopperMachinePart();
        SimpleInventory simpleInventory = new SimpleInventory(3);
        simpleInventory.readNbtList(nbt.getList("inventory",NbtCompound.COMPOUND_TYPE));
        machinePart.inventory = simpleInventory.heldStacks;
        machinePart.direction = Direction.byId(nbt.getInt("direction"));
        machinePart.speed = nbt.getInt("speed");
        return machinePart;
    }

    @Override
    public String toString() {
        return "HopperMachinePart{" +
                "direction=" + direction +
                ", inventory=" + inventory +
                ", speed=" + speed +
                ", locked=" + locked +
                '}';
    }
}
