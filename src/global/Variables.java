/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package global;

/**
 *
 * @author KHJ
 */
public class Variables {

    public final static int featureNum = 500;           // Feature 의 ranked attributes 에서 몇개를 가져올 것인지에 대한 변수
    public final static int kMeansThreshold = 1000;     // K-Means 반복하는 쓰레숄드 지정
    public final static int clstrNum = 5;              // Cluster 의 개수
    public final static int majorityNum = 10;          // Prediction 을 할 때, 상위 몇개를 가지고 Majority vote 를 할 것이냐에 대한 변수
    public final static String ftrPath = "C:/Users/KHJ/Documents/NetBeansProjects/HJProj/data/FSAVD/GSE21034_100.ftr";
    public static String fileChooserPath = "";

}
