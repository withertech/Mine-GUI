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

package com.withertech.mine_gui;

import net.minecraft.world.inventory.ContainerData;

/**
 * This interface can be implemented on block entity classes
 * for providing a property delegate.
 *
 * @see SyncedGuiDescription#getBlockPropertyDelegate(net.minecraft.world.inventory.ContainerLevelAccess)
 */
public interface PropertyDelegateHolder
{
	/**
	 * Gets this block entity's property delegate.
	 *
	 * <p>On the client, the returned property delegate <b>must</b> have a working implementation of
	 * {@link ContainerData#set(int, int)}.
	 *
	 * @return the property delegate
	 */
	ContainerData getPropertyDelegate();
}
