package us.thinkable.xswing.util;

import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import us.thinkable.xcore.FileUtil;
import us.thinkable.xswing.frame.XFrame;

public class XDialog {
	private static final int DIRECTORIES_ONLY = -2;
	private static final int FILES_AND_DIRECTORIES = -1;

	public static int alert(String msg, String... s) {
		JFrame frame = (JFrame) XFrame.frame;
		String title = XFrame.frame == null ? "Alert" : XFrame.frame.getTitle();
		return JOptionPane.showOptionDialog(frame, msg, title, JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, s, s.length > 0 ? s[0] : null);
	}

	public static int info(String msg, String... s) {
		JFrame frame = (JFrame) XFrame.frame;
		String title = XFrame.frame == null ? "Info" : XFrame.frame.getTitle();
		return JOptionPane.showOptionDialog(frame, msg, title, JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, null, s, s.length > 0 ? s[0] : null);
	}

	public static int warning(String msg, String... s) {
		JFrame frame = (JFrame) XFrame.frame;
		String title = XFrame.frame == null ? "Warning" : XFrame.frame.getTitle();
		return JOptionPane.showOptionDialog(frame, msg, title, JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, null, s, s.length > 0 ? s[0] : null);
	}

	public static int error(String msg, String... s) {
		JFrame frame = (JFrame) XFrame.frame;
		String title = XFrame.frame == null ? "Error" : XFrame.frame.getTitle();
		return JOptionPane.showOptionDialog(frame, msg, title, JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE, null, s, s.length > 0 ? s[0] : null);
	}

	public static int about(String htmlFileName, String... s) throws IOException {
		JFrame frame = (JFrame) XFrame.frame;
		String html = FileUtil.fileRead(htmlFileName);
		html = formatHTML(html);
		String title = XFrame.frame == null ? "About" : "About " + XFrame.frame.getTitle();
		return JOptionPane.showOptionDialog(frame, html, title, JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, null, s, s.length > 0 ? s[0] : null);
	}
	
	private static String formatHTML(String html) {
		if (html == null) return "";
		if (!html.toLowerCase().startsWith("<html")) {
			html = "<html>" + html + "</html>";
		}
		html = html.replaceAll("\r", "");
		html = html.replaceAll("\n", "<br>");
		System.out.println(html);
		return html;
	}

	public static String input(String msg, String... s) {
		JFrame frame = (JFrame) XFrame.frame;
		String title = XFrame.frame == null ? "Input" : XFrame.frame.getTitle();
		String result = null;
		if (s.length > 0) {
			result = (String) JOptionPane.showInputDialog(frame, msg, title, JOptionPane.QUESTION_MESSAGE, null, s, s.length > 0 ? s[0] : null);
		} else {
			result = (String) JOptionPane.showInputDialog(frame, msg, title, JOptionPane.QUESTION_MESSAGE);
		}
		return result;
	}

	private static String lastFile = null;

	private static String fileDialog(int mode, String msg, String defaultValue, String... extensions) {
		String result = null;
		boolean approved = false;
		if (defaultValue == null) {
			if (lastFile != null) {
				defaultValue = lastFile;
			} else {
				defaultValue = ".";
			}
		}
		JFileChooser chooser = new JFileChooser();
		chooser.setCurrentDirectory(new java.io.File(defaultValue));
		// chooser.setAcceptAllFileFilterUsed(false);
		chooser.setDialogTitle(msg);
		if (mode == java.awt.FileDialog.LOAD) {
			chooser.setFileSelectionMode(JFileChooser.OPEN_DIALOG);
			approved = chooser.showOpenDialog(XFrame.frame) == JFileChooser.APPROVE_OPTION;
		} else if (mode == java.awt.FileDialog.SAVE) {
			approved = chooser.showSaveDialog(XFrame.frame) == JFileChooser.APPROVE_OPTION;
			if (approved && chooser.getSelectedFile().exists()) {
				int button = XDialog.warning(chooser.getSelectedFile().getName() + " already exists. Do you want to save over it?", "Cancel", "Save");
				approved = button == 1;
			}
		} else if (mode == FILES_AND_DIRECTORIES) {
			chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
			approved = chooser.showOpenDialog(XFrame.frame) == JFileChooser.APPROVE_OPTION;
		} else if (mode == DIRECTORIES_ONLY) {
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			approved = chooser.showOpenDialog(XFrame.frame) == JFileChooser.APPROVE_OPTION;
		}
		if (approved) {
			File file = chooser.getSelectedFile();
			result = file.getAbsolutePath();
			lastFile = result;
		}
		return result;
	}

	public static String load(String msg, String defaultValue, String... extensions) {
		return fileDialog(java.awt.FileDialog.LOAD, msg, defaultValue, extensions);
	}

	public static String save(String msg, String defaultValue, String... extensions) {
		return fileDialog(java.awt.FileDialog.SAVE, msg, defaultValue, extensions);
	}

	public static String fileOrDirectory(String msg, String defaultValue, String... extensions) {
		return fileDialog(FILES_AND_DIRECTORIES, msg, defaultValue, extensions);
	}

	public static String directory(String msg, String deflt) {
		return fileDialog(DIRECTORIES_ONLY, msg, null);
	}
}
