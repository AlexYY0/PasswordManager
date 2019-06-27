package command;

public class DeleteCommand implements ICommand {
    private Delete delete;
    public DeleteCommand(Delete delete){
        this.delete=delete;
    }
    public void execute(){
        delete.doit();
    }
}
