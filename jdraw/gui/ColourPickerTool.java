package jdraw.gui;

import java.awt.Point;

import util.Assert;

/*
 * Created on 29-Oct-2003
 *
 * @author michaela
 */

public final class ColourPickerTool extends Tool {

	public void clicked(int button, Point p) {
		if (p != null) {
			int col = getCurrentFrame().getPixel(p.x, p.y);
			switch (button) {
				case LEFT_BUTTON :
					Tool.getPicture().setForeground(col);
					break;
				case RIGHT_BUTTON :
					Tool.getPicture().setBackground(col);
					break;
				default :
					Assert.fail("gui: unknown button " + button);
			}
		}
	}

}
