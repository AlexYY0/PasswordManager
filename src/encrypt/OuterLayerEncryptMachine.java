package encrypt;

public abstract class OuterLayerEncryptMachine extends EncryptMachine{
    protected EncryptMachine cipher;
    public OuterLayerEncryptMachine(EncryptMachine cipher){
        super();
        this.cipher=cipher;
    }
    public String encrypt(String password){
        return cipher.encrypt(password);
    }
}
