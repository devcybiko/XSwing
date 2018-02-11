package us.thinkable.xswing.zed;

import java.net.URL;

import javax.swing.ImageIcon;

import us.thinkable.xcore.FileUtil;

public class XUtil {

	public static ImageIcon createImageIcon(String path) {
		URL imgURL = FileUtil.getURL(path);
		if (imgURL != null) {
			return new ImageIcon(imgURL);
		} else {
			return null;
		}
	}

}
