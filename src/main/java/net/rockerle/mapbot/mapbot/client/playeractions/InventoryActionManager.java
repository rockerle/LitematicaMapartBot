package net.rockerle.mapbot.mapbot.client.playeractions;

import net.minecraft.client.MinecraftClient;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public class InventoryActionManager {

    private MinecraftClient client;
    private Map<BlockPos, Inventory> storageCache = new HashMap<>();

    public InventoryActionManager(MinecraftClient clt){
        this.client=clt;
    }

    //Also works as updateInventoryAt(...)
    public void addToStorageCache(BlockPos pos, Inventory inv){
        this.storageCache.put(pos,inv);
    }

    public void removeFromStorageCache(BlockPos bP){
        this.storageCache.remove(bP);
    }

    public Inventory getInventoryFromBP(BlockPos pos){
        return this.storageCache.get(pos);
    }

    public boolean anyContainerContainsItem(Item item){
        AtomicBoolean result = new AtomicBoolean(false);
        storageCache.forEach((p,i)->{
            if(i.containsAny(Set.of(item))){
                result.set(true);
            }
        });
        return result.get();
    }

    public boolean blockInStorage(){
        return false;
    }
    public String cachToString(){
        StringBuilder result = new StringBuilder("Storage Cache Contains: \n");
        this.storageCache.forEach((b,i)->{
            result.append("    @" + b.toShortString() + " with "+i.toString()+"\n");
        });
        return result.toString();
    }
}