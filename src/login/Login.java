package login;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import JTextFieldLimit.PasswordLimit;
import encrypt.BasicEncryptMachine;
import encrypt.EncryptMachine;
import encrypt.OLEncryptMachine;
import findPasswordWindow.FindPasswordWindow;
import mainWindow.MainWindow;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ArrayListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import utils.AESUtils;
import utils.JDBCUtils;
import utils.Pbkdf2Sha256Utils;

import static utils.AESUtils.parseByte2HexStr;
import static utils.AESUtils.parseHexStr2Byte;

public class Login{
	JFrame loginFrame = new JFrame("登录界面");
	JPasswordField passwordText = new JPasswordField(20);
	//初始化界面
		public void init() {
			passwordText.setDocument(new PasswordLimit(11));
			String sql = null;
			QueryRunner queryRunner = new QueryRunner(JDBCUtils.getDataSource());
			//查询数据库test下有没有存储密码的account表格
			long count = -1;
			sql = "SELECT COUNT(*) FROM information_schema.TABLES t WHERE t.TABLE_SCHEMA ='test' AND t.TABLE_NAME ='account'";
			try {
				count = queryRunner.query(sql, new ScalarHandler<Long>());
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//count为0，表示没有account，通过queryrunner新建一个account表格
			if (count==0) {
				sql = "CREATE TABLE account(id INT PRIMARY KEY AUTO_INCREMENT,pwd VARCHAR(192),salt1 VARCHAR(16),salt2 VARCHAR(16),qa VARCHAR(192))";
				try {
//					statement.executeUpdate(sql);
					queryRunner.update(sql);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			//查询存储账号密码的item表格存不存在
			sql = "SELECT COUNT(*) FROM information_schema.TABLES t WHERE t.TABLE_SCHEMA ='test' AND t.TABLE_NAME ='item'";
			try {
				count = queryRunner.query(sql, new ScalarHandler<Long>());
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//如果不存在，新建item表格
			if (count==0) {
				sql = "CREATE TABLE item(id INT PRIMARY KEY AUTO_INCREMENT,website VARCHAR(192),"
						+ "username VARCHAR(192),pwd VARCHAR(192),url VARCHAR(192))";
				try {
//					statement.executeUpdate(sql);
					queryRunner.update(sql);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//获取主密码初始化盐值并存入数据库
				String hashedPassword1= Pbkdf2Sha256Utils.encode("1234");
				String[] parts1 = hashedPassword1.split("\\$");
				String salt1 = parts1[2];
				String hashedPassword2= Pbkdf2Sha256Utils.encode(parts1[3]);
				String[] parts2 = hashedPassword2.split("\\$");
				String salt2 = parts2[2];
				String hashedPassword=parts2[3];
				//获取问答式登陆密码
				String qa1=Pbkdf2Sha256Utils.getEncodedHash("Alex",salt1,2000);
				String qa2=Pbkdf2Sha256Utils.getEncodedHash(qa1,salt2,2000);
				sql = "INSERT INTO account VALUES (1,?,?,?,?)";
				try {
					queryRunner.update(sql,hashedPassword,salt1,salt2,qa2);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//保存问答式密码
				String etwebsite = parseByte2HexStr(AESUtils.AESencrypt("Your master password",qa1));
				String etuserName = parseByte2HexStr(AESUtils.AESencrypt("no",qa1));
				String etpassword = parseByte2HexStr(AESUtils.AESencrypt("1234",qa1));
				String eturl = parseByte2HexStr(AESUtils.AESencrypt("Master password",qa1));
				sql = "INSERT INTO item (website,username,pwd,url) VALUES (?,?,?,?)";
				try {
					queryRunner.update(sql,etwebsite,etuserName,etpassword,eturl);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			//界面初始化
			JPanel loginPanel1 = new JPanel();
			JPanel loginPanel2 = new JPanel();
			JButton loginButton = new JButton("登录");
			JButton qaButton = new JButton("问答式登录");
			JButton changePasswordButton = new JButton("修改密码");
			
			loginFrame.setLayout(new BorderLayout(10,10));
			loginPanel1.setLayout(new GridLayout(1, 2,0,10));
			loginPanel1.add(new JLabel("密码:"));
			loginPanel1.add(passwordText);
			loginPanel2.setLayout(new GridLayout(1, 2,50,50));
			loginPanel2.add(loginButton);
			loginPanel2.add(qaButton);
			loginPanel2.add(changePasswordButton);
			loginFrame.add(loginPanel1);
			//为了让四周留空，看起来不那么拥挤
			loginFrame.add(loginPanel2,BorderLayout.SOUTH);
			loginFrame.add(new JPanel(),BorderLayout.EAST);
			loginFrame.add(new JPanel(),BorderLayout.WEST);
			loginFrame.add(new JPanel(),BorderLayout.NORTH);
			
			loginFrame.pack();
			loginFrame.setLocationRelativeTo(null);
			loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			loginFrame.setVisible(true);
			//注册按钮事件
			loginButton.addActionListener(new loginDialog());
			qaButton.addActionListener(new qaButtonListener());
			changePasswordButton.addActionListener(new changePasswordButtonListener());
		}
		//更改密码按钮事件，内部类处理
		public class changePasswordButtonListener implements ActionListener {
			public void actionPerformed(ActionEvent e) {
				//需要新建新建一个界面，故单独写了一个类
				new changePasswordDialog().init();
			}
		}	
		
		//登录按钮事件响应，判断输入的密码和存储的密码一不一致，若一致显示主界面
		class loginDialog implements ActionListener{
			public void actionPerformed(ActionEvent e) {
				//检测输入是否达标
				if (String.valueOf(passwordText.getPassword()).equals("")) {
					JOptionPane.showMessageDialog(null, "输入不能为空！", "注意", JOptionPane.INFORMATION_MESSAGE);
					return;
				}
				UserServiceFactory pw=new PWFactory();
				UserService userService=pw.createUserService();
				if (userService.login(String.valueOf(passwordText.getPassword()))) {
					//若一致，关闭登陆界面，显示主界面
					loginFrame.dispose();
					new MainWindow().init(String.valueOf(passwordText.getPassword()));
				}else {
					JOptionPane.showMessageDialog(loginFrame, "密码错误，请重新输入！");
				}
			}
		}

		//问答式登录按钮事件响应，判断输入的答案和存储的答案一不一致，若一致显示主界面
		public class qaButtonListener implements ActionListener{
			public void actionPerformed(ActionEvent e){

				//需要新建新建一个界面，故单独写了一个类
				new qaButtonDialog().init();
			}
		}

}

//问答式登陆
class qaButtonDialog{
	JDialog dialog = new JDialog(new Login().loginFrame,"问答式登陆",true);
	JTextField qaPassword = new JTextField(20);
	JButton confiemButton = new JButton("确定");
	void init() {
		JPanel jPanel = new JPanel(new GridLayout(5,1,10,10));
		dialog.setLayout(new BorderLayout(10, 10));
		jPanel.add(new JLabel("你爷爷叫什么名字？"));
		jPanel.add(qaPassword);
		jPanel.add(confiemButton);
		dialog.add(jPanel);
		dialog.add(new JPanel(),BorderLayout.EAST);
		dialog.add(new JPanel(),BorderLayout.WEST);
		dialog.add(new JPanel(),BorderLayout.NORTH);
		dialog.add(new JPanel(),BorderLayout.SOUTH);

		confiemButton.addActionListener(new qaconfirmListener());
		dialog.pack();
		dialog.setLocationRelativeTo(null);
		dialog.setVisible(true);
	}

	//问答式登录按钮事件响应，判断输入的答案和存储的答案一不一致
	class qaconfirmListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			UserServiceFactory qa=new QAFactory();
			UserService userService=qa.createUserService();
			if (userService.login(qaPassword.getText())) {
				JOptionPane.showMessageDialog(dialog, "问答式登录成功！");
				dialog.dispose();
				new FindPasswordWindow().init(qaPassword.getText());
			}else {
				JOptionPane.showMessageDialog(dialog, "答案错误，请重新输入！");
			}
		}
	}
}

//主要是更改密码界面初始化
class changePasswordDialog {
	JDialog dialog = new JDialog(new Login().loginFrame,"修改密码",true);
	JTextField oldPassWord = new JTextField(20);
	JTextField newPassWord = new JTextField(20);
	JButton confiemButton = new JButton("确定");
	void init() {
		oldPassWord.setDocument(new PasswordLimit(11));
		newPassWord.setDocument(new PasswordLimit(11));
		JPanel jPanel = new JPanel(new GridLayout(5,1,10,10));
		dialog.setLayout(new BorderLayout(10, 10));
		jPanel.add(new JLabel("旧密码"));
		jPanel.add(oldPassWord);
		jPanel.add(new JLabel("新密码"));
		jPanel.add(newPassWord);
		jPanel.add(confiemButton);
		dialog.add(jPanel);
		dialog.add(new JPanel(),BorderLayout.EAST);
		dialog.add(new JPanel(),BorderLayout.WEST);
		dialog.add(new JPanel(),BorderLayout.NORTH);
		dialog.add(new JPanel(),BorderLayout.SOUTH);
		
//		dialog.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		confiemButton.addActionListener(new confirmListener());
		dialog.pack();
		dialog.setLocationRelativeTo(null);
		dialog.setVisible(true);
	}
	
	//修改密码对话框确定按钮事件，先判断原密码与数据库中一不一致，若一致可以更改
	class confirmListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (oldPassWord.getText().equals("")||newPassWord.getText().equals("")) {
				JOptionPane.showMessageDialog(null, "输入不能为空！", "注意", JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			QueryRunner queryRunner = new QueryRunner(JDBCUtils.getDataSource());
			String sql = "select pwd from account where id = 1";
			String password = null;
			try {
				password = queryRunner.query(sql,new ScalarHandler<String>());
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			//现场加密对比
			EncryptMachine em=new OLEncryptMachine(new BasicEncryptMachine());
			String oldHashedPassword=em.encrypt(oldPassWord.getText());
			//获取初始化盐值存入数据库
			String hashedPassword1= Pbkdf2Sha256Utils.encode(newPassWord.getText());
			String[] parts1 = hashedPassword1.split("\\$");
			String salt1 = parts1[2];
			String hashedPassword2= Pbkdf2Sha256Utils.encode(parts1[3]);
			String[] parts2 = hashedPassword2.split("\\$");
			String salt2 = parts2[2];
			String hashedPassword=parts2[3];
			if (oldHashedPassword.equals(password)) {
				BasicEncryptMachine be=new BasicEncryptMachine();
				String oldKey=be.encrypt(oldPassWord.getText());
				//更新account值
				//更新问答式登陆密码
				String qa1=Pbkdf2Sha256Utils.getEncodedHash("Alex",salt1,2000);
				String qa2=Pbkdf2Sha256Utils.getEncodedHash(qa1,salt2,2000);
				sql = "update account set pwd = ?,salt1=?,salt2=?,qa=? where id = 1";
				try {
					queryRunner.update(sql,hashedPassword,salt1,salt2,qa2);
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				JOptionPane.showMessageDialog(dialog, "修改密码成功！");
				String newKey=be.encrypt(newPassWord.getText());
				List<Object[]> item = null;
				//密码被修改了，加密密钥也就被修改了，需要重新解密再加密
				sql = "SELECT website,username,pwd,url FROM item where id not in (1)";
				try {
					item = queryRunner.query(sql,new ArrayListHandler());
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				//解密再加密数据
				for(Object[] objects:item) {
					int i=0;
					for(Object object:objects) {
						String decrypt = new String(AESUtils.AESdecrypt(parseHexStr2Byte((String) object),oldKey));
						String encrypt = parseByte2HexStr(AESUtils.AESencrypt(decrypt,newKey));
						object=(Object)encrypt;
						objects[i]=object;
						i++;
					}
				}
				Object[] data = (Object[])item.toArray();
				Object[][] allData = new Object[data.length][];
				for(int i=0;i<data.length;i++){
					allData[i] = (Object[])data[i];
				}
				//重置数据表
				sql="truncate table item";
				try {
					queryRunner.update(sql);
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				//更新问答式密码
				String etwebsite = parseByte2HexStr(AESUtils.AESencrypt("Your master password",be.encrypt("Alex")));
				String etuserName = parseByte2HexStr(AESUtils.AESencrypt("no",be.encrypt("Alex")));
				String etpassword = parseByte2HexStr(AESUtils.AESencrypt(newPassWord.getText(),be.encrypt("Alex")));
				String eturl = parseByte2HexStr(AESUtils.AESencrypt("Master password",be.encrypt("Alex")));
				sql = "INSERT INTO item (website,username,pwd,url) VALUES (?,?,?,?)";
				try {
					queryRunner.update(sql,etwebsite,etuserName,etpassword,eturl);
				} catch (SQLException el) {
					// TODO Auto-generated catch block
					el.printStackTrace();
				}
				//新密码加密数据
				sql = "INSERT INTO item (website,username,pwd,url) VALUES (?,?,?,?)";
				try {
					queryRunner.batch(sql,allData);
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				dialog.dispose();
			}else {
				JOptionPane.showMessageDialog(dialog, "原密码错误，请重新输入！");
			}
		}
	}	
}






