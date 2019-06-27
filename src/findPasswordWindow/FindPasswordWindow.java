package findPasswordWindow;

import encrypt.BasicEncryptMachine;
import mainWindow.MainWindow;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ArrayListHandler;
import utils.AESUtils;
import utils.JDBCUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

import static utils.AESUtils.parseHexStr2Byte;

public class FindPasswordWindow {
    private static String AESkey;
    public JFrame mainFrame = new JFrame("找回密码");
    public static JTable jTable;

    //初始化界面
    public void init(String AESkey) {
        this.AESkey=AESkey;

        //界面初始化
        List<Object[]> item = null;

        QueryRunner queryRunner = new QueryRunner(JDBCUtils.getDataSource());
        String sql = "SELECT website,username,pwd,url FROM item where id=1";
        try {
            item = queryRunner.query(sql,new ArrayListHandler());
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
        //读取数据库中的数据并显示在表格中
        Vector<Vector<Object>> tableData = new Vector<Vector<Object>>();
        for(Object[] objects:item) {
            Vector<Object> objects2 = new Vector<Object>();
            for(Object object:objects) {
                BasicEncryptMachine be=new BasicEncryptMachine();
                String decrypt = new String(AESUtils.AESdecrypt(parseHexStr2Byte((String) object),be.encrypt(AESkey)));
                object=(Object)decrypt;
                objects2.add(object);
            }
            tableData.add(objects2);
        }
        Vector<String> tableTitle = new Vector<String>();
        tableTitle.addElement("网站");tableTitle.addElement("用户名");
        tableTitle.addElement("密码");tableTitle.addElement("网址");
        DefaultTableModel tableModel = new DefaultTableModel(tableData, tableTitle) {
            /**
             * 设置Jtable不能编辑只可以选择
             */
            private static final long serialVersionUID = 1L;

            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        jTable = new JTable(tableModel);
        mainFrame.add(new JScrollPane(jTable));
        mainFrame.pack();
        mainFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setVisible(true);
    }
}
