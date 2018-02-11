package us.thinkable.xswing.panel;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.lang.reflect.Method;

import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import us.thinkable.xcore.XDB;
import us.thinkable.xcore.reflect.ReflectionUtil;
import us.thinkable.xswing.frame.XFrame;

@SuppressWarnings("serial")
public abstract class XPanel extends JPanel implements ChangeListener, ActionListener {
	protected XTabbedPanel xtabbedPanel = null;
	protected XFrame xFrame = null;
	protected String title = null;
	protected XToolBar xtoolBar = null;
	protected boolean actionsEnabled = true;
	protected Component altParent = null;
	protected Component lastField = null;

	public XPanel(XFrame frame, String title) {
		this.title = title;
		this.xFrame = frame;
	}

	public void setToolBar(String fname) throws IOException {
		this.setLayout(new BorderLayout());
		xtoolBar = new XToolBar(this, fname);
		add(xtoolBar, BorderLayout.PAGE_START);
	}

	private void handleRadioButtons(JRadioButton x) {
		String xname = x.getName();
		if (xname == null)
			return;
		for (int i = 0; i < this.getComponentCount(); i++) {
			Component c = this.getComponent(i);
			if (c instanceof JRadioButton) {
				JRadioButton y = (JRadioButton) c;
				if (x == y)
					continue;
				String yname = y.getName();
				if (yname == null)
					continue;
				if (yname.equals(xname))
					y.setSelected(false);
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		if (!actionsEnabled) {
			return;
		}

		XDB.xflush();
		lastField = (Component) event.getSource();
		if (lastField instanceof JRadioButton) {
			handleRadioButtons((JRadioButton) lastField);
		}
		// first try to find the method on the panel
		Object obj = this;
		Method method = ReflectionUtil.getMethod(obj, event.getActionCommand() + "Action");

		if (method == null) {
			// if its not there then find it on the xframe
			obj = this.xFrame;
			method = ReflectionUtil.getMethod(obj, event.getActionCommand() + "Action");
		}

		if (method == null) {
			// maybe its in the split editor
			obj = this.altParent;
			method = ReflectionUtil.getMethod(obj, event.getActionCommand() + "Action");
		}

		if (method != null) {
			// if we found the method then invoke it
			ReflectionUtil.invoke(method, obj);
		} else {
			System.err.println("Unimplemented Action on " + this.getClass().getName() + ": " + event.getActionCommand()
					+ "Action");
		}
	}

	@Override
	public void stateChanged(ChangeEvent s) {
		if (getXTabbedPanel().getSelectedComponent() == this) {
		}
	}

	public abstract void dismiss();

	public XTabbedPanel getXTabbedPanel() {
		return xtabbedPanel;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
		if (xtabbedPanel != null) {
			xtabbedPanel.setTitleAt(this, title);
		}
	}

	public void toFront() {
		if (xtabbedPanel != null) {
			xtabbedPanel.setSelectedComponent(this);
		}
	}

	public void remove() {
		xFrame.getXTabbedEditorPanel().remove(this);
		xFrame.setAdditionalXMenuBar(null);
	}

	public boolean isActionsEnabled() {
		return actionsEnabled;
	}

	public void setActionsEnabled(boolean actionsEnabled) {
		this.actionsEnabled = actionsEnabled;
	}

	public XFrame getXFrame() {
		return xFrame;
	}

	public void setXFrame(XFrame xFrame) {
		this.xFrame = xFrame;
	}

	public Component getAltParent() {
		return altParent;
	}

	public void setAltParent(Component altParent) {
		this.altParent = altParent;
	}

}
