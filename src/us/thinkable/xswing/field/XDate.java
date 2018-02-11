package us.thinkable.xswing.field;

import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import com.qt.datapicker.DatePicker;

import us.thinkable.xcore.StringUtil;
import us.thinkable.xcore.XDB;
import us.thinkable.xswing.editor.XEditor;

@SuppressWarnings("serial")
public class XDate extends JTextField implements XField, Observer, MouseListener {
	JLabel label = null;

	public XDate(XEditor form, XContext c) {
		super();
		this.setName(c.getName());
		label = new JLabel(c.getTitle());
		form.layout.putConstraint(SpringLayout.EAST, label, -5, SpringLayout.WEST, this);
		form.layout.putConstraint(SpringLayout.SOUTH, label, 1, SpringLayout.SOUTH, this);
		Dimension dim = this.getPreferredSize();
		dim.width = c.getW();
		dim.height = c.getH();
		this.setPreferredSize(dim);
		this.setValue((String) XDB.xget(this.getName()));
		this.addMouseListener(this);
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
		result += "type=Date";
		result += "\tname=" + this.getName();
		result += "\ttitle=" + this.label.getText();
		result += "\tx=" + this.getX();
		result += "\ty=" + this.getY();
		result += "\tw=" + this.getWidth();
		result += "\th=" + this.getHeight();
		if (!StringUtil.isEmpty(this.getValue())) result += "\tvalue=" + this.getValue();
		return result;
	}

	public void update(Observable o, Object arg) {
		Calendar calendar = (Calendar) arg;
		DatePicker dp = (DatePicker) o;
		// System.out.println("picked=" + dp.formatDate(calendar));
		setValue(dp.formatDate(calendar));
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		// instantiate the DatePicker
		DatePicker dp = new DatePicker((Observer) this, Locale.US) {
			public String formatDate(Date date) {
				SimpleDateFormat sdf = null;
				if (date == null) return "";
				if (sdf == null) sdf = new SimpleDateFormat("MM/dd/yyyy");
				return sdf.format(date);
			}
		};
		// previously selected date
		Date selectedDate = dp.parseDate(this.getText());
		dp.setSelectedDate(selectedDate);
		dp.start(this);
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
	}

	@Override
	public void mouseExited(MouseEvent arg0) {

	}

	@Override
	public void mousePressed(MouseEvent arg0) {
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
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
		return "Date";
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
