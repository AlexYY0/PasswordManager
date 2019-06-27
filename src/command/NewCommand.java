package command;

public class NewCommand implements ICommand {
    private NewOne newone;
    public NewCommand(NewOne newone){
        this.newone=newone;
    }
    public void execute(){
        newone.doit();
    }
}
