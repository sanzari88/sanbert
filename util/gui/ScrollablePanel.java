package util.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.Rectangle;

import javax.swing.JPanel;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;

/*
 * ScrollablePanel.java - created on 04.11.2003 by J-Domain
 * 
 * @author Michaela Behling
 */

public class ScrollablePanel extends JPanel implements Scrollable {

	private int unitWidth;
	private int unitHeight;
	private int blockWidth;
	private int blockHeight;

	private boolean forceWidth = false;
	private boolean forceHeight = false;

	/** Erzeugt ein scrollbares Panel.
		 * @param manager Ein Layout-Manager
		 * @param _unitWidth Länge einer Einheit in Pixeln.
		 * @param _unitHeight Höhe einer Einheit in Pixeln.
		 * @param _blockWidth Länge eines Blocks in Einheiten.
		 * @param _blockWidth Höhe eines Blocks in Einheiten.	
		 */
	public ScrollablePanel(LayoutManager manager, int unitSize, int blockSize) {
		this(manager, unitSize, unitSize, blockSize, blockSize);
	}

	/** Erzeugt ein scrollbares Panel.
	 * @param manager Ein Layout-Manager
	 * @param _unitWidth Länge einer Einheit in Pixeln.
	 * @param _unitHeight Höhe einer Einheit in Pixeln.
	 * @param _blockWidth Länge eines Blocks in Einheiten.
	 * @param _blockWidth Höhe eines Blocks in Einheiten.	
	 */
	public ScrollablePanel(
		LayoutManager manager,
		int aUnitWidth,
		int aUnitHeight,
		int aBlockWidth,
		int aBlockHeight) {
		super(manager);
		unitWidth = aUnitWidth;
		unitHeight = aUnitHeight;
		blockWidth = aBlockWidth;
		blockHeight = aBlockHeight;
	}

	/** Erzeugt ein scrollbares Panel mit BorderLayout.
		 * @param unitSize Länge und Höhe einer Einheit in Pixeln.		 
		 * @param blockSize Länge und Höhe eines Blocks in Einheiten.		 
		 */
	public ScrollablePanel(int unitSize, int blockSize) {
		this(new BorderLayout(0, 0), unitSize, unitSize, blockSize, blockSize);
	}

	public final Dimension getPreferredScrollableViewportSize() {
		return getPreferredSize();
	}

	public final int getScrollableUnitIncrement(
		Rectangle visibleRect,
		int orientation,
		int direction) {
		if (orientation == SwingConstants.VERTICAL) { // up/down
			return unitHeight;
		}
		else { // left/right
			return unitWidth;
		}
	}

	public final int getScrollableBlockIncrement(
		Rectangle visibleRect,
		int orientation,
		int direction) {
		if (orientation == SwingConstants.VERTICAL) { // up/down
			return blockHeight * unitHeight;
		}
		else { // left/right
			return blockWidth * unitWidth;
		}
	}

	public final boolean getScrollableTracksViewportHeight() {
		return forceHeight;
	}

	public final boolean getScrollableTracksViewportWidth() {
		return forceWidth;
	}

	public final void setBlockHeight(int i) {
		blockHeight = i;
		update();
	}

	public final void setBlockWidth(int i) {
		blockWidth = i;
		update();
	}

	public final void setUnitHeight(int i) {
		unitHeight = i;
		update();
	}

	public final void setUnitWith(int i) {
		unitWidth = i;
		update();
	}

	public final void setForceHeight(boolean b) {
		forceHeight = b;
		update();
	}

	public final void setForceWidth(boolean b) {
		forceWidth = b;
		update();
	}

	private void update() {
		invalidate();
	}

}
