package us.thinkable.xswing.field;

import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

import us.thinkable.xswing.editor.XEditor;

@SuppressWarnings("serial")
public class XBorder extends JPanel implements XField {
	Border titledBorder = null;
	String title = null;

	public XBorder(XEditor form, XContext c) {
		super();
		String name = c.getName();
		title = c.getTitle();
		int i = 0;
		int x = c.getX();
		int y = c.getY();
		int w = c.getW();
		int h = c.getH();
		this.setName(name);
		Dimension dim = this.getPreferredSize();
		dim.width = c.getW();
		dim.height = c.getH();
		this.setPreferredSize(dim);
		Border border = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
		titledBorder = BorderFactory.createTitledBorder(border, title);
		this.setBorder(titledBorder);
		form.position(this, x, y);
		form.add(this);
		this.toBack();
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
		return true;
	}

	@Override
	public void remove() {
		this.getParent().remove(this);
	}

	@Override
	public String toString() {
		String result = "";
		result += "type=Border";
		result += "\tname=" + this.getName();
		result += "\ttitle=" + this.getTitle();
		result += "\tx=" + this.getX();
		result += "\ty=" + this.getY();
		result += "\tw=" + this.getWidth();
		result += "\th=" + this.getHeight();
		return result;
	}

	@Override
	public void setValue(String value) {
		// this.setText(value);
	}

	@Override
	public String getValue() {
		// return this.getText();
		return "";
	}

	@Override
	public String getType() {
		return "Border";
	}

	@Override
	public String getTitle() {
		return title;
		// return this.getText();
	}

	@Override
	public void setTitle(String title) {
		// this.setText(title);
	}
}
