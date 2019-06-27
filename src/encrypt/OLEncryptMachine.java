package encrypt;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import utils.JDBCUtils;
import utils.Pbkdf2Sha256Utils;

import java.sql.SQLException;

public class OLEncryptMachine extends OuterLayerEncryptMachine {
    public OLEncryptMachine(EncryptMachine cipher){
        super(cipher);
    }
    public String encrypt(String password){
        password=super.encrypt(password);
        QueryRunner queryRunner = new QueryRunner(JDBCUtils.getDataSource());
        String sql = "select salt2 from account where id = 1";
        String salt2 = null;
        try {
            salt2 = queryRunner.query(sql,new ScalarHandler<String>());
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
        return Pbkdf2Sha256Utils.getEncodedHash(password,salt2,2000);
    }
}
