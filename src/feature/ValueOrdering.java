/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package feature;

/**
 *
 * @author KHJ
 */
public class ValueOrdering {

    private double[] v;
    private int length;

    public ValueOrdering(double[] v) {
        this.v = v;
        length = v.length;
        quickSort(0,length-1);
    }

    private void quickSort(int a, int z) {
        double pivot = v[z];
        int i = a;
        int j = z-1;
        while(i < j) {
            if(v[i] < pivot) {
                i++;
            }
            else {
                if(v[j] >= pivot) {
                    j--;
                }
                else {
                    swap(i,j);
                }
            }
        }

        if(v[j] >= v[z]) {
            swap(j,z);
            if(a < (j-1)) {
                quickSort(a,j-1);
            }
            if((j+1) < z) {
                quickSort(j+1,z);
            }
        }
        else if(a < (j-1)){
            quickSort(a,j);
        }
    }

    private void swap(int i, int j) {
        double temp = v[i];
        v[i] = v[j];
        v[j] = temp;
    }

    public double[] getOrdered() {
        return v;
    }

    private void printResult() {
        for(int i = 0; i < length; i++) {
            System.out.print(v[i] + " ");
        }
        System.out.println();
    }

}
