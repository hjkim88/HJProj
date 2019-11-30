/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package graph;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.BasicStroke;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 *
 * @author KHJ
 */
public class ClusterScatterPlot {

    private String[][][] data;

    public ClusterScatterPlot() {}

    public ChartPanel scatterPlot(String[][][] data) {
        this.data = data;

        XYDataset dataset = createDataset();
        JFreeChart chart = createChart(dataset);

        return new ChartPanel(chart);
    }

    private XYDataset createDataset()
    {

        XYSeries[] series = new XYSeries[data.length];

        for(int i = 0; i < data.length; i++)
        {
            series[i] = new XYSeries(i+1);
            for(int j = 0; j < data[0][0].length-1; j++) {
                if( (data[i][0][j+1] != null) && (data[i][1][j+1] != null) ) {
                    series[i].add(Double.parseDouble(data[i][0][j+1]), Double.parseDouble(data[i][1][j+1]));
                }
            }
        }

        XYSeriesCollection dataset = new XYSeriesCollection();

        for(int j = 0; j < data.length; j++)
        {
            dataset.addSeries(series[j]);
        }

        return dataset;

    }

    private JFreeChart createChart(XYDataset dataset)
    {
        // create the chart...
        JFreeChart chart = ChartFactory.createScatterPlot(
            "Gene Expression Data Scatter Plot Chart",      // chart title
            data[0][0][0],                      // x axis label
            data[0][1][0],      // y axis label
            dataset,                  // data
            PlotOrientation.HORIZONTAL, // horizontal or vertical
            true,                     // include legend
            true,                     // tooltips
            false                     // urls
        );

        // NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...
        // set the background color for the chart...
        chart.setBackgroundPaint(Color.white);

        // get a reference to the plot for further customisation...
        XYPlot plot = chart.getXYPlot();
        plot.setBackgroundPaint(Color.lightGray);
        plot.setDomainGridlinePaint(Color.white);
        plot.setDomainGridlinesVisible(true);
        plot.setRangeGridlinePaint(Color.white);

        // set the range axis to display integers only...
        //final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        //rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

        // disable bar outlines...
        XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer();
        renderer.setDrawOutlines(true);

        // set up gradient paints for series...
        GradientPaint gp0 = new GradientPaint(
          0.0f, 0.0f, Color.blue,
          0.0f, 0.0f, new Color(0, 0, 64)
          );
        GradientPaint gp1 = new GradientPaint(
          0.0f, 0.0f, Color.green,
          0.0f, 0.0f, new Color(0, 64, 0)
          );
        GradientPaint gp2 = new GradientPaint(
          0.0f, 0.0f, Color.red,
          0.0f, 0.0f, new Color(64, 0, 0)
          );
        GradientPaint gp3 = new GradientPaint(
          0.0f, 0.0f, Color.YELLOW,
          0.0f, 0.0f, new Color(64, 64, 0)
          );

        for(int i=0;i<plot.getSeriesCount();i++)
        {
            renderer.setSeriesStroke(i, new BasicStroke(5.0f));
        }

        renderer.setSeriesPaint(0, gp0);
        renderer.setSeriesPaint(1, gp1);
        renderer.setSeriesPaint(2, gp2);
        renderer.setSeriesPaint(3, gp3);

        // OPTIONAL CUSTOMISATION COMPLETED.
        return chart;

    }

}
