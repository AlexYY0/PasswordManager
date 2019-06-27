package command;

public class AboutCommand implements ICommand {
    private About about;
    public AboutCommand(About about){
        this.about=about;
    }
    public void execute(){
        about.doit();
    }
}
