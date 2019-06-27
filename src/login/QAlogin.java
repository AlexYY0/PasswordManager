package login;

import encrypt.BasicEncryptMachine;
import encrypt.EncryptMachine;
import encrypt.OLEncryptMachine;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import utils.JDBCUtils;
import java.sql.SQLException;

public class QAlogin implements UserService {
    public boolean login(String answer){
        QueryRunner queryRunner = new QueryRunner(JDBCUtils.getDataSource());
        String sql = "select qa from account where id = 1";
        String qa = null;
        try {
            qa = queryRunner.query(sql,new ScalarHandler<String>());
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
        EncryptMachine em=new OLEncryptMachine(new BasicEncryptMachine());
        String hashedQAPassword=em.encrypt(answer);
        if(hashedQAPassword.equals(qa)){
            return true;
        }else {
            return false;
        }
    }
}
