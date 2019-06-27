package command;

public class EditCommand implements ICommand {
    private Edit edit;
    public EditCommand(Edit edit){
        this.edit=edit;
    }
    public void execute(){
        edit.doit();
    }
}
