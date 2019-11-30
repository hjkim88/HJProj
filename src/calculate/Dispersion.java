/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package calculate;

/**
 *
 * @author KHJ
 */
public class Dispersion {

    private double[][] data;      //  [axis][total cluster]

    public Dispersion(double[][][] d) {     //  [class][axis][cluster]
        initVariables(d);
    }

    private void initVariables(double[][][] d) {
        data = new double[2][(d.length) * (d[0][0].length)];

        for(int i = 0; i < d.length; i++) {
            for(int j = 0; j < d[0][0].length; j++) {
                data[0][(d[0][0].length * i) + j] = d[i][0][j];
                data[1][(d[0][0].length * i) + j] = d[i][1][j];
            }
        }
    }

    public double getDPS() {
        double d1 = 0;
        double d2 = 0;
        int tCnt = data[0].length;

        for(int i = 0; i < tCnt; i++) {
            d1 = d1 + data[0][i];
            d2 = d2 + data[1][i];
        }

        double md1 = d1 / tCnt;
        double md2 = d2 / tCnt;

        d1 = 0;
        d2 = 0;

        for(int j = 0; j < tCnt; j++) {
            d1 = d1 + Math.pow(data[0][j] - md1, 2);
            d2 = d2 + Math.pow(data[1][j] - md2, 2);
        }

        return (d1 + d2) / tCnt;
    }

}
