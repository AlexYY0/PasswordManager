package mainWindow;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTable;;
import javax.swing.table.DefaultTableModel;

import command.*;
import encrypt.BasicEncryptMachine;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ArrayListHandler;
import utils.AESUtils;
import utils.JDBCUtils;

import static utils.AESUtils.parseHexStr2Byte;

//主界面类
public class MainWindow {
	private static String AESkey;
	public static String getAESkey(){
		return AESkey;
	}
	public JFrame mainFrame = new JFrame("密码管理");
	JMenu menu = new JMenu("更改");
	JMenuItem newMenuItem = new JMenuItem("新建");
	JMenuItem editMenuItem = new JMenuItem("修改");
	JMenuItem deleteMenuItem = new JMenuItem("删除");
	JMenuItem aboutMenuItem = new JMenuItem("关于");
	public static JTable jTable;

	//初始化界面
	public void init(String AESkey) {
		this.AESkey=AESkey;

		//界面初始化
		JMenuBar menuBar = new JMenuBar();
		List<Object[]> item = null;
		
		menu.add(newMenuItem);
		menu.add(editMenuItem);
		menu.add(deleteMenuItem);
		menu.add(aboutMenuItem);
		menuBar.add(menu);

		QueryRunner queryRunner = new QueryRunner(JDBCUtils.getDataSource());
		String sql = "SELECT website,username,pwd,url FROM item where id not in (1)";
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
		//注册按钮事件
		newMenuItem.addActionListener(new newMenuListener());
		editMenuItem.addActionListener(new editMenuListener());
		deleteMenuItem.addActionListener(new deleteMenuListener());
		aboutMenuItem.addActionListener(new aboutMenuListener());
		
		mainFrame.setJMenuBar(menuBar);
		mainFrame.add(new JScrollPane(jTable));
		mainFrame.pack();
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setLocationRelativeTo(null);
		mainFrame.setVisible(true);
	}
	//关于按钮事件响应
	public class aboutMenuListener implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			//添加命令
			AboutCommand about=new AboutCommand(new About());
			CommandControl cc=new CommandControl(about);
			cc.ButtonPressed();
		}
	}
	//新建按钮事件响应
	public class newMenuListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			//添加命令
			NewCommand newone=new NewCommand(new NewOne());
			CommandControl cc=new CommandControl(newone);
			cc.ButtonPressed();
		}
	}	
	//编辑按钮事件响应
	public class editMenuListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			//添加命令
			EditCommand edit=new EditCommand(new Edit());
			CommandControl cc=new CommandControl(edit);
			cc.ButtonPressed();
		}
	}	
	//删除按钮事件响应，不用界面，只写了内部类
	public class deleteMenuListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			//添加命令
			DeleteCommand delete=new DeleteCommand(new Delete());
			CommandControl cc=new CommandControl(delete);
			cc.ButtonPressed();
		}
	}	
}
