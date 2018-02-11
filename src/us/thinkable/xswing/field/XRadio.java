package us.thinkable.xswing.field;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.SpringLayout;

import us.thinkable.xcore.StringUtil;
import us.thinkable.xswing.editor.XEditor;

@SuppressWarnings("serial")
public class XRadio extends JRadioButton implements XField {
	JLabel label = null;
	private String command = null;

	public XRadio(XEditor form, XContext c) {
		super();
		this.setName(c.getName());
		this.setActionCommand(c.getName());
		if (c.getCommand() != null) {
			this.setActionCommand(c.getCommand());
			this.setCommand(c.getCommand());
		}
		label = new JLabel(c.getTitle());
		form.layout.putConstraint(SpringLayout.EAST, label, -5, SpringLayout.WEST, this);
		form.layout.putConstraint(SpringLayout.SOUTH, label, -2, SpringLayout.SOUTH, this);
		// /this.setValue((String) XDB.xget(this.getName()));
		this.addActionListener(form);
		// this.setText(c.getTitle());
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
		result += "type=Radio";
		result += "\tname=" + this.getName();
		result += "\ttitle=" + this.label.getText();
		result += "\tx=" + this.getX();
		result += "\ty=" + this.getY();
		result += "\tw=" + this.getWidth();
		result += "\th=" + this.getHeight();
		if (!StringUtil.isEmpty(this.getValue()))
			result += "\tvalue=" + this.getValue();
		return result;
	}

	@Override
	public void setValue(String value) {
		for (int i = 0; i < this.getParent().getComponentCount(); i++) {
			Component c = this.getParent().getComponent(i);
			if (c instanceof XRadio) {
				XRadio y = (XRadio) c;
				if (y.getName().equals(this.getName()))
					y.setSelected(y.getTitle().equals(value));
			}
		}
	}

	@Override
	public String getValue() {
		if (this.isSelected())
			return this.getTitle();
		String xname = this.getName();
		for (int i = 0; i < this.getParent().getComponentCount(); i++) {
			Component c = this.getParent().getComponent(i);
			if (c instanceof XRadio) {
				XRadio y = (XRadio) c;
				String yname = y.getName();
				if (yname.equals(xname) && y.isSelected())
					return y.getTitle();
			}
		}
		return null;
	}

	@Override
	public String getType() {
		return "Radio";
	}

	@Override
	public String getTitle() {
		return label.getText();
	}

	@Override
	public void setTitle(String title) {
		this.label.setText(title);
	}

	public String getCommand() {
		return this.command;
	}

	public void setCommand(String command) {
		this.command = command;
	}
}
