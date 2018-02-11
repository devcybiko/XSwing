package us.thinkable.xswing.frame;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;

import us.thinkable.xcore.FileUtil;
import us.thinkable.xcore.OSUtil;
import us.thinkable.xswing.access.XAccess;
import us.thinkable.xswing.access.XAccessInterface;
import us.thinkable.xswing.util.XNode;

@SuppressWarnings("serial")
public class XMenuBar extends JMenuBar {
	XAccessInterface frame;
	XAccess access;
	List<JMenu> menuList = new ArrayList<JMenu>();
	List<JMenu> rightMenuList = new ArrayList<JMenu>();

	public XMenuBar(XAccessInterface frame, String menuFname, String rightMenuFname) throws IOException {
		super();
		init(frame, menuFname, rightMenuFname);
	}

	public XMenuBar(XAccessInterface frame, String menuFname) throws IOException {
		super();
		init(frame, menuFname, null);
	}

	public XMenuBar(XMenuBar mainMenuBar, XMenuBar otherMenuBar) {
		super();
		for (JMenu menu : mainMenuBar.getMenuList()) {
			this.add(menu);
		}
		for (JMenu menu : otherMenuBar.getMenuList()) {
			this.add(menu);
		}

		this.add(Box.createHorizontalGlue());

		for (JMenu menu : otherMenuBar.getRightMenuList()) {
			this.add(menu);
		}
		for (JMenu menu : mainMenuBar.getRightMenuList()) {
			this.add(menu);
		}
	}

	private void init(XAccessInterface frame, String menuFname, String rightMenuFname) throws IOException {
		this.frame = frame;
		this.access = frame.getAccess();
		if (menuFname != null) {
			XNode root = readFile(menuFname);
			makeMenu(root, -1);
			populateMenuList(root, menuList);
		}

		this.add(Box.createHorizontalGlue());

		if (rightMenuFname != null) {
			XNode root = readFile(rightMenuFname);
			makeMenu(root, -1);
			populateMenuList(root, rightMenuList);
		}
	}

	private void populateMenuList(XNode root, List<JMenu> menuList) {
		for (int i = 0; i < root.getChildCount(); i++) {
			XNode menuNode = (XNode) root.getChildAt(i);
			Object obj = menuNode.getUserObject();
			if (obj.getClass().getName().contains("JMenu")) {
				menuList.add((JMenu) obj);
			}
		}
	}

	private int tabCount(String s) {
		for (int i = 0; i < s.length(); i++) {
			if (s.charAt(i) != '\t') { return i; }
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
				JMenu menu = (JMenu) node.getParent().getUserObject();
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
						menuItem.addActionListener(frame);
					}
					if (fields.length > indent + 2) {
						String shortcut = fields[indent + 2].toUpperCase();
						if (shortcut.charAt(0) == '^') {
							if (OSUtil.isOSX()) {
								menuItem.setAccelerator(KeyStroke.getKeyStroke(shortcut.charAt(1), ActionEvent.META_MASK));
							} else {
								menuItem.setAccelerator(KeyStroke.getKeyStroke(shortcut.charAt(1), ActionEvent.CTRL_MASK));
							}
						} else if (shortcut.charAt(0) == '*') {
							if (OSUtil.isOSX()) {
								menuItem.setAccelerator(KeyStroke.getKeyStroke(shortcut.charAt(1), ActionEvent.META_MASK + ActionEvent.SHIFT_MASK));
							} else {
								menuItem.setAccelerator(KeyStroke.getKeyStroke(shortcut.charAt(1), ActionEvent.CTRL_MASK + ActionEvent.SHIFT_MASK));
							}
						}
					}
				}
			} else {
				String groupNames = null;
				if (fields.length > indent + 1) {
					groupNames = fields[indent + 1];
				}
				if (access == null || fields.length == indent + 1 || access.hasAccess(groupNames)) {
					JMenu menu = new JMenu(fields[indent]);
					menu.setMnemonic(fields[indent].charAt(0));
					node.setUserObject(menu);
					if (indent != 0) {
						JMenu parentMenu = (JMenu) node.getParent().getUserObject();
						if (parentMenu != null) {
							parentMenu.add(menu);
						}
					}
					for (int i = 0; i < node.getChildCount(); i++) {
						XNode child = (XNode) node.getChildAt(i);
						makeMenu(child, indent + 1);
					}
					this.add(menu);
				}
			}
		} else {
			for (int i = 0; i < node.getChildCount(); i++) {
				XNode child = (XNode) node.getChildAt(i);
				makeMenu(child, indent + 1);
			}
		}
	}

	public void restoreMenus() {
		this.removeAll();
		for (JMenu menu : this.getMenuList()) {
			this.add(menu);
		}

		this.add(Box.createHorizontalGlue());

		for (JMenu menu : this.getRightMenuList()) {
			this.add(menu);
		}
	}

	public List<JMenu> getMenuList() {
		return menuList;
	}

	public void setMenuList(List<JMenu> menuList) {
		this.menuList = menuList;
	}

	public List<JMenu> getRightMenuList() {
		return rightMenuList;
	}

	public void setRightMenuList(List<JMenu> rightMenuList) {
		this.rightMenuList = rightMenuList;
	}
}
