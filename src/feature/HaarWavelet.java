/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package feature;

/**
 *
 * @author KHJ
 */
public class HaarWavelet {
    
    private double[] a, a1, a2, d1, d2;
    private int originalLen;
    private int Len1, Len2;
    private double threshold;
    private int[] d2Set;
    private int[] boundary;
    private double[] mean;
    private int setNum;

    public HaarWavelet(double[] d) {
        this.a = d;
        initVariables();
        HaarTransform();
        setDetection();
        calculateSetNum();
        calculateMean();
    }

    private void initVariables() {
        originalLen = a.length;
        Len1 = originalLen / 2;
        Len2 = Len1 / 2;
        a1 = new double[Len1];
        d1 = new double[Len1];
        a2 = new double[Len2];
        d2 = new double[Len2];
        boundary = new int[originalLen];
        for(int i = 0; i < originalLen; i++) {
            boundary[i] = 1;
        }
        setNum = 0;
    }

    private void HaarTransform() {
        for(int i = 0; i < Len1; i++) {
            a1[i] = (a[2*i] + a[2*i+1]) / Math.sqrt(2);
            d1[i] = Math.abs(a[2*i] - a[2*i+1]) / Math.sqrt(2);
        }

        for(int j = 0; j < Len2; j++) {
            a2[j] = (a1[2*j] + a1[2*j+1]) / Math.sqrt(2);
            d2[j] = Math.abs(a1[2*j] - a1[2*j+1]) / Math.sqrt(2);
        }
    }

    private void setDetection() {
        double max = d2[0];
        double min = d2[0];
        
        for(int i = 0; i < Len2; i++) {
            max = Math.max(d2[i], max);
            min = Math.min(d2[i], min);
        }
        
        threshold = (max - min) / 2;
        
        int diffNum = 0;
        for(int j = 0; j < Len2; j++) {
            if(threshold < d2[j]) {
                diffNum++;
            }
        }
        d2Set = new int[diffNum];
        
        int cnt = 0;
        for(int k = 0; k < Len2; k++) {
            if(threshold < d2[k]) {
                d2Set[cnt] = k; 
                cnt++;
            }
        }
        
        for(int l = 0; l < diffNum; l++) {
            for(int m = 0; m < 4; m++) {
                boundary[(d2Set[l]*4)+m] = 0;
            }
        }
    }
    
    private void calculateSetNum() {
        for(int i = 0; i < (originalLen-1); i++) {
            if((boundary[i] == 1) && (boundary[i+1] == 0)) {
                setNum++;
            }
        }
        if(boundary[originalLen-1] == 1) {
            setNum++;
        }
    }
    
    private void calculateMean() {
        mean = new double[setNum];
        double sum = 0;
        int cnt = 0;
        int checkP = 0;
        
        for(int i = 0; i < (originalLen-1); i++) {
            sum = sum + a[i];
            if((boundary[i] == 1) && (boundary[i+1] == 0)) {
                mean[cnt] = sum / (double) (i - checkP+1);
                cnt++;
                sum = 0;
            }
            if((boundary[i] == 0) && (boundary[i+1] == 1)) {
                checkP = i+1;
            }
        }
    }
    
    public int getSetNum() {
        return this.setNum;
    }
    
    public double[] getMean() {
        return this.mean;
    }
    
}
