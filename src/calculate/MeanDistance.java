/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package calculate;

/**
 *
 * @author KHJ
 */
public class MeanDistance {
    
    private double[][] data1;      //  [axis][total cluster]
    private double[][][] data2;    //  [class][axis][cluster]
    private int classNum;
    private int clstrNum;
    private int totalClstrNum;
    private int specificCls;

    public MeanDistance(double[][][] d) {    //  [class][cluster][axis]
        classNum = d.length;
        clstrNum = d[0][0].length;
        totalClstrNum = classNum * clstrNum;
        initVariables(d);
        data2 = d;
    }

    public MeanDistance(double[][][] d, int cls) {  // [class][cluster][axis], specific class
        classNum = d.length;
        clstrNum = d[0].length;
        specificCls = cls;
        data2 = d;
    }

    private void initVariables(double[][][] d) {
        data1 = new double[2][totalClstrNum];

        for(int i = 0; i < classNum; i++) {
            for(int j = 0; j < clstrNum; j++) {
                data1[0][(clstrNum * i) + j] = d[i][0][j];
                data1[1][(clstrNum * i) + j] = d[i][1][j];
            }
        }
    }
    
    private double getDistance(double x1, double y1, double x2, double y2) {
        double d = 0;

        d = Math.pow((x1 - x2), 2) + Math.pow((y1 - y2), 2); //계산량을 줄이기 위해 sqrt는 하지 않음

        return d;
    }

    public double getMeanSpecific() {
        double distance = 0;

        for(int i = 0; i < clstrNum; i++) {
            for(int j = 0; j < classNum; j++) {
                if(j != specificCls) {
                    for(int k = 0; k < clstrNum; k++) {
                        distance = distance + getDistance(data2[specificCls][i][0], data2[specificCls][i][1], data2[j][k][0], data2[j][k][1]);
                    }
                }
            }
        }

        return distance;
    }

    public double getMeanAll() {
        double distance = 0;

        for(int i = 0; i < totalClstrNum; i++) {
            for(int j = i+1; j < totalClstrNum; j++) {
                distance = distance + getDistance(data1[0][i], data1[1][i], data1[0][j], data1[1][j]);
            }
        }
        //계산량을 줄이기 위해 n(n-1)/2 를 사용하지 않음

        return distance;
    }

    public double getMeanEach() {
        double distance = 0;

        for(int i = 0; i < totalClstrNum; i++) {
            for(int j = i+1; j < totalClstrNum; j++) {
                distance = distance + getDistance(data1[0][i], data1[1][i], data1[0][j], data1[1][j]);
            }
        }

        for(int s = 0; s < classNum; s++) {
            for(int t = 0; t < clstrNum; t++) {
                for(int u = t+1; u < clstrNum; u++) {
                    distance = distance - getDistance(data2[s][0][t], data2[s][1][t], data2[s][0][u], data2[s][1][u]);
                }
            }
        }
        
        //계산량을 줄이기 위해 n(n-1)/2 를 사용하지 않음

        return distance;
    }
}
