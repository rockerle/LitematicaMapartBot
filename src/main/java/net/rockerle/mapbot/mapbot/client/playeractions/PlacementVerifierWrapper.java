package net.rockerle.mapbot.mapbot.client.playeractions;

import fi.dy.masa.litematica.schematic.placement.SchematicPlacement;
import fi.dy.masa.litematica.schematic.verifier.SchematicVerifier;
import fi.dy.masa.litematica.world.SchematicWorldHandler;
import fi.dy.masa.malilib.interfaces.ICompletionListener;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.rockerle.mapbot.mapbot.client.MapbotClient;
import net.rockerle.mapbot.mapbot.client.pathfinding.astar.PathFinding;
import net.rockerle.mapbot.mapbot.client.pathfinding.astar.PathFoundListener;
import net.rockerle.mapbot.mapbot.client.pathfinding.astar.PathResult;
import net.rockerle.mapbot.mapbot.client.rendering.OutlineRenderer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class PlacementVerifierWrapper {

    OutlineRenderer oR = new OutlineRenderer(MapbotClient.mc);
    SchematicVerifier verifier;
    SchematicPlacement placement;
    List<BlockPos> awailableBlocks = new ArrayList<>();
    PlayerWalker pw;

    public PlacementVerifierWrapper(PlayerWalker pw) {
        this.pw = pw;
    }

    public void verify(SchematicPlacement placement) {
        oR.clearRenderedPath();
        this.placement = placement;
        this.verifier = placement.getSchematicVerifier();
        this.verifier.resetIgnoredStateMismatches();
        this.verifier.startVerification(
                MapbotClient.mc.world,
                SchematicWorldHandler.getSchematicWorld(),
                this.placement,
                new PlacementVerifierCompletionListenerImpl(oR, this.verifier, this)
        );
    }

    public boolean schematicFinished() {
        if (this.verifier == null) {
            return false;
        }
        int leftOver = this.verifier.getMissingBlocks();
        if(leftOver<1)
            MapbotClient.runningBot=false;
        return leftOver < 1 && this.verifier.isFinished();
    }

    class PlacementVerifierCompletionListenerImpl implements ICompletionListener {
        Predicate<SchematicVerifier.BlockMismatch> inventoryFilter;
        SchematicVerifier ver;
        PlayerInventory inv;
        PlacementVerifierWrapper wrapper;
        OutlineRenderer oR;

        public PlacementVerifierCompletionListenerImpl(OutlineRenderer oR, SchematicVerifier sv, PlacementVerifierWrapper pvw) {
            this.ver = sv;
            this.inv = MapbotClient.mc.player.getInventory();
            this.wrapper = pvw;
            this.oR = oR;
            this.inventoryFilter = bM -> !(inv.contains(bM.stateExpected.getBlock().asItem().getDefaultStack()));
        }

        @Override
        public void onTaskCompleted() {
            class PathRunnable implements Runnable, PathFoundListener {
                BlockPos bP;
                BlockState goalState;

                public PathRunnable(BlockPos goal, BlockState bS) {
                    this.bP = goal;
                    this.goalState = bS;
                }

                @Override
                public void run() {
                    new PathFinding(this::onPathFound, bP);
                }

                @Override
                public void onPathFound(PathResult p) {
                    oR.clearRenderedPath();
                    if (!p.path().isEmpty()) {
                        oR.visualizePath(p.path());
                        pw.walkPath(p.path(), bP, goalState);
                        pw.toggle();
                    }
                }
            }
            List<SchematicVerifier.BlockMismatch> missmatches = this.ver.getMismatchOverviewFor(SchematicVerifier.MismatchType.MISSING).stream().filter(inventoryFilter).toList();
            this.ver.addIgnoredStateMismatches(missmatches);
            this.ver.toggleMismatchCategorySelected(SchematicVerifier.MismatchType.MISSING);
            this.wrapper.awailableBlocks = this.ver.getSelectedMismatchBlockPositionsForRender();
            if(!this.wrapper.awailableBlocks.isEmpty()){
                MapbotClient.mc.options.getAutoJump().setValue(true);
                BlockPos pos = this.ver.getSelectedMismatchBlockPositionsForRender().get(0);
                new Thread(new PathRunnable(pos, SchematicWorldHandler.getSchematicWorld().getBlockState(pos))).start();
            } else {
                MapbotClient.mc.options.getAutoJump().setValue(false);
                System.out.println("No SelectedMismatchBlockPos in list! Stoping building");
            }
        }
    }
}