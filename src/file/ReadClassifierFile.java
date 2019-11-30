/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package file;

import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.StringTokenizer;

/**
 *
 * @author KHJ
 */
public class ReadClassifierFile {

    private int[][] resultPair;
    private double[][][][] classifier;        // [pair number][class][cluster][axis]

    public ReadClassifierFile(String path) {
        read(path);
    }

    private void read(String path) {
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

                br.readLine();
                
                classifier = new double[info[0]][info[1]][info[2]][2];
                resultPair = new int[info[3]][2];

                for(int j = 0; j < info[0]; j++) {
                    for(int k = 0; k < info[1]; k++) {
                        for(int l = 0; l < info[2]; l++) {
                            line = br.readLine();
                            st = new StringTokenizer(line, " ");
                            classifier[j][k][l][0] = Double.parseDouble(st.nextToken());
                            classifier[j][k][l][1] = Double.parseDouble(st.nextToken());
                        }
                    }
                }

                br.readLine();

                for(int m = 0; m < info[3]; m++) {
                    line = br.readLine();
                    st = new StringTokenizer(line, " ");
                    resultPair[m][0] = Integer.parseInt(st.nextToken());
                    resultPair[m][1] = Integer.parseInt(st.nextToken());
                }

                br.close();
                fr.close();
        }
        catch(IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public int[][] getResultPair() {
        return this.resultPair;
    }

    public double[][][][] getClassifier() {
        return this.classifier;
    }

}
