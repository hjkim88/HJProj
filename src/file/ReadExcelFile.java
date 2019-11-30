/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package file;

import java.io.File;
import java.io.IOException;
import jxl.Workbook;
import jxl.Sheet;
import jxl.NumberCell;


/**
 *
 * @author KHJ
 */
public class ReadExcelFile {

    private table.TableModel tm;
    private NumberCell nc;

    public ReadExcelFile() {}

    public void read(int order, String path) {
        try {
            tm = new table.TableModel();
            Workbook workbook = Workbook.getWorkbook(new File(path));
            Sheet sheet = workbook.getSheet(0);

            int colNum = sheet.getColumns();
            int rowNum = sheet.getRows();
            String[] col = new String[colNum];
            String[] row = new String[rowNum-1];
            tm.newData(rowNum-1, colNum);

            for(int i = 1; i < rowNum; i++) {
                row[i-1] = sheet.getCell(0,i).getContents();
                tm.setValueAt(sheet.getCell(0,i).getContents(), i-1, 0);
            }
            col[0] = "";
            for(int i = 1; i < colNum; i++) {
                col[i] = sheet.getCell(i, 0).getContents();
                tm.setValueAt(sheet.getCell(i,1).getContents(), 0, i);
                for(int j = 2; j < rowNum; j++) {
                    nc = (NumberCell) sheet.getCell(i, j);
                    nc.getNumberFormat().setMaximumFractionDigits(10);
                    tm.setValueAt(nc.getContents(), j-1, i);
                }
            }
            tm.setColumnNames(col);
            tm.setRowNames(row);

            workbook.close();
        }
        catch(IOException ioe) {
            ioe.printStackTrace();
        }
        catch(jxl.JXLException jxle) {
            jxle.printStackTrace();
        }
    }

    public table.TableModel getTableModel() {
        return this.tm;
    }
}