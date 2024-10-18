package net.rockerle.mapbot.mapbot.client.pathfinding.astar;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.rockerle.mapbot.mapbot.client.MapbotClient;
import net.rockerle.mapbot.mapbot.client.pathfinding.astar.util.BlockPosUtil;
import net.rockerle.mapbot.mapbot.client.pathfinding.astar.util.McInstanceUtilException;
import net.rockerle.mapbot.mapbot.client.rendering.OutlineRenderer;

import java.util.*;


public class AStar implements PathFinder {

    private OutlineRenderer or;
    private final MinecraftClient mc;

    public AStar(MinecraftClient mc) {
        this.mc = mc;
        this.or = new OutlineRenderer(mc);
    }

    public LinkedList<BlockPos> findPath(BlockPos start, BlockPos end) throws NoPathFoundException {
        if(!or.visCurrent)
            or.visCurrent = true;
//        if (!BlockPosUtil.isTraversable(end)) {
//            //mc.inGameHud.addChatMessage(MessageType.SYSTEM, Text.of("Point is not traversable, try a different position!"), mc.player.getUuid());
////            mc.player.sendMessage(Text.of("Point is not traversable, try a different position!"));
//            throw new NoPathFoundException();
//        }
//
//        if (BlockPosUtil.isUpperTraversable(end)) {
//            System.out.println("going up");
//            end = end.up();
//        } else if (BlockPosUtil.isLowerTraversable(end)) {
//            System.out.println("going down");
//            end = end.down();
//        }

        HashMap<BlockPos, BlockPos> cameFrom = new HashMap<>();
        HashMap<BlockPos, Integer> fScore = new HashMap<>();

        PriorityQueue<BlockPos> openSet = new PriorityQueue<>(Comparator.comparingInt(o ->
                fScore.getOrDefault(o, Integer.MAX_VALUE)));
        HashSet<BlockPos> closedSet = new HashSet<>();

        openSet.add(start);

        // f(x) = g(x) + h(x)
        // g(x) = distance(current, start)
        // h(x) = distance(current, end)
        // at start g(x) is 0 as we are at the starting node
        fScore.put(start, BlockPosUtil.distance(start, end));

        while (!openSet.isEmpty()) {
            BlockPos current = openSet.remove();
            closedSet.add(current);
            if (current.equals(end) || current.toCenterPos().distanceTo(end.toCenterPos())<3.5){
//            For 1.20.5
//            if (current.equals(end) || current.toCenterPos().distanceTo(end.toCenterPos())<mc.player.getBlockInteractionRange()-1) {
                or.visCurrent = false;
                return constructPath(cameFrom, current);
            }

            // we won't move diagonally (moving in two directions on the xz plane) at first
            HashSet<BlockPos> neighbours = new HashSet<>();

            neighbours.add(current.up());
            neighbours.add(current.down());
            neighbours.add(current.east());
            neighbours.add(current.west());
            neighbours.add(current.north());
            neighbours.add(current.south());
            for (BlockPos neighbour : neighbours) {
                if (!BlockPosUtil.isTraversable(neighbour) || closedSet.contains(neighbour)) {
                    continue;
                }

                if (BlockPosUtil.isUpperTraversable(neighbour)) {
                    closedSet.add(neighbour);
                    neighbour = neighbour.up();
                } else if (BlockPosUtil.isLowerTraversable(neighbour)) {
                    closedSet.add(neighbour);
                    neighbour = neighbour.down();
                }

                if (neighbour.equals(current) || closedSet.contains(neighbour)) {
                    continue;
                }
                LinkedList<BlockPos> currentPath = constructPath(cameFrom,current);
                int fScoreNeighbour = (int)Math.pow(currentPath.size(),2) + BlockPosUtil.distance(neighbour, end);
                if (!openSet.contains(neighbour) || fScoreNeighbour < fScore.getOrDefault(neighbour, Integer.MAX_VALUE)) {
                    fScore.put(neighbour, fScoreNeighbour);
                    cameFrom.put(neighbour, current);
                    if (!openSet.contains(neighbour))
                        openSet.add(neighbour);
                }
            }
        }

        throw new NoPathFoundException();
    }

    private LinkedList<BlockPos> constructPath(HashMap<BlockPos, BlockPos> cameFrom, BlockPos last) {
        LinkedList<BlockPos> path = new LinkedList<>();
        BlockPos current = last;
        path.add(current);
        while (cameFrom.containsKey(current)) {
            current = cameFrom.get(current);
            path.addFirst(current);
        }
        return path;
    }
}