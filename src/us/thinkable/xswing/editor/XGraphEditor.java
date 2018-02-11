package us.thinkable.xswing.editor;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.Timer;

import us.thinkable.xswing.frame.XFrame;
import us.thinkable.xswing.zed.XGraphEdge;
import us.thinkable.xswing.zed.XGraphNode;

@SuppressWarnings("serial")
public class XGraphEditor extends XEditor implements MouseListener, MouseMotionListener, ActionListener {
	List<XGraphNode> gnList = new ArrayList<XGraphNode>();
	List<XGraphEdge> geList = new ArrayList<XGraphEdge>();
	XGraphNode dragging = null;
	int draggingDX = 0;
	int draggingDY = 0;
	Date lastClick = new Date();
	private static final long DOUBLE_CLICK_THRESHOLD = 300;
	Timer doubleClickTimer = null;
	XGraphNode clickedGN = null;
	int clickedX = 0;
	int clickedY = 0;
	int clickedModifiers = 0;
	int dragToX = 0;
	int dragToY = 0;

	public XGraphEditor(XFrame frame, String title, String menuFname) throws IOException {
		super(frame, title, menuFname);
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
	}

	public List<XGraphNode> getGnList() {
		return gnList;
	}

	public void setGnList(List<XGraphNode> gnList) {
		this.gnList = gnList;
	}

	public List<XGraphEdge> getGeList() {
		return geList;
	}

	public void setGeList(List<XGraphEdge> geList) {
		this.geList = geList;
	}

	public void add(XGraphNode sf) {
		gnList.add(sf);
	}

	public void paint(Graphics g) {
		paint((Graphics2D) g);
	}

	public void paint(Graphics2D g2) {
		super.paint(g2);
		if (dragging != null && clickedModifiers != 0) {
			g2.setColor(Color.black);
			g2.drawLine(dragging.getCenterX(), dragging.getCenterY(), dragToX, dragToY);
		}
		for (XGraphEdge ge : geList) {
			ge.paint(g2);
		}
		for (XGraphNode gn : gnList) {
			gn.paint(g2);
		}
	}

	/**
	 * single-click timeout
	 */
	public void actionPerformed(ActionEvent ae) {
		if (ae.getSource() == doubleClickTimer) {
			// the doubleClickTimer expired, it was a single click
			if (clickedGN != null) {
				// the left mouse button was clicked
				if ((clickedModifiers & MouseEvent.CTRL_DOWN_MASK) != 0) {
					clickedGN.controlClick(this, clickedX, clickedY);
				} else {
					clickedGN.singleClick(this, clickedX, clickedY);
				}
				clickedGN = null;
			}
		}
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		clickedGN = null;
		XGraphNode gn = findDraggable(arg0.getX(), arg0.getY());
		if (gn != null) {
			if (arg0.getButton() == MouseEvent.BUTTON1) {
				if (doubleClickTimer != null && doubleClickTimer.isRunning()) {
					doubleClickTimer.stop();
					gn.doubleClick(this, clickedX, clickedY);
				} else {
					clickedGN = gn;
					clickedX = arg0.getX() - gn.getX();
					clickedY = arg0.getY() - gn.getY();
					clickedModifiers = arg0.getModifiersEx();

					doubleClickTimer = new Timer((int) DOUBLE_CLICK_THRESHOLD, this);
					doubleClickTimer.setRepeats(false);
					doubleClickTimer.start();
				}
			} else if (arg0.getButton() == MouseEvent.BUTTON3) {
				gn.menuClick(this, arg0.getX(), arg0.getY());
			}
		}
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	private XGraphNode findDraggable(int x, int y) {
		for (XGraphNode gn : gnList) {
			if (inBounds(gn, x, y)) {
				return gn;
			}
		}
		return null;
	}

	private boolean inBounds(XGraphNode gn, int x, int y) {
		return (gn.getX() <= x && x < gn.getX() + gn.getImageIcon().getIconWidth() //
				&& gn.getY() <= y && y < gn.getY() + gn.getImageIcon().getIconHeight());

	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		if (arg0.getButton() == MouseEvent.BUTTON1) {
			dragging = null;
			XGraphNode gn = findDraggable(arg0.getX(), arg0.getY());
			if (gn != null) {
				dragging = gn;
				draggingDX = gn.getX() - arg0.getX();
				draggingDY = gn.getY() - arg0.getY();
				clickedModifiers = arg0.getModifiersEx() & (MouseEvent.SHIFT_DOWN_MASK | MouseEvent.CTRL_DOWN_MASK | MouseEvent.ALT_DOWN_MASK | MouseEvent.ALT_GRAPH_DOWN_MASK);
			}
		}
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		if (dragging != null && clickedModifiers != 0) {
			XGraphNode dropTarget = findDraggable(arg0.getX(), arg0.getY());
			if (dropTarget != null && dropTarget != dragging) {
				dragging.dropTarget(this, dropTarget, clickedModifiers);
			}
		}
		dragging = null;
		clickedModifiers = 0;
		repaint();
	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
		if (dragging != null) {
			// dragging something around...
			if (clickedModifiers != 0) {
				// drawing a line between two GNs
				dragToX = arg0.getX();
				dragToY = arg0.getY();
				repaint();
			} else {
				// dragging a GN around
				dragging.setX(arg0.getX() + draggingDX);
				dragging.setY(arg0.getY() + draggingDY);
				repaint();
			}
		}
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		if (dragging != null && clickedModifiers != 0) {
			dragToX = arg0.getX();
			dragToY = arg0.getY();
			repaint();
		}
	}

	public boolean addEdge(XGraphEdge edge) {
		for (XGraphEdge ge : geList) {
			if (ge.getFrom() == edge.getFrom() && ge.getTo() == edge.getTo()) {
				return false;
			}
		}
		geList.add(edge);
		repaint();
		return true;
	}

	@Override
	public void readFile(String fname) throws IOException {
		// TODO Auto-generated method stub
		
	}
}
