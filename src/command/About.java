package command;

import mainWindow.MainWindow;

import javax.swing.*;
import java.awt.*;

public class About {
    public void doit(){
        JDialog aboutDialog = new JDialog(new MainWindow().mainFrame, "关于", true);
        JLabel label1 = new JLabel("PasswordManager");
        JLabel label2 = new JLabel("密码管理器桌面离线版");
        JPanel jPanel = new JPanel(new GridLayout(2, 1));
        aboutDialog.setLayout(new BorderLayout(20,20));

        jPanel.add(label1);
        jPanel.add(label2);
        aboutDialog.add(jPanel);
        aboutDialog.add(new JPanel(),BorderLayout.EAST);
        aboutDialog.add(new JPanel(),BorderLayout.WEST);
        aboutDialog.add(new JPanel(),BorderLayout.NORTH);
        aboutDialog.add(new JPanel(),BorderLayout.SOUTH);

        aboutDialog.pack();
        aboutDialog.setLocationRelativeTo(null);
        aboutDialog.setVisible(true);
    }
}
