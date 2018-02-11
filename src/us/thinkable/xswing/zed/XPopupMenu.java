package us.thinkable.xswing.zed;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;

import us.thinkable.xcore.FileUtil;
import us.thinkable.xswing.util.XNode;

@SuppressWarnings("serial")
public class XPopupMenu extends JPopupMenu {
	ActionListener listener;

	public XPopupMenu(ActionListener listener, String menuFname) throws IOException {
		super();
		this.listener = listener;
		XNode root = readFile(menuFname);
		makeMenu(root, -1);
	}

	private int tabCount(String s) {
		for (int i = 0; i < s.length(); i++) {
			if (s.charAt(i) != '\t') {
				return i;
			}
		}
		return s.length();
	}

	private XNode readFile(String menuFname) throws IOException {
		XNode root = new XNode();
		List<String> lines = FileUtil.fileReadLinesSansComments(menuFname);

		ArrayList<XNode> list = new ArrayList<XNode>();
		list.add(root);

		for (String strLine : lines) {
			int indent = tabCount(strLine);
			XNode child = new XNode(strLine);
			list.get(indent).add(child);
			list.add(indent + 1, child);
		}
		return root;
	}

	private void makeMenu(XNode node, int indent) {
		if (indent > -1) {
			String strLine = (String) node.getUserObject();
			String[] fields = strLine.split("\\t");
			if (node.isLeaf()) {
				JMenu menu = (JMenu) ((XNode) node.getParent()).getUserObject();
				if (fields[indent].startsWith("---")) {
					JSeparator sep = new JSeparator();
					menu.add(sep);
				} else {
					JMenuItem menuItem = new JMenuItem(fields[indent]);
					node.setUserObject(menuItem);
					menu.add(menuItem);
					menuItem.setMnemonic(fields[indent].charAt(0));
					if (fields.length > indent + 1) {
						menuItem.setActionCommand(fields[indent + 1]);
						menuItem.addActionListener(listener);
					}
					if (fields.length > indent + 2) {
						String shortcut = fields[indent + 2].toUpperCase();
						if (shortcut.charAt(0) == '^') {
							menuItem.setAccelerator(KeyStroke.getKeyStroke(shortcut.charAt(1), ActionEvent.CTRL_MASK));
						} else if (shortcut.charAt(0) == '*') {
							menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.ALT_MASK));
							menuItem.addActionListener(listener);
						}
					}
				}
			} else {
				JMenu menu = new JMenu(fields[indent]);
				menu.setMnemonic(fields[indent].charAt(0));
				node.setUserObject(menu);
				if (indent != 0) {
					JMenu parentMenu = (JMenu) ((XNode) node.getParent()).getUserObject();
					if (parentMenu != null) {
						parentMenu.add(menu);
					}
				} else {
					this.add(menu);
				}
			}
		}
		for (int i = 0; i < node.getChildCount(); i++) {
			XNode child = (XNode) node.getChildAt(i);
			makeMenu(child, indent + 1);
		}
	}
}
