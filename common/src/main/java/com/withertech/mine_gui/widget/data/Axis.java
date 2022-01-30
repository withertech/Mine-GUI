/*
 * Mine GUI
 * Copyright (C) 2022 WitherTech
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.withertech.mine_gui.widget.data;

public enum Axis
{
	HORIZONTAL,
	VERTICAL;

	/**
	 * Chooses a value based on this axis.
	 *
	 * @param horizontal the value returned if this axis is horizontal
	 * @param vertical   the value returned if this axis is vertical
	 * @param <T>        the type of the value
	 * @return the corresponding value for this axis
	 */
	public <T> T choose(T horizontal, T vertical)
	{
		return this == HORIZONTAL ? horizontal : vertical;
	}
}
