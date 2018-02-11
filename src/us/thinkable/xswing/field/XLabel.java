package us.thinkable.xswing.field;

import java.awt.Dimension;
import java.awt.Insets;

import javax.swing.JLabel;

import us.thinkable.xcore.XDB;
import us.thinkable.xswing.editor.XEditor;

@SuppressWarnings("serial")
public class XLabel extends JLabel implements XField {
	private String command = null;

	public XLabel(XEditor form, XContext c) {
		super();
		String name = c.getName();
		String slabel = c.getTitle();
		int x = c.getX();
		int y = c.getY();
		int w = c.getW();
		int h = c.getH();
		this.setName(name);
		if ("default".equals(slabel)) {
			this.setValue((String) XDB.xget(this.getName()));
		} else {
			this.setValue(slabel);
		}
		form.position(this, x, y);
		form.add(this);
	}

	@Override
	public void toFront() {
		this.getParent().setComponentZOrder(this, 0);
		((XEditor) this.getParent()).refresh();
	}

	@Override
	public void toBack() {
		this.getParent().setComponentZOrder(this, this.getParent().getComponentCount() - 1);
		((XEditor) this.getParent()).refresh();
	}

	@Override
	public boolean canResize() {
		return false;
	}

	@Override
	public void remove() {
		this.getParent().remove(this);
	}

	@Override
	public String toString() {
		String result = "";
		result += "type=Label";
		result += "\tname=" + this.getName();
		result += "\ttitle=" + this.getValue();
		result += "\tx=" + this.getX();
		result += "\ty=" + this.getY();
		result += "\tw=" + this.getWidth();
		result += "\th=" + this.getHeight();
		return result;
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
		return "Label";
	}

	@Override
	public String getTitle() {
		return this.getText();
	}

	@Override
	public void setTitle(String title) {
		this.setText(title);
	}
}
