/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package file;

import javax.swing.JTable;
import java.io.File;
import java.io.IOException;
import jxl.Workbook;
import jxl.write.WritableWorkbook;
import jxl.write.WritableSheet;
import jxl.write.WritableCellFormat;
import jxl.write.WriteException;
import jxl.write.Label;
import jxl.write.Number;

/**
 *
 * @author KHJ
 */
public class SaveXLSFile {

    String path = "";
    JTable table;

    public SaveXLSFile(String p, JTable t) {
        this.path = p;
        this.table = t;
    }

    public void Save() {
        try {
            WritableWorkbook wb = Workbook.createWorkbook(new File(path));
            WritableSheet sheet = wb.createSheet("Sheet1", 0);

            WritableCellFormat dataFormat = new WritableCellFormat();

            Label l;
            Number n;

            for(int k = 0; k < table.getColumnCount(); k++) {
                l = new Label(k, 0, table.getColumnName(k), dataFormat);
                sheet.addCell(l);
                l = new Label(k, 1, table.getValueAt(0, k).toString(), dataFormat);
                sheet.addCell(l);
            }

            for(int i = 1; i < table.getRowCount(); i++) {
                l = new Label(0, i+1, table.getValueAt(i, 0).toString(), dataFormat);
                sheet.addCell(l);
                for(int j = 1; j < table.getColumnCount(); j++) {
                    n = new Number(j, i+1, Double.parseDouble(table.getValueAt(i, j).toString()), dataFormat);
                    sheet.addCell(n);
                }
            }
            wb.write();
            wb.close();
        }
        catch(IOException ioe) {
            ioe.printStackTrace();
        }
        catch(WriteException we) {
            we.printStackTrace();
        }
        
    }

}
