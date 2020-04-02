package newbank.server.Commands;

public class NewBankCommandResponse {
  private ResponseType type;
  private String description;

  public enum ResponseType {
    EMPTY,
    SUCCEEDED,
    FAILED,
    INVALIDREQUEST,
    HELP
  }

  public static final NewBankCommandResponse EMPTY =
      new NewBankCommandResponse().set(ResponseType.EMPTY, "");

  public static NewBankCommandResponse createSucceeded(String description) {
    return new NewBankCommandResponse().succeeded(description);
  }

  public static NewBankCommandResponse createFailed(String description) {
    return new NewBankCommandResponse().failed(description);
  }

  public static NewBankCommandResponse createInvalidRequest(String description) {
    return new NewBankCommandResponse().invalidRequest(description);
  }

  public static NewBankCommandResponse createHelp() {
    return new NewBankCommandResponse().help("");
  }

  public NewBankCommandResponse succeeded(String description) {
    return set(ResponseType.SUCCEEDED, description);
  }

  public NewBankCommandResponse failed(String description) {
    return set(ResponseType.FAILED, description);
  }

  public NewBankCommandResponse invalidRequest(String description) {
    return set(ResponseType.INVALIDREQUEST, description);
  }

  public NewBankCommandResponse help(String description) {
    return set(ResponseType.HELP, description);
  }

  private NewBankCommandResponse set(ResponseType type, String description) {
    this.type = type;
    this.description = description;
    return this;
  }

  public NewBankCommandResponse() {}

  public ResponseType getType() {
    return this.type;
  }

  public String getDescription() {
    return description;
  }
}
