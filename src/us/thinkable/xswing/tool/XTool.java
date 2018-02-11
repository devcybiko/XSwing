package us.thinkable.xswing.tool;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;

import us.thinkable.xswing.frame.XFrame;
import us.thinkable.xswing.panel.XPanel;
import us.thinkable.xswing.panel.XTabbedPanel;
import us.thinkable.xswing.zed.XPopupMenu;

@SuppressWarnings("serial")
public class XTool extends XPanel {
	protected XPopupMenu xpopupMenu = null;
	protected MouseListener popupListener = null;

	public XTool(XFrame frame, String title, String menuFname) throws IOException {
		super(frame, title);
		xtabbedPanel = frame.getXTabbedToolPanel();
		frame.addTool(this, title);
		this.setVisible(false);
		if (menuFname != null) {
			xpopupMenu = new XPopupMenu(this, menuFname);
			popupListener = new PopupListener();
			this.addMouseListener(popupListener);
		}
	}

	//Add listener to components that can bring up popup menus.
	class PopupListener extends MouseAdapter {
		public void mousePressed(MouseEvent e) {
			maybeShowPopup(e);
		}

		public void mouseReleased(MouseEvent e) {
			maybeShowPopup(e);
		}

		private void maybeShowPopup(MouseEvent e) {
			if (e.isPopupTrigger()) {
				xpopupMenu.show(e.getComponent(), e.getX(), e.getY());
			}
		}
	}

	public XTabbedPanel getXTabbedPanel() {
		return xFrame.getXTabbedToolPanel();
	}

	public void dismiss() {
		System.out.println("Dismiss this!");
	}

	public XPopupMenu getXPopupMenu() {
		return xpopupMenu;
	}

	public void setXPopupMenu(XPopupMenu xpopupMenu) {
		this.xpopupMenu = xpopupMenu;
	}

}
