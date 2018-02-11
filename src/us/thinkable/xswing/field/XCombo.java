package us.thinkable.xswing.field;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.SpringLayout;

import us.thinkable.xcore.StringUtil;
import us.thinkable.xcore.XDB;
import us.thinkable.xswing.editor.XEditor;

@SuppressWarnings("serial")
public class XCombo extends JComboBox implements XField {
	JLabel label = null;
	String[] items = null;
	Map<String, String> map = new HashMap<String, String>();

	public XCombo(XEditor form, XContext c) {
		super();
		this.setName(c.getName());
		this.setActionCommand(c.getName());
		label = new JLabel(c.getTitle());
		form.layout.putConstraint(SpringLayout.EAST, label, -5, SpringLayout.WEST, this);
		form.layout.putConstraint(SpringLayout.SOUTH, label, 2, SpringLayout.SOUTH, this);
		this.setValues(c.getItems());
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
		result += "type=Combo";
		result += "\tname=" + this.getName();
		result += "\ttitle=" + this.label.getText();
		result += "\tx=" + this.getX();
		result += "\ty=" + this.getY();
		result += "\tw=" + this.getWidth();
		result += "\th=" + this.getHeight();
		result += "\titems=" + this.getItemsAsString();
		if (!StringUtil.isEmpty(this.getValue())) result += "\tvalue=" + this.getValue();
		return result;
	}

	public String getItemsAsString() {
		String result = "";
		String comma = "";
		for (int i = 0; i < this.getItemCount(); i++) {
			Object item = this.getItemAt(i);
			String itemValue = map.get(item);
			result += comma + item + "=" + itemValue;
			comma = ",";
		}
		return result;
	}

	@Override
	public void setValue(String value) {
		this.setSelectedItem(null);
		for (int i = 0; i < this.getItemCount(); i++) {
			Object item = this.getItemAt(i);
			String itemValue = map.get(item);
			if (itemValue != null && itemValue.equals(value)) {
				this.setSelectedItem(item);
			}
		}
	}

	@Override
	public String getValue() {
		String item = (String) this.getSelectedItem();
		String result = (String) map.get(item);
		return result;
	}

	public void setValues(String[] items) {
		List<String> list = new ArrayList<String>();
		for (String item : items) {
			list.add(item);
		}
		setValues(list);
	}

	public void setValues(List<String> items) {
		map = new HashMap<String, String>();
		this.removeAllItems();
		for (int j = 0; j < items.size(); j++) {
			String item = items.get(j);
			String[] pair = item.split("=");
			if (pair.length == 1) {
				map.put(pair[0], pair[0]);
			} else {
				map.put(pair[0], pair[1]);
			}
			this.addItem(pair[0]);
		}
		if (items.size() != 0) {
			this.setSelectedIndex(0);
		}
		this.setValue((String) XDB.xget(this.getName()));
	}

	@Override
	public String getType() {
		return "Combo";
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
