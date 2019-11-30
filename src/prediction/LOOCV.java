/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package prediction;

import javax.swing.JTable;
import javax.swing.JProgressBar;
import javax.swing.JFrame;
import javax.swing.JRootPane;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import org.jfree.ui.RefineryUtilities;
import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.io.FileReader;
import java.io.BufferedReader;
import java.util.StringTokenizer;

/**
 *
 * @author KHJ
 */
public class LOOCV {

    private JTable[] table;
    private int[][][] resultPair;               // [total samle 수][majority num][2]
    private int[][][][] resultPair2;            // [total sample수][class number][majority num][2]
    private double[] acResult;
    private boolean[][] result;                // [class 수][최대 sample 수]
    private double[][][][][] classifier;        // [total sample 수][majority num][class][cluster][axis]
    private double[][][][][][] classifier2;     // [total sample 수][class num][majoritynum][class][cluster][axis]
    private String[] classLabel;
    private String[][] classLabel2;
    private int featureNum;
    private int classNum;
    private int clstrNum;
    private int totalSample;
    private int majorityNum;
    private int rowNum;
    private double[][][][][] clstrResult;
    private double[][] distanceSet;
    private double[][][] distanceSet2;
    private FileWriter fw;
    private BufferedWriter bw;
    private PrintWriter outFile;
    private String path;
    private String resultStr;
    private viewer.ProgressBarDlg pbd;
    private JProgressBar jpb;
    private String[] info;
    private int max;
    private int gp1, gp2, gp3;

    public LOOCV(JTable[] table, JFrame mf, String[] str) {
        info = str;
        this.table = table;
        path = info[0];
        clstrNum = Integer.parseInt(info[1]);                      // cluster 개수
        majorityNum = Integer.parseInt(info[2]);
        featureNum = global.Variables.featureNum;
        classNum = table.length;
        acResult = new double[classNum+1];
        classLabel = new String[majorityNum];
        classLabel2 = new String[classNum][majorityNum];
        rowNum = table[0].getRowCount();
        distanceSet = new double[rowNum-1][rowNum];
        distanceSet2 = new double[classNum][rowNum-1][rowNum];
        //pbd = new viewer.ProgressBarDlg(mf, false);
        //RefineryUtilities.centerFrameOnScreen(pbd);
        //pbd.setVisible(true);
        
        initVariables();

        if(info[3].equals("new")) {
            //writeNGet();
            writeNGet2();
        }
        else {
            //read();
            read2();
        }
    }

    public String getResultStr() {
        return this.resultStr;
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

    private void writeNGet() {
        try {
            fw = new FileWriter(path);
            bw = new BufferedWriter(fw);
            outFile = new PrintWriter(bw);
            getPair();
            getResults();
            outFile.println(resultStr);
            outFile.println("END");
            bw.close();
            fw.close();
        }
        catch(IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private void writeNGet2() {             // class 마다 투표자 배정.
        try {
            fw = new FileWriter(path);
            bw = new BufferedWriter(fw);
            outFile = new PrintWriter(bw);
            getPair2();
            getResults2();
            outFile.println(resultStr);
            outFile.println("END");
            bw.close();
            fw.close();
        }
        catch(IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private void initVariables() {
        max = 0;
        totalSample = 0;
        for(int i = 0; i < table.length; i++) {
            max = Math.max(max, table[i].getColumnCount()-1);
            totalSample = totalSample + table[i].getColumnCount()-1;
        }
        result = new boolean[classNum][max];   // [class 수][최대 sample 수]
        //JRootPane jrp = (JRootPane) pbd.getComponent(0);
        //JLayeredPane jlp = (JLayeredPane) jrp.getComponent(1);
        //JPanel jp = (JPanel) jlp.getComponent(0);
        //jpb = (JProgressBar) jp.getComponent(0);
        clstrResult = new double[rowNum-2][rowNum-2][classNum][clstrNum][2];
        classifier = new double[totalSample][majorityNum][classNum][clstrNum][2];
        classifier2 = new double[totalSample][classNum][majorityNum][classNum][clstrNum][2];
        resultPair = new int[totalSample][majorityNum][2];
        resultPair2 = new int[totalSample][classNum][majorityNum][2];
        resultStr = "";
        gp1 = 0;
        gp2 = 0;
        gp3 = 0;
    }

    private double[] makeData(JTable t, int row, int one) {
        int colNum = t.getColumnCount();
        double[] data = new double[colNum-2];
        boolean isCurrent = false;

        for(int i = 1; i < colNum-1; i++) {
            if(i == (one+1)) {
                isCurrent = true;
            }

            if(isCurrent == false) {
                data[i-1] = Double.parseDouble(t.getValueAt(row, i).toString());
            }
            else {
                data[i-1] = Double.parseDouble(t.getValueAt(row, i+1).toString());
            }
        }

        return data;
    }

    private void read() {
        try {
                File f = new File(path);
                FileReader fr = new FileReader(f);
                BufferedReader br = new BufferedReader(fr);
                String line = "";
                int[] info = new int[4];
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

                classifier = new double[totalSample][majorityNum][classNum][clstrNum][2];
                resultPair = new int[totalSample][majorityNum][2];
                classLabel = new String[majorityNum];
                result = new boolean[classNum][max];   // [class 수][최대 sample 수]
                acResult = new double[classNum+1];

                br.readLine();

                for(int i = 0; i < totalSample; i++) {
                    for(int j = 0; j < majorityNum; j++) {
                        for(int k = 0; k < classNum; k++) {
                            for(int l = 0; l < clstrNum; l++) {
                                line = br.readLine();
                                st = new StringTokenizer(line, " ");
                                classifier[i][j][k][l][0] = Double.parseDouble(st.nextToken());
                                classifier[i][j][k][l][1] = Double.parseDouble(st.nextToken());
                            }
                        }
                    }
                    br.readLine();
                }

                for(int n = 0; n < totalSample; n++) {
                    for(int t = 0; t < majorityNum; t++) {
                        line = br.readLine();
                        st = new StringTokenizer(line, " ");
                        resultPair[n][t][0] = Integer.parseInt(st.nextToken());
                        resultPair[n][t][1] = Integer.parseInt(st.nextToken());
                    }
                    br.readLine();
                }

                br.close();
                fr.close();

                getResults();
        }
        catch(IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private void read2() {
        try {
                File f = new File(path);
                FileReader fr = new FileReader(f);
                BufferedReader br = new BufferedReader(fr);
                String line = "";
                int[] info = new int[4];
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

                classifier2 = new double[totalSample][classNum][majorityNum][classNum][clstrNum][2];
                resultPair2 = new int[totalSample][classNum][majorityNum][2];
                classLabel2 = new String[classNum][majorityNum];
                result = new boolean[classNum][max];   // [class 수][최대 sample 수]
                acResult = new double[classNum+1];

                br.readLine();

                for(int i = 0; i < totalSample; i++) {
                    for(int m = 0; m < classNum; m++) {
                        for(int j = 0; j < majorityNum; j++) {  //majorityNum
                            for(int k = 0; k < classNum; k++) {  //ClassNum
                                for(int l = 0; l < clstrNum; l++) {  //ClstrNum
                                    line = br.readLine();
                                    st = new StringTokenizer(line, " ");
                                    classifier2[i][m][j][k][l][0] = Double.parseDouble(st.nextToken());
                                    classifier2[i][m][j][k][l][1] = Double.parseDouble(st.nextToken());
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
                            resultPair2[n][p][t][0] = Integer.parseInt(st.nextToken());
                            resultPair2[n][p][t][1] = Integer.parseInt(st.nextToken());
                        }
                        br.readLine();
                    }
                }

                br.close();
                fr.close();

                getResults2();
        }
        catch(IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private void getClassifier(int t) {
        for(int i = 0; i < majorityNum; i++) {
            for(int j = 0; j < classNum; j++) {
                for(int k = 0; k < clstrNum; k++) {
                    classifier[t][i][j][k][0] = clstrResult[resultPair[t][i][0]-1][resultPair[t][i][1]-2][j][k][0];
                    classifier[t][i][j][k][1] = clstrResult[resultPair[t][i][0]-1][resultPair[t][i][1]-2][j][k][1];
                    outFile.println(classifier[t][i][j][k][0] + " " + classifier[t][i][j][k][1]);
                }
            }
        }
        outFile.println("#" + (int) (t+1));
    }

    private void getClassifier2(int t, int a) {
        for(int i = 0; i < majorityNum; i++) {
            for(int j = 0; j < classNum; j++) {
                for(int k = 0; k < clstrNum; k++) {
                    classifier2[t][a][i][j][k][0] = clstrResult[resultPair2[t][a][i][0]-1][resultPair2[t][a][i][1]-2][j][k][0];
                    classifier2[t][a][i][j][k][1] = clstrResult[resultPair2[t][a][i][0]-1][resultPair2[t][a][i][1]-2][j][k][1];
                    outFile.println(classifier2[t][a][i][j][k][0] + " " + classifier2[t][a][i][j][k][1]);
                }
            }
        }
        outFile.println("#" + (int) (t+1) + "-" + (int) (a+1));
    }
    
    private void getPair() {
        double[][] buffer;

        for(int t = 0; t < totalSample; t++) {
            if(t == 0) {
            outFile.println("// Feature Number:" + featureNum);
            outFile.println("// Class Number:" + classNum);
            outFile.println("// Cluster Number:" + clstrNum);
            outFile.println("// Vote Set Number:" + majorityNum);
            outFile.println("#-");
            }
            for(int i = 1; i < rowNum-1; i++) {
            
                for(int j = i+1; j < rowNum; j++) {
                    int currentSample = 0;
                    for(int k = 0; k < classNum; k++) {
                        if(k != 0) {
                            currentSample = currentSample + table[k-1].getColumnCount()-1;
                        }
                        for(int n = 0; n < clstrNum; n++) {
                            buffer = new clustering.KMeans(clstrNum, makeData(table[k], 
                                    i, t-currentSample), makeData(table[k],
                                    j, t-currentSample)).startKMeans();
                            clstrResult[i-1][j-2][k][n][0] = buffer[0][n];
                            clstrResult[i-1][j-2][k][n][1] = buffer[1][n];
                        }
                    }
                    distanceSet[i][j] = new calculate.MeanDistance(clstrResult[i-1][j-2]).getMeanEach();
                }

            }
            resultPair[t] = new calculate.GetMaxValue(distanceSet, majorityNum).getResultPair();
            getClassifier(t);
            //jpb.setValue((int) (( (double)  (t+1) / totalSample) * 95));
            System.out.println((int) (( (double) (t+1) / totalSample) * 100));
        }
        for(int i = 0; i < totalSample; i++) {
            for(int j = 0; j < resultPair[0].length; j++) {
                outFile.println(resultPair[i][j][0] + " " + resultPair[i][j][1]);
            }
            outFile.println("#" + (int) (i+1));
        }
    }

    private void getPair2() {
        double[][] buffer;

        for(int t = 0; t < totalSample; t++) {
            if(t == 0) {
            outFile.println("// Feature Number:" + featureNum);
            outFile.println("// Class Number:" + classNum);
            outFile.println("// Cluster Number:" + clstrNum);
            outFile.println("// Vote Set Number:" + majorityNum);
            outFile.println("#-");
            }
            for(int i = 1; i < rowNum-1; i++) {

                for(int j = i+1; j < rowNum; j++) {
                    int currentSample = 0;
                    for(int k = 0; k < classNum; k++) {
                        if(k != 0) {
                            currentSample = currentSample + table[k-1].getColumnCount()-1;
                        }
                        for(int n = 0; n < clstrNum; n++) {
                            buffer = new clustering.KMeans(clstrNum, makeData(table[k],
                                    i, t-currentSample), makeData(table[k],
                                    j, t-currentSample)).startKMeans();
                            clstrResult[i-1][j-2][k][n][0] = buffer[0][n];
                            clstrResult[i-1][j-2][k][n][1] = buffer[1][n];
                        }
                    }
                    for(int m = 0; m < classNum; m++) {
                        distanceSet2[m][i][j] = new calculate.MeanDistance(clstrResult[i-1][j-2], m).getMeanSpecific();
                    }
                }

            }
            for(int a = 0; a < classNum; a++) {
                resultPair2[t][a] = new calculate.GetMaxValue(distanceSet2[a], majorityNum).getResultPair();
                getClassifier2(t, a);
            }
            //jpb.setValue((int) (( (double)  (t+1) / totalSample) * 95));
            System.out.println((int) (( (double) (t+1) / totalSample) * 100));
        }
        for(int i = 0; i < totalSample; i++) {
            for(int j = 0; j < classNum; j++) {
                for(int k = 0; k < majorityNum; k++) {
                    outFile.println(resultPair2[i][j][k][0] + " " + resultPair2[i][j][k][1]);
                }
                outFile.println("#" + (int) (i+1) + "-" + (int) (j+1));
            }
        }
    }

    private double getDistance(double x1, double y1, double x2, double y2) {
        double d = 0;

        d = Math.pow((x1 - x2), 2) + Math.pow((y1 - y2), 2); //계산량을 줄이기 위해 sqrt는 하지 않음

        return d;
    }

    private String majorityVote(String[] classLabel) {
        int[] cnt = new int[classNum];
        int finalResult = 0;
        int max = 0;
        int maxCnt = 0;

        for(int n = 0; n < classNum; n++) {
            cnt[n] = 0;
        }

        for(int i = 0; i < classLabel.length; i++) {
            for(int j = 0; j < classNum; j++) {
                if(classLabel[i].equals(table[j].getValueAt(0, 1))) {
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

        String str = table[finalResult].getValueAt(0, 1).toString();

        return str;
    }

    private String majorityVote2(String[][] classLabel2) {
        String str = "";
        String cls = "";
        int[] cnt = new int[classNum];

        for(int i = 0; i < classNum; i++) {
            cnt[i] = 0;
            cls = table[i].getValueAt(0, 1).toString();
            for(int j = 0; j < majorityNum; j++) {
                if(classLabel2[i][j].equals(cls)) {
                    cnt[i] ++;
                }
            }
        }
        
        int oldMax = 0;
        int max = 0;
        for(int a = 0; a < classNum; a++) {
            oldMax = max;
            max = Math.max(max, cnt[a]);
            if(oldMax != max) {
                str = table[a].getValueAt(0, 1).toString();
            }
            /*
            if((oldMax == max) && (a > 0)) {
                if(table[a-1].getColumnCount() < table[a].getColumnCount()) {
                    str = table[a].getValueAt(0, 1).toString();
                    System.out.println("1" + str);
                }
                else {
                    str = table[a-1].getValueAt(0, 1).toString();
                    System.out.println("2" + str);
                }
            }
            */
        }

        return str;
    }

    private String predict(double[] sample, int s) {
        double min = 0;
        double oldMin= 0;

        for(int i = 0; i < classLabel.length; i++) {
            classLabel[i] = "";
            min = getDistance(sample[resultPair[s][i][0]],
                            sample[resultPair[s][i][1]], classifier[s][i][0][0][0],
                            classifier[s][i][0][0][1]);
            for(int j = 0; j < classNum; j++) {
                oldMin = min;
                for(int k = 0; k < clstrNum; k++) {
                    min = Math.min(min, getDistance(sample[resultPair[s][i][0]],
                            sample[resultPair[s][i][1]], classifier[s][i][j][k][0],
                            classifier[s][i][j][k][1]));
                }
                if(min != oldMin) {
                        classLabel[i] = table[j].getValueAt(0, 1).toString();
                }
            }
        }

        return majorityVote(classLabel);
    }
    
    private String predict2(double[] sample, int s) {
        double min = 0;
        double oldMin= 0;

        for(int a = 0; a < classNum; a++) {
            for(int i = 0; i < majorityNum; i++) {
                classLabel2[a][i] = "";
                min = getDistance(sample[resultPair2[s][a][i][0]],
                                sample[resultPair2[s][a][i][1]], classifier2[s][a][i][0][0][0],
                                classifier2[s][a][i][0][0][1]);
                for(int j = 0; j < classNum; j++) {
                    oldMin = min;
                    for(int k = 0; k < clstrNum; k++) {
                        min = Math.min(min, getDistance(sample[resultPair2[s][a][i][0]],
                                sample[resultPair2[s][a][i][1]], classifier2[s][a][i][j][k][0],
                                classifier2[s][a][i][j][k][1]));
                    }
                    if(min != oldMin) {
                            classLabel2[a][i] = table[j].getValueAt(0, 1).toString();
                    }
                }
            }
        }
        return majorityVote2(classLabel2);
    }

    private void getResults() {
        double[] sample = new double[table[0].getRowCount()];
        String orgClass = "";
        int cnt = 0;

        for(int i = 0; i < classNum; i++) {
            for(int j = 1; j < table[i].getColumnCount(); j++) {
                orgClass = table[i].getValueAt(0, j).toString();
                for(int k = 1; k < sample.length; k++) {
                    sample[k] = Double.parseDouble(table[i].getValueAt(k, j).toString());
                }
                if(orgClass.equals(predict(sample, cnt))) {
                    result[i][j-1] = true;
                }
                else {
                    result[i][j-1] = false;
                }

                cnt++;
            }
            //jpb.setValue(90 + (int) (( (double) (i+1) / classNum) * 10));
        }
        getAccuracy();
    }

    private void getResults2() {
        double[] sample = new double[table[0].getRowCount()];
        String orgClass = "";
        int cnt = 0;

        for(int i = 0; i < classNum; i++) {
            for(int j = 1; j < table[i].getColumnCount(); j++) {
                orgClass = table[i].getValueAt(0, j).toString();
                for(int k = 1; k < sample.length; k++) {
                    sample[k] = Double.parseDouble(table[i].getValueAt(k, j).toString());
                }
                if(orgClass.equals(predict2(sample, cnt))) {
                    result[i][j-1] = true;
                }
                else {
                    result[i][j-1] = false;
                }

                cnt++;
            }
            //jpb.setValue(90 + (int) (( (double) (i+1) / classNum) * 10));
        }
        getGP();
        getAccuracy();
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
                    gpCount1[resultPair2[n][p][t][0]-1][resultPair2[n][p][t][1]-1] ++;
                    gpCount2[resultPair2[n][p][t][0]-1] ++;
                    gpCount2[resultPair2[n][p][t][1]-1] ++;
                }
            }
        }

        int max1 = 0, max2 = 0;
        int oldMax1 = 0, oldMax2 = 0;

        for(int i = 0; i < featureNum; i++) {
            for(int j = (i+1); j < featureNum; j++) {
                oldMax1 = max1;
                max1 = Math.max(max1, gpCount1[i][j]);
                if(oldMax1 != max1) {
                    gp1 = i+1;
                    gp2 = j+1;
                }
            }
            oldMax2 = max2;
            max2 = Math.max(max2, gpCount2[i]);
            if(oldMax2 != max2) {
                gp3 =i+1;
            }
        }

    }

}
