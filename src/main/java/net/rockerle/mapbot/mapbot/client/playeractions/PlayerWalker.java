package net.rockerle.mapbot.mapbot.client.playeractions;

import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.rockerle.mapbot.mapbot.client.MapbotClient;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PlayerWalker {

    private MinecraftClient client = MapbotClient.mc;
    BlockPlacer bplacer;
    private List<BlockPos> path;
    private Iterator<BlockPos> posIter;
    private BlockPos nextPos;
    private boolean isActive = false;

    private BlockState goalState;
    private BlockPos goalPos;

    public PlayerWalker(ClientPlayerEntity player) {
        System.out.println("Player walker created!");
        bplacer = new BlockPlacer(player);
    }

    public void tick() {
        System.out.println("[PlayerWalker]: tick()");
        if (this.isActive && !this.path.isEmpty() && posIter != null) {
            if (posIter.hasNext()) {
//                if(client.player.getBlockPos().equals(goalPos)){
                if (client.player.getPos().distanceTo(goalPos.toCenterPos()) < client.interactionManager.getReachDistance() - 1) {
                    this.toggle();
                    bplacer.selectBlock(goalState.getBlock().asItem());
                    bplacer.placeBlock(path.get(path.size() - 1));
                    return;
                }
                if (client.player.getBlockPos().equals(nextPos))
                    nextPos = posIter.next();
                if (nextPos != null)
                    goToBlockPos(nextPos);
                else
                    System.out.println("Huh?!?!?! next pos is null");
            } else {
                System.out.println("No more nextPos left");
                toggle();
            }
        }
    }

    public boolean isActive() {
        return this.isActive;
    }

    public void toggle() {
        System.out.println("toggle walker");
        this.isActive = !this.isActive;
    }

    public void walkPath(List<BlockPos> path, BlockPos toPlace, BlockState bS) {
        if (!this.isActive) {
            this.goalState = bS;
            this.goalPos = toPlace;
            this.path = new ArrayList<>(path);
            posIter = this.path.iterator();
            if (posIter.hasNext())
                nextPos = posIter.next();
        }
    }

    public void goToBlockPos(BlockPos bP) {
        System.out.println("Going to: " + bP.toString());
        Vec3d player = client.player.getPos();
        Vec3d pos = bP.toCenterPos().add(player.negate());
        System.out.println("Trying to go from playerPos: " + player.toString() + " to next BlockPos: " + bP.toShortString());
        client.player.move(MovementType.SELF, pos.normalize());
    }
}