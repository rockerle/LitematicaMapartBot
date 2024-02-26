package net.rockerle.mapbot.mapbot.client.pathfinding.astar;

import net.minecraft.util.math.BlockPos;
import net.rockerle.mapbot.mapbot.client.pathfinding.astar.util.McInstanceUtilException;

import java.util.LinkedList;

public interface PathFinder {
    LinkedList<BlockPos> findPath(BlockPos start, BlockPos end) throws NoPathFoundException, McInstanceUtilException;
}