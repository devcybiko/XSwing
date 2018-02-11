package us.thinkable.xswing.frame;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JSplitPane;
import javax.swing.ProgressMonitor;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;

import us.thinkable.xcore.MemoryWarningSystem;
import us.thinkable.xcore.XDB;
import us.thinkable.xcore.reflect.ReflectionUtil;
import us.thinkable.xswing.access.XAccess;
import us.thinkable.xswing.access.XAccessInterface;
import us.thinkable.xswing.editor.XEditor;
import us.thinkable.xswing.panel.VTextIcon;
import us.thinkable.xswing.panel.XButtonTabComponent;
import us.thinkable.xswing.panel.XPanel;
import us.thinkable.xswing.panel.XTabbedPanel;
import us.thinkable.xswing.tool.XTool;
import us.thinkable.xswing.util.XDialog;

@SuppressWarnings("serial")
public abstract class XFrame extends JFrame implements ActionListener, WindowListener, XAccessInterface, ComponentListener {
	public static XFrame frame;
	private static List<XFrame> xframeList = new ArrayList<XFrame>();

	private XTabbedPanel xtabbedEditorPanel = null;
	private XTabbedPanel xtabbedToolPanel = null;
	private XMenuBar xmenuBar = null;
	private JSplitPane jsplitPane = null;
	private ProgressMonitor progressMonitor;
	protected XAccess access = null;

	protected XFrame(String title) {
		super(title);
	}

	public XFrame(String title, String menuFname) throws IOException {
		super(title);
		init(menuFname, null);
	}

	public XFrame(String title, String menuFname, String rightMenuFname) throws IOException {
		super(title);
		init(menuFname, rightMenuFname);
	}

	public XFrame(String title, String menuFname, XAccess access) throws IOException {
		super(title);
		this.access = access;
		init(menuFname, null);
	}

	public XFrame(String title, String menuFname, String rightMenuFname, XAccess access) throws IOException {
		super(title);
		this.access = access;
		init(menuFname, rightMenuFname);
	}

	private void init(String menuFname, String rightMenuFname) throws IOException {
		initMenuBar(menuFname, rightMenuFname);
		initMemoryWarningSystem();
		initTabBar();
		initSize();
		this.addComponentListener(this);
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		XFrame.frame = this;
	}

	public static void registerXFrame(XFrame frame) {
		XFrame.xframeList.add(frame);
	}

	public static void unregisterXFrame(XFrame frame) {
		XFrame.xframeList.remove(frame);
	}

	public void initSize() {
		Integer width = (Integer) XDB.xget("XFrame_width", new Integer(500));
		Integer height = (Integer) XDB.xget("XFrame_height", new Integer(300));
		this.setSize(width, height);
	}

	private void initMemoryWarningSystem() {
		MemoryWarningSystem.setPercentageUsageThreshold(0.6);

		MemoryWarningSystem mws = new MemoryWarningSystem();
		mws.addListener(new MemoryWarningSystem.Listener() {
			public void memoryUsageLow(long usedMemory, long maxMemory) {
				double percentageUsed = ((double) usedMemory) / maxMemory * 100.0;
				int ok = XDialog.alert(String.format("Memory is low: %2.0f%% used", percentageUsed), "Continue", "Quit Program");
				if (ok == 1) {
					System.exit(0);
				}
				MemoryWarningSystem.setPercentageUsageThreshold(0.8);
			}
		});
	}

	protected void initMenuBar(String menuFname, String rightMenuFname) throws IOException {
		this.addWindowListener(this);
		xmenuBar = new XMenuBar(this, menuFname, rightMenuFname);
		this.setJMenuBar(xmenuBar);
	}

	protected void initTabBar() {
		// Create the tabpanel
		xtabbedEditorPanel = new XTabbedPanel();
		this.add(xtabbedEditorPanel);
	}

	public void progressMonitor(String msg, int max, SwingWorker task) {
		this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		progressMonitor = new ProgressMonitor(XFrame.frame.xtabbedEditorPanel.getSelectedComponent(), msg, "", 0, max);
		progressMonitor.setMillisToDecideToPopup(100);
		XFrame.frame.setEnabled(false);
		task.execute();
	}

	public void progressMonitorUpdate(String msg, int n) {
		progressMonitor.setNote(msg);
		progressMonitor.setProgress(n);
		System.out.println(msg);
	}

	public void progressMonitorHide() {
		progressMonitor.close();
		this.setCursor(null);
		XFrame.frame.setEnabled(true);
	}

	public boolean progressMonitorIsCanceled() {
		return progressMonitor.isCanceled();
	}

	public void addEditor(XEditor xeditor, String title) {
		if (xeditor != null) {
			// its a detached panel
			if (title == null) {
				Component comp = new XButtonTabComponent(xeditor);
				xtabbedEditorPanel.addPanel(xeditor, xeditor.getTitle(), comp);
			} else {
				Component comp = new XButtonTabComponent(xeditor);
				xtabbedEditorPanel.addPanel(xeditor, title, comp);
			}
			xeditor.toFront();
		}
	}

	public void addTool(XTool xtool, String title) {
		if (jsplitPane == null) {
			// create the split pane
			this.remove(xtabbedEditorPanel);
			xtabbedToolPanel = new XTabbedPanel(SwingConstants.LEFT);
			jsplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, xtabbedToolPanel, xtabbedEditorPanel);
			this.add(jsplitPane, BorderLayout.CENTER);
		}
		Icon icon = new VTextIcon(xtool, title, VTextIcon.ROTATE_LEFT);
		Component comp = new XButtonTabComponent(xtool, icon);
		xtabbedToolPanel.addPanel(xtool, null, comp);
		jsplitPane.setDividerLocation(200);
	}

	public void setAdditionalXMenuBar(XMenuBar xpanelMenuBar) {
		if (xpanelMenuBar == null && this.xmenuBar != null) {
			this.xmenuBar.restoreMenus();
			this.setJMenuBar(xmenuBar);
			xmenuBar.revalidate();
		} else if (xpanelMenuBar != null && this.xmenuBar != null) {
			// attach the panel's menubar to the frame's menubar
			XMenuBar newMenuBar = new XMenuBar(this.xmenuBar, xpanelMenuBar);
			this.setJMenuBar(newMenuBar);
			newMenuBar.revalidate();
		}
	}

	public XTabbedPanel getXTabbedEditorPanel() {
		return xtabbedEditorPanel;
	}

	public XTabbedPanel getXTabbedToolPanel() {
		return xtabbedToolPanel;
	}

	public void actionPerformed(ActionEvent event) {
		Method method = ReflectionUtil.getMethod(this, event.getActionCommand() + "Action");
		if (method != null) {
			ReflectionUtil.invoke(method, this);
		} else {
			System.err.println("Unimplented action on XFrame: " + event.getActionCommand() + "Action");
		}
	}

	public void exit() {
		this.processWindowEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
	}

	public void setVisible(boolean b) {
		if (b == true) {
			initAction();
		}
		super.setVisible(b);
	}

	public abstract void initAction();

	@Override
	public void windowActivated(WindowEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowClosed(WindowEvent arg0) {
		Integer width = this.getWidth();
		Integer height = this.getHeight();
		XDB.xput("XFrame_width", width);
		XDB.xput("XFrame_height", height);
		XDB.xflush();
		this.setVisible(false);
	}

	@Override
	public void windowClosing(WindowEvent arg0) {
		List<XEditor> editors = this.getAllEditors();
		for (XEditor editor : editors) {
			if (editor.isDirty()) {
				int button = XDialog.warning("You have unsaved changes.", "Cancel Exit", "Exit Program");
				if (button == 0) { return; }
				break;
			}
		}
		for (XFrame frame : xframeList) {
			frame.dispose();
		}
		this.dispose();
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		// System.out.println(e);
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		System.out.println(e);
	}

	@Override
	public void windowIconified(WindowEvent e) {
		System.out.println(e);
	}

	@Override
	public void windowOpened(WindowEvent e) {
		// System.out.println(e);
	}

	@Override
	public void componentResized(ComponentEvent e) {
		// System.out.println(e);
		XDB.xput("XFrame_width", e.getComponent().getWidth());
		XDB.xput("XFrame_height", e.getComponent().getHeight());
	}

	@Override
	public void componentMoved(ComponentEvent e) {
		// System.out.println(e);
	}

	@Override
	public void componentShown(ComponentEvent e) {
		// System.out.println(e);
	}

	@Override
	public void componentHidden(ComponentEvent e) {
		System.out.println(e);
	}

	public XAccess getAccess() {
		return access;
	}

	public void setAccess(XAccess access) {
		this.access = access;
	}

	public XEditor getSelectedEditor() {
		XTabbedPanel tabs = this.getXTabbedEditorPanel();
		XEditor tab = (XEditor) tabs.getSelectedComponent();
		return tab;
	}

	public List<XEditor> getAllEditors() {
		List<XEditor> results = new ArrayList<XEditor>();
		List<XPanel> panels = this.getXTabbedEditorPanel().getAllPanels();
		for (XPanel panel : panels) {
			if (panel instanceof XEditor) {
				results.add((XEditor) panel);
			}
		}
		return results;
	}

}
