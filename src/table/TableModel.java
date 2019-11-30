/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package table;

import javax.swing.table.AbstractTableModel;

/**
 *
 * @author KHJ
 */
public class TableModel extends AbstractTableModel  {

    private String[] columnNames;
    private String[] rowNames;
    private Object[][] data;

    public TableModel() {}

    public void newData(int col, int row) {
        this.data = new Object[col][row];
    }

    public void setData(Object[][] str) {
        this.data = str;
    }

    public void setColumnNames(String[] str) {
        this.columnNames = str;
    }

    public void setRowNames(String[] str) {
        this.rowNames = str;
    }

    public Object[][] getData() {
        return this.data;
    }

    public String[] getColumnNames() {
        return this.columnNames;
    }

    public String[] getRowNames() {
        return this.rowNames;
    }

    public int getColumnCount() {
        return columnNames.length;
    }

    public int getRowCount() {
        return rowNames.length;
    }

    public String getColumnName(int col) {
        return columnNames[col];
    }

    public String getRowName(int row) {
        return rowNames[row];
    }

    public Object getValueAt(int row, int col) {
        return data[row][col];
    }
    
    public Class getColumnClass(int c) {
        return getValueAt(0, c).getClass();
    }
    
    public Class getRowClass(int r) {
        return getValueAt(r, 0).getClass();
    }

    /*
     * Don't need to implement this method unless your table's
     * editable.
     */
    public boolean isCellEditable(int row, int col) {
        //Note that the data/cell address is constant,
        //no matter where the cell appears onscreen.
        if (col < 1) {
            return false;
        }
        else if(row < 1) {
            return false;
        }
        else {
            return true;
        }
    }

    /*
     * Don't need to implement this method unless your table's
     * data can change.
     */
    public void setValueAt(Object value, int row, int col) {
        data[row][col] = value;
        fireTableCellUpdated(row, col);
    }

}
