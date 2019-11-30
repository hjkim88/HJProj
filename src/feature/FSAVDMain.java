/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package feature;

import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.io.IOException;
import javax.swing.JTable;

/**
 *
 * @author KHJ
 */
public class FSAVDMain {

    private JTable[] t;
    private int classNum;
    private int featureNum;
    private int columnNum;
    private String[] featureList;
    private double[] featureValue;
    private int[] featureOrder;
    private double[][] meanSet;

    public FSAVDMain(JTable[] t, int n) {
        this.t = t;
        this.featureNum = n;
        initVariables();
        processing();
        makeFTR();
    }

    private void initVariables() {
        classNum = t.length;
        columnNum = t[0].getRowCount()-1;
        meanSet = new double[classNum][];
        featureList = new String[featureNum];
        featureValue = new double[featureNum];
        featureOrder = new int[featureNum];
        for(int i = 0; i < featureNum; i++) {
            featureList[i] = "";
            featureValue[i] = 0;
            featureOrder[i] = 0;
        }
    }

    private void processing() {
        double[] sampleV;
        double[] orderedV;
        double distanceSum = 0;
        
        for(int i = 0; i < columnNum; i++) {
            for(int j = 0; j < classNum; j++) {
                sampleV = new double[t[j].getColumnCount()-1];
                for(int k = 0; k < sampleV.length; k++) {
                    sampleV[k] = Double.parseDouble(t[j].getValueAt(i+1, k+1).toString());
                }
                orderedV = new feature.ValueOrdering(sampleV).getOrdered();
                meanSet[j] = new feature.HaarWavelet(orderedV).getMean();
            }
            
            distanceSum = 0;
            for(int a = 0; a < classNum; a++) {
                for(int b = 0; b < meanSet[a].length; b++) {
                    for(int c = a+1; c < classNum; c++) {
                        for(int d = 0; d < meanSet[c].length; d++) {
                            distanceSum = distanceSum + getDistance(meanSet[a][b], meanSet[c][d]);
                        }
                    }
                }
            }
            compare((i+1), t[0].getValueAt(i+1, 0).toString(), distanceSum);
            System.out.println("Column number -> " + (i+1));
        }
    }

    private void compare(int o, String s, double d) {
        double min = featureValue[0];
        double min2 = 0;
        int order = 0;
        boolean isFinished = false;
        
        for(int i = 0; i < featureNum; i++) {
            if(featureList[i].equals("")) {
                featureList[i] = s;
                featureValue[i] = d;
                featureOrder[i] = o;
                isFinished = true;
                break;
            }
            else {
                min2 = min;
                min = Math.min(featureValue[i], min);
                if(min2 != min) {
                    order = i;
                }
            }
        }

        if((isFinished == false) && (min < d)) {
            featureList[order] = s;
            featureValue[order] = d;
            featureOrder[order] = o;
        }
    }

    private void makeFTR() {
        try {
            FileWriter fw = new FileWriter(global.Variables.ftrPath);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter outFile = new PrintWriter(bw);

            outFile.println("Hi, I'm Chris Kim");
            outFile.println("And this is FSAVD");
            outFile.println();
            outFile.println("Ranked attributes:");

            for(int i = 0; i < featureNum; i++) {
                outFile.println((i+1) + "        " + featureOrder[i] + " " + featureList[i] + " " + featureValue[i]);
            }

            outFile.close();
            bw.close();
            fw.close();
        }
        catch(IOException ioe) {
            ioe.printStackTrace();
        }
        
    }

    private double getDistance(double x, double y) {
        return Math.abs(y-x);
    }
    
    public String[] getFeatureList() {
        return featureList;
    }

}
