package us.thinkable.xswing.frame;

import java.awt.Component;
import java.awt.Container;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.JFrame;

import us.thinkable.xswing.access.XAccess;
import us.thinkable.xswing.editor.XEditor;

@SuppressWarnings("serial")
public class XPopUpFrame extends XFrame {
	private int maxX = 0;
	private int maxY = 0;
	private int curX = 0;
	private int curY = 0;

	public XPopUpFrame(String title, String menuFname) throws IOException {
		super(title);
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		initMenuBar(menuFname, null);
		XFrame.registerXFrame(this);
		this.setVisible(true);
	}

	public XPopUpFrame(String title, String menuFname, XAccess access) throws IOException {
		super(title);
		this.access = access;
		initMenuBar(menuFname, null);
		this.setVisible(true);
	}

	public void resize() {
		maxX = 0;
		maxY = 0;
		curX = 0;
		curY = 0;
		//this.revalidate();
		resize(this);
		Rectangle r = this.getBounds();
		this.setBounds((int) r.getX(), (int) r.getY(), (int) r.getWidth() + maxX, (int) r.getHeight() + maxY);
	}

	private void resize(Container c) {
		int cx = c.getX();
		int cy = c.getY();

		curX = curX + cx;
		curY = curY + cy;
		// System.out.println(c);
		int n = c.getComponentCount();
		for (int i = 0; i < n; i++) {
			Component cc = c.getComponent(i);
			// System.out.println(cc);
			int x = curX + cc.getX() + cc.getWidth();
			int y = curY + cc.getY() + cc.getHeight();
			// System.out.println(cc.getX() + "," + cc.getY() + "," +
			// cc.getWidth() + "," + cc.getHeight());
			if (x > maxX) {
				maxX = x;
				// System.out.println(">" + maxX + "," + maxY);
			}
			if (y > maxY) {
				maxY = y;
				// System.out.println(">" + maxX + "," + maxY);
			}
			if (cc instanceof Container) {
				resize((Container) cc);
			}
		}

		curX = curX - cx;
		curY = curY - cy;
	}

	@Override
	public void addEditor(XEditor xeditor, String title) {
		this.add(xeditor);
	}

	@Override
	public void initAction() {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowClosing(WindowEvent arg0) {
		XFrame.unregisterXFrame(this);
		this.dispose();
	}
}
