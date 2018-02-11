package us.thinkable.xswing.editor;

import java.awt.BorderLayout;
import java.io.IOException;

import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.html.HTMLEditorKit;

import us.thinkable.xswing.frame.XFrame;

@SuppressWarnings("serial")
public class XHtmlEditor extends XEditor implements DocumentListener, ChangeListener {
	private JEditorPane editorPane = null;
	private JScrollPane scrollPane = null;

	public XHtmlEditor(XFrame frame, String title, String menuFname) throws IOException {
		this(frame, title, menuFname, null);
	}

	public XHtmlEditor(XFrame frame, String title, String menuFname, String toolBarFname) throws IOException {
		super(frame, title, menuFname);
		editorPane = new JEditorPane();
		editorPane.setEditable(true);
		editorPane.setEditorKit(new HTMLEditorKit());

		scrollPane = new JScrollPane(editorPane);
		this.setLayout(new BorderLayout());
		if (toolBarFname != null) {
			this.setToolBar(toolBarFname);
		}
		this.add(scrollPane, BorderLayout.CENTER);
	}

	public void addText(String s) {
		try {
			editorPane.getDocument().insertString(0, s, null);
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setText(String s) {
		editorPane.setText(s);
		setDirty(false);
	}

	public String getText() {
		return editorPane.getText();
	}

	public void setEditable(boolean editable) {
		editorPane.setEditable(editable);
	}

	@Override
	public void changedUpdate(DocumentEvent arg0) {
		setDirty(true);
	}

	@Override
	public void insertUpdate(DocumentEvent arg0) {
		setDirty(true);
	}

	@Override
	public void removeUpdate(DocumentEvent arg0) {
		setDirty(true);
	}

	public void loadUrl(String url) {
		try {
			editorPane.setPage(url);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void readFile(String fname) throws IOException {
		// TODO Auto-generated method stub

	}
}