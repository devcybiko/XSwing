package us.thinkable.xswing.editor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import us.thinkable.xcore.StringUtil;
import us.thinkable.xcore.reflect.ReflectionUtil;
import us.thinkable.xswing.frame.XFrame;

public class XPreferencesEditor<T extends XPreferences> extends XEditor implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private T data;

	public XPreferencesEditor(XFrame frame, String title, String menuFname, T data) throws IOException {
		super(frame, title, menuFname);
		this.data = data;
		this.data.setParent(this);
		populate();
	}

	public T get() {
		return data;
	}

	public void update() {
		// copy data to form
	}

	private String getType(String name, String alt) {
		if (name.endsWith("Check")) return "Check";
		if (name.endsWith("Combo")) return "Combo";
		if (name.endsWith("Date")) return "Date";
		if (name.endsWith("File")) return "File";
		if (name.endsWith("Input")) return "Input";
		if (name.endsWith("Label")) return "Label";
		if (name.endsWith("Nonmod")) return "Nonmod";
		if (name.endsWith("Password")) return "Password";
		if (name.endsWith("Radio")) return "Radio";
		if (name.endsWith("Text")) return "Text";
		if (name.endsWith("Time")) return "Time";
		return alt;
	}

	private void populate() throws IOException {
		int x = 100;
		int y = 30;
		List<Map<String, String>> form = new ArrayList<Map<String, String>>();

		for (Field field : getFields(data)) {
			String name = data.makeName(field);
			String type = getType(name, "Input");
			String title = field.getName();
			Map<String, String> map = new HashMap<String, String>();
			map.put("name", name);
			map.put("type", type);
			map.put("title", makeTitle(title) + ":");
			map.put("x", "" + x);
			map.put("y", "" + y);
			map.put("w", "500");
			map.put("items", getItems(field));
			y += 30;
			form.add(map);
		}
		for (Method method : getMethods(data)) {
			String buttonName = method.getName().substring(0, method.getName().length() - 6);
			String type = "Button";
			String title = buttonName;
			Map<String, String> map = new HashMap<String, String>();
			map.put("name", buttonName);
			map.put("command", buttonName);
			map.put("type", type);
			map.put("title", makeTitle(title));
			map.put("x", "" + x);
			map.put("y", "" + y);
			y += 30;
			form.add(map);
		}
		this.deserialize(form);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		Method method = ReflectionUtil.getMethod(data, event.getActionCommand() + "Action");

		if (method != null) {
			// if we found the method then invoke it
			ReflectionUtil.invoke(method, data);
		} else {
			super.actionPerformed(event);
		}
	}

	private static class MethodComparator implements Comparator<Method> {
		public MethodComparator() {
			//
		}

		@Override
		public int compare(Method o1, Method o2) {
			return o1.getName().compareTo(o2.getName());
		}

	}

	private static class FieldComparator implements Comparator<Field> {
		public FieldComparator() {
			//
		}

		@Override
		public int compare(Field o1, Field o2) {
			return o1.getName().compareTo(o2.getName());
		}

	}

	public String getItems(Field obj) {
		String name = obj.getName() + "Items";
		Field[] fields = data.getClass().getDeclaredFields();
		for (Field field : fields) {
			int modifiers = field.getModifiers();
			if ((modifiers & Modifier.STATIC) != 0) {
				if (field.getName().equals(name)) {
					try {
						return (String) field.get(name);
					} catch (IllegalArgumentException | IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		return null;
	}

	public static List<Field> getFields(Object obj) {
		Field[] fields = obj.getClass().getDeclaredFields();
		Arrays.sort(fields, new FieldComparator());
		List<Field> results = new ArrayList<Field>();
		for (Field field : fields) {
			int modifiers = field.getModifiers();
			if ((modifiers & Modifier.PUBLIC) != 0 && (modifiers & Modifier.STATIC) == 0) {
				results.add(field);
			}
		}
		return results;
	}

	private static List<Method> getMethods(Object obj) {
		Method[] methods = obj.getClass().getMethods();
		Arrays.sort(methods, new MethodComparator());
		List<Method> results = new ArrayList<Method>();
		for (Method method : methods) {
			String name = method.getName();
			if (name.endsWith("Action")) {
				results.add(method);
			}
		}
		return results;
	}

	private String makeTitle(String s) {
		String result = "";
		s = StringUtil.capitalize(s);
		String type = getType(s, "");
		s = s.substring(0, s.length() - type.length());
		String space = "";
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (StringUtil.isUpper(c)) {
				result += space;
				space = " ";
			}
			result += c;
		}
		return result;
	}
}
