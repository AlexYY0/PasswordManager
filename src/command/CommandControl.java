package command;

public class CommandControl {
    private ICommand command;
    public CommandControl(ICommand command){
        this.command=command;
    }

    public void setCommand(ICommand command) {
        this.command = command;
    }
    public void ButtonPressed(){
        command.execute();
    }
}
