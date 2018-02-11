package us.thinkable.xswing.panel;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JToolBar;

import us.thinkable.xcore.FileUtil;
import us.thinkable.xswing.zed.XUtil;

public class XToolBar extends JToolBar {
	public XToolBar(XPanel xpanel, String fname) throws IOException {
		List<String> lines = FileUtil.fileReadLinesSansComments(fname);
		for (String line : lines) {
			String[] fields = line.trim().split("\\t");
			int i = 0;
			String action = null;
			if (fields.length > i) {
				action = fields[i++];
			}
			String image = null;
			if (fields.length > i) {
				image = fields[i++];
			}
			String cmd = null;
			if (fields.length > i) {
				cmd = fields[i++];
			}
			if (action.startsWith("---")) {
				this.addSeparator();
			} else {
				JButton button = new JButton();
				button.setActionCommand(action);
				button.addActionListener(xpanel);
				this.add(button);
				ImageIcon imageIcon = XUtil.createImageIcon(image);
				button.setIcon(imageIcon);
			}

		}
	}
}
