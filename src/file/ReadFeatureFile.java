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
public class ReadFeatureFile {

    private String path = "";
    private int num;        // Feature의 ranked attributes 에서 몇개를 가져올 것인지에 대한 변수.
    private int[] row;

    public ReadFeatureFile(String p) {
        this.path = p;
        this.num = global.Variables.featureNum;
        this.row = new int[num];
    }

    public int[] getOrder() {

        try {
                File f = new File(path);
                FileReader fr = new FileReader(f);
                BufferedReader br = new BufferedReader(fr);
                String line = "";

                while(!br.readLine().equals("Ranked attributes:")) {}

                for(int i = 0; i < row.length; i++) {
                    line = br.readLine();
                    StringTokenizer st = new StringTokenizer(line, "      ");
                    st.nextToken();
                    row[i] = Integer.parseInt(st.nextToken());
                }

                br.close();
                fr.close();
        }
        catch(IOException ioe) {
            ioe.printStackTrace();
        }

        return row;
    }

}
