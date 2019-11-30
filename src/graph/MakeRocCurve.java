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
public class MakeRocCurve {

    private String chartName;
    private double[] sensitivity;
    private double[] specificity;

    public MakeRocCurve(String chartName, double[] sensitivity, double[] specificity) {
        this.chartName = chartName;
        this.sensitivity = sensitivity;
        this.specificity = specificity;
    }

    public ChartPanel getChartPanel() {
        XYDataset dataset = createDataset();
        JFreeChart chart = createChart(dataset);

        return new ChartPanel(chart);
    }

    private XYDataset createDataset()
    {
        XYSeries series = new XYSeries(chartName);
        for(int i = 0; i < sensitivity.length; i++) {
            series.add((1-specificity[i]), sensitivity[i]);
            //System.out.println(1-specificity[i] + " " + sensitivity[i]);
        }
        
        XYSeries random = new XYSeries("random");
        random.add(0, 0);
        random.add(1, 1);

        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series);
        dataset.addSeries(random);

        return dataset;
    }

    private JFreeChart createChart(XYDataset dataset)
    {
        // create the chart...
        JFreeChart chart = ChartFactory.createXYLineChart(
            "ROC CURVE",                // chart title
            "False Positive Rate",      // x axis label
            "True Positive Rate",       // y axis label
            dataset,                    // data
            PlotOrientation.VERTICAL,   // horizontal or vertical
            true,                       // include legend
            true,                       // tooltips
            false                       // urls
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
          0.0f, 0.0f, Color.BLACK,
          0.0f, 0.0f, new Color(0, 0, 0)
          );
        GradientPaint gp1 = new GradientPaint(
          0.0f, 0.0f, Color.red,
          0.0f, 0.0f, new Color(64, 0, 0)
          );
        GradientPaint gp2 = new GradientPaint(
          0.0f, 0.0f, Color.green,
          0.0f, 0.0f, new Color(0, 64, 0)
          );
        GradientPaint gp3 = new GradientPaint(
          0.0f, 0.0f, Color.blue,
          0.0f, 0.0f, new Color(0, 0, 64)
          );

        /*
        for(int i=0;i<plot.getSeriesCount();i++)
        {
            renderer.setSeriesStroke(i, new BasicStroke(5.0f));
        }
        */

        renderer.setSeriesStroke(0, new BasicStroke(5.0f));
        renderer.setSeriesStroke(1, new BasicStroke(1.0f));

        renderer.setSeriesPaint(0, gp0);
        renderer.setSeriesPaint(1, gp1);
        renderer.setSeriesPaint(2, gp2);
        renderer.setSeriesPaint(3, gp3);

        // OPTIONAL CUSTOMISATION COMPLETED.
        return chart;

    }

}
