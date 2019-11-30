/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package prediction;

import javax.swing.JTable;

/**
 *
 * @author KHJ
 */
public class Prediction {

    private JTable[] table;
    private int[][] resultPair;
    private double[] sample;
    private double[][][][] classifier;        // [pair number][class][cluster][axis]
    private int pairNum;
    private int classNum;
    private int clstrNum;
    private int[] classLabel;

    public Prediction(JTable[] table, int[][] resultPair, double[] sample) {
        this.table = table;
        this.resultPair = resultPair;
        this.sample = sample;
        this.pairNum = resultPair.length;
        this.classNum = table.length;
        this.clstrNum = global.Variables.clstrNum;                      // cluster 개수
        this.classLabel = new int[pairNum];

        makeClassifier();
    }

    private void makeClassifier() {
        classifier = new double[pairNum][classNum][clstrNum][2];
        double[] buffer1, buffer2;
        double[][] meanResult;
        for(int i = 0; i < pairNum; i++) {
            for(int j = 0; j < classNum; j++) {
                
                buffer1 = new double[table[j].getColumnCount()-1];
                buffer2 = new double[table[j].getColumnCount()-1];
                for(int n = 0; n < table[j].getColumnCount()-1; n++) {
                    buffer1[n] = Double.parseDouble(table[j].getValueAt(resultPair[i][0], n+1).toString());
                    buffer2[n] = Double.parseDouble(table[j].getValueAt(resultPair[i][1], n+1).toString());
                }
                meanResult = new clustering.KMeans(clstrNum, buffer1 , buffer2).startKMeans();
                for(int k = 0; k < clstrNum; k++) {
                    classifier[i][j][k][0] = meanResult[0][k];
                    classifier[i][j][k][1] = meanResult[1][k];
                }

            }
        }
    }

    private double getDistance(double x1, double y1, double x2, double y2) {
        double d = 0;

        d = Math.pow((x1 - x2), 2) + Math.pow((y1 - y2), 2); //계산량을 줄이기 위해 sqrt는 하지 않음

        return d;
    }

    private String majorityVote(int[] classLabel) {
        int[] cnt = new int[classNum];
        int finalResult = 0;
        int max = 0;
        int maxCnt = 0;

        for(int n = 0; n < classNum; n++) {
            cnt[n] = 0;
        }
        
        for(int i = 0; i < classLabel.length; i++) {
            for(int j = 0; j < classNum; j++) {
                if(classLabel[i] == j) {
                    cnt[j]++;
                }
            }
        }
        for(int k = 0; k < classNum; k++) {
            max = Math.max(max, cnt[k]);
        }
        for(int t = 0; t < classNum; t++) {
            if(cnt[t] == max) {
                finalResult = t;
                maxCnt++;
            }
        }
 
        if(maxCnt > 1) {
            int[] buffer = new int[maxCnt];
            int bCnt = 0;
            int maxNum = 0;
            for(int a = 0; a < classNum; a++) {
                if(cnt[a] == max) {
                    buffer[bCnt] = a;
                    bCnt++;
                }
            }
            for(int b = 0; b < maxCnt; b++) {
                maxNum = Math.max(maxNum, table[buffer[b]].getColumnCount());
                if(maxNum == table[buffer[b]].getColumnCount()) {
                    finalResult = buffer[b];
                }
            }
        }

        String str = "" + (finalResult+1);

        return str;
    }

    public String predictSS() {
        double min = 0;
        double oldMin= 0;

        for(int i = 0; i < classLabel.length; i++) {
            classLabel[i] = 0;
            min = getDistance(sample[resultPair[i][0]],
                            sample[resultPair[i][1]], classifier[i][0][0][0],
                            classifier[i][0][0][1]);
            for(int j = 0; j < classNum; j++) {
                oldMin = min;
                for(int k = 0; k < clstrNum; k++) {
                    min = Math.min(min, getDistance(sample[resultPair[i][0]],
                            sample[resultPair[i][1]], classifier[i][j][k][0],
                            classifier[i][j][k][1]));
                }
                if(min != oldMin) {
                        classLabel[i] = j;
                }
            }
        }

        return majorityVote(classLabel);
    }

}
