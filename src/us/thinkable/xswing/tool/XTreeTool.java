package us.thinkable.xswing.tool;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import us.thinkable.xswing.frame.XFrame;
import us.thinkable.xswing.util.XNode;

@SuppressWarnings("serial")
public class XTreeTool extends XTool implements TreeSelectionListener {
	protected XNode root = null;
	protected JTree tree = null;
	protected JScrollPane treeView = null;
	protected DefaultTreeModel treeModel = null;
	protected boolean editable = false;

	public XTreeTool(XFrame frame, String title, String menuFname) throws IOException {
		super(frame, title, menuFname);
		this.setLayout(new BorderLayout());
		root = new XNode("root");
		setRoot(root);
	}

	public XNode getRoot() {
		return this.root;
	}

	public void setRoot(XNode root) {
		this.root = root;
		tree = new JTree(root);
		tree.setRootVisible(false);
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.addTreeSelectionListener(this);
		tree.addMouseListener(new MyMouseAdapter(this));
		tree.addMouseListener(this.popupListener);
		tree.setEditable(editable);
		treeModel = new DefaultTreeModel(root);
		treeModel.addTreeModelListener(new MyTreeModelListener());
		tree.setModel(treeModel);
		treeView = new JScrollPane(tree);
		if (this.getXPopupMenu() != null) {
			treeView.add(this.getXPopupMenu());
		}
		this.removeAll();
		this.add(treeView, BorderLayout.CENTER);
		treeModel.reload(root);
	}

	public XNode getSelectedNode() {
		return (XNode) tree.getLastSelectedPathComponent();
	}

	class MyTreeModelListener implements TreeModelListener {

		public void treeNodesChanged(TreeModelEvent e) {
			DefaultMutableTreeNode node;
			node = (DefaultMutableTreeNode) (e.getTreePath().getLastPathComponent());

			/*
			 * If the event lists children, then the changed
			 * node is the child of the node we have already
			 * gotten.  Otherwise, the changed node and the
			 * specified node are the same.
			 */
			try {
				int index = e.getChildIndices()[0];
				node = (DefaultMutableTreeNode) (node.getChildAt(index));
				edited((XNode) node);
			} catch (NullPointerException exc) {
			}
		}

		public void treeNodesInserted(TreeModelEvent e) {
		}

		public void treeNodesRemoved(TreeModelEvent e) {
		}

		public void treeStructureChanged(TreeModelEvent e) {
		}
	}

	class MyMouseAdapter extends MouseAdapter {
		private final XTreeTool tool;

		public MyMouseAdapter(XTreeTool tool) {
			this.tool = tool;
		}

		public void mousePressed(MouseEvent e) {
			int selRow = tree.getRowForLocation(e.getX(), e.getY());
			TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
			if (selRow != -1) {
				if (e.getClickCount() == 1) {
					//tool.singleClick((XNode) selPath.getLastPathComponent());
				} else if (e.getClickCount() == 2) {
					tool.doubleClick((XNode) selPath.getLastPathComponent());
				}
			}
		}
	};

	public void valueChanged(TreeSelectionEvent e) {
		XNode node = (XNode) tree.getLastSelectedPathComponent();

		if (node != null) {
			selected(node);
		}
	}

	public void edited(XNode node) {

	}

	public void addChild(XNode parent, XNode child) {
		treeModel.insertNodeInto(child, parent, parent.getChildCount());
		tree.scrollPathToVisible(new TreePath(child.getPath()));
		treeModel.reload(parent);
	}

	public void removeChild(XNode child) {
		XNode parent = child.getParent();
		treeModel.removeNodeFromParent(child);
		treeModel.reload(parent);
	}

	public void selected(XNode node) {
		//System.out.println("SELECT! " + node);
	}

	public void doubleClick(XNode node) {
		//System.out.println("DOUBLE CLICK! " + node);
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
		tree.setEditable(editable);
	}

	public boolean getEditable() {
		return this.editable;
	}
}
