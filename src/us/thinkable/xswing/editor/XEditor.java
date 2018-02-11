package us.thinkable.xswing.editor;

import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Map;

import javax.swing.SpringLayout;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import us.thinkable.xcore.FileUtil;
import us.thinkable.xswing.access.XAccess;
import us.thinkable.xswing.access.XAccessInterface;
import us.thinkable.xswing.field.XBorder;
import us.thinkable.xswing.field.XContext;
import us.thinkable.xswing.field.XField;
import us.thinkable.xswing.frame.XFrame;
import us.thinkable.xswing.frame.XMenuBar;
import us.thinkable.xswing.panel.XPanel;
import us.thinkable.xswing.util.XConstants;
import us.thinkable.xswing.util.XDialog;

public class XEditor extends XPanel implements ChangeListener, XAccessInterface {
	protected XMenuBar xmenuBar = null;
	public SpringLayout layout = new SpringLayout();
	private boolean dirty = false;
	private String[] fileNameExtensions = null;
	private String saveDialogTitle = "Save";
	private String saveAsDialogTitle = "Save As";
	private String editedFileName = null;
	private XSplitEditor splitEditor = null;
	private boolean editable = true;

	public XEditor(XFrame frame, String title, String menuFname) throws IOException {
		super(frame, title);
		this.setLayout(layout);
		if (frame != null) {
			xtabbedPanel = frame.getXTabbedEditorPanel();
			if (menuFname != null) {
				xmenuBar = new XMenuBar(this, menuFname);
			}
			frame.addEditor(this, title);
			this.myRepaint();
		}
	}

	public void refresh() {
		this.layout.invalidateLayout(this);
		this.revalidate();
		this.repaint();
	}

	public void position(Component obj, Integer x, Integer y) {
		layout.putConstraint(SpringLayout.WEST, obj, x, SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.NORTH, obj, y, SpringLayout.NORTH, this);
	}

	// any XEditor can show a set of XFields
	public void readForm(String formName) throws IOException {
		List<Map<String, String>> lines = FileUtil.fileReadKeyValues(formName);
		deserialize(lines);
	}

	public void deserialize(List<Map<String, String>> lines) throws IOException {
		for (Map<String, String> map : lines) {
			XContext context = new XContext(map);
			XField newField = add(context);
			if (newField instanceof XBorder) {
				newField.toBack();
			} else {
				newField.toFront();
			}
		}
	}

	public XField add(XContext context) throws IOException {
		try {
			String type = context.get("type", "");
			if (type == null) { throw new IOException("Missing type:" + context.toString()); }
			Class<?> clazz = Class.forName(XConstants.XFIELD_BASE_CLASS + ".X" + type);
			Constructor<?> constructor = clazz.getDeclaredConstructor(XEditor.class, XContext.class);
			return (XField) constructor.newInstance(this, context);
		} catch (Exception ex) {
			throw new IOException(ex.getMessage(), ex);
		}
	}

	public void setValue(String name, String value) {
		Component c = this.getComponent(name);
		if (c != null) {
			XField xfield = (XField) c;
			xfield.setValue(value);
		}
	}

	public String getValue(String name) {
		Component c = this.getComponent(name);
		if (c != null) {
			XField xfield = (XField) c;
			return xfield.getValue();
		}
		return null;
	}

	public String getStringValue(String name, String dflt) {
		String value = getValue(name);
		if (value == null) return dflt;
		return value;
	}

	public Integer getIntegerValue(String name, Integer dflt) {
		String value = getValue(name);
		Integer result = dflt;
		if (value == null) return dflt;
		try {
			result = Integer.parseInt(value);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return result;
	}

	public Boolean getBooleanValue(String name, Boolean dflt) {
		String value = getValue(name);
		Boolean result = dflt;
		if (value == null) return dflt;
		try {
			result = Boolean.parseBoolean(value);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return result;
	}

	public Component getComponent(String name) {
		for (int i = 0; i < this.getComponentCount(); i++) {
			Component c = this.getComponent(i);
			if (c.getName() != null && c.getName().equals(name)) { return c; }
		}
		return null;
	}

	@Override
	public void stateChanged(ChangeEvent s) {
		if (getXTabbedPanel().getSelectedComponent() == this) {
			xFrame.setAdditionalXMenuBar(this.getXMenuBar());
		}
	}

	public XMenuBar getXMenuBar() {
		return xmenuBar;
	}

	public void setXMenuBar(XMenuBar xmenuBar) {
		this.xmenuBar = xmenuBar;
	}

	@Override
	public XAccess getAccess() {
		return xFrame.getAccess();
	}

	public boolean isDirty() {
		return dirty;
	}

	public void setDirty(boolean dirty) {
		this.dirty = dirty;
		if (getXTabbedPanel() != null) {
			getXTabbedPanel().setMyTitle(this, (dirty ? "*" : "") + getTitle());
		} else if (splitEditor != null) {
			splitEditor.setDirty(dirty);
		}
	}

	/**
	 * The open() method should be called after an openDialog() call. If
	 * openDialog() returns non-null, then you should create a new instance of a
	 * class that extends XEditor and call that object's open() method.
	 * 
	 * @param fname
	 * @throws IOException
	 */
	public void open(String fname) throws IOException {
		this.readFile(fname);
		this.toFront();
		this.setEditedFileName(fname);
		this.setDirty(false);
	}

	/**
	 * This is the "deserializer" method for the editor's content. You should
	 * implement this for each XEditor you create. By the time this is called,
	 * the fname should have been checked for existence
	 * 
	 * @param fname
	 * @throws IOException
	 */
	public void readFile(String fname) throws IOException {
		XDialog.error("You have not implemented readFile(String fname) for " + this.getClass().getName());
	}

	/**
	 * This is the "serializer" method for the editor's content. You should
	 * implement this for each XEditor you create. By the time this is called
	 * the fname should have been checked for existence
	 * 
	 * @param fname
	 * @throws IOException
	 */
	public void saveFile(String fname) throws IOException {
		XDialog.error("You have not implemented saveFile(fname) for " + this.getClass().getName());
	}

	/**
	 * Save the currently edited file. returns the name of the file saved or
	 * throws IOexception if there was an error. or returns null if the file was
	 * not dirty. file is not saved if the file was not dirty. if the file has
	 * not been assigned an editedFileName, saveAs() is called
	 * 
	 * @return
	 * @throws IOException
	 */
	public String save() throws IOException {
		if (this.getEditedFileName() == null) {
			// the file has not been edited. save as...
			return this.saveAsDialog();
		}
		if (isDirty()) {
			// if the file is dirty, save it and reset the dirty flag
			saveFile(this.getEditedFileName());
			this.setDirty(false);
		}
		return this.getEditedFileName();
	}

	/**
	 * display the saveAs dialog and return the filename if successful or throw
	 * an exception on error. returns null if the user cancels the saveAs dialog
	 * 
	 * @return
	 * @throws IOException
	 */
	public String saveAsDialog() throws IOException {
		String fileName = XDialog.save(this.getSaveDialogTitle(), this.getEditedFileName(), this.getFileNameExtensions());
		if (fileName != null) {
			saveFile(fileName);
			this.setEditedFileName(fileName);
			this.setDirty(false);
		}
		return fileName;
	}

	/**
	 * if the window is dismissed (thanks to [X] button) we double-check that
	 * the file has been saved first. Call this in preference to remove() if you
	 * want to ensure that the file is saved before the editor is removed.
	 */
	@Override
	public void dismiss() {
		if (this.isDirty()) {
			int button = XDialog.warning("Do you want to save changes you made to " + this.getTitle() + "?", "Save", "Discard Changes", "Continue Editing");
			if (button == 0) {
				try {
					// Save
					this.save();
					this.remove();
					return;
				} catch (Exception ex) {
					XDialog.error("There was a problem saving your file");
					return;
				}
			} else if (button == 1) {
				// discard changes
				this.remove();
				return;
			} else if (button == 2) {
				// continue editing
				return;
			}
		} else {
			// the file is not dirty, go ahead and remove the form
			this.remove();
			return;
		}
	}

	/**
	 * remove() with prejudice. In other words, remove the editor without
	 * checking the dirty bit. To make sure you don't remove the panel before
	 * the edited file is saved, call dismiss() instead.
	 */
	@Override
	public void remove() {
		super.remove();
		XEditor xeditor = (XEditor) getXTabbedPanel().getSelectedComponent();
		if (xeditor != null) {
			xFrame.setAdditionalXMenuBar(xeditor.getXMenuBar());
		} else {
			xFrame.setAdditionalXMenuBar(null);
		}
		myRepaint();
	}

	/**
	 * this is a special repaint method. It waits a short sub-second period of
	 * time before doing a repaint. The problem is that some repaint events
	 * don't happen within the current thread. So you have to wait for the
	 * thread to exit. Hence the timer.
	 */
	public void myRepaint() {
		new Thread() {
			public void run() {
				try {
					Thread.sleep(200);
					xFrame.repaint();
				} catch (Exception exc) {
					System.out.println(exc);
				}
			}
		}.start();
	}

	/**
	 * set the editedFileName update the tab with the new edited filename
	 * 
	 * @param fullFname
	 */
	public void setEditedFileName(String fullFname) {
		File file = new File(fullFname);
		String fname = file.getName();
		this.setTitle(fname);
		this.editedFileName = fullFname;
		this.setDirty(this.isDirty()); // set the asterisk
	}

	public String getEditedFileName() {
		return this.editedFileName;
	}

	public XMenuBar getXmenuBar() {
		return xmenuBar;
	}

	public void setXmenuBar(XMenuBar xmenuBar) {
		this.xmenuBar = xmenuBar;
	}

	public String[] getFileNameExtensions() {
		return fileNameExtensions;
	}

	public void setFileNameExtensions(String[] fileNameExtensions) {
		this.fileNameExtensions = fileNameExtensions;
	}

	public String getSaveDialogTitle() {
		return saveDialogTitle;
	}

	public void setSaveDialogTitle(String saveDialogTitle) {
		this.saveDialogTitle = saveDialogTitle;
	}

	public String getSaveAsDialogTitle() {
		return saveAsDialogTitle;
	}

	public void setSaveAsDialogTitle(String saveAsDialogTitle) {
		this.saveAsDialogTitle = saveAsDialogTitle;
	}

	public XSplitEditor getSplitEditor() {
		return splitEditor;
	}

	public void setSplitEditor(XSplitEditor splitEditor) {
		this.splitEditor = splitEditor;
	}

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	};

}
