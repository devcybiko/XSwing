package us.thinkable.xswing.field;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import us.thinkable.xcore.XDB;
import us.thinkable.xswing.editor.XEditor;

@SuppressWarnings("serial")
public class XButton extends JButton implements XField, ActionListener {
	public XButton(XEditor form, XContext c) {
		super();

		this.setName(c.getName());
		this.setTitle(c.getTitle());
		this.setActionCommand(this.getName());
		Dimension dim = this.getPreferredSize();
		dim.width = c.getW();
		dim.height = c.getH();
		this.setPreferredSize(dim);
		this.addActionListener(this);
		this.addActionListener(form);
		form.position(this, c.getX(), c.getY());
		this.setEnabled(c.getEnabled());
		form.add(this);
	}

	@Override
	public void remove() {
		this.getParent().remove(this);
	}

	@Override
	public String toString() {
		String result = "";
		result += "type=Button";
		result += "\tname=" + this.getName();
		result += "\ttitle=" + this.getValue();
		result += "\tx=" + this.getX();
		result += "\ty=" + this.getY();
		result += "\tw=" + this.getWidth();
		result += "\th=" + this.getHeight();
		if (!this.isEnabled()) {
			result += "\tenabled=" + this.isEnabled();
		}
		return result;
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
	public void setValue(String value) {
		this.setText(value);
	}

	@Override
	public String getValue() {
		return this.getText();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Component[] list = this.getParent().getComponents();
		for (Component c : list) {
			if (c instanceof XField) {
				XField x = (XField) c;
				XDB.xput(c.getName(), x.getValue());
			}
		}
		XDB.xflush();
	}

	@Override
	public String getType() {
		return "Button";
	}

	@Override
	public String getTitle() {
		return getText();
	}

	@Override
	public void setTitle(String title) {
		this.setText(title);

	}
}
