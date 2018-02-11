package us.thinkable.xswing.editor;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.ChangeEvent;

import us.thinkable.xswing.frame.XFrame;

@SuppressWarnings("serial")
public class XStatusEditor extends XEditor {
	private JTextArea textArea = null;

	public XStatusEditor(XFrame frame, String title, String menuFname) throws IOException {
		super(frame, title, menuFname);
		textArea = new JTextArea(5, 20);
		textArea.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(textArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		GridBagConstraints gridCons2 = new GridBagConstraints();
		gridCons2.weightx = 1.0;
		gridCons2.weighty = 1.0;
		gridCons2.fill = GridBagConstraints.BOTH;
		GridBagLayout gridBag = new GridBagLayout();
		this.setLayout(gridBag);
		this.add(scrollPane, gridCons2);
	}

	public void addText(String s) {
		Date now = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss");
		textArea.append(sdf.format(now) + " " + s + "\n");
		getXTabbedPanel().setMyTitle(this, "*" + getTitle());
	}

	public void addText(Exception ex) {
		final StringWriter sw = new StringWriter();
		final PrintWriter pw = new PrintWriter(sw, true);
		ex.printStackTrace(pw);
		addText(sw.getBuffer().toString());
	}

	public void setText(String s) {
		textArea.setText(s);
	}

	@Override
	public void stateChanged(ChangeEvent s) {
		super.stateChanged(s);
		if (getXTabbedPanel().getSelectedComponent() == this) {
			getXTabbedPanel().setMyTitle(this, getTitle());
		}
	}

	@Override
	public void readFile(String fname) throws IOException {
		// TODO Auto-generated method stub

	}
}
