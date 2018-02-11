package us.thinkable.xswing.editor;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import us.thinkable.xcore.XDB;

public class XPreferences {
	private XPreferencesEditor parent;

	public XPreferences() {
		//
	}

	public void setParent(XPreferencesEditor parent) {
		this.parent = parent;
	}

	public boolean validate() {
		return true;
	}

	protected XPreferences copy() {
		Object copy = null;
		try {
			copy = this.getClass().getConstructor().newInstance();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return null;
		}

		for (Field field : XPreferencesEditor.getFields(this)) {
			String name = makeName(field);
			String value = this.parent.getStringValue(name, "");
			try {
				field.set(copy, value);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return (XPreferences) copy;
	}

	protected void update() {
		for (Field field : XPreferencesEditor.getFields(this)) {
			String name = makeName(field);
			String value = this.parent.getStringValue(name, "");
			try {
				field.set(this, value);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	protected void refresh() {
		for (Field field : XPreferencesEditor.getFields(this)) {
			String name = makeName(field);
			try {
				this.parent.setValue(name, (String) field.get(this));
			} catch (IllegalArgumentException | IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	protected void save() {
		if (this.validate()) {
			this.update();
			XDB.xflush();
			this.remove();
		}
	}

	protected void cancel() {
		this.refresh();
		this.remove();
	}

	protected void remove() {
		this.parent.remove();
	}

	public String makeName(Field field) {
		return this.getClass().getSimpleName() + "." + field.getName();
	}

}
