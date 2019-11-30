/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package calculate;

/**
 *
 * @author KHJ
 */
public class GetMaxValue {

    private int[][] resultPair;
    private double[][] data;              // [gene pair1의 rowNum][gene pair2의 rowNum]
    private int th;                       // 상위 x개 Threshold
    private double[] dataPool;
    private double min;
    private int currentOrder;

    public GetMaxValue(double[][] str, int n) { //majority number
        this.data = str;
        this.th = n;
        this.resultPair = new int [th][2];
        this.dataPool = new double[th];
        initVariables();
    }

    private void initVariables() {
        for(int i = 0; i < dataPool.length; i++) {
            dataPool[i] = 0;
        }
    }

    public int[][] getResultPair() {
        for(int i = 1; i < data.length; i++) {
            for(int j = i+1; j < data[0].length; j++) {
                getMin(dataPool);
                if(min < data[i][j]) {  //currentOrder = the index of minimum value in dataPool
                    dataPool[currentOrder] = data[i][j];
                    resultPair[currentOrder][0] = i;
                    resultPair[currentOrder][1] = j;
                }
            }
        }

        for(int i = 0; i < resultPair.length; i++) {
            System.out.println("resultPair = " + resultPair[i][0] + ", " + resultPair[i][1]);
        }

        return this.resultPair;
    }

    private void getMin(double[] d) {
        min = d[0];
        for(int i = 1; i < d.length; i++) {
            min = Math.min(min, d[i]);
        }
        for(int j = 0; j < d.length; j++) {
            if(d[j] == min) {
                currentOrder = j;
            }
        }
    }

}
