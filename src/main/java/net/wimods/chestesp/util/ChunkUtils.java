/*
 * Copyright (c) 2023 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wimods.chestesp.util;

import me.shedaniel.autoconfig.ConfigHolder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.mrquba.data.BlockInfo;
import net.mrquba.data.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.WorldChunk;
import net.wimods.chestesp.ChestEspConfig;
import org.joml.Vector2i;

import java.util.*;
import java.util.stream.Stream;


public enum ChunkUtils
{
	;
	private static final MinecraftClient MC = MinecraftClient.getInstance();
	public static final BlockInfo block = new BlockInfo();

    public static Stream<BlockEntity> getLoadedBlockEntities(ConfigHolder<ChestEspConfig> configHolder)
	{
		return getLoadedChunks(configHolder)
			.flatMap(chunk -> chunk.getBlockEntities().values().stream());
	}
public static List<Pair<Block, BlockPos>> getBlocksList(ConfigHolder<ChestEspConfig> configHolder) {
	int radius = Math.max(2, MC.options.getClampedViewDistance()*16);
	assert MC.player != null;
	BlockPos min;
	BlockPos max;
	BlockPos center = MC.player.getBlockPos();
	if (!configHolder.get().enable_area) {
		min = new BlockPos(center.getX() - radius, configHolder.get().min_height, center.getZ() - radius);
		max = new BlockPos(center.getX() + radius, configHolder.get().max_height, center.getZ() + radius);

	} else {
		min = new BlockPos(configHolder.get().area_start_x, configHolder.get().min_height, configHolder.get().area_start_z);
		max = new BlockPos(configHolder.get().area_end_x, configHolder.get().max_height, configHolder.get().area_end_z);
	}
	int y = min.getY();
	int x = min.getX();
	int z = min.getZ();

	block.clear();
	// prevention of returning null value
	block.add(Blocks.OBSIDIAN, new BlockPos(1, -60, 1));

		while (x <= max.getX() && z <= max.getZ() && y <= max.getY()) {
			if (MC.world == null) break;

			BlockPos currentPos = new BlockPos(x, y, z);
			BlockState bs = MC.world.getBlockState(currentPos);

			if (bs.getBlock().equals(Blocks.OBSIDIAN) || bs.getBlock().equals(Blocks.CRYING_OBSIDIAN) || bs.getBlock().equals(Blocks.REINFORCED_DEEPSLATE)) {
				block.add(bs.getBlock(), currentPos);
			}

			y++;
			if (y > max.getY()) {
				y = min.getY();
				x++;
			}
			if (x > max.getX()) {
				x = min.getX();
				z++;
			}
		}
		return block.getList();
	}

	public static Stream<WorldChunk> getLoadedChunks(ConfigHolder<ChestEspConfig> configHolder)
	{
		if(configHolder.get().enable_area){
			return searchByArea(configHolder);
		}
		return searchByChunks();
	}
	private static Stream<WorldChunk> searchByArea(ConfigHolder<ChestEspConfig> configHolder){
		Vector2i minBlock = new Vector2i(configHolder.get().area_start_x, configHolder.get().area_start_z);
		Vector2i maxBlock = new Vector2i(configHolder.get().area_end_x, configHolder.get().area_end_z);
		ChunkPos min = new ChunkPos(minBlock.x >> 4, minBlock.y >> 4);
		ChunkPos max = new ChunkPos(maxBlock.x >> 4, maxBlock.y >> 4);
		return Stream.iterate(min, pos -> {

					int x = pos.x;
					int z = pos.z;

					x++;

					if (x > max.x) {
						x = min.x;
						z++;
					}

					if (z > max.z)
						throw new IllegalStateException("Stream limit didn't work.");

					return new ChunkPos(x, z);

				}).limit((long) (max.x - min.x + 1) * (max.z - min.z + 1))
				.filter(c -> {
					assert MC.world != null;
					return MC.world.isChunkLoaded(c.x, c.z);
				}).map(c -> MC.world.getChunk(c.x, c.z)).filter(Objects::nonNull);
	}
	private static Stream<WorldChunk> searchByChunks(){

		int radius = Math.max(2, MC.options.getClampedViewDistance()) + 3;
		int diameter = radius * 2 + 1;
        assert MC.player != null;
        ChunkPos center = MC.player.getChunkPos();
		ChunkPos min = new ChunkPos(center.x - radius, center.z - radius);
		ChunkPos max = new ChunkPos(center.x + radius, center.z + radius);

        return Stream.iterate(min, pos -> {

                    int x = pos.x;
                    int z = pos.z;

                    x++;

                    if(x > max.x)
                    {
                        x = min.x;
                        z++;
                    }

                    if(z > max.z)
                        throw new IllegalStateException("Stream limit didn't work.");

                    return new ChunkPos(x, z);

                }).limit((long) diameter * diameter)
                .filter(c -> {
                    assert MC.world != null;
                    return MC.world.isChunkLoaded(c.x, c.z);
                })
                .map(c -> MC.world.getChunk(c.x, c.z)).filter(Objects::nonNull);
	}
	
}
