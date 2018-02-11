package us.thinkable.xswing.editor;

import java.awt.BorderLayout;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.parser.ParserDelegator;

import us.thinkable.xcore.FileUtil;
import us.thinkable.xswing.frame.XFrame;

@SuppressWarnings("serial")
public class XCodeEditor extends XEditor implements DocumentListener, ChangeListener {
	private JEditorPane editorPane = null;
	private JScrollPane scrollPane = null;
	private List<Modifier> modifierList = new ArrayList<Modifier>();

	public XCodeEditor(XFrame frame, String title, String menuFname) throws IOException {
		super(frame, title, menuFname);
		editorPane = new JEditorPane();
		editorPane.setEditable(true);
		editorPane.setEditorKit(new HTMLEditorKit());
		scrollPane = new JScrollPane(editorPane);
		this.setLayout(new BorderLayout());
		this.add(scrollPane, BorderLayout.CENTER);
	}

	private void doLoadCommand(JTextComponent textComponent, String text) throws IOException {
		StringReader reader = null;
		reader = new StringReader(text);

		// Create empty HTMLDocument to read into
		HTMLEditorKit htmlKit = new HTMLEditorKit();
		HTMLDocument htmlDoc = (HTMLDocument) htmlKit.createDefaultDocument();
		// Create parser (javax.swing.text.html.parser.ParserDelegator)
		HTMLEditorKit.Parser parser = new ParserDelegator();
		// Get parser callback from document
		HTMLEditorKit.ParserCallback callback = htmlDoc.getReader(0);
		// Load it (true means to ignore character set)
		parser.parse(reader, callback, true);
		// Replace document
		textComponent.setDocument(htmlDoc);
		reader.close();
	}

	public Modifier addModifier(String word, String s) {
		Modifier modifier = new Modifier(word, s);
		modifierList.add(modifier);
		return modifier;
	}

	public void addText(String s) throws IOException {
		doLoadCommand(editorPane, s);
	}

	public void setText(String s) {
		String html = markUpText(s);
		editorPane.setText(html);
		setDirty(false);
	}

	private String markUpText(String s) {
		String result = "";
		s = s.replaceAll("(\r\n|\n)", "<br/>");
		s = s.replaceAll("\t", "&nbsp;&nbsp;&nbsp;&nbsp;");
		String pattern = "";
		String bar = "";
		for (Modifier modifier : modifierList) {
			pattern += bar + modifier.getRegex();
			bar = "|";
		}

		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(s);
		int cursor = 0;
		while (m.find()) {
			String matched = s.substring(m.start(), m.end());
			Modifier modifier = findModifier(matched);
			result += s.substring(cursor, m.start());
			result += matched.replaceAll(modifier.getRegex(), modifier.replacement());
			cursor = m.end();
		}
		result += s.substring(cursor);
		return "<html><body><div style=\"font-family:Monospace; font-size:16; font-style:bold; color:#222222\">"+result+"</div></body></html>";
	}

	private Modifier findModifier(String s) {
		for (Modifier modifier : modifierList) {
			if (s.matches(modifier.getRegex())) { return modifier; }
		}
		return null;
	}

	public String getText() {
		return editorPane.getText();
	}

	public void setEditable(boolean editable) {
		editorPane.setEditable(editable);
	}

	@Override
	public void changedUpdate(DocumentEvent arg0) {
		setDirty(true);
	}

	@Override
	public void insertUpdate(DocumentEvent arg0) {
		setDirty(true);
	}

	@Override
	public void removeUpdate(DocumentEvent arg0) {
		setDirty(true);
	}

	@Override
	public void readFile(String fname) throws IOException {
		String text = FileUtil.fileRead(fname);
		this.setText(text);
	}

	@Override
	public void saveFile(String fname) throws IOException {
		String text = this.getText();
		FileUtil.fileWrite(new File(fname), text);
	}

	public void setJava() {
		String keywordModifier = "b,color=#990000";
		String commentModifier = "b,color=green";
		String literalModifier = "b,color=blue";
		String dataTypeModifier = "b,color=#990000";
		String classModifier = "u,color=black";
		String annotationModifier = "b,color=gray";
		String methodModifier = "i,color=#222222";

		Modifier comment = this.addModifier("//", commentModifier);
		comment.setRegex("([/][/].*?[<]br[/][>])");

		comment = this.addModifier("/*", commentModifier);
		comment.setRegex("([/][*].*?[*][/])");

		comment = this.addModifier("@", annotationModifier);
		comment.setRegex("([@][A-Z][A-Za-z0-9_]*)");

		comment = this.addModifier("method", methodModifier);
		comment.setRegex("([.][A-Za-z0-9_]*[(])");

		this.addModifier("abstract", keywordModifier);
		this.addModifier("continue", keywordModifier);
		this.addModifier("for", keywordModifier);
		this.addModifier("new", keywordModifier);
		this.addModifier("switch", keywordModifier);
		this.addModifier("assert", keywordModifier);
		this.addModifier("default", keywordModifier);
		this.addModifier("goto", keywordModifier);
		this.addModifier("package", keywordModifier);
		this.addModifier("synchronized", keywordModifier);
		this.addModifier("boolean", keywordModifier);
		this.addModifier("do", keywordModifier);
		this.addModifier("if", keywordModifier);
		this.addModifier("private", keywordModifier);
		this.addModifier("this", keywordModifier);
		this.addModifier("break", keywordModifier);
		this.addModifier("double", keywordModifier);
		this.addModifier("implements", keywordModifier);
		this.addModifier("protected", keywordModifier);
		this.addModifier("throw", keywordModifier);
		this.addModifier("byte", keywordModifier);
		this.addModifier("else", keywordModifier);
		this.addModifier("import", keywordModifier);
		this.addModifier("public", keywordModifier);
		this.addModifier("throws", keywordModifier);
		this.addModifier("case", keywordModifier);
		this.addModifier("enum", keywordModifier);
		this.addModifier("instanceof", keywordModifier);
		this.addModifier("return", keywordModifier);
		this.addModifier("transient", keywordModifier);
		this.addModifier("catch", keywordModifier);
		this.addModifier("extends", keywordModifier);
		this.addModifier("try", keywordModifier);
		this.addModifier("final", keywordModifier);
		this.addModifier("interface", keywordModifier);
		this.addModifier("static", keywordModifier);
		this.addModifier("class", keywordModifier);
		this.addModifier("finally", keywordModifier);
		this.addModifier("long", keywordModifier);
		this.addModifier("strictfp", keywordModifier);
		this.addModifier("volatile", keywordModifier);
		this.addModifier("native", keywordModifier);
		this.addModifier("super", keywordModifier);
		this.addModifier("while", keywordModifier);

		Modifier literal = this.addModifier("\"", literalModifier);
		literal.setRegex("([\"].*?[\"])");

		this.addModifier("null", literalModifier);
		this.addModifier("true", literalModifier);
		this.addModifier("false", literalModifier);
		this.addModifier("([0-9]+)", literalModifier);

		this.addModifier("int", dataTypeModifier);
		this.addModifier("short", dataTypeModifier);
		this.addModifier("void", dataTypeModifier);
		this.addModifier("const", dataTypeModifier);
		this.addModifier("float", dataTypeModifier);
		this.addModifier("char", dataTypeModifier);

		Modifier clazz = this.addModifier("clazz", classModifier);
		clazz.setRegex("\\b([A-Z][A-Za-z0-9_]*)\\b");

	}

	public static class Modifier {
		private boolean italic;
		private boolean bold;
		private boolean underline;
		private String color;
		private String regex;

		public Modifier(String word, String attributes) {
			this.regex = "\\b(" + Matcher.quoteReplacement(word) + ")\\b";
			color = "black";
			String[] attrs = attributes.split(",");
			for (String attr : attrs) {
				if (attr.equals("b")) {
					this.setBold(true);
				} else if (attr.equals("i")) {
					this.setItalic(true);
				} else if (attr.equals("u")) {
					this.setUnderline(true);
				} else if (attr.startsWith("color=")) {
					String[] words = attr.split("=");
					this.setColor(words[1]);
				}
			}
		}

		protected String replacement() {
			String result = "";
			if (getColor() != null) {
				result += "<font color=\"" + getColor() + "\">";
			}
			if (isBold()) result += "<b>";
			if (isItalic()) result += "<i>";
			if (isUnderline()) result += "<u>";

			result += "$1";

			if (isUnderline()) result += "</u>";
			if (isItalic()) result += "</i>";
			if (isBold()) result += "</b>";
			if (getColor() != null) {
				result += "</font>";
			}
			return result;
		}

		public boolean isItalic() {
			return italic;
		}

		public void setItalic(boolean italic) {
			this.italic = italic;
		}

		public boolean isBold() {
			return bold;
		}

		public void setBold(boolean bold) {
			this.bold = bold;
		}

		public boolean isUnderline() {
			return underline;
		}

		public void setUnderline(boolean underline) {
			this.underline = underline;
		}

		public String getColor() {
			return color;
		}

		public void setColor(String color) {
			this.color = color;
		}

		public String getRegex() {
			return regex;
		}

		public void setRegex(String regex) {
			this.regex = regex;
		}

	}
}