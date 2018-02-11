package us.thinkable.xswing.field;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import us.thinkable.xcore.Context;

public class XContext extends Context {

	public XContext() {
		super();
	}

	public XContext(Map<String, String> map) {
		super(map);
	}

	public String getTitle() {
		return get("title", "title");
	}

	public String getCommand() {
		return getString("command", null);
	}

	public String getName() {
		return get("name", "name");
	}

	public List<String> getItems() {
		List<String> itemList = new ArrayList<String>();
		String items = get("items", "");
		if (items != "") {
			String[] words = items.split(",");
			for (String word : words) {
				itemList.add(word);
			}
		}
		return itemList;
	}

	public Integer getRows() {
		return get("rows", 25);
	}

	public Integer getCols() {
		return get("cols", 40);
	}

	public Integer getX() {
		return get("x", 40);
	}

	public Integer getY() {
		return get("y", 10);
	}

	public Integer getW() {
		return get("w", 125);
	}

	public Integer getH() {
		return get("h", 20);
	}

	public String getValue() {
		return getString("value", "");
	}

	public String getFont() {
		return getString("font", "Monospaced");
	}

	public Integer getFontsize() {
		return getInteger("fontsize", 16);
	}

	public boolean getEnabled() {
		String s = getString("enabled", "true");
		if (s.equals("true"))
			return true;
		return false;
	}
	
	public String getStyle() {
		return getString("style", "load");
	}
}
