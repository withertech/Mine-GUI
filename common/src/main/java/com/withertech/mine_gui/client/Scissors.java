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

package com.withertech.mine_gui.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;

import java.util.ArrayDeque;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Contains a stack for GL scissors for restricting the drawn area of a widget.
 *
 * @since 2.0.0
 */
@Environment(EnvType.CLIENT)
public final class Scissors
{
	private static final ArrayDeque<Frame> STACK = new ArrayDeque<>();

	private Scissors()
	{
	}

	/**
	 * Pushes a new scissor frame onto the stack and refreshes the scissored area.
	 *
	 * @param x      the frame's X coordinate
	 * @param y      the frame's Y coordinate
	 * @param width  the frame's width in pixels
	 * @param height the frame's height in pixels
	 * @return the pushed frame
	 */
	public static Frame push(int x, int y, int width, int height)
	{
		Frame frame = new Frame(x, y, width, height);
		STACK.push(frame);
		refreshScissors();

		return frame;
	}

	/**
	 * Pops the topmost scissor frame and refreshes the scissored area.
	 *
	 * @throws IllegalStateException if there are no scissor frames on the stack
	 */
	public static void pop()
	{
		if (STACK.isEmpty())
		{
			throw new IllegalStateException("No scissors on the stack!");
		}

		STACK.pop();
		refreshScissors();
	}

	static void refreshScissors()
	{
		Minecraft mc = Minecraft.getInstance();

		if (STACK.isEmpty())
		{
			// Just use the full window framebuffer as a scissor
			GL11.glScissor(0, 0, mc.getWindow().getWidth(), mc.getWindow().getHeight());
			return;
		}

		int x = Integer.MIN_VALUE;
		int y = Integer.MIN_VALUE;
		int width = -1;
		int height = -1;

		for (Frame frame : STACK)
		{
			if (x < frame.x)
			{
				x = frame.x;
			}
			if (y < frame.y)
			{
				y = frame.y;
			}
			if (width == -1 || x + width > frame.x + frame.width)
			{
				width = frame.width - (x - frame.x);
			}
			if (height == -1 || y + height > frame.y + frame.height)
			{
				height = frame.height - (y - frame.y);
			}
		}

		int windowHeight = mc.getWindow().getHeight();
		double scale = mc.getWindow().getGuiScale();
		int scaledWidth = (int) (width * scale);
		int scaledHeight = (int) (height * scale);

		// Expression for Y coordinate adapted from vini2003's Spinnery (code snippet released under WTFPL)
		GL11.glScissor((int) (x * scale), (int) (windowHeight - (y * scale) - scaledHeight), scaledWidth, scaledHeight);
	}

	/**
	 * Internal method. Throws an {@link IllegalStateException} if the scissor stack is not empty.
	 */
	static void checkStackIsEmpty()
	{
		if (!STACK.isEmpty())
		{
			throw new IllegalStateException("Unpopped scissor frames: " + STACK.stream().map(Frame::toString).collect(Collectors.joining(", ")));
		}
	}

	/**
	 * A single scissor frame in the stack.
	 */
	public static final class Frame implements AutoCloseable
	{
		private final int x;
		private final int y;
		private final int width;
		private final int height;

		private Frame(int x, int y, int width, int height)
		{
			if (width < 0) throw new IllegalArgumentException("Negative width for a stack frame");
			if (height < 0) throw new IllegalArgumentException("Negative height for a stack frame");

			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
		}

		/**
		 * Pops this frame from the stack.
		 *
		 * @throws IllegalStateException if: <ul>
		 *                               <li>this frame is not on the stack, or</li>
		 *                               <li>this frame is not the topmost element on the stack</li>
		 *                               </ul>
		 * @see Scissors#pop()
		 */
		@Override
		public void close()
		{
			if (STACK.peekLast() != this)
			{
				if (STACK.contains(this))
				{
					throw new IllegalStateException(this + " is not on top of the stack!");
				} else
				{
					throw new IllegalStateException(this + " is not on the stack!");
				}
			}

			pop();
		}

		@Override
		public boolean equals(Object o)
		{
			if (this == o) return true;
			if (!(o instanceof Frame frame)) return false;
			return x == frame.x && y == frame.y && width == frame.width && height == frame.height;
		}

		@Override
		public int hashCode()
		{
			return Objects.hash(x, y, width, height);
		}

		@Override
		public String toString()
		{
			return "Frame{" +
					"x=" + x +
					", y=" + y +
					", width=" + width +
					", height=" + height +
					'}';
		}
	}
}
