package valid.namecompletion;

public class Command {
  
  private String value;
  
  public Command(String value) {
    this.value = value;
  }
  
  public static Command STOP() {
    return new Command("STOP");
  }
}