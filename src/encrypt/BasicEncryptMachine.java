package encrypt;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import utils.JDBCUtils;
import utils.Pbkdf2Sha256Utils;

import java.sql.SQLException;

public class BasicEncryptMachine extends EncryptMachine {
    public String encrypt(String password){
        QueryRunner queryRunner = new QueryRunner(JDBCUtils.getDataSource());
        String sql = "select salt1 from account where id = 1";
        String salt1 = null;
        try {
            salt1 = queryRunner.query(sql,new ScalarHandler<String>());
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
        return Pbkdf2Sha256Utils.getEncodedHash(password,salt1,2000);
    }
}
