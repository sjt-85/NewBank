package newbank.server.Commands;

public class NewBankCommandResponse {
  private final ResponseType type;
  private String description;

  public enum ResponseType {
    EMPTY,
    SUCCEEDED,
    FAILED,
    INVALIDREQUEST,
    HELP
  }
  
  public static final NewBankCommandResponse EMPTY = new NewBankCommandResponse(ResponseType.EMPTY, "");

  public static NewBankCommandResponse succeeded(String description) {
    return new NewBankCommandResponse(ResponseType.SUCCEEDED, description);
  }

  public static NewBankCommandResponse failed(String description) {
    return new NewBankCommandResponse(ResponseType.FAILED, description);
  }

  public static NewBankCommandResponse invalidRequest(String description) {
    return new NewBankCommandResponse(ResponseType.INVALIDREQUEST, description);
  }
  
  public static NewBankCommandResponse help() {
    return new NewBankCommandResponse(ResponseType.HELP, "");
  }
  
  protected NewBankCommandResponse(ResponseType type, String description) {
    this.type = type;
    this.description = description;
  }

  public ResponseType getType() {
    return this.type;
  }

  public String getDescription() {
    return description;
  }
}
