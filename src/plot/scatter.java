package plot;

import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class scatter extends JFrame{
	double[] xData;
	double[] yData;
	XYDataset dataset;
	JFreeChart chart;
	ChartPanel chartPanel;
	String chartTitle;
	String xLabel;
	String yLabel;
	
	public scatter(String title,String xlabel,String ylabel,double[] xSeries,double[] ySeries){
		chartTitle = title;
		xLabel = xlabel;
		yLabel = ylabel;
		xData = xSeries;
		yData = ySeries;
		if(xData.length==yData.length){
		dataset = createDataSet();
		chart = createChart();
		chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new java.awt.Dimension(800,800));
		chartPanel.setFillZoomRectangle(true);
		setContentPane(chartPanel);
		}else{
			System.out.println("ERROR..... the size of x and y are not equal with x having "+xData.length+" and y having "+yData.length);
			System.exit(0);
		}
	}
	
	public XYDataset createDataSet(){
		XYSeriesCollection result = new XYSeriesCollection();
		XYSeries series = new XYSeries("Random");	
		for(int i=0;i<xData.length;i++){
			series.add(xData[i],yData[i]);
		}
		result.addSeries(series);
		return result;
	}
	
	private JFreeChart createChart(){
		JFreeChart chart = ChartFactory.createScatterPlot(chartTitle,xLabel,yLabel, dataset, PlotOrientation.VERTICAL, false, true, false);
		return chart;
	}
}
