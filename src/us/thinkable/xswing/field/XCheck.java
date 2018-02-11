package us.thinkable.xswing.field;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.SpringLayout;

import org.jfree.util.StringUtils;

import us.thinkable.xcore.StringUtil;
import us.thinkable.xcore.XDB;
import us.thinkable.xswing.editor.XEditor;

@SuppressWarnings("serial")
public class XCheck extends JCheckBox implements XField {
	JLabel label = null;

	public XCheck(XEditor form, XContext c) {
		super();
		this.setName(c.getName());
		this.setActionCommand(c.getCommand());
		if (StringUtil.isEmpty(c.getCommand())) {
			this.setActionCommand(c.getName());
		}
		label = new JLabel(c.getTitle());
		form.layout.putConstraint(SpringLayout.EAST, label, -5, SpringLayout.WEST, this);
		form.layout.putConstraint(SpringLayout.SOUTH, label, -2, SpringLayout.SOUTH, this);
		this.setValue((String) XDB.xget(this.getName()));
		this.addActionListener(form);
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
		result += "type=Check";
		result += "\tname=" + this.getName();
		result += "\ttitle=" + this.label.getText();
		result += "\tx=" + this.getX();
		result += "\ty=" + this.getY();
		result += "\tw=" + this.getWidth();
		result += "\th=" + this.getHeight();
		if (this.isSelected())
			result += "\tvalue=" + this.getValue();
		return result;
	}

	@Override
	public void setValue(String value) {
		if ("true".equals(value)) {
			this.setSelected(true);
			XDB.xput(this.getName(), "true");
		} else {
			this.setSelected(false);
			XDB.xput(this.getName(), "false");
		}
	}

	@Override
	public String getValue() {
		return "" + this.isSelected();
	}

	@Override
	public String getType() {
		return "Check";
	}

	@Override
	public String getTitle() {
		return label.getText();
	}

	@Override
	public void setTitle(String title) {
		label.setText(title);

	}
}
