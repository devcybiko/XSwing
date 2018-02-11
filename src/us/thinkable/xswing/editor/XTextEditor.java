package us.thinkable.xswing.editor;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

import us.thinkable.xcore.FileUtil;
import us.thinkable.xcore.OSUtil;
import us.thinkable.xswing.frame.XFrame;

@SuppressWarnings("serial")
public class XTextEditor extends XEditor implements DocumentListener, ChangeListener, KeyListener {
	private JTextArea textArea = null;
	protected UndoManager undoManager = new UndoManager();

	public XTextEditor(XFrame frame, String title, String menuFname) throws IOException {
		super(frame, title, menuFname);
		textArea = new JTextArea(5, 20);
		textArea.setEditable(true);
		this.setFontMonospaced(12);
		JScrollPane scrollPane = new JScrollPane(textArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		GridBagConstraints gridCons2 = new GridBagConstraints();
		gridCons2.weightx = 1.0;
		gridCons2.weighty = 1.0;
		gridCons2.fill = GridBagConstraints.BOTH;
		GridBagLayout gridBag = new GridBagLayout();
		this.setLayout(gridBag);
		this.add(scrollPane, gridCons2);
		textArea.getDocument().addDocumentListener(this);

		textArea.getDocument().addUndoableEditListener(new UndoableEditListener() {
			public void undoableEditHappened(UndoableEditEvent e) {
				undoManager.addEdit(e.getEdit());
			}
		});

		textArea.addKeyListener(this);

	}

	public void setFontMonospaced(int size) {
		textArea.setFont(new Font("Monospaced", Font.PLAIN, size));
	}

	public void setFontSerif(int size) {
		textArea.setFont(new Font("Serif", Font.PLAIN, size));
	}

	public void setFontSansSerif(int size) {
		textArea.setFont(new Font("SansSerif", Font.PLAIN, size));
	}

	public void addText(String s) {
		textArea.append(s);
	}

	public void setText(String s) {
		textArea.setText(s);
		setDirty(false);
	}

	public String getText() {
		return textArea.getText();
	}

	@Override
	public boolean isEditable() {
		return textArea.isEditable();
	}

	@Override
	public void setEditable(boolean editable) {
		textArea.setEditable(editable);
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

	@Override
	public void readFile(String fname) throws IOException {
		String text = FileUtil.fileRead(fname);
		this.setText(text);
	}

	@Override
	public void saveFile(String fname) throws IOException {
		String text = this.getText();
		FileUtil.fileWrite(new File(fname), text);
	}

	/**
	 * This should be an independent method of "undo()" and "redo";
	 */

	public void undo() {
		try {
			undoManager.undo();
		} catch (CannotUndoException cre) {
			//
		}
	}

	public void redo() {
		try {
			undoManager.redo();
		} catch (CannotUndoException cre) {
			//
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
		int mods = e.getModifiers();
		int key = e.getKeyChar();
		if (OSUtil.isOSX()) {
			int mask = KeyEvent.META_MASK + KeyEvent.SHIFT_MASK;
			if ((mods & mask) == KeyEvent.META_MASK && key == 122) {
				undo();
			} else if ((mods & mask) == mask && key == 122) {
				redo();
			}
		} else {
			int mask = KeyEvent.CTRL_MASK + KeyEvent.SHIFT_MASK;
			if ((mods & mask) == KeyEvent.CTRL_MASK && key == 26) {
				this.undo();
			} else if ((mods & mask) == mask && key == 26) {
				this.redo();
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub

	}

}
