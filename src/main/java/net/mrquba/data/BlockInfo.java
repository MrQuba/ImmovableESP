package net.mrquba.data;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class BlockInfo {
    private List<Pair<Block, BlockPos>> BlockInfoList = new ArrayList<>();
    public void add(Block b, BlockPos pos){
        if(BlockInfoList == null) return;
        if(b == null || pos == null) return;
        BlockInfoList.add(new Pair<>(b, pos));
    }
    public void clear(){
        if(BlockInfoList == null) return;
        BlockInfoList.clear();
    }
    public List<Pair<Block, BlockPos>> getList(){
        return BlockInfoList;
    }
    public void copy(List<Pair<Block, BlockPos>> bil){
        if(bil == null) return;
        BlockInfoList = bil;
    }
}
