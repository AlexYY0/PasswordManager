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

public class NewOne {
    JDialog newItemDialog = new JDialog(new MainWindow().mainFrame,"新建",true);
    JTextField websiteText = new JTextField(20);
    JTextField userNameText = new JTextField(20);
    JTextField passwordText = new JTextField(20);
    JTextField urlText = new JTextField(20);
    JButton confiemButton = new JButton("确定");
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
        newItemDialog.pack();
        newItemDialog.setLocationRelativeTo(null);
        newItemDialog.setVisible(true);
    }
    //新建项目对话框确认按钮事件响应
    class confirmListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            BasicEncryptMachine be=new BasicEncryptMachine();
            String etwebsite = parseByte2HexStr(AESUtils.AESencrypt(websiteText.getText(),be.encrypt(MainWindow.getAESkey())));
            String etuserName = parseByte2HexStr(AESUtils.AESencrypt(userNameText.getText(),be.encrypt(MainWindow.getAESkey())));
            String etpassword = parseByte2HexStr(AESUtils.AESencrypt(passwordText.getText(),be.encrypt(MainWindow.getAESkey())));
            String eturl = parseByte2HexStr(AESUtils.AESencrypt(urlText.getText(),be.encrypt(MainWindow.getAESkey())));
            QueryRunner queryRunner = new QueryRunner(JDBCUtils.getDataSource());
            String sql = null;
            //先判断用户有无输入内容
            if (websiteText.getText().equals("")) {
                JOptionPane.showMessageDialog(newItemDialog, "请输入网站名！");
                return;
            }
            //查询数据库中有无此记录，若有不让添加
            sql = "SELECT id FROM item WHERE website=? AND username=? "
                    + "AND pwd=? AND url=?;";
            try {
                Integer id = queryRunner.query(sql,new ScalarHandler<Integer>(),etwebsite,etuserName,etpassword,eturl);
                if (id!=null) {
                    JOptionPane.showMessageDialog(newItemDialog, "已存在此记录，请重新输入！");
                    return;	//直接返回
                }
            } catch (SQLException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            //若没有此记录，添加进数据库
            sql = "INSERT INTO item (website,username,pwd,url) VALUES (?,?,?,?)";
            try {
                queryRunner.update(sql,etwebsite,etuserName,etpassword,eturl);
            } catch (SQLException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            DefaultTableModel tableModel = (DefaultTableModel) MainWindow.jTable.getModel();
            tableModel.addRow(new Object[] {websiteText.getText(),userNameText.getText(),
                    passwordText.getText(),urlText.getText()});
            newItemDialog.dispose();
        }
    }
}
