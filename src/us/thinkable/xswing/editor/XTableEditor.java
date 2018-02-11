package us.thinkable.xswing.editor;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import us.thinkable.xcore.StringUtil;
import us.thinkable.xswing.frame.XFrame;

@SuppressWarnings("serial")
public class XTableEditor extends XEditor {
	protected JTable table = null;
	protected JScrollPane tableScrollPane = null;
	protected Object[][] tableData;
	protected String[] highlightPatterns = null;
	protected Color highlightColor = Color.YELLOW;

	public XTableEditor(XFrame frame, String title, String menuFname) throws IOException {
		super(frame, title, menuFname);
		this.setLayout(new GridLayout(1, 0));
	}

	public void setData(String[] headers, Object[][] data, boolean addIt) {
		if (tableScrollPane != null) {
			this.remove(tableScrollPane);
		}
		tableData = data;
		DefaultTableModel model = new DefaultTableModel(data, headers);
		table = new JTable(model) {
			public boolean isCellEditable(int rowIndex, int colIndex) {
				return isEditable();
			}

			public Class getColumnClass(int c) {
				Class clazz = getValueAt(0, c).getClass();
				// if (clazz == Date.class) {
				table.getColumnModel().getColumn(c).setCellRenderer(tableCellRenderer);
				// }
				return clazz;
			}
		};

		table.setPreferredScrollableViewportSize(new Dimension(500, 70));
		table.setFillsViewportHeight(true);

		// Create the scroll pane and add the table to it.
		tableScrollPane = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		if (addIt) {
			// Add the scroll pane to this panel.
			add(tableScrollPane);
		}
	}

	private TableCellRenderer tableCellRenderer = new DefaultTableCellRenderer() {

		SimpleDateFormat f = new SimpleDateFormat("MM/dd/yy hh:mm:ss a");

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			Object savedValue = value;
			
			f.setTimeZone(TimeZone.getDefault());
			if (value instanceof Date) {
				value = f.format(value);
			}
			if (value instanceof String) {
				String value$ = (String) value;
				if (StringUtil.isGUID(value$)) {
					value = "{" + value$.substring(0, 8) + //
							"-" + //
							value$.substring(8, 12) + //
							"-" + //
							value$.substring(12, 16) + //
							"-" + //
							value$.substring(16, 20) + //
							"-" + //
							value$.substring(20, 32) + //
							"}";
				}
			}

			Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

			if (highlightPatterns != null) {
				for (String h : highlightPatterns) {
					if (savedValue.toString().toLowerCase().contains(h)) {
						component.setBackground(highlightColor);
						return component;
					}
				}
			}
			component.setBackground(Color.WHITE);
			return component;
		}
	};

	public Object[][] getData() {
		int numRows = table.getRowCount();
		int numCols = table.getColumnCount();
		Object[][] result = new Object[numRows][numCols];
		javax.swing.table.TableModel model = table.getModel();

		for (int i = 0; i < numRows; i++) {
			for (int j = 0; j < numCols; j++) {
				result[i][j] = model.getValueAt(i, j);
			}
		}
		return result;
	}

	@Override
	public void readFile(String fname) throws IOException {
		// TODO Auto-generated method stub

	}

	public String[] getHighlightPattern() {
		return highlightPatterns;
	}

	public void setHighlightPattern(String highlightPattern) {
		this.highlightPatterns = highlightPattern.toLowerCase().split("\\s|:");
	}

	public Color getHighlightColor() {
		return highlightColor;
	}

	public void setHighlightColor(Color highlightColor) {
		this.highlightColor = highlightColor;
	}

}