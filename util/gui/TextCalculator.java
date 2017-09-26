package util.gui;

import java.awt.Font;
import java.awt.Rectangle;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.StringTokenizer;

import javax.swing.JLabel;

import util.Log;
import util.Util;
import util.Util.StrippedHTMLString;

/*
 * TextCalculator.java - created on 16.11.2003 by J-Domain
 * 
 * @author Michaela Behling
 */

public final class TextCalculator {

	private static final String SEPARATORS = " ,.;:!?\n\t";
	public static final int DEFAULT_TAB_WIDTH = 2;

	private final String text;
	private final int availableWidth;
	private final Font font;
	private double maxLineHeight;
	private String[] textLines = null;
	private final FontRenderContext renderContext;
	private final int tabWidth;

	public TextCalculator(
		int width,
		String aText,
		Font aFont,
		boolean antialiased) {
		this(width, aText, aFont, antialiased, DEFAULT_TAB_WIDTH);
	}

	public TextCalculator(
		int width,
		String aText,
		Font aFont,
		boolean antialiased,
		int tabSize) {
		text = aText;
		availableWidth = width;
		font = aFont;
		renderContext = new FontRenderContext(null, antialiased, true);
		Rectangle2D r = font.getMaxCharBounds(renderContext);
		tabWidth = tabSize * ((int) Math.round(r.getWidth() + 0.5));
		calculate();
	}

	public LineMetrics getMetrics() {
		return font.getLineMetrics("DefaultText", renderContext);
	}

	public static Rectangle getStringWidth(
		String aText,
		Font f,
		boolean antiAliased) {
		FontRenderContext rc = new FontRenderContext(null, antiAliased, true);
		Rectangle2D r2d = f.getStringBounds(aText, rc);
		Rectangle r = new Rectangle();
		r.x = (int) Math.round(r2d.getX());
		r.y = (int) Math.round(r2d.getY());
		r.width = (int) Math.round(r2d.getWidth());
		r.height = (int) Math.round(r2d.getHeight());
		return r;
	}

	public String[] getLines() {
		return textLines;
	}

	public JLabel createLabel() {
		StringBuffer labelText = new StringBuffer("<html>");
		final int lines = getLineCount();
		for (int i = 0; i < lines; i++) {
			if (i > 0) {
				labelText.append("<br>");
			}
			labelText.append(textLines[i]);
		}
		labelText.append("</html>");
		return new JLabel(labelText.toString());
	}

	public String getText() {
		StringBuffer buffer = new StringBuffer();
		final int lines = getLineCount();
		for (int i = 0; i < lines; i++) {
			buffer.append(textLines[i]);
		}
		return buffer.toString();
	}

	public int getTextHeight() {
		final int lineHeight = getLineHeight();
		final int lineCount = textLines.length;

		return lineHeight * lineCount;
	}

	public int getLineHeight() {
		final float lineHeight =
			(float) font.getStringBounds("\n", renderContext).getHeight();
		LineMetrics metrics = font.getLineMetrics("\n", renderContext);
		final float leading = metrics.getLeading();

		return (int) Math.round(lineHeight + leading + 0.5f);
	}

	public int getLineCount() {
		return textLines.length;
	}

	private void calculate() {
		calculateTextLines();

		if (Log.DEBUG) {
			Log.debug("Text broken into:");
			for (int i = 0; i < textLines.length; i++) {
				Log.debug("'" + textLines[i] + "'");
			}
		}
	}

	public int getTextWidth() {
		final int max = textLines.length;
		int w = 0;
		for (int i = 0; i < max; i++) {
			int width =
				(int) font.getStringBounds(textLines[i], renderContext).getWidth();
			if (width > w) {
				w = width;
			}
		}
		return w;
	}

	private void calculateTextLines() {
		ArrayList lines = new ArrayList();
		StringBuffer buf = new StringBuffer();		
		StringTokenizer st = new StringTokenizer(text, SEPARATORS, true);
		String token;
		Rectangle2D bounds, tokenBounds;
		double width = 0;

		StrippedHTMLString noHTML;
		StrippedHTMLString tokenHTML;

		while (st.hasMoreElements()) {
			token = st.nextToken();
			if (token.charAt(0) == '\n') { // manual line break
				lines.add(buf.toString());
				buf = new StringBuffer();
				width = 0;
			}
			else if (token.charAt(0) == '\t') {
				buf.append(token);
				width = width + tabWidth;
			}
			else {
				noHTML = Util.stripHTMLCode(buf.toString() + token);
				tokenHTML = Util.stripHTMLCode(token);
				bounds = font.getStringBounds(noHTML.stripped, renderContext);
				tokenBounds =
					font.getStringBounds(tokenHTML.stripped, renderContext);
				double tokenWidth = bounds.getWidth() - width;
				if ((availableWidth==0) || ((width + tokenWidth) <= availableWidth)) {
					buf.append(token);
					width = width + tokenWidth;
				}
				else {
					lines.add(buf.toString());
					buf = new StringBuffer();
					if (tokenHTML.endsInHTML) {
						buf.append(tokenHTML.trailingHTML);
					}
					buf.append(token);
					width = tokenBounds.getWidth();
				}
			}
		}
		if (buf.length() != 0) {
			lines.add(buf.toString());
		}

		final int size = lines.size();
		textLines = new String[size];
		lines.toArray(textLines);
	}

}
