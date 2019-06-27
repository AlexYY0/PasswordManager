package login;

public class QAFactory implements UserServiceFactory {
    public UserService createUserService(){
        return new QAlogin();
    }
}
