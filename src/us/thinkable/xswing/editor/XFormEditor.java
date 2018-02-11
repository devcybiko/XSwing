package us.thinkable.xswing.editor;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import javax.swing.JLabel;
import javax.swing.JSpinner;

import us.thinkable.xcore.FileUtil;
import us.thinkable.xswing.field.XBorder;
import us.thinkable.xswing.field.XContext;
import us.thinkable.xswing.field.XField;
import us.thinkable.xswing.frame.XFrame;
import us.thinkable.xswing.util.XDialog;

@SuppressWarnings("serial")
public class XFormEditor extends XEditor implements MouseListener, MouseMotionListener, ActionListener {
	Component dragging = null;
	int draggingX = 0;
	int draggingY = 0;
	int draggingDX = 0;
	int draggingDY = 0;
	int draggingStartX = 0;
	int draggingStartY = 0;
	boolean draggingEnabled = false;
	Color bgColor = null;
	String[] formExtenisons = { "form" };
	List<XField> groupXFields = new ArrayList<XField>();
	Stack<List<String>> undoStack = new Stack<List<String>>();
	Stack<List<String>> redoStack = new Stack<List<String>>();

	private static int NONE = 0;
	private static int UPLEFT = 1;
	private static int UPRIGHT = 2;
	private static int DOWNLEFT = 3;
	private static int DOWNRIGHT = 4;
	int corner = NONE;

	public XFormEditor(XFrame frame, String title, String menuFname) throws IOException {
		super(frame, title, menuFname);
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		bgColor = this.getBackground();
		this.setBackground(Color.WHITE);
		this.setFileNameExtensions(formExtenisons);
	}

	@Override
	public void setEditable(boolean editable) {
		super.setEditable(editable);
		if (!editable) {
			Component[] components = this.getComponents();
			for (Component component : components) {
				component.removeMouseListener(this);
				component.removeMouseMotionListener(this);
			}
			this.setBackground(bgColor);
		} else {
			Component[] components = this.getComponents();
			for (Component component : components) {
				component.addMouseListener(this);
				component.addMouseMotionListener(this);
			}
			this.setBackground(Color.WHITE);

		}
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (dragging != null && corner == UPLEFT) {
			g.setColor(Color.black);
			g.drawRect(draggingX, draggingY, dragging.getX() - draggingX + dragging.getWidth(), dragging.getY() - draggingY + dragging.getHeight());
		}
		if (dragging != null && corner == DOWNRIGHT) {
			g.setColor(Color.black);
			g.drawRect(dragging.getX(), dragging.getY(), draggingX - dragging.getX(), draggingY - dragging.getY());
		}
	}

	private void moveEnclosed(int x, int y) {
		// move them all
		for (XField f : this.groupXFields) {
			Component c = (Component) f;
			int newX = x + c.getX() - dragging.getX();
			int newY = y + c.getY() - dragging.getY();
			this.position(c, newX, newY);
			c.setBounds(x, y, c.getWidth(), c.getHeight());
			if (!(f instanceof XBorder)) {
				f.toFront();
				this.setDirty(true);
			} else {
				f.toBack();
				this.setDirty(true);
			}
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (dragging != null && (e.getComponent() == dragging || getLabelFor(e.getComponent()) == dragging)) {
			// dragging a Component around
			if (corner == NONE) {
				Component c = e.getComponent();
				int x = c.getX() + e.getX() + draggingDX;
				int y = c.getY() + e.getY() + draggingDY;
				x = x - x % 5;
				y = y - y % 5;
				if (dragging instanceof XBorder) {
					moveEnclosed(x, y);
				}
				this.position(dragging, x, y);
				dragging.setBounds(x, y, dragging.getWidth(), dragging.getHeight());
				this.setDirty(true);
				this.refresh();
			} else if (corner == UPLEFT) {
				Component c = e.getComponent();
				int x = c.getX() + e.getX() + draggingDX;
				int y = c.getY() + e.getY() + draggingDY;
				draggingX = x - x % 5;
				draggingY = y - y % 5;
				this.setDirty(true);
				this.refresh();
			} else if (corner == DOWNRIGHT) {
				Component c = e.getComponent();
				int x = dragging.getX() + e.getX();
				int y = dragging.getY() + e.getY();
				draggingX = x - x % 5;
				draggingY = y - y % 5;
				this.refresh();
				this.setDirty(true);
			} else {
				Component c = e.getComponent();
				int x = c.getX();
				int y = c.getY();
				x = x - x % 5;
				y = y - y % 5;
				this.position(dragging, x, y);
				dragging.setBounds(x, y, dragging.getWidth(), dragging.getHeight());
				this.setDirty(true);
				this.refresh();
			}
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		if (e.getSource() == this) { return; }

	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getSource() == this) { return; }
		if (e.getClickCount() == 2) {
			Component c = (Component) e.getSource();
			if (c instanceof JLabel) {
				JLabel label = (JLabel) c;
				if (label.getLabelFor() != null) {
					c = label.getLabelFor();
				}
			}
			try {
				doubleClick(this, c);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}

	public void doubleClick(XFormEditor form, Component c) throws IOException {
		XDialog.alert("You have not implemented doubleClick(XFormEditor, Component) method for " + this.getClass().getName(), "Ok");
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		if (e.getSource() == this) { return; }

	}

	@Override
	public void mouseExited(MouseEvent e) {
		if (e.getSource() == this) { return; }

	}

	private void groupEnclosed() {
		this.groupXFields.clear();
		for (int i = 0; i < this.getComponentCount(); i++) {
			Component c = this.getComponent(i);
			if (c instanceof XField) {
				if (dragging.getX() < c.getX() && //
						dragging.getY() < c.getY() && //
						c.getX() + c.getWidth() < dragging.getX() + dragging.getWidth() && //
						c.getY() + c.getHeight() < dragging.getY() + dragging.getHeight()) {
					groupXFields.add((XField) c);
				}
			}
		}
	}

	public void undoAction() throws IOException {
		if (!undoStack.isEmpty()) {
			redoStack.push(serialize());
			List<String> undoForm = undoStack.pop();
			List<Component> list = new ArrayList<Component>();
			for (int i = 0; i < this.getComponentCount(); i++) {
				Component c = this.getComponent(i);
				list.add(c);
			}
			for (Component c : list) {
				this.remove(c);
			}
			FileUtil.fileWriteLines(new File("undo.form"), undoForm);
			this.readFile("undo.form");
			this.refresh();
			if (undoStack.isEmpty()) {
				this.setDirty(false);
			}
		}
	}

	public void redoAction() throws IOException {
		if (!redoStack.isEmpty()) {
			undoStack.push(serialize());
			List<String> redoForm = redoStack.pop();
			List<Component> list = new ArrayList<Component>();
			for (int i = 0; i < this.getComponentCount(); i++) {
				Component c = this.getComponent(i);
				list.add(c);
			}
			for (Component c : list) {
				this.remove(c);
			}
			FileUtil.fileWriteLines(new File("undo.form"), redoForm);
			this.readFile("undo.form");
			this.setDirty(true);
			this.refresh();
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (e.getSource() == this) { return; }

		if (e.getButton() == MouseEvent.BUTTON1) {
			dragging = null;
			Component c = (Component) e.getSource();
			if (c != null) {
				if (getLabelFor(c) == null) {
					// its an XField
					XField xfield = (XField) c;
					xfield.toFront();
					dragging = c;
					undoStack.push(serialize());
					redoStack.clear();
					draggingEnabled = c.isEnabled();
					dragging.setEnabled(false);
					draggingDX = -e.getX();
					draggingDY = -e.getY();
					draggingStartX = dragging.getX();
					draggingStartY = dragging.getY();
					if (dragging instanceof XBorder) {
						groupEnclosed();
					}
					if (e.getX() < 10 && e.getY() < 10 && xfield.canResize()) {
						corner = UPLEFT;
						int x = c.getX() + e.getX() + draggingDX;
						int y = c.getY() + e.getY() + draggingDY;
						draggingX = x - x % 5;
						draggingY = y - y % 5;
					}
					if (e.getX() > c.getWidth() - 10 && e.getY() > c.getHeight() - 10 && xfield.canResize()) {
						corner = DOWNRIGHT;
						int x = c.getX() + e.getX();
						int y = c.getY() + e.getY();
						draggingX = x - x % 5;
						draggingY = y - y % 5;
					}
				} else {
					// it's a label
					dragging = getLabelFor(c);
					undoStack.push(serialize());
					redoStack.clear();
					XField xfield = (XField) dragging;
					xfield.toFront();
					draggingEnabled = c.isEnabled();
					dragging.setEnabled(false);
					draggingDX = c.getWidth() - e.getX() + 5;
					draggingDY = -e.getY();
					draggingStartX = dragging.getX();
					draggingStartY = dragging.getY();
					if (dragging instanceof XBorder) {
						groupEnclosed();
					}
				}
			}
		} else if (e.getButton() == MouseEvent.BUTTON3) {
			Component c = (Component) e.getSource();
			if (c != null) {
				if (getLabelFor(c) == null) {
					// its an XField
					XField xfield = (XField) c;
					xfield.toBack();
					this.setDirty(true);
				} else {
					c = getLabelFor(c);
					XField xfield = (XField) c;
					xfield.toBack();
					this.setDirty(true);
				}
			}
		}

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (e.getSource() == this) { return; }

		if (dragging != null) {
			if (corner == NONE) {
				dragging.setEnabled(draggingEnabled);
				this.refresh();
				// if (dragging.getX() != draggingStartX || dragging.getY() !=
				// draggingStartY) {
				this.setDirty(true);
				// }
				if (dragging instanceof XBorder) {
					((XBorder) dragging).toBack();
				}
				dragging = null;
			} else if (corner == UPLEFT) {
				corner = NONE;
				String info = dragging.toString();
				Map<String, String> map = new HashMap<String, String>();
				FileUtil.parseKeyValueString(info, map);
				XContext c = new XContext(map);
				int w = dragging.getX() - draggingX + dragging.getWidth();
				int h = dragging.getY() - draggingY + dragging.getHeight();
				c.put("x", "" + draggingX);
				c.put("y", "" + draggingY);
				c.put("w", "" + w);
				c.put("h", "" + h);

				this.remove(dragging);
				try {
					dragging = (Component) this.add(c);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				dragging.setEnabled(draggingEnabled);
				this.refresh();
				if (dragging.getX() != draggingStartX || dragging.getY() != draggingStartY) {
					this.setDirty(true);
				}
				dragging = null;
			} else if (corner == DOWNRIGHT) {
				corner = NONE;
				String info = dragging.toString();
				Map<String, String> map = new HashMap<String, String>();
				FileUtil.parseKeyValueString(info, map);
				XContext c = new XContext(map);
				int w = draggingX - dragging.getX();
				int h = draggingY - dragging.getY();
				c.put("x", "" + dragging.getX());
				c.put("y", "" + dragging.getY());
				c.put("w", "" + w);
				c.put("h", "" + h);

				this.remove(dragging);
				try {
					dragging = (Component) this.add(c);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				dragging.setEnabled(draggingEnabled);
				this.refresh();
				if (dragging.getX() != draggingStartX || dragging.getY() != draggingStartY) {
					this.setDirty(true);
				}
				dragging = null;
			}
		}
	}

	/**
	 * if it's a label, return the component it represents (labelFor). if it's
	 * not a label, return null. if it's a label that points to a JSpinner,
	 * return the label itself.
	 * 
	 * @param c
	 * @return
	 */
	private Component getLabelFor(Component c) {
		if (c instanceof JLabel) {
			JLabel label = (JLabel) c;
			Component obj = label.getLabelFor();
			return obj;
		}
		return null;
	}

	@Override
	public Component add(Component c) {
		c.addMouseListener(this);
		c.addMouseMotionListener(this);
		return super.add(c);
	}

	/**
	 * use the generic form reader to read the form into the panel
	 */
	public void readFile(String fname) throws IOException {
		this.readForm(fname);
	}

	/**
	 * saves the form to the specified fileName. returns the filename if all
	 * went well. returns null on an error
	 * 
	 * @param fname
	 * @return
	 * @throws IOException
	 */
	public void saveFile(String fname) throws IOException {
		List<String> lines = serialize();
		FileUtil.fileWriteLines(new File(fname), lines);
		this.setDirty(false);
		undoStack.clear();
		redoStack.clear();
		return;
	}

	public List<String> serialize() {
		List<String> lines = new ArrayList<String>();
		for (Component c : this.getComponents()) {
			if (c instanceof XField) {
				String description = c.toString();
				lines.add(description);
			}
		}
		return lines;
	}
}
