package us.thinkable.xswing.field;

import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.SpringLayout;

import us.thinkable.xcore.StringUtil;
import us.thinkable.xcore.XDB;
import us.thinkable.xswing.editor.XEditor;

@SuppressWarnings("serial")
public class XNonmod extends JLabel implements XField {
	JLabel label = null;

	public XNonmod(XEditor form, XContext c) {
		super();
		this.setName(c.getName());
		Dimension dim = this.getPreferredSize();
		dim.width = c.getW();
		dim.height = c.getH();
		this.setPreferredSize(dim);
		label = new JLabel(c.getTitle());
		form.layout.putConstraint(SpringLayout.EAST, label, -5, SpringLayout.WEST, this);
		form.layout.putConstraint(SpringLayout.SOUTH, label, -2, SpringLayout.SOUTH, this);
		this.setValue(c.getValue());
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
		return false;
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
		result += "type=Nonmod";
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
		return "Nonmod";
	}

	@Override
	public String getTitle() {
		return this.label.getText();
	}

	@Override
	public void setTitle(String title) {
		this.label.setText(title);
	}
}
