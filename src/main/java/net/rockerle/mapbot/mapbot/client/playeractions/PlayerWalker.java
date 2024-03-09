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
        bplacer = new BlockPlacer(player);
    }

    public void tick() {
        if (this.isActive && !this.path.isEmpty() && posIter != null) {
            if (posIter.hasNext()) {
                if (client.player.getBlockPos().equals(nextPos))
                    nextPos = posIter.next();
                else
                    goToBlockPos(nextPos);
            } else {
                this.toggle();
                bplacer.selectBlock(goalState.getBlock().asItem());
                bplacer.placeBlock(goalPos);
            }
        }
    }

    public boolean isActive() {
        return this.isActive;
    }

    public void toggle() {
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
        Vec3d player = client.player.getBlockPos().toCenterPos();
        Vec3d pos = bP.toCenterPos().add(player.negate());
        client.player.move(MovementType.SELF, pos.normalize());
    }
}