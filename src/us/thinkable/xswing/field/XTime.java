package us.thinkable.xswing.field;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;
import javax.swing.SpringLayout;

import us.thinkable.xcore.StringUtil;
import us.thinkable.xcore.XDB;
import us.thinkable.xswing.editor.XEditor;

@SuppressWarnings("serial")
public class XTime extends JLabel implements XField, Observer {
	private static final String TIME_FORMAT = "hh:mm a";
	private static final SimpleDateFormat timeSDF = new SimpleDateFormat(TIME_FORMAT);
	private JSpinner spinner = null;

	public XTime(XEditor form, XContext c) {
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

		spinner = new JSpinner();
		SpinnerDateModel model = new SpinnerDateModel();
		spinner.setModel(model);
		JSpinner.DateEditor timeEditor = new JSpinner.DateEditor(spinner, TIME_FORMAT);
		spinner.setEditor(timeEditor);
		spinner.setValue(new Date()); // will only show the current time

		form.layout.putConstraint(SpringLayout.WEST, spinner, 5, SpringLayout.EAST, this);
		form.layout.putConstraint(SpringLayout.SOUTH, spinner, 2, SpringLayout.SOUTH, this);
		//form.position(spinner, c.getX(), c.getY());
		form.add(spinner);
	}

	@Override
	public void toFront() {
		this.getParent().setComponentZOrder(this, 0);
		// this.getParent().setComponentZOrder(spinner, 0);
		((XEditor) this.getParent()).refresh();
	}

	@Override
	public void toBack() {
		this.getParent().setComponentZOrder(this, this.getParent().getComponentCount() - 1);
		// this.getParent().setComponentZOrder(spinner,
		// this.getParent().getComponentCount() - 1);
		((XEditor) this.getParent()).refresh();
	}

	@Override
	public boolean canResize() {
		return false;
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		// spinner.setVisible(visible);
	}

	@Override
	public void remove() {
		// this.getParent().remove(spinner);
		this.getParent().remove(this);
	}

	@Override
	public String toString() {
		String result = "";
		result += "type=Time";
		result += "\tname=" + this.getName();
		result += "\ttitle=" + this.getText();
		result += "\tx=" + this.getX();
		result += "\ty=" + this.getY();
		result += "\tw=" + this.getWidth();
		result += "\th=" + this.getHeight();
		if (!StringUtil.isEmpty(this.getValue())) result += "\tvalue=" + this.getValue();
		return result;
	}

	public void update(Observable o, Object arg) {
	}

	public void setValue2(String value) {
		// try {
		// spinner.setValue(timeSDF.parse(value));
		// } catch (ParseException e) {
		// e.printStackTrace();
		// }
	}

	public String getValue2() {
		return timeSDF.format(spinner.getValue());
	}

	@Override
	public String getType() {
		return "Time";
	}

	@Override
	public String getTitle() {
		return this.getText();
	}

	@Override
	public void setTitle(String title) {
		this.setText(title);
	}

	@Override
	public String getValue() {
		return this.getText();
	}

	@Override
	public void setValue(String value) {
		this.setText(value);
	}
}
