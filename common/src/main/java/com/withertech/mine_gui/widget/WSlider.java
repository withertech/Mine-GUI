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

package com.withertech.mine_gui.widget;

import com.mojang.blaze3d.vertex.PoseStack;
import com.withertech.mine_gui.MineGui;
import com.withertech.mine_gui.client.BackgroundPainter;
import com.withertech.mine_gui.client.ScreenDrawing;
import com.withertech.mine_gui.widget.data.Axis;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * A simple slider widget that can be used to select int values.
 *
 * @see WAbstractSlider for supported listeners
 */
public class WSlider extends WAbstractSlider
{
	public static final int TRACK_WIDTH = 6;
	public static final int THUMB_SIZE = 8;
	public static final ResourceLocation LIGHT_TEXTURE = new ResourceLocation(MineGui.MOD_ID, "textures/widget/slider_light.png");
	public static final ResourceLocation DARK_TEXTURE = new ResourceLocation(MineGui.MOD_ID, "textures/widget/slider_dark.png");

	@Environment(EnvType.CLIENT)
	@Nullable
	private BackgroundPainter backgroundPainter = null;

	public WSlider(int min, int max, Axis axis)
	{
		super(min, max, axis);
	}

	@Override
	protected int getThumbWidth()
	{
		return THUMB_SIZE;
	}

	@Override
	protected boolean isMouseInsideBounds(int x, int y)
	{
		// ao = axis-opposite mouse coordinate, aoCenter = center of ao's axis
		int ao = axis == Axis.HORIZONTAL ? y : x;
		int aoCenter = (axis == Axis.HORIZONTAL ? height : width) / 2;

		// Check if cursor is inside or <=2px away from track
		return ao >= aoCenter - TRACK_WIDTH / 2 - 2 && ao <= aoCenter + TRACK_WIDTH / 2 + 2;
	}

	@SuppressWarnings("SuspiciousNameCombination")
	@Environment(EnvType.CLIENT)
	@Override
	public void paint(PoseStack matrices, int x, int y, int mouseX, int mouseY)
	{
		if (backgroundPainter != null)
		{
			backgroundPainter.paintBackground(matrices, x, y, this);
		} else
		{
			float px = 1 / 32f;
			// thumbX/Y: thumb position in widget-space
			int thumbX, thumbY;
			// thumbXOffset: thumb texture x offset in pixels
			int thumbXOffset;
			ResourceLocation texture = MineGui.isDarkMode() ? DARK_TEXTURE : LIGHT_TEXTURE;

			if (axis == Axis.VERTICAL)
			{
				int trackX = x + width / 2 - TRACK_WIDTH / 2;
				thumbX = width / 2 - THUMB_SIZE / 2;
				thumbY = direction == Direction.UP
						? (height - THUMB_SIZE) + 1 - (int) (coordToValueRatio * (value - min))
						: Math.round(coordToValueRatio * (value - min));
				thumbXOffset = 0;

				ScreenDrawing.texturedRect(matrices, trackX, y + 1, TRACK_WIDTH, 1, texture, 16 * px, 0 * px, 22 * px, 1 * px, 0xFFFFFFFF);
				ScreenDrawing.texturedRect(matrices, trackX, y + 2, TRACK_WIDTH, height - 2, texture, 16 * px, 1 * px, 22 * px, 2 * px, 0xFFFFFFFF);
				ScreenDrawing.texturedRect(matrices, trackX, y + height, TRACK_WIDTH, 1, texture, 16 * px, 2 * px, 22 * px, 3 * px, 0xFFFFFFFF);
			} else
			{
				int trackY = y + height / 2 - TRACK_WIDTH / 2;
				thumbX = direction == Direction.LEFT
						? (width - THUMB_SIZE) - (int) (coordToValueRatio * (value - min))
						: Math.round(coordToValueRatio * (value - min));
				thumbY = height / 2 - THUMB_SIZE / 2;
				thumbXOffset = 8;

				ScreenDrawing.texturedRect(matrices, x, trackY, 1, TRACK_WIDTH, texture, 16 * px, 3 * px, 17 * px, 9 * px, 0xFFFFFFFF);
				ScreenDrawing.texturedRect(matrices, x + 1, trackY, width - 2, TRACK_WIDTH, texture, 17 * px, 3 * px, 18 * px, 9 * px, 0xFFFFFFFF);
				ScreenDrawing.texturedRect(matrices, x + width - 1, trackY, 1, TRACK_WIDTH, texture, 18 * px, 3 * px, 19 * px, 9 * px, 0xFFFFFFFF);
			}

			// thumbState values:
			// 0: default, 1: dragging, 2: hovered
			int thumbState = dragging ? 1 : (mouseX >= thumbX && mouseX <= thumbX + THUMB_SIZE && mouseY >= thumbY && mouseY <= thumbY + THUMB_SIZE ? 2 : 0);
			ScreenDrawing.texturedRect(matrices, x + thumbX, y + thumbY, THUMB_SIZE, THUMB_SIZE, texture, thumbXOffset * px, 0 * px + thumbState * 8 * px, (thumbXOffset + 8) * px, 8 * px + thumbState * 8 * px, 0xFFFFFFFF);

			if (thumbState == 0 && isFocused())
			{
				ScreenDrawing.texturedRect(matrices, x + thumbX, y + thumbY, THUMB_SIZE, THUMB_SIZE, texture, 0 * px, 24 * px, 8 * px, 32 * px, 0xFFFFFFFF);
			}
		}
	}

	@Environment(EnvType.CLIENT)
	@Nullable
	public BackgroundPainter getBackgroundPainter()
	{
		return backgroundPainter;
	}

	@Environment(EnvType.CLIENT)
	public void setBackgroundPainter(@Nullable BackgroundPainter backgroundPainter)
	{
		this.backgroundPainter = backgroundPainter;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (!(o instanceof WSlider wSlider)) return false;
		if (!super.equals(o)) return false;
		return Objects.equals(getBackgroundPainter(), wSlider.getBackgroundPainter());
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(super.hashCode(), getBackgroundPainter());
	}

	@Override
	public String toString()
	{
		return "WSlider{" +
				"axis=" + axis +
				", min=" + min +
				", max=" + max +
				", direction=" + direction +
				", value=" + value +
				", dragging=" + dragging +
				", valueToCoordRatio=" + valueToCoordRatio +
				", coordToValueRatio=" + coordToValueRatio +
				", backgroundPainter=" + backgroundPainter +
				", parent=" + parent +
				", x=" + x +
				", y=" + y +
				", width=" + width +
				", height=" + height +
				", host=" + host +
				'}';
	}
}
