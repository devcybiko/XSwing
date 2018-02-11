package us.thinkable.xswing.panel;

import java.awt.Component;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import us.thinkable.xswing.editor.XEditor;

@SuppressWarnings("serial")
public class XTabbedPanel extends JPanel {
	public JTabbedPane jtabbedPane = null;

	public XTabbedPanel() {
		jtabbedPane = new JTabbedPane();
		setLayout(new GridLayout(1, 1));
		jtabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		add(jtabbedPane);
	}

	public XTabbedPanel(int orientation) {
		jtabbedPane = new JTabbedPane(orientation);
		setLayout(new GridLayout(1, 1));
		add(jtabbedPane);
	}

	public void setMyTitle(JPanel panel, String title) {
		int index = jtabbedPane.indexOfComponent(panel);
		if (index != -1) {
			jtabbedPane.setTitleAt(index, title);
		}
	}

	public Component getSelectedComponent() {
		return jtabbedPane.getSelectedComponent();
	}

	public void setSelectedComponent(Component component) {
		jtabbedPane.setSelectedComponent(component);
	}

	public int indexOfTabComponent(Component comp) {
		return jtabbedPane.indexOfTabComponent(comp);
	}

	public String getTitleAt(int i) {
		return jtabbedPane.getTitleAt(i);
	}

	public void addPanel(XPanel xpanel, String title, Component comp) {
		jtabbedPane.addChangeListener(xpanel);
		jtabbedPane.insertTab(title, null, xpanel, null, 0);
		jtabbedPane.setTabComponentAt(0, comp);
	}

	public Component getComponentAt(int i) {
		return jtabbedPane.getComponentAt(i);
	}

	public void remove(Component comp) {
		jtabbedPane.remove(comp);
	}

	public void setTitleAt(Component comp, String title) {
		int i = findComponentIndex(comp);
		jtabbedPane.setTitleAt(i, title);
	}

	public int findComponentIndex(Component x) {
		for (int i = 0; i != -1; i++) {
			// loop forever because tabs.getComponentCount() doesn't work right
			Component c;
			try {
				c = jtabbedPane.getComponentAt(i);
				if (x == c) {
					return i;
				}
			} catch (Exception ex) {
				break;
			}
		}
		return -1;

	}

	public List<XPanel> getAllPanels() {
		List<XPanel> results = new ArrayList<XPanel>();
		for (int i = 0; i != -1; i++) {
			// loop forever because tabs.getComponentCount() doesn't work right
			Component c;
			try {
				c = jtabbedPane.getComponentAt(i);
			} catch (Exception ex) {
				break;
			}
			if (c instanceof XPanel) {
				results.add((XPanel) c);
			}
		}
		return results;
	}

}
