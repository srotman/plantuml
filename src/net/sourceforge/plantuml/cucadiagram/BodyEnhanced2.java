/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2013, Arnaud Roques
 *
 * Project Info:  http://plantuml.sourceforge.net
 * 
 * This file is part of PlantUML.
 *
 * PlantUML is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PlantUML distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public
 * License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301,
 * USA.
 *
 * [Java is a trademark or registered trademark of Sun Microsystems, Inc.
 * in the United States and other countries.]
 *
 * Original Author:  Arnaud Roques
 * 
 * Revision $Revision: 7637 $
 *
 */
package net.sourceforge.plantuml.cucadiagram;

import java.awt.geom.Dimension2D;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.plantuml.FontParam;
import net.sourceforge.plantuml.SpriteContainer;
import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.graphic.HorizontalAlignment;
import net.sourceforge.plantuml.graphic.HtmlColor;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.graphic.TextBlockLineBefore;
import net.sourceforge.plantuml.graphic.TextBlockUtils;
import net.sourceforge.plantuml.graphic.TextBlockVertical2;
import net.sourceforge.plantuml.ugraphic.UFont;
import net.sourceforge.plantuml.ugraphic.UGraphic;

public class BodyEnhanced2 implements TextBlock {

	private TextBlock area2;
	private final FontConfiguration titleConfig;
	private final Display rawBody2;
	private final SpriteContainer spriteContainer;

	private final HorizontalAlignment align;

	// private final List<Url> urls = new ArrayList<Url>();

	public BodyEnhanced2(Display rawBody, FontParam fontParam, SpriteContainer spriteContainer,
			HorizontalAlignment align, UFont fontNote, HtmlColor fontColor) {
		this.rawBody2 = rawBody;
		// this.fontParam = fontParam;
		this.spriteContainer = spriteContainer;

		this.titleConfig = new FontConfiguration(fontNote, fontColor);
		this.align = align;
	}

	private TextBlock decorate(StringBounder stringBounder, TextBlock b, char separator, TextBlock title) {
		if (separator == 0) {
			return b;
		}
		if (title == null) {
			return new TextBlockLineBefore(TextBlockUtils.withMargin(b, 0, 4), separator);
		}
		final Dimension2D dimTitle = title.calculateDimension(stringBounder);
		final TextBlock raw = new TextBlockLineBefore(TextBlockUtils.withMargin(b, 0, 6, dimTitle.getHeight() / 2, 4),
				separator, title);
		return TextBlockUtils.withMargin(raw, 0, 0, dimTitle.getHeight() / 2, 0);
	}

	public Dimension2D calculateDimension(StringBounder stringBounder) {
		return getArea(stringBounder).calculateDimension(stringBounder);
	}

	private TextBlock getArea(StringBounder stringBounder) {
		if (area2 != null) {
			return area2;
		}
		// urls.clear();
		final List<TextBlock> blocks = new ArrayList<TextBlock>();

		char separator = 0;
		TextBlock title = null;
		Display members2 = new Display();
		for (CharSequence s : rawBody2) {
			if (isBlockSeparator(s.toString())) {
				blocks.add(decorate(stringBounder, getTextBlock(members2, stringBounder), separator, title));
				separator = s.charAt(0);
				title = getTitle(s.toString(), spriteContainer);
				members2 = new Display();
			} else {
				members2 = members2.add(s);
			}
		}
		blocks.add(decorate(stringBounder, getTextBlock(members2, stringBounder), separator, title));

		if (blocks.size() == 1) {
			this.area2 = blocks.get(0);
		} else {
			this.area2 = new TextBlockVertical2(blocks, align);
		}

		return area2;
	}

	private TextBlock getTextBlock(Display members2, StringBounder stringBounder) {
		final TextBlock result = TextBlockUtils.create(members2, titleConfig, align, spriteContainer);
		return result;
	}

	public static boolean isBlockSeparator(String s) {
		if (s.startsWith("--") && s.endsWith("--")) {
			return true;
		}
		if (s.startsWith("==") && s.endsWith("==")) {
			return true;
		}
		if (s.startsWith("..") && s.endsWith("..")) {
			return true;
		}
		if (s.startsWith("__") && s.endsWith("__")) {
			return true;
		}
		return false;
	}

	private TextBlock getTitle(String s, SpriteContainer spriteContainer) {
		if (s.length() <= 4) {
			return null;
		}
		s = s.substring(2, s.length() - 2).trim();
		return TextBlockUtils
				.create(Display.getWithNewlines(s), titleConfig, HorizontalAlignment.LEFT, spriteContainer);
	}

	public void drawU(UGraphic ug) {
		getArea(ug.getStringBounder()).drawU(ug);
	}

}
