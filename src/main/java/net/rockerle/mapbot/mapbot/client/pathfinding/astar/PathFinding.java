package net.rockerle.mapbot.mapbot.client.pathfinding.astar;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.rockerle.mapbot.mapbot.client.MapbotClient;

import java.util.LinkedList;

public class PathFinding {

    private PathResult path;
    private final PathFoundListener callback;
    private final MinecraftClient mc;

    public PathFinding(PathFoundListener listener, BlockPos walkTo) {
        this.path = null;
        this.callback = listener;
        this.mc = MapbotClient.mc;

        getJavaPath(walkTo);
    }

    public void checkResults() {
        if (this.path == null)
            return;

        if (this.callback != null)
            this.callback.onPathFound(this.path);
    }

    public void getJavaPath(BlockPos walkTo) {
        new Thread(() -> {
            long start = System.currentTimeMillis();
            System.out.println("Starting path calculation at " + start);
            LinkedList<BlockPos> points = new LinkedList<>();
            try {
                if (mc.player == null) {
                    throw new NullPointerException("mc player null");
                }
                points = new AStar(mc).findPath(mc.player.getBlockPos(), walkTo);
            } catch (NoPathFoundException e) {
                mc.player.sendMessage(Text.of("Could not find a Path from " +
                        mc.player.getBlockPos().toString() +
                        " to the desired " +
                        "destination XYZ: " + walkTo.getX() + " / " + walkTo.getY() + " / " + walkTo.getZ() + "! "));
            } catch (Exception e) {
                if (mc.player != null) {
                    mc.player.sendMessage(Text.of("Pathfinding went horribly wrong! " +
                            "(console contains detailed error)"));
                }
                e.printStackTrace();
            }
            System.out.println("Finished path calculation in " + (System.currentTimeMillis() - start) + "ms");
            this.path = new PathResult(points, System.currentTimeMillis() - start);
            checkResults();
        }).start();
    }
}