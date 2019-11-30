/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package prediction;

import javax.swing.JTable;
import javax.swing.JFrame;
import java.io.File;
import java.io.IOException;
import java.io.FileReader;
import java.io.BufferedReader;
import java.util.StringTokenizer;

/**
 *
 * @author KHJ
 */
public class ROCCurve {

    private viewer.MainFrame mf;
    private String resultStr;
    private String path;
    private int[] info;
    private JTable[] table;
    private int featureNum;
    private int classNum;
    private int clstrNum;
    private int totalSample;
    private int majorityNum;
    private double[][][][][][] classifier;     // [total sample 수][class num][majoritynum][class][cluster][axis]
    private int[][][][] resultPair;            // [total sample수][class number][majority num][2]
    private String[][] classLabel;             // [classNum][majorityNum]
    private boolean[][] result;                // [class 수][최대 sample 수]
    private double[] acResult;
    private int max;
    private int[][] rocResult;
    private double[] sensitivity;
    private double[] specificity;
    private String chartName = "";
    private int gp1, gp2, gp3;

    public ROCCurve(JTable[] table, viewer.MainFrame mf, String str) {
        this.mf = mf;
        path = str;
        this.table = table;
        initVariables();

        read();

        for(int n = (-majorityNum-1); n < majorityNum+2; n++) {
            getResults(n);
        }
        //printStatus();
        makeROC();
        makeAUC();
    }

    private void initVariables() {
        info = new int[4];
        resultStr = "";
        max = 0;
        totalSample = 0;
        for(int i = 0; i < table.length; i++) {
            max = Math.max(max, table[i].getColumnCount()-1);
            totalSample = totalSample + table[i].getColumnCount()-1;
            chartName = chartName + table[i].getValueAt(0, 0).toString() + " ";
        }
        gp1 = 0;
        gp2 = 0;
        gp3 = 0;
    }

    public String getResultStr() {
        return this.resultStr;
    }

    private void printStatus() {
        for(int i = 0; i < rocResult.length; i++) {
            System.out.println("n = " + (i-majorityNum-1));
            System.out.println("True Positive = " + rocResult[i][0]);
            System.out.println("False Positive = " + rocResult[i][1]);
            System.out.println("False Negative = " + rocResult[i][2]);
            System.out.println("True Negative = " + rocResult[i][3]);
            System.out.println("False Positive Rate = " + (1-specificity[i]));
            System.out.println("True Positive Rate = " + sensitivity[i]);
            System.out.println("----------------------------");
        }
    }

    private void makeAUC() {
        double AUC = new calculate.AUC(specificity, sensitivity).getAUC();
        resultStr = resultStr + "AUC = " + (int)(10000 * AUC) / 10000F + "\n";
    }
    
    private void makeROC() {
        mf.getGraphPanel().addTab(chartName, new graph.MakeRocCurve(chartName, sensitivity, specificity).getChartPanel());
        mf.getGraphPanel().setSelectedIndex(mf.getGraphPanel().getTabCount()-1);
        mf.getGraphBtn().setSelected(true);
        mf.getExlPanel().setVisible(false);
        mf.getGraphPanel().setVisible(true);
    }

    private void read() {
        try {
                File f = new File(path);
                FileReader fr = new FileReader(f);
                BufferedReader br = new BufferedReader(fr);
                String line = "";
                StringTokenizer st;

                for(int i = 0; i < 4; i++) {
                    line = br.readLine();
                    st = new StringTokenizer(line, ":");
                    st.nextToken();
                    info[i] = Integer.parseInt(st.nextToken());
                }

                featureNum = info[0];
                classNum = info[1];
                clstrNum = info[2];
                majorityNum = info[3];

                classifier = new double[totalSample][classNum][majorityNum][classNum][clstrNum][2];
                resultPair = new int[totalSample][classNum][majorityNum][2];
                classLabel = new String[classNum][majorityNum];
                result = new boolean[classNum][max];   // [class 수][최대 sample 수]
                acResult = new double[classNum+1];
                rocResult = new int[(2*(majorityNum))+3][4]; // (0 : TP) (1 : FP) (2 : FN) (3 : TN)
                sensitivity = new double[(2*(majorityNum))+3];
                specificity = new double[(2*(majorityNum))+3];

                br.readLine();

                for(int i = 0; i < totalSample; i++) {
                    for(int m = 0; m < classNum; m++) {
                        for(int j = 0; j < majorityNum; j++) {  //majorityNum
                            for(int k = 0; k < classNum; k++) {  //ClassNum
                                for(int l = 0; l < clstrNum; l++) {  //ClstrNum
                                    line = br.readLine();
                                    st = new StringTokenizer(line, " ");
                                    classifier[i][m][j][k][l][0] = Double.parseDouble(st.nextToken());
                                    classifier[i][m][j][k][l][1] = Double.parseDouble(st.nextToken());
                                }
                            }
                        }
                        br.readLine();
                    }
                }

                for(int n = 0; n < totalSample; n++) {
                    for(int p = 0; p < classNum; p++) {
                        for(int t = 0; t < majorityNum; t++) {
                            line = br.readLine();
                            st = new StringTokenizer(line, " ");
                            resultPair[n][p][t][0] = Integer.parseInt(st.nextToken());
                            resultPair[n][p][t][1] = Integer.parseInt(st.nextToken());
                        }
                        br.readLine();
                    }
                }

                br.close();
                fr.close();
        }
        catch(IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private double getDistance(double x1, double y1, double x2, double y2) {
        double d = 0;

        d = Math.pow((x1 - x2), 2) + Math.pow((y1 - y2), 2); //계산량을 줄이기 위해 sqrt는 하지 않음

        return d;
    }

    private String majorityVote(String[][] classLabel, int n) {
        String str = "";
        String cls = "";
        int[] cnt = new int[classNum];

        for(int i = 0; i < classNum; i++) {
            cnt[i] = 0;
            cls = table[i].getValueAt(0, 1).toString();
            for(int j = 0; j < majorityNum; j++) {
                if(classLabel[i][j].equals(cls)) {
                    cnt[i] ++;
                }
            }
        }

        cnt[0] = cnt[0] + n;    //class가 2개뿐이라 가정하므로 0 에다가 n만큼을 더한다.

        if(cnt[0] < cnt[1]) {
            str = table[1].getValueAt(0, 1).toString();
        }
        else if(cnt[0] > cnt[1]) {
            str = table[0].getValueAt(0, 1).toString();
        }
        else {
            if(table[0].getColumnCount() < table[1].getColumnCount()) {
                str = table[1].getValueAt(0, 1).toString();
            }
            else {
                str = table[0].getValueAt(0, 1).toString();
            }
        }

        return str;
    }

    private String predict(double[] sample, int s, int n) { //해당 샘플, 샘플 번호, roc measureNum
        double min = 0;
        double oldMin= 0;

        for(int a = 0; a < classNum; a++) {
            for(int i = 0; i < majorityNum; i++) {
                classLabel[a][i] = "";
                min = getDistance(sample[resultPair[s][a][i][0]],
                                sample[resultPair[s][a][i][1]], classifier[s][a][i][0][0][0],
                                classifier[s][a][i][0][0][1]);
                for(int j = 0; j < classNum; j++) {
                    oldMin = min;
                    for(int k = 0; k < clstrNum; k++) {
                        min = Math.min(min, getDistance(sample[resultPair[s][a][i][0]],
                                sample[resultPair[s][a][i][1]], classifier[s][a][i][j][k][0],
                                classifier[s][a][i][j][k][1]));
                    }
                    if(min != oldMin) {
                            classLabel[a][i] = table[j].getValueAt(0, 1).toString();
                    }
                }
            }
        }
        return majorityVote(classLabel, n);
    }

    private void initRocResult(int[] r) {
        for(int i = 0; i < 4; i++) {
            r[i] = 0;
        }
    }

    private void getResults(int n) {
        double[] sample = new double[table[0].getRowCount()];
        String orgClass = "";
        int cnt = 0;

        initRocResult(rocResult[n+majorityNum+1]);

        for(int i = 0; i < classNum; i++) {
            for(int j = 1; j < table[i].getColumnCount(); j++) {
                orgClass = table[i].getValueAt(0, j).toString();
                for(int k = 1; k < sample.length; k++) {
                    sample[k] = Double.parseDouble(table[i].getValueAt(k, j).toString());
                }
                if(orgClass.equals(predict(sample, cnt, n))) {
                    result[i][j-1] = true;
                    if(i == 0) {
                        rocResult[n+majorityNum+1][0] ++;
                    }
                    else {
                        rocResult[n+majorityNum+1][3] ++;
                    }
                }
                else {
                    result[i][j-1] = false;
                    if(i == 0) {
                        rocResult[n+majorityNum+1][2] ++;
                    }
                    else {
                        rocResult[n+majorityNum+1][1] ++;
                    }
                }

                cnt++;
            }
            //jpb.setValue(90 + (int) (( (double) (i+1) / classNum) * 10));
        }

        makeData(n);

        if(n == 0) {
            getGP();
            getAccuracy();
        }
    }

    private void makeData(int n) {
        sensitivity[n+majorityNum+1] = ((double) rocResult[n+majorityNum+1][0]) /
                (rocResult[n+majorityNum+1][0] + rocResult[n+majorityNum+1][2]);
        specificity[n+majorityNum+1] = ((double) rocResult[n+majorityNum+1][3]) /
                (rocResult[n+majorityNum+1][3] + rocResult[n+majorityNum+1][1]);
    }

    private void getAccuracy() {
        int cnt = 0;
        int totalCnt = 0;

        for(int i = 0; i < classNum; i++) {
            cnt = 0;
            for(int j = 0; j < table[i].getColumnCount()-1; j++) {
                if(result[i][j] == true) {
                    cnt++;
                    totalCnt++;
                }
            }
            acResult[i] = ((double) cnt / (double) (table[i].getColumnCount()-1)) * 100;
        }
        acResult[classNum] = ((double) totalCnt / (double) totalSample) * 100;

        for(int k = 0; k < classNum; k++) {
            resultStr = resultStr + "Class " + (k+1) + " Accuracy = " + (int)(100 * acResult[k]) / 100F + "%\n";
        }
        resultStr = resultStr + "Total Accuracy = " + (int)(100 * acResult[classNum]) / 100F + "%\n";
        resultStr = resultStr + "Sensitivity = " + (int)(100 * sensitivity[majorityNum]) / 100F + "\n";
        resultStr = resultStr + "Specificity = " + (int)(100 * specificity[majorityNum]) / 100F + "\n";
        resultStr = resultStr + "Path = " + path + "\n";
        resultStr = resultStr + "Class = ";
        for(int s = 0; s < classNum; s++) {
            resultStr = resultStr + table[s].getValueAt(0, 0).toString() + " ";
        }
        resultStr = resultStr + "\nFeature Number = " + featureNum + "\n";
        resultStr = resultStr + "Cluster Number = " + clstrNum + "\n";
        resultStr = resultStr + "Vote Set Number = " + majorityNum + "\n";
        resultStr = resultStr + "Most frequent Gene pair = (" + gp1 + ", " + gp2 + ")\n";
        resultStr = resultStr + "Most frequent Gene = " + gp3 + "\n";
    }

    private void getGP() {
        int[][] gpCount1 = new int[featureNum][featureNum];
        int[] gpCount2 = new int[featureNum];

        for(int i = 0; i < featureNum; i++) {
            for(int j = 0; j < featureNum; j++) {
                if(i < j) {
                    gpCount1[i][j] = 0;
                }
                else {
                    gpCount1[i][j] = -1;
                }
            }
            gpCount2[i] = 0;
        }

        for(int n = 0; n < totalSample; n++) {
            for(int p = 0; p < classNum; p++) {
                for(int t = 0; t < majorityNum; t++) {
                    gpCount1[resultPair[n][p][t][0]-1][resultPair[n][p][t][1]-1] ++;
                    gpCount2[resultPair[n][p][t][0]-1] ++;
                    gpCount2[resultPair[n][p][t][1]-1] ++;
                }
            }
        }

        int max1 = 0, max2 = 0;
/*
        int oldMax1 = 0, oldMax2 = 0;
        for(int i = 0; i < featureNum; i++) {
            for(int j = (i+1); j < featureNum; j++) {
                oldMax1 = max1;
                max1 = Math.max(max1, gpCount1[i][j]);
                if(oldMax1 != max1) {
                    gp1 = i+1;
                    gp2 = j+1;
                    System.out.println("gp1 = " + gp1 + ", gp2 = " + gp2);
                }
            }
            oldMax2 = max2;
            max2 = Math.max(max2, gpCount2[i]);
            if(oldMax2 != max2) {
                gp3 =i+1;
            }
        }
*/

        for(int i = 0; i < featureNum; i++) {
            for(int j = (i+1); j < featureNum; j++) {
                max1 = Math.max(max1, gpCount1[i][j]);
            }
            max2 = Math.max(max2, gpCount2[i]);
        }
        
        for(int i = 0; i < featureNum; i++) {
            for(int j = (i+1); j < featureNum; j++) {
                System.out.println("gpCount1[" + i + "][" + j + "] = " + gpCount1[i][j]);
                if(gpCount1[i][j] == max1) {
                    gp1 = i+1;
                    gp2 = j+1;
                    System.out.println("gp1 = " + gp1 + ", gp2 = " + gp2);
                }
            }
            if(gpCount2[i] == max2) {
                gp3 =i+1;
            }
        }

        System.out.println("----------------------------");
    }

}
