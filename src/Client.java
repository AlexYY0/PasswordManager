import login.Login;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;


public class Client
{
	public static void main(String[] args)
	{
		//程序入口，显示登陆界面
		new Login().init();
		
		//设置UI风格
		try {
			UIManager.setLookAndFeel(
					"com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel");
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
