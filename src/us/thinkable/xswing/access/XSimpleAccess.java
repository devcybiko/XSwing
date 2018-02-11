package us.thinkable.xswing.access;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.JOptionPane;

import us.thinkable.xcore.FileUtil;

public class XSimpleAccess extends XAccess {
	public XSimpleAccess(String fname) throws IOException {
		read(fname);
		String username = (String) JOptionPane.showInputDialog(null, "Username:", "LOGIN", JOptionPane.QUESTION_MESSAGE, null, null, null);
		if (username == null) {
			System.exit(0);
		}
		String password = (String) JOptionPane.showInputDialog(null, "Password:", "LOGIN", JOptionPane.QUESTION_MESSAGE, null, null, null);
		if (password == null) {
			System.exit(0);
		}
		this.setUsername(username);
		if (!this.isVerified(password)) {
			JOptionPane.showOptionDialog(null, "Login Failed", "LOGIN", JOptionPane.OK_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE, null, null, null);
			System.exit(0);
		}
	}

	private void read(String fname) throws IOException {
		List<String> list = FileUtil.fileReadLinesSansComments(fname);
		for (String line : list) {
			System.out.println(line);
			String[] fields = line.split("\\t");
			String username = fields[0];
			String password = fields[1];
			String groupNames = fields[2];
			this.newUser(username, password, groupNames);
		}
	}
}
