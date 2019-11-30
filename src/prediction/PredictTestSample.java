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
public class PredictTestSample {

    private JTable[] table;
    private int[][] resultPair;
    private double[] acResult;
    private boolean[][] result;                // [class 수][최대 sample 수]
    private double[][][][] classifier;        // [pair number][class][cluster][axis]
    private int[] classLabel;
    private int pairNum;
    private int classNum;
    private int clstrNum;

    public PredictTestSample(double[][][][] classifier, int[][] resultPair, JTable[] table) {
        this.table = table;
        this.resultPair = resultPair;
        this.pairNum = resultPair.length;
        this.classNum = table.length;
        this.clstrNum = global.Variables.clstrNum;                      // cluster 개수
        this.acResult = new double[classNum+1];
        this.classLabel = new int[pairNum];
        this.classifier = classifier;
        
        initVariables();
        getResults();
    }

    public double[] getAccuracy() {
        int cnt = 0;
        int totalCnt = 0;
        int totalSample = 0;
        for(int i = 0; i < classNum; i++) {
            cnt = 0;
            for(int j = 0; j < table[i].getColumnCount()-1; j++) {
                if(result[i][j] == true) {
                    cnt++;
                    totalCnt++;
                }
            }
            acResult[i] = ((double) cnt / (double) (table[i].getColumnCount()-1)) * 100;
            totalSample = totalSample + table[i].getColumnCount()-1;
        }
        acResult[classNum] = ((double) totalCnt / (double) totalSample) * 100;

        return acResult;
    }

    private void initVariables() {
        int max = 0;
        for(int i = 0; i < table.length; i++) {
            max = Math.max(max, table[i].getColumnCount()-1);
        }
        this.result = new boolean[classNum][max];   // [class 수][최대 sample 수]
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

    private String predict(double[] sample) {
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

    private void getResults() {
        double[] sample = new double[table[0].getRowCount()];

        for(int i = 0; i < classNum; i++) {
            for(int j = 1; j < table[i].getColumnCount(); j++) {
                for(int k = 0; k < sample.length; k++) {
                    sample[k] = Double.parseDouble(table[i].getValueAt(k, j).toString());
                }
                if(sample[0] == Double.parseDouble(predict(sample))) {
                    result[i][j-1] = true;
                }
                else {
                    result[i][j-1] = false;
                }
            }
        }
    }

}
