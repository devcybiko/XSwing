package us.thinkable.xswing.access;

import java.util.HashMap;
import java.util.Map;

public class XAccess {
	private Map<String, XGroup> groupMap = new HashMap<String, XGroup>();
	private Map<String, XUser> userMap = new HashMap<String, XUser>();
	private String username;

	//
	// note: all access is via string names. XGroup and XUser are internal
	//
	public XAccess() {
		//
	}

	// returns a new group if it doesn't exist
	// if it already exists, returns null
	public XGroup newGroup(String groupName) {
		XGroup group = groupMap.get(groupName);
		if (group == null) {
			group = new XGroup(groupName);
			groupMap.put(groupName, group);
		} else {
			group = null;
		}
		return group;
	}

	// given a comma-separated list of groups, will create the groups
	public String[] newGroups(String groupNames) {
		String[] list = groupNames.split(",");
		for (String groupName : list) {
			newGroup(groupName);
		}
		return list;
	}

	// returns a new user if it doesn't exist
	// if it already exists, returns null
	// groupnames is a comma separated list of group names
	public XUser newUser(String userName, String password, String groupNames) {
		XUser user = userMap.get(userName);
		if (user == null) {
			String[] list = newGroups(groupNames);
			user = new XUser(userName, password);
			userMap.put(user.getUsername(), user);
			for (String groupName : list) {
				this.newGroup(groupName);
				this.addToGroup(userName, groupName);
			}
		} else {
			user = null;
		}
		return user;
	}

	public void addToGroup(String username, String groupName) {
		XGroup group = groupMap.get(groupName);
		XUser user = userMap.get(username);
		if (group != null && user != null) {
			group.add(user);
		} else {
			if (user == null) {
				System.out.println("ERROR: Unknown user=" + username);
			} else {
				System.out.println("ERROR: Unknown group=" + groupName);
			}
		}
	}

	// if username is null allow access
	// if groupNames is null allow access
	// groupNames is a comma-separated list of groupnames
	public boolean hasAccess(String groupNames) {
		boolean result = false;
		if (username == null || groupNames == null) {
			result = true;
		} else {
			XUser user = userMap.get(username);
			if (user == null) {
				System.out.println("ERROR: Unknown user=" + username);
			} else {
				String[] list = groupNames.split(",");
				for (String groupName : list) {
					XGroup group = groupMap.get(groupName);
					if (group != null && group.get(username) != null) {
						result = true;
						break;
					}
				}
			}
		}
		return result;
	}

	public boolean isVerified(String password) {
		boolean result = false;
		XUser user = this.userMap.get(username);
		if (user == null) {
			result = false;
		} else {
			result = user.getPassword().equals(password);
		}
		return result;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

}
