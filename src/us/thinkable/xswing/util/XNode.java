package us.thinkable.xswing.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;

import us.thinkable.xcore.FileUtil;

@SuppressWarnings("serial")
public class XNode<T> extends DefaultMutableTreeNode {
	public XNode() {
		super();
	}

	public XNode(XNode<T> parent, T obj) {
		super(obj);
		if (parent != null) {
			parent.add(this);
		}
	}

	public XNode(T obj) {
		super(obj);
	}

	public XNode<T> getParent() {
		return (XNode<T>) super.getParent();
	}

	private static int tabCount(String s) {
		for (int i = 0; i < s.length(); i++) {
			if (s.charAt(i) != '\t') {
				return i;
			}
		}
		return s.length();
	}

	public static XNode readFile(File infile) {
		XNode root = new XNode();
		try {
			List<String> lines = FileUtil.fileReadLinesSansComments(infile);

			ArrayList<XNode> list = new ArrayList<XNode>();
			list.add(root);

			for (String strLine : lines) {
				int indent = tabCount(strLine);
				XNode child = new XNode(strLine);
				list.get(indent).add(child);
				list.add(indent + 1, child);
			}
		} catch (Exception e) {// Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
		return root;
	}

}
