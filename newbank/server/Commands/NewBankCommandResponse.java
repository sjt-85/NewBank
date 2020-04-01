package newbank.server.Commands;

public class NewBankCommandResponse {
  private final INewBankCommand command;
  private final ResponseType type;
  private String description;

  public enum ResponseType {
    EMPTY,
    SUCCEEDED,
    FAILED,
    INVALIDREQUEST,
    HELP
  }
  
  public static NewBankCommandResponse EMPTY = new NewBankCommandResponse(null, ResponseType.EMPTY, "");

  public static NewBankCommandResponse succeeded(INewBankCommand command, String description) {
    return new NewBankCommandResponse(command, ResponseType.SUCCEEDED, description);
  }

  public static NewBankCommandResponse failed(INewBankCommand command, String description) {
    return new NewBankCommandResponse(command, ResponseType.FAILED, description);
  }

  public static NewBankCommandResponse invalidRequest(INewBankCommand command, String description) {
    return new NewBankCommandResponse(command, ResponseType.INVALIDREQUEST, description);
  }
  
  public static NewBankCommandResponse help(INewBankCommand command) {
    return new NewBankCommandResponse(command, ResponseType.HELP, "");
  }
  
  protected NewBankCommandResponse(INewBankCommand command, ResponseType type, String description) {
    this.type = type;
    this.description = description;
    this.command = command;
  }

  public ResponseType getType() {
    return this.type;
  }

  public String getDescription() {
    return description;
  }
  
  public INewBankCommand getCommand() {
    return command;
  }
}
