/*
 * Copyright (c) 2023 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wimods.chestesp;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.EnumHandler.EnumDisplayOption;
import net.minecraft.util.math.Vec3d;

@Config(name = "chestesp")
public final class ChestEspConfig implements ConfigData
{
	@ConfigEntry.Gui.Tooltip
	public boolean enable = false;
	public int min_height = -64;
	public int max_height = 320;
	@ConfigEntry.Gui.EnumHandler(option = EnumDisplayOption.BUTTON)
	@ConfigEntry.Gui.Tooltip
	public ChestEspStyle style = ChestEspStyle.BOXES;
	
	@ConfigEntry.ColorPicker
	@ConfigEntry.Gui.Tooltip
	public int chest_color = 0x00FF00;
	
	public boolean include_trap_chests = true;
	
	@ConfigEntry.ColorPicker
	@ConfigEntry.Gui.Tooltip
	public int trap_chest_color = 0xFF8000;
	
	public boolean include_ender_chests = true;
	
	@ConfigEntry.ColorPicker
	@ConfigEntry.Gui.Tooltip
	public int ender_chest_color = 0x00FFFF;
	
	public boolean include_chest_carts = true;
	
	@ConfigEntry.ColorPicker
	@ConfigEntry.Gui.Tooltip
	public int chest_cart_color = 0xFFFF00;
	
	public boolean include_chest_boats = true;
	
	@ConfigEntry.ColorPicker
	@ConfigEntry.Gui.Tooltip
	public int chest_boat_color = 0xFFFF00;
	
	public boolean include_barrels = true;
	
	@ConfigEntry.ColorPicker
	@ConfigEntry.Gui.Tooltip
	public int barrel_color = 0x00FF00;
	
	public boolean include_shulker_boxes = true;
	
	@ConfigEntry.ColorPicker
	@ConfigEntry.Gui.Tooltip
	public int shulker_box_color = 0xFF00FF;
	
	public boolean include_hoppers = true;
	
	@ConfigEntry.ColorPicker
	@ConfigEntry.Gui.Tooltip
	public int hopper_color = 0xFFFFFF;
	
	public boolean include_hopper_carts = true;
	
	@ConfigEntry.ColorPicker
	@ConfigEntry.Gui.Tooltip
	public int hopper_cart_color = 0xFFFF00;
	public boolean include_wardens = true;

	@ConfigEntry.ColorPicker
	@ConfigEntry.Gui.Tooltip
	public int warden_color = 0xFFFF00;
	
	public boolean include_droppers = true;
	
	@ConfigEntry.ColorPicker
	@ConfigEntry.Gui.Tooltip
	public int dropper_color = 0xFFFFFF;
	
	public boolean include_dispensers = true;
	
	@ConfigEntry.ColorPicker
	@ConfigEntry.Gui.Tooltip
	public int dispenser_color = 0xFF8000;
	
	public boolean include_furnaces = true;
	
	@ConfigEntry.ColorPicker
	@ConfigEntry.Gui.Tooltip
	public int furnace_color = 0xFF0000;
	public boolean include_sculk = true;
	@ConfigEntry.ColorPicker
	@ConfigEntry.Gui.Tooltip
	public int sculk_color = 0xFF0000;
	public boolean include_spawner = true;
	@ConfigEntry.ColorPicker
	@ConfigEntry.Gui.Tooltip
	public int spawner_color = 0xFF0000;
    public boolean include_obsidian = true;
	@ConfigEntry.ColorPicker
	@ConfigEntry.Gui.Tooltip
	public int obsidian_color = 0xFF0000;
	public boolean include_deepslate = true;
	@ConfigEntry.ColorPicker
	@ConfigEntry.Gui.Tooltip
	public int deepslate_color = 0xFF0000;
	public boolean include_frames = true;

	@ConfigEntry.ColorPicker
	@ConfigEntry.Gui.Tooltip
	public int frame_color = 0xFF0000;
}
