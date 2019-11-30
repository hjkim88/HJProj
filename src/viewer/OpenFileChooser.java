/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package viewer;

import javax.swing.JFrame;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author KHJ
 */
public class OpenFileChooser {

    public OpenFileChooser() {}

    public String openChooser(JFrame f, int n) {
        JFileChooser fileChooser = new JFileChooser(global.Variables.fileChooserPath);
        String returnStr = "";

        if(n == 1) {
            fileChooser.setDialogTitle("Select cf File Path");
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            fileChooser.setMultiSelectionEnabled(false);
            int returnVal = fileChooser.showDialog(f, "OK");

            if(returnVal == JFileChooser.APPROVE_OPTION) {
                returnStr = fileChooser.getSelectedFile().getPath();
                global.Variables.fileChooserPath = fileChooser.getSelectedFile().getParent();
            }
        }
        else if(n == 2) {
            fileChooser.setDialogTitle("Select cf File");
            fileChooser.setMultiSelectionEnabled(false);
            FileFilter cfFilter = new FileNameExtensionFilter(
                    "Classifier file (cf)", "cf");
            fileChooser.setFileFilter(cfFilter);
            int returnVal = fileChooser.showDialog(f, "OK");

            if(returnVal == JFileChooser.APPROVE_OPTION) {
                returnStr = fileChooser.getSelectedFile().getPath();
                global.Variables.fileChooserPath = fileChooser.getSelectedFile().getParent();
            }
        }
        else {
            System.out.println("error");
        }

        return returnStr;
    }

}
