package net.mrquba.data;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class BlockInfo {
    private List<Pair<Block, BlockPos>> BlockInfoList = new ArrayList<>();
    public void DataContainer(){
        BlockInfoList = new ArrayList<>();
    }
    public boolean add(Block b, BlockPos pos){
        if(BlockInfoList == null) return  false;
        if(b == null || pos == null) return false;
        BlockInfoList.add(new Pair<>(b, pos));
        return true;
    }
    public Pair<Block, BlockPos> get(int index){
        if(BlockInfoList.isEmpty()) return null;
        if(index < 0 && index >= BlockInfoList.size()) return  null;
        return BlockInfoList.get(index);
    }
    public boolean clear(){
        if(BlockInfoList == null) return false;
        BlockInfoList.clear();
        return  true;
    }
    public List<Pair<Block, BlockPos>> getList(){
        return BlockInfoList;
    }
    public Block getBlock(int index){
        Pair<Block, BlockPos> tempPair = this.get(index);
        return tempPair.getFirst();
    }
    public BlockPos getBlockPos(int index){
        Pair<Block, BlockPos> tempPair = this.get(index);
        return tempPair.getSecond();
    }
    public boolean copy(List<Pair<Block, BlockPos>> bil){
        if(bil == null) return  false;
        BlockInfoList = bil;
        return  true;
    }
    public boolean displayPair(Pair<Block, BlockPos> in) {
        if (in.getFirst() == null || in.getSecond() == null) return false;
        System.out.println("Block: " + in.getFirst() + " Pos" + in.getSecond());
        return  true;
    }
}
