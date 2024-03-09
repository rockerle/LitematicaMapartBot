package net.rockerle.mapbot.mapbot.client.playeractions;

import fi.dy.masa.litematica.data.DataManager;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.rockerle.mapbot.mapbot.client.MapbotClient;

public class BlockPlacer {

    ClientPlayerEntity player;
    ClientPlayNetworkHandler networkHandler;
    ClientPlayerInteractionManager interactionManager;

    public BlockPlacer(ClientPlayerEntity p) {
        this.player = p;
        this.networkHandler = p.networkHandler;
        this.interactionManager = MapbotClient.mc.interactionManager;
    }

    public boolean selectBlock(Item item) {
        PlayerInventory pInv = player.getInventory();
        for (int i = 0; i < pInv.size(); i++) {
            if (pInv.getStack(i).isOf(item)) {
                if (i < 9) {
                    pInv.selectedSlot = i;
                    return true;
                } else {
                    interactionManager.pickFromInventory(i);
                    pInv.selectedSlot = pInv.indexOf(item.getDefaultStack());
                    return true;
                }
            }
        }
        return false;
    }

    public void placeBlock(BlockPos bP) {
        interactionManager.interactBlock(player, Hand.MAIN_HAND, new BlockHitResult(bP.toCenterPos(), Direction.DOWN, bP, true));
        DataManager.getSchematicPlacementManager().getSelectedSchematicPlacement().getSchematicVerifier().markBlockChanged(bP);
        MapbotClient.runningBot = true;
    }
}