package us.thinkable.xswing.editor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.io.IOException;

import javax.swing.JSplitPane;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;

import us.thinkable.xswing.frame.XFrame;

@SuppressWarnings("serial")
public class XSplitEditor extends XEditor {
	private JSplitPane jsplitPane = null;
	private XEditor topEditor = null;
	private XEditor bottomEditor = null;

	public XSplitEditor(XFrame frame, String title, String menuFname, XEditor top, XEditor bottom) throws IOException {
		super(frame, title, menuFname);
		this.setLayout(new GridLayout(1, 1));
		jsplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, topEditor, bottomEditor);
		this.add(jsplitPane, BorderLayout.CENTER);
		this.setTopEditor(top);
		this.setBottomEditor(bottom);
		this.setDividerColor(Color.gray);
	}

	private void setDividerColor(final Color color) {
		jsplitPane.setUI(new BasicSplitPaneUI() {
			public BasicSplitPaneDivider createDefaultDivider() {
				return new BasicSplitPaneDivider(this) {
					public void setBorder(Border b) {
					}

					@Override
					public void paint(Graphics g) {
						g.setColor(color);
						g.fillRect(0, 0, getSize().width, getSize().height);
						super.paint(g);
					}
				};
			}
		});
		//jsplitPane.setBorder(null);
	}

	public void setTopEditor(XEditor editor) {
		this.topEditor = editor;
		jsplitPane.setTopComponent(topEditor);
		jsplitPane.setBottomComponent(bottomEditor);
		if (editor != null) {
			editor.setSplitEditor(this);
			editor.setAltParent(this.getXFrame());
		}
	}

	public void setBottomEditor(XEditor editor) {
		this.bottomEditor = editor;
		jsplitPane.setTopComponent(topEditor);
		jsplitPane.setBottomComponent(bottomEditor);
		if (editor != null) {
			editor.setSplitEditor(this);
			editor.setAltParent(this.getXFrame());
		}
	}

	public void setDividerLocation(final double splitDividerPercentage) {
		new Thread() {
			public void run() {
				try {
					Thread.sleep(100);
					jsplitPane.setDividerLocation(splitDividerPercentage);
					jsplitPane.revalidate();
				} catch (Exception exc) {
					System.out.println(exc);
				}
			}
		}.start();
	}

	public XEditor getTopEditor() {
		return topEditor;
	}

	public XEditor getBottomEditor() {
		return bottomEditor;
	}

	public void setDividerLocation(int n) {
		jsplitPane.setDividerLocation(n);
	}

	public int getDividerLocation() {
		return jsplitPane.getDividerLocation();
	}

}
