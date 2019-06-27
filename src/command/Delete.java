package command;

import encrypt.BasicEncryptMachine;
import mainWindow.MainWindow;
import org.apache.commons.dbutils.QueryRunner;
import utils.AESUtils;
import utils.JDBCUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.SQLException;

import static mainWindow.MainWindow.jTable;
import static utils.AESUtils.parseByte2HexStr;

public class Delete {
    public void doit(){
        int selectRow = jTable.getSelectedRow();
        if (selectRow==-1) {
            JOptionPane.showMessageDialog(new MainWindow().mainFrame, "请选择要删除的行");
            return;
        }
        int option = JOptionPane.showConfirmDialog(new MainWindow().mainFrame, "确定删除？");
        //确定删除执行代码
        if (option==JOptionPane.YES_OPTION) {
            DefaultTableModel model = (DefaultTableModel)jTable.getModel();
            //删除mysql数据库记录
            BasicEncryptMachine be=new BasicEncryptMachine();
            String etwebsite = parseByte2HexStr(AESUtils.AESencrypt((String) model.getValueAt(selectRow, 0),be.encrypt(MainWindow.getAESkey())));
            String etuserName = parseByte2HexStr(AESUtils.AESencrypt((String)model.getValueAt(selectRow, 1),be.encrypt(MainWindow.getAESkey())));
            String etpassword = parseByte2HexStr(AESUtils.AESencrypt((String)model.getValueAt(selectRow, 2),be.encrypt(MainWindow.getAESkey())));
            String eturl = parseByte2HexStr(AESUtils.AESencrypt((String)model.getValueAt(selectRow, 3),be.encrypt(MainWindow.getAESkey())));
            QueryRunner queryRunner = new QueryRunner(JDBCUtils.getDataSource());
            String sql = "DELETE FROM item WHERE website=? AND username=? AND pwd=? AND url=?";
            try {
                queryRunner.update(sql,etwebsite,etuserName,etpassword,eturl);
            } catch (SQLException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            //删除jtable数据
            model.removeRow(selectRow);
        }
    }
}
