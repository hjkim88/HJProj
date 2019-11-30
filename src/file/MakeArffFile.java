/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package file;

import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.io.IOException;
import javax.swing.JTable;

/**
 *
 * @author KHJ
 */
public class MakeArffFile {

    private FileWriter fw;
    private BufferedWriter bw;
    private PrintWriter outFile;

    public MakeArffFile() {

    }

    public void makeFile(String name, JTable t, String path) {

         try {
            fw = new FileWriter(path + "\\" + name + ".arff");
            bw = new BufferedWriter(fw);
            outFile = new PrintWriter(bw);

            outFile.println("%Title : GSE15484N Data");
            outFile.println("%User : Hyunjin Kim, Yonsei Univ., Seoul, Korea");
            outFile.println("%E-mail : firadazer@gmail.com");
            outFile.println("%");
            outFile.println("%");
            outFile.println("@RELATION GSE15484N");
            outFile.println("%");

            for(int i = 0; i < t.getRowCount()-1; i++) {
                outFile.println("@ATTRIBUTE" + "\t" + t.getValueAt(i+1, 0) + "\t" + "REAL");
            }
            outFile.println("@ATTRIBUTE class   {T, N}");
            outFile.println("%");

            outFile.println("@DATA");

            for(int j = 0; j < t.getColumnCount()-1; j++) {
                for(int k = 0; k < t.getRowCount()-1; k++) {
                    outFile.print(t.getValueAt(k+1, j+1) + ", ");
                }
                outFile.println(t.getValueAt(0, j+1));
            }
            outFile.println("%");
            outFile.println("%");

            outFile.close();
            bw.close();
            fw.close();
        }
        catch(IOException ioe) {
            ioe.printStackTrace();
        }

    }

}
