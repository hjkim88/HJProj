/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package calculate;

import javax.swing.JTable;
import javax.swing.JProgressBar;
import javax.swing.JRootPane;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import org.jfree.ui.RefineryUtilities;

/**
 *
 * @author KHJ
 */
public class GetPairSet {
    
    private JTable[] table;
    private int clstrNum;
    private int classNum;
    private int rowNum;
    private double[][][] clstrResult;       // [class][axis][cluster]
    private double[][] distanceSet;
    private int majorityNum;
    private viewer.ProgressBarDlg pbd;
    private JProgressBar jpb;

    public GetPairSet(JTable[] t, viewer.MainFrame mf) {
        this.table = t;
        this.clstrNum = global.Variables.clstrNum;                       // k 값 (cluster 수)
        this.classNum = table.length;
        this.rowNum = table[0].getRowCount();
        this.clstrResult = new double[classNum][2][clstrNum];
        this.distanceSet = new double[rowNum-1][rowNum];
        this.majorityNum = global.Variables.majorityNum;

        //pbd = new viewer.ProgressBarDlg(mf, false);
        //RefineryUtilities.centerFrameOnScreen(pbd);
        //pbd.setVisible(true);

        //initVariables();
    }

    private void initVariables() {
        JRootPane jrp = (JRootPane) pbd.getComponent(0);
        JLayeredPane jlp = (JLayeredPane) jrp.getComponent(1);
        JPanel jp = (JPanel) jlp.getComponent(0);
        jpb = (JProgressBar) jp.getComponent(0);
    }

    private double[] makeData(JTable t, int row) {
        int colNum = t.getColumnCount();
        double[] data = new double[colNum-1];
        for(int i = 1; i < colNum; i++) {
            data[i-1] = Double.parseDouble(t.getValueAt(row, i).toString());
        }

        return data;
    }

    public int[][] Kmeans_MD() {
        
        for(int i = 1; i < rowNum-1; i++) {
            for(int j = i+1; j < rowNum; j++) {
                for(int k = 0; k < classNum; k++) {
                    clstrResult[k] = new clustering.KMeans(clstrNum, makeData(table[k], i), makeData(table[k], j)).startKMeans();
                }
                distanceSet[i][j] = new calculate.MeanDistance(clstrResult).getMeanEach();
            }
            //jpb.setValue((i / (rowNum-2))*100);
        }

        return new calculate.GetMaxValue(distanceSet, majorityNum).getResultPair();
    }

}
