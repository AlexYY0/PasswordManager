package login;

public class UserServiceFactory {
    public UserService getPWUserService(){
        return new PWlogin();
    }
    public UserService getQAUserService(){
        return new QAlogin();
    }
}
