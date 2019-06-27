package login;

import encrypt.BasicEncryptMachine;
import encrypt.EncryptMachine;
import encrypt.OLEncryptMachine;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import utils.JDBCUtils;

import java.sql.SQLException;

public class PWlogin implements UserService {
    public boolean login(String pw){
        QueryRunner queryRunner = new QueryRunner(JDBCUtils.getDataSource());
        String sql = "select pwd from account where id = 1";
        String password = null;
        try {
            password = queryRunner.query(sql,new ScalarHandler<String>());
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
        EncryptMachine em=new OLEncryptMachine(new BasicEncryptMachine());
        String hashedPassword=em.encrypt(pw);
        if(hashedPassword.equals(password)){
            return true;
        }else {
            return false;
        }
    }
}
