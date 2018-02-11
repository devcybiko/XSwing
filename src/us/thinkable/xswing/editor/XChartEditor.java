package us.thinkable.xswing.editor;

import java.awt.Dimension;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.IOException;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import us.thinkable.xswing.frame.XFrame;

@SuppressWarnings("serial")
public class XChartEditor extends XEditor implements ComponentListener {

	XYSeriesCollection dataset = new XYSeriesCollection();
	ChartPanel chartPanel;

	public XChartEditor(XFrame frame, String title, String menuFname)
			throws IOException {
		super(frame, title, menuFname);
		this.addComponentListener(this);
	}

	public void addSeries(String series, Double[] x, Double[] y) {
		final XYSeries xyseries = new XYSeries(series);
		for (int i = 0; i < x.length; i++) {
			xyseries.add(x[i], y[i]);
		}
		dataset.addSeries(xyseries);
	}

	public void resetSeries() {
		dataset = new XYSeriesCollection();
	}

	public void lineChart(String title, String xAxis, String yAxis) {
		if (chartPanel != null) {
			this.remove(chartPanel);
		}
		final JFreeChart chart = ChartFactory.createXYLineChart(//
				title, //
				xAxis,//
				yAxis,//
				dataset, // data
				PlotOrientation.VERTICAL, true, // include legend
				true, // tooltips
				false // urls
				);
		chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(this.getBounds().getSize());
		this.add(chartPanel);
		this.revalidate();

	}

	public void scatterPlot(String title, String xAxis, String yAxis) {
		if (chartPanel != null) {
			this.remove(chartPanel);
		}
		final JFreeChart chart = ChartFactory.createScatterPlot(//
				title, //
				xAxis,//
				yAxis,//
				dataset, // data
				PlotOrientation.VERTICAL, true, // include legend
				true, // tooltips
				false // urls
				);
		chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(this.getBounds().getSize());
		this.add(chartPanel);
		this.revalidate();
	}

	@Override
	public void componentResized(ComponentEvent e) {
		if (chartPanel != null) {
			Dimension dim = new Dimension(e.getComponent().getWidth(), e
					.getComponent().getHeight());
			chartPanel.setPreferredSize(dim);
		}
	}

	@Override
	public void componentMoved(ComponentEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void componentShown(ComponentEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void componentHidden(ComponentEvent e) {
		// TODO Auto-generated method stub

	}

}