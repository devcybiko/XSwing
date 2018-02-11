package us.thinkable.xswing.frame;

import java.awt.Component;
import java.awt.Container;
import java.awt.Rectangle;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JFrame;

import us.thinkable.xswing.access.XAccess;
import us.thinkable.xswing.editor.XEditor;
import us.thinkable.xswing.field.XField;

@SuppressWarnings("serial")
public class XPopUpForm extends XPopUpFrame {
	public XEditor panel = null;

	public XPopUpForm(String title, String menuFname, String formFname) throws IOException {
		super(title, menuFname);
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		initMenuBar(menuFname, null);
		XFrame.registerXFrame(this);
		this.setVisible(true);
		panel = new XEditor(this, title, null);
		panel.readForm(formFname);
		this.add(panel);
		this.resize();
	}

	@Override
	public void initAction() {
		// TODO Auto-generated method stub

	}
}
