package us.thinkable.xswing.field;

import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import us.thinkable.xcore.StringUtil;
import us.thinkable.xcore.XDB;
import us.thinkable.xswing.editor.XEditor;
import us.thinkable.xswing.util.XDialog;

@SuppressWarnings("serial")
public class XFile extends JTextField implements XField, MouseListener {
	JLabel label = null;
	private String style = "load";
	private String extensions = "";
	private String message = null;

	public XFile(XEditor form, XContext c) {
		super();
		this.setName(c.getName());
		Dimension dim = this.getPreferredSize();
		dim.width = c.getW();
		dim.height = c.getH();
		this.setPreferredSize(dim);
		label = new JLabel(c.getTitle());
		form.layout.putConstraint(SpringLayout.EAST, label, -5, SpringLayout.WEST, this);
		form.layout.putConstraint(SpringLayout.SOUTH, label, 1, SpringLayout.SOUTH, this);
		this.setValue((String) XDB.xget(this.getName()));
		this.addActionListener(form);
		this.addMouseListener(this);
		form.position(this, c.getX(), c.getY());
		form.add(this);
		form.add(label);
		label.setLabelFor(this);
	}

	@Override
	public void toFront() {
		this.getParent().setComponentZOrder(this, 0);
		this.getParent().setComponentZOrder(label, 0);
		((XEditor) this.getParent()).refresh();
	}

	@Override
	public void toBack() {
		this.getParent().setComponentZOrder(this, this.getParent().getComponentCount() - 1);
		this.getParent().setComponentZOrder(label, this.getParent().getComponentCount() - 1);
		((XEditor) this.getParent()).refresh();
	}

	@Override
	public boolean canResize() {
		return true;
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		label.setVisible(visible);
	}

	@Override
	public void remove() {
		this.getParent().remove(label);
		this.getParent().remove(this);
	}

	@Override
	public String toString() {
		String result = "";
		result += "type=File";
		result += "\tname=" + this.getName();
		result += "\ttitle=" + this.label.getText();
		result += "\tx=" + this.getX();
		result += "\ty=" + this.getY();
		result += "\tw=" + this.getWidth();
		result += "\th=" + this.getHeight();
		if (!StringUtil.isEmpty(this.getValue())) result += "\tvalue=" + this.getValue();
		return result;
	}

	@Override
	public void setText(String value) {
		super.setText(value);
		XDB.xput(this.getName(), value);
	}

	@Override
	public void setValue(String value) {
		this.setText(value);
	}

	@Override
	public String getValue() {
		return this.getText();
	}

	@Override
	public String getType() {
		return "File";
	}

	@Override
	public String getTitle() {
		return label.getText();
	}

	@Override
	public void setTitle(String title) {
		label.setText(title);
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		//
		// style=load, save, directoryOnly, fileAndDirectory
		// extensions=ext1,ext2,ext3
		String filename = null;
		if (this.getStyle() == null || this.getStyle().equals("load")) {
			filename = XDialog.load(this.getMessage(), this.getValue(), this.getExtensions().split(","));
		} else if (this.getStyle().equals("save")) {
			filename = XDialog.save(this.getMessage(), this.getValue(), this.getExtensions().split(","));
		} else if (this.getStyle().equals("directory")) {
			filename = XDialog.directory(this.getMessage(), this.getValue());
		} else if (this.getStyle().equals("any")) {
			filename = XDialog.fileOrDirectory(this.getMessage(), this.getValue(), this.getExtensions().split(","));
		}
		if (filename != null) {
			this.setValue(filename);
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

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	public String getExtensions() {
		return extensions;
	}

	public void setExtensions(String extensions) {
		this.extensions = extensions;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
