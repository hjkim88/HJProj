/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package calculate;

/**
 *
 * @author KHJ
 */
public class AUC {
    
    private double[] FPR;
    private double[] TPR;
    private int length;
    private double AUC;

    public AUC(double[] specificity, double[] sensitivity) {
        length = specificity.length;
        initVariables(specificity, sensitivity);
        calculateAUC();
    }

    private void initVariables(double[] specificity, double[] sensitivity) {
        AUC = 0;
        FPR = new double[length];
        TPR = new double[length];
        for(int i = 0; i < length; i++) {
                FPR[i] = 1 - specificity[i];
                TPR[i] = sensitivity[i];
        }
    }

    private void calculateAUC() {
        for(int i = 1; i < length; i++) {
            if(FPR[i-1] != FPR[i]) {
                AUC = AUC + ((TPR[i-1] + TPR[i]) * (FPR[i] - FPR[i-1]) / 2) ;
            }
        }
    }

    public double getAUC() {
        return this.AUC;
    }

}
