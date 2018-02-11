package us.thinkable.xswing.access;

import java.util.HashMap;
import java.util.Map;

public class XGroup {
	private String groupName;
	private Map<String, XUser> map = new HashMap<String, XUser>();

	public XGroup(String groupName) {
		this.groupName = groupName;
	}

	public void add(XUser user) {
		this.map.put(user.getUsername(), user);
	}

	public XUser get(String username) {
		return this.map.get(username);
	}

	public Map<String, XUser> getMap() {
		return map;
	}

	public void setMap(Map<String, XUser> map) {
		this.map = map;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

}
