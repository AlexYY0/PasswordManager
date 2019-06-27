package command;

import encrypt.BasicEncryptMachine;
import mainWindow.MainWindow;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import utils.AESUtils;
import utils.JDBCUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

import static utils.AESUtils.parseByte2HexStr;

public class Edit {
    JDialog newItemDialog = new JDialog(new MainWindow().mainFrame,"修改",true);
    JTextField userNameText = new JTextField(20);
    JTextField passwordText = new JTextField(20);
    JTextField websiteText = new JTextField(20);
    JTextField urlText = new JTextField(20);
    JButton confiemButton = new JButton("确定");
    int databaseDataID = 0;
    public void doit(){
        JPanel newItemPanel = new JPanel();

        newItemDialog.setLayout(new BorderLayout(10,10));
        newItemPanel.setLayout(new GridLayout(9,1,10,10));
        newItemPanel.add(new JLabel("网站"));
        newItemPanel.add(websiteText);
        newItemPanel.add(new JLabel("用户名"));
        newItemPanel.add(userNameText);
        newItemPanel.add(new JLabel("密码"));
        newItemPanel.add(passwordText);
        newItemPanel.add(new JLabel("网址"));
        newItemPanel.add(urlText);
        newItemPanel.add(confiemButton);
        newItemDialog.add(newItemPanel);
        newItemDialog.add(new JPanel(),BorderLayout.EAST);
        newItemDialog.add(new JPanel(),BorderLayout.WEST);
        newItemDialog.add(new JPanel(),BorderLayout.NORTH);
        newItemDialog.add(new JPanel(),BorderLayout.SOUTH);

        confiemButton.addActionListener(new confirmListener());

        //先判断有无选中行
        int editRowNum = MainWindow.jTable.getSelectedRow();
        if (editRowNum!=-1) {
            //显示选中行的信息
            String website = MainWindow.jTable.getValueAt(editRowNum, 0).toString();
            String userName = MainWindow.jTable.getValueAt(editRowNum, 1).toString();
            String password = MainWindow.jTable.getValueAt(editRowNum, 2).toString();
            String url = MainWindow.jTable.getValueAt(editRowNum, 3).toString();

            websiteText.setText(website);
            userNameText.setText(userName);
            passwordText.setText(password);
            urlText.setText(url);

            BasicEncryptMachine be=new BasicEncryptMachine();
            String etwebsite = parseByte2HexStr(AESUtils.AESencrypt(websiteText.getText(),be.encrypt(MainWindow.getAESkey())));
            String etuserName = parseByte2HexStr(AESUtils.AESencrypt(userNameText.getText(),be.encrypt(MainWindow.getAESkey())));
            String etpassword = parseByte2HexStr(AESUtils.AESencrypt(passwordText.getText(),be.encrypt(MainWindow.getAESkey())));
            String eturl = parseByte2HexStr(AESUtils.AESencrypt(urlText.getText(),be.encrypt(MainWindow.getAESkey())));

            QueryRunner queryRunner = new QueryRunner(JDBCUtils.getDataSource());
            String sql = "SELECT id FROM item WHERE website=? AND username=? "
                    + "AND pwd=? AND url=?;";
            try {
                databaseDataID = queryRunner.query(sql,new ScalarHandler<Integer>(),etwebsite,etuserName,etpassword,eturl);
            } catch (SQLException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }

            newItemDialog.setLocationRelativeTo(null);
            newItemDialog.pack();
            newItemDialog.setVisible(true);

        }else {
            //若没有选中行，不显示界面
            JOptionPane.showMessageDialog(newItemDialog, "请选择要更改的行！");
            newItemDialog.dispose();
        }
    }
    //修改记录界面确认按钮事件
    class confirmListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            //更新JTable
            DefaultTableModel tableModel = (DefaultTableModel) MainWindow.jTable.getModel();
            int editRowNum = MainWindow.jTable.getSelectedRow();
            tableModel.setValueAt(websiteText.getText(), editRowNum, 0);
            tableModel.setValueAt(userNameText.getText(), editRowNum, 1);
            tableModel.setValueAt(passwordText.getText(), editRowNum, 2);
            tableModel.setValueAt(urlText.getText(), editRowNum, 3);

            BasicEncryptMachine be=new BasicEncryptMachine();
            String etwebsite = parseByte2HexStr(AESUtils.AESencrypt(websiteText.getText(),be.encrypt(MainWindow.getAESkey())));
            String etuserName = parseByte2HexStr(AESUtils.AESencrypt(userNameText.getText(),be.encrypt(MainWindow.getAESkey())));
            String etpassword = parseByte2HexStr(AESUtils.AESencrypt(passwordText.getText(),be.encrypt(MainWindow.getAESkey())));
            String eturl = parseByte2HexStr(AESUtils.AESencrypt(urlText.getText(),be.encrypt(MainWindow.getAESkey())));
            //更新数据库
            QueryRunner queryRunner = new QueryRunner(JDBCUtils.getDataSource());
            String sql = "UPDATE item SET website=?,username=?,pwd=?,url=? WHERE id=?";
            try {
                queryRunner.update(sql,etwebsite,etuserName,etpassword,eturl,databaseDataID);
            } catch (SQLException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            newItemDialog.dispose();
        }
    }
}
