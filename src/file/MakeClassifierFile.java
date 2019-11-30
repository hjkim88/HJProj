/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package file;

import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.io.IOException;
import javax.swing.JTable;
import javax.swing.JProgressBar;
import org.jfree.ui.RefineryUtilities;
import javax.swing.JRootPane;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;

/**
 *
 * @author KHJ
 */
public class MakeClassifierFile {

    private JTable[] table;
    private int clstrNum;
    private int classNum;
    private int rowNum;
    private double[][][] clstrResult;       // [class][axis][cluster]
    private double[][] distanceSet;
    private int majorityNum;
    private String path;
    private FileWriter fw;
    private BufferedWriter bw;
    private PrintWriter outFile;
    private viewer.ProgressBarDlg pbd;
    private JProgressBar jpb;

    public MakeClassifierFile(String p, JTable[] t, viewer.MainFrame mf) {
        this.path = p;
        this.table = t;
        this.clstrNum = global.Variables.clstrNum;                       // k 값 (cluster 수)
        this.classNum = table.length;
        this.rowNum = table[0].getRowCount();
        this.clstrResult = new double[classNum][2][clstrNum];
        this.distanceSet = new double[rowNum-1][rowNum];
        this.majorityNum = global.Variables.majorityNum;

        pbd = new viewer.ProgressBarDlg(mf, false);
        RefineryUtilities.centerFrameOnScreen(pbd);
        pbd.setVisible(true);

        initVariables();
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
        double[][] buffer;
        int[][] result = null;
        
        try {
            fw = new FileWriter(path);
            bw = new BufferedWriter(fw);
            outFile = new PrintWriter(bw);

            outFile.println("// Feature Number:" + global.Variables.featureNum);
            outFile.println("// Class Number:" + classNum);
            outFile.println("// Cluster Number:" + global.Variables.clstrNum);
            outFile.println("// Vote Set Number:" + global.Variables.majorityNum);
            outFile.println("#");

             for(int i = 1; i < rowNum-1; i++) {
                for(int j = i+1; j < rowNum; j++) {
                    for(int k = 0; k < classNum; k++) {
                        for(int n = 0; n < clstrNum; n++) {
                            buffer = new clustering.KMeans(clstrNum, makeData(table[k], i), makeData(table[k], j)).startKMeans();
                            clstrResult[k][0][n] = buffer[0][n];
                            clstrResult[k][1][n] = buffer[1][n];
                        }
                    }
                    distanceSet[i][j] = new calculate.MeanDistance(clstrResult).getMeanEach();

                }
            }
            result = new calculate.GetMaxValue(distanceSet, majorityNum).getResultPair();
            for(int i = 0; i < result.length; i++) {
                outFile.println(result[i][0] + " " + result[i][1]);
                for(int j = 0; j < classNum; j++) {
                    for(int k = 0; k < clstrNum; k++) {
                        buffer = new clustering.KMeans(clstrNum, makeData(table[j], result[i][0]), makeData(table[j], result[i][1])).startKMeans();
                        outFile.println(buffer[0][k] + " " + buffer[1][k]);
                    }
                }
                outFile.println("#");
            }

            outFile.close();
            bw.close();
            fw.close();
        }
        catch(IOException ioe) {
            ioe.printStackTrace();
        }
        
        return result;
    }

}
