/*
 * Copyright (c) 2023 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wimods.chestesp;

import me.shedaniel.autoconfig.ConfigHolder;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.wimods.chestesp.util.BlockUtils;
import net.mrquba.data.Pair;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;


public final class ChestEspNormalBlockGroup extends ChestEspGroup
{
	public ChestEspNormalBlockGroup(ConfigHolder<ChestEspConfig> configHolder,
                                    ToIntFunction<ChestEspConfig> color, Predicate<ChestEspConfig> enabled)
	{
		super(configHolder, color, enabled);
	}

	public void add(Pair<Block, BlockPos> p)
	{
		Box box = getBox(p.second);
		if(box == null)
			return;

		boxes.add(box);
	}

	private Box getBox(BlockPos pos)
	{
		if(!BlockUtils.canBeClicked(pos))
			return null;
		return BlockUtils.getBoundingBox(pos);
	}


}
