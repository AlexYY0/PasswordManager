package login;

public class PWFactory implements UserServiceFactory {
    public UserService createUserService(){
        return new PWlogin();
    }
}
