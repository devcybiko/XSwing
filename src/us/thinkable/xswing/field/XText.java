package us.thinkable.xswing.field;

import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.SpringLayout;

import us.thinkable.xcore.StringUtil;
import us.thinkable.xcore.XDB;
import us.thinkable.xswing.editor.XEditor;

@SuppressWarnings("serial")
public class XText extends JScrollPane implements XField {
	JTextArea textArea = null;
	JLabel label = null;
	String fontName;
	Integer fontSize;

	public XText(XEditor form, XContext c) {
		super();
		this.setName(c.getName());
		textArea = new JTextArea();
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		Dimension dim = this.getPreferredSize();
		dim.width = c.getW();
		dim.height = c.getH();
		this.setPreferredSize(dim);
		this.setViewportView(textArea);
		this.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

		String title = c.getTitle();
		if (!title.equals("default")) {
			label = new JLabel(title);
			form.layout.putConstraint(SpringLayout.EAST, label, -5, SpringLayout.WEST, this);
			form.layout.putConstraint(SpringLayout.NORTH, label, 0, SpringLayout.NORTH, this);
			form.add(label);
			label.setLabelFor(this);
		}
		form.position(this, c.getX(), c.getY());
		form.add(this);

		String fontName = c.getFont();
		Integer fontSize = c.getFontsize();
		setFont(fontName, fontSize);
	}

	@Override
	public boolean canResize() {
		return true;
	}

	public String getFontName() {
		return fontName;
	}

	public void setFontName(String fontName) {
		this.fontName = fontName;
	}

	public Integer getFontSize() {
		return fontSize;
	}

	public void setFontSize(Integer fontSize) {
		this.fontSize = fontSize;
	}

	public void setFont(String fontName, int size) {
		this.fontName = fontName;
		this.fontSize = size;
		textArea.setFont(new Font(fontName, Font.PLAIN, size));
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
		result += "type=Text";
		result += "\tname=" + this.getName();
		result += "\ttitle=" + this.label.getText();
		result += "\tx=" + this.getX();
		result += "\ty=" + this.getY();
		result += "\tw=" + this.getWidth();
		result += "\th=" + this.getHeight();
		result += "\tfont=" + this.getFontName();
		result += "\tfontsize=" + this.getFontSize();
		if (!StringUtil.isEmpty(this.getValue())) result += "\tvalue=" + this.getValue();
		return result;
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
	public void setValue(String value) {
		textArea.setText(value);
		XDB.xput(this.getName(), value);
	}

	@Override
	public String getValue() {
		return textArea.getText();
	}

	@Override
	public String getType() {
		return "Text";
	}

	@Override
	public String getTitle() {
		return label.getText();
	}

	@Override
	public void setTitle(String title) {
		label.setText(title);
	}
	
	public void setLineWrap(boolean wrap) {
		textArea.setLineWrap(wrap);
		textArea.setWrapStyleWord(true);
	}
	
	public void append(String s) {
		textArea.append(s);
	}
}
